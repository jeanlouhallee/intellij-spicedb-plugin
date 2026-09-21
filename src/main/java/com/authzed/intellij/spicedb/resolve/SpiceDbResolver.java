package com.authzed.intellij.spicedb.resolve;

import com.authzed.intellij.spicedb.SpiceDbFileType;
import com.authzed.intellij.spicedb.psi.SpiceDbArrowExpr;
import com.authzed.intellij.spicedb.psi.SpiceDbArrowTail;
import com.authzed.intellij.spicedb.psi.SpiceDbCaveatDef;
import com.authzed.intellij.spicedb.psi.SpiceDbFile;
import com.authzed.intellij.spicedb.psi.SpiceDbLocalRef;
import com.authzed.intellij.spicedb.psi.SpiceDbMemberRef;
import com.authzed.intellij.spicedb.psi.SpiceDbNamedElement;
import com.authzed.intellij.spicedb.psi.SpiceDbObjectDef;
import com.authzed.intellij.spicedb.psi.SpiceDbPartialDef;
import com.authzed.intellij.spicedb.psi.SpiceDbPartialSplat;
import com.authzed.intellij.spicedb.psi.SpiceDbPermissionDecl;
import com.authzed.intellij.spicedb.psi.SpiceDbRelationDecl;
import com.authzed.intellij.spicedb.psi.SpiceDbTypeAnnotation;
import com.authzed.intellij.spicedb.psi.SpiceDbTypeUnion;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Resolution engine for SpiceDB schema references.
 *
 * SpiceDB composable schemas share a single global namespace, so all lookups
 * search every .zed file in the project (no import-graph walking needed).
 */
public final class SpiceDbResolver {

    private SpiceDbResolver() {
    }

    @NotNull
    public static List<SpiceDbFile> schemaFiles(@NotNull Project project) {
        List<SpiceDbFile> result = new ArrayList<>();
        Collection<VirtualFile> files =
                FileTypeIndex.getFiles(SpiceDbFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        PsiManager psiManager = PsiManager.getInstance(project);
        for (VirtualFile vf : files) {
            if (isExcluded(vf)) {
                continue;
            }
            if (psiManager.findFile(vf) instanceof SpiceDbFile spiceDbFile) {
                result.add(spiceDbFile);
            }
        }
        return result;
    }

    /**
     * Copies of the schema that duplicate every hand-written declaration are
     * excluded from resolution and usage search: composed output under a generated/
     * directory, build output under target/, and the packaged copy under
     * src/main/resources.
     */
    public static boolean isExcluded(@NotNull VirtualFile file) {
        for (VirtualFile dir = file.getParent(); dir != null; dir = dir.getParent()) {
            if ("generated".equals(dir.getName()) || "target".equals(dir.getName())) {
                return true;
            }
            if ("resources".equals(dir.getName())
                    && dir.getParent() != null && "main".equals(dir.getParent().getName())
                    && dir.getParent().getParent() != null && "src".equals(dir.getParent().getParent().getName())) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    public static List<SpiceDbObjectDef> findDefinitions(@NotNull Project project, @NotNull String name) {
        List<SpiceDbObjectDef> result = new ArrayList<>();
        for (SpiceDbFile file : schemaFiles(project)) {
            for (SpiceDbObjectDef def : PsiTreeUtil.getChildrenOfTypeAsList(file, SpiceDbObjectDef.class)) {
                if (name.equals(def.getName())) {
                    result.add(def);
                }
            }
        }
        return result;
    }

    @NotNull
    public static List<SpiceDbPartialDef> findPartials(@NotNull Project project, @NotNull String name) {
        List<SpiceDbPartialDef> result = new ArrayList<>();
        for (SpiceDbFile file : schemaFiles(project)) {
            for (SpiceDbPartialDef def : PsiTreeUtil.getChildrenOfTypeAsList(file, SpiceDbPartialDef.class)) {
                if (name.equals(def.getName())) {
                    result.add(def);
                }
            }
        }
        return result;
    }

    @NotNull
    public static List<SpiceDbCaveatDef> findCaveats(@NotNull Project project, @NotNull String name) {
        List<SpiceDbCaveatDef> result = new ArrayList<>();
        for (SpiceDbFile file : schemaFiles(project)) {
            for (SpiceDbCaveatDef def : PsiTreeUtil.getChildrenOfTypeAsList(file, SpiceDbCaveatDef.class)) {
                if (name.equals(def.getName())) {
                    result.add(def);
                }
            }
        }
        return result;
    }

    /**
     * All named members (relations + permissions) of a definition or partial,
     * including members pulled in transitively via {@code ...partial} splats.
     */
    @NotNull
    public static List<SpiceDbNamedElement> members(@NotNull PsiElement defOrPartial) {
        List<SpiceDbNamedElement> result = new ArrayList<>();
        collectMembers(defOrPartial, result, new HashSet<>());
        return result;
    }

    private static void collectMembers(@NotNull PsiElement defOrPartial,
                                       @NotNull List<SpiceDbNamedElement> result,
                                       @NotNull Set<String> visitedPartials) {
        List<SpiceDbRelationDecl> relations;
        List<SpiceDbPermissionDecl> permissions;
        List<SpiceDbPartialSplat> splats;

        if (defOrPartial instanceof SpiceDbObjectDef def) {
            relations = def.getRelationDeclList();
            permissions = def.getPermissionDeclList();
            splats = def.getPartialSplatList();
        } else if (defOrPartial instanceof SpiceDbPartialDef partial) {
            relations = partial.getRelationDeclList();
            permissions = partial.getPermissionDeclList();
            splats = partial.getPartialSplatList();
        } else {
            return;
        }

        result.addAll(relations);
        result.addAll(permissions);

        for (SpiceDbPartialSplat splat : splats) {
            String partialName = splat.getPartialRef().getText();
            if (partialName.isEmpty() || !visitedPartials.add(partialName)) {
                continue;
            }
            for (SpiceDbPartialDef partial : findPartials(defOrPartial.getProject(), partialName)) {
                collectMembers(partial, result, visitedPartials);
            }
        }
    }

    /**
     * Members with the given name in the definition (or partial) named {@code defName}.
     */
    @NotNull
    public static List<SpiceDbNamedElement> findMembers(@NotNull Project project,
                                                        @NotNull String defName,
                                                        @NotNull String memberName) {
        List<SpiceDbNamedElement> result = new ArrayList<>();
        for (SpiceDbObjectDef def : findDefinitions(project, defName)) {
            for (SpiceDbNamedElement member : members(def)) {
                if (memberName.equals(member.getName())) {
                    result.add(member);
                }
            }
        }
        if (result.isEmpty()) {
            for (SpiceDbPartialDef partial : findPartials(project, defName)) {
                for (SpiceDbNamedElement member : members(partial)) {
                    if (memberName.equals(member.getName())) {
                        result.add(member);
                    }
                }
            }
        }
        return result;
    }

    /**
     * The enclosing definition or partial for an element, or null at top level.
     */
    @Nullable
    public static PsiElement enclosingDef(@NotNull PsiElement element) {
        SpiceDbObjectDef def = PsiTreeUtil.getParentOfType(element, SpiceDbObjectDef.class);
        if (def != null) {
            return def;
        }
        return PsiTreeUtil.getParentOfType(element, SpiceDbPartialDef.class);
    }

    /**
     * Target type names of a relation declaration ({@code relation r: a | b#m | c:*} → a, b, c).
     */
    @NotNull
    public static List<String> relationTargetTypeNames(@NotNull SpiceDbRelationDecl relation) {
        Set<String> names = new LinkedHashSet<>();
        SpiceDbTypeUnion union = relation.getTypeUnion();
        if (union != null) {
            for (SpiceDbTypeAnnotation annotation : union.getTypeAnnotationList()) {
                names.add(annotation.getTypeRef().getText());
            }
        }
        return new ArrayList<>(names);
    }

    /**
     * Resolves a bare identifier used in a permission expression: a relation or
     * permission of the enclosing definition (including splatted partials), or —
     * as a fallback — a top-level definition with that name.
     */
    @NotNull
    public static List<PsiElement> resolveLocal(@NotNull SpiceDbLocalRef ref) {
        String name = ref.getText();
        List<PsiElement> result = new ArrayList<>();
        PsiElement def = enclosingDef(ref);
        if (def != null) {
            for (SpiceDbNamedElement member : members(def)) {
                if (name.equals(member.getName())) {
                    result.add(member);
                }
            }
        }
        if (result.isEmpty()) {
            result.addAll(findDefinitions(ref.getProject(), name));
        }
        return result;
    }

    /**
     * Resolves an arrow target ({@code base->x} or {@code base.any(x)}): the member
     * named {@code x} in every target type of the base relation.
     */
    @NotNull
    public static List<PsiElement> resolveMember(@NotNull SpiceDbMemberRef ref) {
        String name = ref.getText();
        List<PsiElement> result = new ArrayList<>();
        for (SpiceDbRelationDecl base : baseRelations(ref)) {
            for (String typeName : relationTargetTypeNames(base)) {
                result.addAll(findMembers(ref.getProject(), typeName, name));
            }
        }
        return dedupe(result);
    }

    /**
     * The relation declarations the arrow base of {@code ref} resolves to.
     * Handles chains: in {@code a->b->c}, the base of c is the resolution of b.
     */
    @NotNull
    private static List<SpiceDbRelationDecl> baseRelations(@NotNull SpiceDbMemberRef ref) {
        SpiceDbArrowTail myTail = PsiTreeUtil.getParentOfType(ref, SpiceDbArrowTail.class);
        SpiceDbArrowExpr arrowExpr = PsiTreeUtil.getParentOfType(ref, SpiceDbArrowExpr.class);
        if (myTail == null || arrowExpr == null) {
            return List.of();
        }

        List<SpiceDbArrowTail> tails = arrowExpr.getArrowTailList();
        int index = tails.indexOf(myTail);
        if (index < 0) {
            return List.of();
        }

        List<SpiceDbRelationDecl> bases = new ArrayList<>();
        if (index == 0) {
            SpiceDbLocalRef localRef = arrowExpr.getLocalRef();
            if (localRef != null) {
                for (PsiElement resolved : resolveLocal(localRef)) {
                    if (resolved instanceof SpiceDbRelationDecl relation) {
                        bases.add(relation);
                    }
                }
            }
        } else {
            SpiceDbMemberRef previous = tails.get(index - 1).getMemberRef();
            if (previous != null) {
                for (PsiElement resolved : resolveMember(previous)) {
                    if (resolved instanceof SpiceDbRelationDecl relation) {
                        bases.add(relation);
                    }
                }
            }
        }
        return bases;
    }

    /**
     * Resolves {@code type#subrel}: the relation (or permission) named
     * {@code subrel} in the annotation's type.
     */
    @NotNull
    public static List<PsiElement> resolveSubrel(@NotNull PsiElement subrelRef) {
        SpiceDbTypeAnnotation annotation =
                PsiTreeUtil.getParentOfType(subrelRef, SpiceDbTypeAnnotation.class);
        if (annotation == null) {
            return List.of();
        }
        String typeName = annotation.getTypeRef().getText();
        return new ArrayList<>(
                findMembers(subrelRef.getProject(), typeName, subrelRef.getText()));
    }

    @NotNull
    private static List<PsiElement> dedupe(@NotNull List<PsiElement> elements) {
        Set<PsiElement> seen = new LinkedHashSet<>();
        for (PsiElement element : elements) {
            if (element != null) {
                seen.add(element);
            }
        }
        return new ArrayList<>(seen);
    }
}
