package com.authzed.intellij.spicedb.psi.impl;

import com.authzed.intellij.spicedb.resolve.SpiceDbReference;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

/**
 * Mixin base class for all reference-carrying PSI rules
 * (type_ref, subrel_ref, caveat_ref, partial_ref, local_ref, member_ref).
 */
public abstract class SpiceDbRefElementImpl extends ASTWrapperPsiElement {

    public SpiceDbRefElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new SpiceDbReference(this);
    }
}
