package com.authzed.intellij.spicedb.psi.impl;

import com.authzed.intellij.spicedb.SpiceDbIcons;
import com.authzed.intellij.spicedb.psi.SpiceDbElementFactory;
import com.authzed.intellij.spicedb.psi.SpiceDbNamedElement;
import com.authzed.intellij.spicedb.psi.SpiceDbTypes;
import com.authzed.intellij.spicedb.resolve.SpiceDbResolver;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.DelegatingGlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * Mixin base class for all named SpiceDB declarations
 * (definition, partial, caveat, relation, permission).
 * The name is the first IDENTIFIER token child (right after the keyword).
 */
public abstract class SpiceDbNamedElementImpl extends ASTWrapperPsiElement implements SpiceDbNamedElement {

    public SpiceDbNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        ASTNode node = getNode().findChildByType(SpiceDbTypes.IDENTIFIER);
        return node != null ? node.getPsi() : null;
    }

    @Nullable
    @Override
    public String getName() {
        PsiElement id = getNameIdentifier();
        return id != null ? id.getText() : null;
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        PsiElement id = getNameIdentifier();
        if (id == null) {
            throw new IncorrectOperationException("No name identifier");
        }
        id.replace(SpiceDbElementFactory.createIdentifier(getProject(), newName));
        return this;
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id != null ? id.getTextOffset() : super.getTextOffset();
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        SearchScope scope = super.getUseScope();
        if (scope instanceof GlobalSearchScope global) {
            return new DelegatingGlobalSearchScope(global) {
                @Override
                public boolean contains(@NotNull VirtualFile file) {
                    return super.contains(file) && !SpiceDbResolver.isExcluded(file);
                }
            };
        }
        return scope;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                return declarationKind() + " " + getName();
            }

            @Override
            public String getLocationString() {
                return getContainingFile() != null ? getContainingFile().getName() : null;
            }

            @Override
            public Icon getIcon(boolean unused) {
                return SpiceDbIcons.FILE;
            }
        };
    }

    private String declarationKind() {
        ASTNode first = getNode().getFirstChildNode();
        return first != null ? first.getText() : "declaration";
    }
}
