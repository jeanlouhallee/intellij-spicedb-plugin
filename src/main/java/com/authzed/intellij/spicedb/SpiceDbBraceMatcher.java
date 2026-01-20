package com.authzed.intellij.spicedb;

import com.authzed.intellij.spicedb.psi.SpiceDbTokenTypes;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Brace matching for SpiceDB schema files.
 */
public class SpiceDbBraceMatcher implements PairedBraceMatcher {

    private static final BracePair[] PAIRS = new BracePair[]{
            new BracePair(SpiceDbTokenTypes.LBRACE, SpiceDbTokenTypes.RBRACE, true),
            new BracePair(SpiceDbTokenTypes.LPAREN, SpiceDbTokenTypes.RPAREN, false)
    };

    @Override
    public BracePair @NotNull [] getPairs() {
        return PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
