package com.authzed.intellij.spicedb;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles "Go to Definition" (Ctrl+Click) for SpiceDB schema references.
 *
 * Supports navigation for:
 * - Type references in relations: `relation foo: type_name` → `definition type_name`
 * - Arrow permissions: `platform->some_permission` → permission in the relation's target type
 * - Local references: `read_profile` in a permission expression → local permission/relation
 *
 * Searches only in ./schema/schema.zed and ./schema/compose/*.zed
 */
public class SpiceDbGotoDeclarationHandler implements GotoDeclarationHandler {

    private static final String SCHEMA_PATH = "schema/schema.zed";
    private static final String COMPOSE_PATH = "schema/compose";

    private static final Pattern DEFINITION_PATTERN = Pattern.compile("definition\\s+(\\w+)");
    private static final Pattern ARROW_BEFORE_PATTERN = Pattern.compile("(\\w+)\\s*->\\s*$");

    @Override
    public PsiElement @Nullable [] getGotoDeclarationTargets(@Nullable PsiElement sourceElement,
                                                              int offset,
                                                              Editor editor) {
        if (sourceElement == null) {
            return null;
        }

        PsiFile file = sourceElement.getContainingFile();
        if (file == null || !(file.getFileType() instanceof SpiceDbFileType)) {
            return null;
        }

        String word = getWordAtElement(sourceElement);
        if (word == null || word.isEmpty()) {
            return null;
        }

        if (isDeclaration(sourceElement, "definition", "permission", "relation")) {
            return null;
        }

        Project project = sourceElement.getProject();
        VirtualFile projectBase = project.getBaseDir();
        if (projectBase == null) {
            return null;
        }

        List<VirtualFile> schemaFiles = collectSchemaFiles(projectBase);
        PsiManager psiManager = PsiManager.getInstance(project);
        int elementOffset = sourceElement.getTextOffset();

        List<PsiElement> targets = new ArrayList<>();
        String fileText = file.getText();
        String currentDef = findCurrentDefinition(fileText, elementOffset);

        // 1. Check if clicking on left side of arrow (e.g., "platform" in platform->permission)
        //    Navigate to the relation declaration
        if (isBeforeArrow(fileText, elementOffset, word)) {
            if (currentDef != null) {
                findRelationInDefinition(schemaFiles, psiManager, currentDef, word, targets);
            }
        }

        // 2. Check for arrow reference (e.g., clicking "something" in platform->something)
        //    Navigate to the permission or relation in the relation's target type
        if (targets.isEmpty()) {
            String relationName = getArrowRelationName(fileText, elementOffset);
            if (relationName != null && currentDef != null) {
                String targetType = findRelationType(schemaFiles, psiManager, currentDef, relationName);
                if (targetType != null) {
                    findPermissionOrRelationInDefinition(schemaFiles, psiManager, targetType, word, targets);
                }
            }
        }

        // 3. Try local permission/relation in current definition
        if (targets.isEmpty() && currentDef != null) {
            findLocalPermissionOrRelation(schemaFiles, psiManager, currentDef, word, targets);
        }

        // 3. Try type definition lookup
        if (targets.isEmpty()) {
            findDefinitions(schemaFiles, psiManager, word, targets);
        }

        return targets.isEmpty() ? null : targets.toArray(new PsiElement[0]);
    }

    private String getWordAtElement(PsiElement element) {
        String text = element.getText();
        return (text != null && text.matches("\\w+")) ? text : null;
    }

    private boolean isDeclaration(PsiElement element, String... keywords) {
        PsiFile file = element.getContainingFile();
        if (file == null) {
            return false;
        }

        String fileText = file.getText();
        int offset = element.getTextOffset();
        int searchStart = Math.max(0, offset - 50);
        String precedingText = fileText.substring(searchStart, offset).trim();

        for (String keyword : keywords) {
            if (precedingText.endsWith(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the word at the given offset is followed by an arrow (->).
     * Used to detect clicking on the left side of an arrow expression.
     */
    private boolean isBeforeArrow(String fileText, int offset, String word) {
        int afterWord = offset + word.length();
        int searchEnd = Math.min(fileText.length(), afterWord + 10);
        String followingText = fileText.substring(afterWord, searchEnd).trim();
        return followingText.startsWith("->");
    }

    /**
     * If the element follows an arrow (->), returns the relation name before the arrow.
     */
    @Nullable
    private String getArrowRelationName(String fileText, int offset) {
        int searchStart = Math.max(0, offset - 100);
        String precedingText = fileText.substring(searchStart, offset);

        if (!precedingText.trim().endsWith("->")) {
            return null;
        }

        Matcher matcher = ARROW_BEFORE_PATTERN.matcher(precedingText);
        return matcher.find() ? matcher.group(1) : null;
    }

    /**
     * Finds which definition contains the given offset.
     */
    @Nullable
    private String findCurrentDefinition(String fileText, int offset) {
        Matcher matcher = DEFINITION_PATTERN.matcher(fileText);
        String lastDefinition = null;

        while (matcher.find() && matcher.start() <= offset) {
            lastDefinition = matcher.group(1);
        }
        return lastDefinition;
    }

    /**
     * Extracts the definition body (content between braces) for a given definition name.
     */
    @Nullable
    private DefinitionBody findDefinitionBody(String fileText, String definitionName) {
        Pattern pattern = Pattern.compile("definition\\s+" + Pattern.quote(definitionName) + "\\s*\\{");
        Matcher matcher = pattern.matcher(fileText);

        if (!matcher.find()) {
            return null;
        }

        int bodyStart = matcher.end();
        int braceCount = 1;
        int pos = bodyStart;

        while (pos < fileText.length() && braceCount > 0) {
            char c = fileText.charAt(pos);
            if (c == '{') braceCount++;
            else if (c == '}') braceCount--;
            pos++;
        }

        return new DefinitionBody(fileText.substring(bodyStart, pos - 1), bodyStart);
    }

    /**
     * Finds the type of a relation within a definition.
     */
    @Nullable
    private String findRelationType(List<VirtualFile> schemaFiles, PsiManager psiManager,
                                    String definitionName, String relationName) {
        Pattern relationPattern = Pattern.compile(
                "relation\\s+" + Pattern.quote(relationName) + "\\s*:\\s*(\\w+)");

        for (VirtualFile vf : schemaFiles) {
            PsiFile psiFile = psiManager.findFile(vf);
            if (psiFile == null) continue;

            DefinitionBody body = findDefinitionBody(psiFile.getText(), definitionName);
            if (body == null) continue;

            Matcher matcher = relationPattern.matcher(body.content);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private void findPermissionOrRelationInDefinition(List<VirtualFile> schemaFiles, PsiManager psiManager,
                                                       String definitionName, String name,
                                                       List<PsiElement> results) {
        Pattern permPattern = Pattern.compile("permission\\s+(" + Pattern.quote(name) + ")");
        Pattern relPattern = Pattern.compile("relation\\s+(" + Pattern.quote(name) + ")\\s*:");

        for (VirtualFile vf : schemaFiles) {
            PsiFile psiFile = psiManager.findFile(vf);
            if (psiFile == null) continue;

            DefinitionBody body = findDefinitionBody(psiFile.getText(), definitionName);
            if (body == null) continue;

            // Try permission first
            Matcher permMatcher = permPattern.matcher(body.content);
            if (permMatcher.find()) {
                PsiElement element = psiFile.findElementAt(body.offset + permMatcher.start(1));
                if (element != null) {
                    results.add(element);
                    return;
                }
            }

            // Then try relation
            Matcher relMatcher = relPattern.matcher(body.content);
            if (relMatcher.find()) {
                PsiElement element = psiFile.findElementAt(body.offset + relMatcher.start(1));
                if (element != null) {
                    results.add(element);
                    return;
                }
            }
        }
    }

    private void findRelationInDefinition(List<VirtualFile> schemaFiles, PsiManager psiManager,
                                          String definitionName, String relationName,
                                          List<PsiElement> results) {
        Pattern relPattern = Pattern.compile("relation\\s+(" + Pattern.quote(relationName) + ")\\s*:");

        for (VirtualFile vf : schemaFiles) {
            PsiFile psiFile = psiManager.findFile(vf);
            if (psiFile == null) continue;

            DefinitionBody body = findDefinitionBody(psiFile.getText(), definitionName);
            if (body == null) continue;

            Matcher matcher = relPattern.matcher(body.content);
            if (matcher.find()) {
                PsiElement element = psiFile.findElementAt(body.offset + matcher.start(1));
                if (element != null) {
                    results.add(element);
                    return;
                }
            }
        }
    }

    private void findLocalPermissionOrRelation(List<VirtualFile> schemaFiles, PsiManager psiManager,
                                               String definitionName, String name,
                                               List<PsiElement> results) {
        Pattern permPattern = Pattern.compile("permission\\s+(" + Pattern.quote(name) + ")\\s*=");
        Pattern relPattern = Pattern.compile("relation\\s+(" + Pattern.quote(name) + ")\\s*:");

        for (VirtualFile vf : schemaFiles) {
            PsiFile psiFile = psiManager.findFile(vf);
            if (psiFile == null) continue;

            DefinitionBody body = findDefinitionBody(psiFile.getText(), definitionName);
            if (body == null) continue;

            // Try permission first
            Matcher permMatcher = permPattern.matcher(body.content);
            if (permMatcher.find()) {
                PsiElement element = psiFile.findElementAt(body.offset + permMatcher.start(1));
                if (element != null) {
                    results.add(element);
                    return;
                }
            }

            // Then try relation
            Matcher relMatcher = relPattern.matcher(body.content);
            if (relMatcher.find()) {
                PsiElement element = psiFile.findElementAt(body.offset + relMatcher.start(1));
                if (element != null) {
                    results.add(element);
                    return;
                }
            }
        }
    }

    private void findDefinitions(List<VirtualFile> schemaFiles, PsiManager psiManager,
                                 String definitionName, List<PsiElement> results) {
        for (VirtualFile vf : schemaFiles) {
            PsiFile psiFile = psiManager.findFile(vf);
            if (psiFile == null) continue;

            Matcher matcher = DEFINITION_PATTERN.matcher(psiFile.getText());
            while (matcher.find()) {
                if (matcher.group(1).equals(definitionName)) {
                    PsiElement element = psiFile.findElementAt(matcher.start(1));
                    if (element != null) {
                        results.add(element);
                    }
                }
            }
        }
    }

    private List<VirtualFile> collectSchemaFiles(VirtualFile projectBase) {
        List<VirtualFile> files = new ArrayList<>();

        VirtualFile mainSchema = projectBase.findFileByRelativePath(SCHEMA_PATH);
        if (mainSchema != null && mainSchema.exists() && !mainSchema.isDirectory()) {
            files.add(mainSchema);
        }

        VirtualFile composeDir = projectBase.findFileByRelativePath(COMPOSE_PATH);
        if (composeDir != null && composeDir.exists() && composeDir.isDirectory()) {
            for (VirtualFile child : composeDir.getChildren()) {
                if (!child.isDirectory() && "zed".equals(child.getExtension())) {
                    files.add(child);
                }
            }
        }

        return files;
    }

    private record DefinitionBody(String content, int offset) {}
}
