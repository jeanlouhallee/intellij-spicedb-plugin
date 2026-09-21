package com.authzed.intellij.spicedb.psi;

import com.intellij.psi.tree.TokenSet;

/**
 * Token sets for SpiceDB schema language.
 */
public interface SpiceDbTokenSets {

    TokenSet COMMENTS = TokenSet.create(SpiceDbTypes.LINE_COMMENT, SpiceDbTypes.BLOCK_COMMENT);

    TokenSet STRINGS = TokenSet.create(SpiceDbTypes.STRING);

    TokenSet KEYWORDS = TokenSet.create(
            SpiceDbTypes.DEFINITION,
            SpiceDbTypes.PARTIAL,
            SpiceDbTypes.CAVEAT,
            SpiceDbTypes.RELATION,
            SpiceDbTypes.PERMISSION,
            SpiceDbTypes.USE,
            SpiceDbTypes.IMPORT,
            SpiceDbTypes.WITH,
            SpiceDbTypes.AND,
            SpiceDbTypes.NIL);

    TokenSet IDENTIFIERS = TokenSet.create(SpiceDbTypes.IDENTIFIER);
}
