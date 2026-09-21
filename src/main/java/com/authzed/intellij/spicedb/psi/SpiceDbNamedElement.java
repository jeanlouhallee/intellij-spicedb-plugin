package com.authzed.intellij.spicedb.psi;

import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * Common interface for SpiceDB declarations that carry a name:
 * definitions, partials, caveats, relations, and permissions.
 */
public interface SpiceDbNamedElement extends PsiNameIdentifierOwner {
}
