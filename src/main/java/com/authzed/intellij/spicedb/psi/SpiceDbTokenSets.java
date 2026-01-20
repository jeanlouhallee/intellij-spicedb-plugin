package com.authzed.intellij.spicedb.psi;

import com.intellij.psi.tree.TokenSet;

import static com.authzed.intellij.spicedb.psi.SpiceDbTokenTypes.*;

/**
 * Token sets for grouping related SpiceDB tokens.
 */
public interface SpiceDbTokenSets {

    TokenSet KEYWORDS = TokenSet.create(
            KEYWORD_DEFINITION,
            KEYWORD_RELATION,
            KEYWORD_PERMISSION,
            KEYWORD_CAVEAT,
            KEYWORD_WITH,
            KEYWORD_IMPORT,
            KEYWORD_FROM,
            KEYWORD_NIL
    );

    TokenSet OPERATORS = TokenSet.create(
            OP_UNION,
            OP_INTERSECTION,
            OP_EXCLUSION,
            OP_ARROW,
            OP_TYPE_UNION,
            OP_ASSIGN
    );

    TokenSet COMMENTS = TokenSet.create(
            LINE_COMMENT,
            BLOCK_COMMENT
    );

    TokenSet BRACES = TokenSet.create(
            LBRACE,
            RBRACE
    );

    TokenSet PARENTHESES = TokenSet.create(
            LPAREN,
            RPAREN
    );

    TokenSet STRINGS = TokenSet.EMPTY;
}
