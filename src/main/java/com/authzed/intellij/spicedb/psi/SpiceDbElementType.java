package com.authzed.intellij.spicedb.psi;

import com.authzed.intellij.spicedb.SpiceDbLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Composite element type for SpiceDB PSI nodes (generated parser rules).
 */
public class SpiceDbElementType extends IElementType {
    public SpiceDbElementType(@NotNull @NonNls String debugName) {
        super(debugName, SpiceDbLanguage.INSTANCE);
    }
}
