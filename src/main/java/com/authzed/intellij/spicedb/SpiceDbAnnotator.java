package com.authzed.intellij.spicedb;

import com.authzed.intellij.spicedb.psi.SpiceDbCaveatDef;
import com.authzed.intellij.spicedb.psi.SpiceDbNamedElement;
import com.authzed.intellij.spicedb.psi.SpiceDbObjectDef;
import com.authzed.intellij.spicedb.psi.SpiceDbPartialDef;
import com.authzed.intellij.spicedb.psi.SpiceDbPermissionDecl;
import com.authzed.intellij.spicedb.psi.SpiceDbRelationDecl;
import com.authzed.intellij.spicedb.psi.SpiceDbTypeRef;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Applies semantic coloring to declaration names and type references,
 * replacing the old stateful-lexer name highlighting.
 */
public class SpiceDbAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof SpiceDbNamedElement named) {
            PsiElement id = named.getNameIdentifier();
            if (id == null) {
                return;
            }
            TextAttributesKey key = keyFor(named);
            if (key != null) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(id.getTextRange())
                        .textAttributes(key)
                        .create();
            }
        } else if (element instanceof SpiceDbTypeRef) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.getTextRange())
                    .textAttributes(SpiceDbSyntaxHighlighter.TYPE_REFERENCE)
                    .create();
        }
    }

    private static TextAttributesKey keyFor(SpiceDbNamedElement element) {
        if (element instanceof SpiceDbObjectDef || element instanceof SpiceDbPartialDef) {
            return SpiceDbSyntaxHighlighter.DEFINITION_NAME;
        }
        if (element instanceof SpiceDbRelationDecl) {
            return SpiceDbSyntaxHighlighter.RELATION_NAME;
        }
        if (element instanceof SpiceDbPermissionDecl) {
            return SpiceDbSyntaxHighlighter.PERMISSION_NAME;
        }
        if (element instanceof SpiceDbCaveatDef) {
            return SpiceDbSyntaxHighlighter.CAVEAT_NAME;
        }
        return null;
    }
}
