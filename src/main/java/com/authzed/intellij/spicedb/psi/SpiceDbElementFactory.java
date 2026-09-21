package com.authzed.intellij.spicedb.psi;

import com.authzed.intellij.spicedb.SpiceDbFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Creates SpiceDB PSI elements from text, used for rename support.
 */
public final class SpiceDbElementFactory {

    private SpiceDbElementFactory() {
    }

    public static PsiElement createIdentifier(Project project, String name) {
        SpiceDbFile file = createFile(project, "definition " + name + " {}");
        SpiceDbObjectDef def = PsiTreeUtil.findChildOfType(file, SpiceDbObjectDef.class);
        if (def == null || def.getNameIdentifier() == null) {
            throw new IllegalArgumentException("Invalid SpiceDB identifier: " + name);
        }
        return def.getNameIdentifier();
    }

    private static SpiceDbFile createFile(Project project, String text) {
        return (SpiceDbFile) PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.zed", SpiceDbFileType.INSTANCE, text);
    }
}
