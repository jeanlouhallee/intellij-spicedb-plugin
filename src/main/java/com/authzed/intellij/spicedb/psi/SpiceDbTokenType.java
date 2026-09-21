package com.authzed.intellij.spicedb.psi;

import com.authzed.intellij.spicedb.SpiceDbLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Leaf token type for SpiceDB lexer tokens.
 */
public class SpiceDbTokenType extends IElementType {
    public SpiceDbTokenType(@NotNull @NonNls String debugName) {
        super(debugName, SpiceDbLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "SpiceDbTokenType." + super.toString();
    }
}
