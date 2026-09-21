package com.authzed.intellij.spicedb.resolve;

import com.authzed.intellij.spicedb.psi.SpiceDbCaveatRef;
import com.authzed.intellij.spicedb.psi.SpiceDbLocalRef;
import com.authzed.intellij.spicedb.psi.SpiceDbMemberRef;
import com.authzed.intellij.spicedb.psi.SpiceDbPartialRef;
import com.authzed.intellij.spicedb.psi.SpiceDbSubrelRef;
import com.authzed.intellij.spicedb.psi.SpiceDbTypeRef;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Poly-variant reference for all SpiceDB identifier references. The kind of
 * resolution is determined by the PSI rule the identifier belongs to.
 */
public class SpiceDbReference extends PsiPolyVariantReferenceBase<PsiElement> {

    /** Built-in caveat-like trait that has no declaration to navigate to. */
    private static final String BUILTIN_EXPIRATION = "expiration";

    public SpiceDbReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()), isSoftFor(element));
    }

    private static boolean isSoftFor(PsiElement element) {
        return element instanceof SpiceDbCaveatRef
                && BUILTIN_EXPIRATION.equals(element.getText());
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        PsiElement element = getElement();
        List<PsiElement> targets = new ArrayList<>();

        if (element instanceof SpiceDbTypeRef) {
            targets.addAll(SpiceDbResolver.findDefinitions(element.getProject(), element.getText()));
        } else if (element instanceof SpiceDbSubrelRef) {
            targets.addAll(SpiceDbResolver.resolveSubrel(element));
        } else if (element instanceof SpiceDbCaveatRef) {
            if (!BUILTIN_EXPIRATION.equals(element.getText())) {
                targets.addAll(SpiceDbResolver.findCaveats(element.getProject(), element.getText()));
            }
        } else if (element instanceof SpiceDbPartialRef) {
            targets.addAll(SpiceDbResolver.findPartials(element.getProject(), element.getText()));
        } else if (element instanceof SpiceDbLocalRef localRef) {
            targets.addAll(SpiceDbResolver.resolveLocal(localRef));
        } else if (element instanceof SpiceDbMemberRef memberRef) {
            targets.addAll(SpiceDbResolver.resolveMember(memberRef));
        }

        return PsiElementResolveResult.createResults(targets);
    }

    @Override
    public Object @NotNull [] getVariants() {
        return EMPTY_ARRAY;
    }
}
