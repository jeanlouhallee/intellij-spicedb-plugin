package com.authzed.intellij.spicedb;

import com.authzed.intellij.spicedb.lexer.SpiceDbLexerAdapter;
import com.authzed.intellij.spicedb.psi.SpiceDbCaveatDef;
import com.authzed.intellij.spicedb.psi.SpiceDbNamedElement;
import com.authzed.intellij.spicedb.psi.SpiceDbObjectDef;
import com.authzed.intellij.spicedb.psi.SpiceDbPartialDef;
import com.authzed.intellij.spicedb.psi.SpiceDbPermissionDecl;
import com.authzed.intellij.spicedb.psi.SpiceDbRelationDecl;
import com.authzed.intellij.spicedb.psi.SpiceDbTokenSets;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Find Usages support for SpiceDB declarations.
 */
public class SpiceDbFindUsagesProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(
                new SpiceDbLexerAdapter(),
                SpiceDbTokenSets.IDENTIFIERS,
                SpiceDbTokenSets.COMMENTS,
                SpiceDbTokenSets.STRINGS);
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof SpiceDbNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof SpiceDbObjectDef) {
            return "definition";
        }
        if (element instanceof SpiceDbPartialDef) {
            return "partial";
        }
        if (element instanceof SpiceDbRelationDecl) {
            return "relation";
        }
        if (element instanceof SpiceDbPermissionDecl) {
            return "permission";
        }
        if (element instanceof SpiceDbCaveatDef) {
            return "caveat";
        }
        return "declaration";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof SpiceDbNamedElement named && named.getName() != null) {
            return named.getName();
        }
        return element.getText();
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}
