package com.authzed.intellij.spicedb.psi;

import com.authzed.intellij.spicedb.SpiceDbLanguage;
import com.intellij.psi.tree.IElementType;

/**
 * Token types for SpiceDB schema language.
 */
public interface SpiceDbTokenTypes {

    // Keywords
    IElementType KEYWORD_DEFINITION = new SpiceDbTokenType("DEFINITION");
    IElementType KEYWORD_RELATION = new SpiceDbTokenType("RELATION");
    IElementType KEYWORD_PERMISSION = new SpiceDbTokenType("PERMISSION");
    IElementType KEYWORD_CAVEAT = new SpiceDbTokenType("CAVEAT");
    IElementType KEYWORD_WITH = new SpiceDbTokenType("WITH");
    IElementType KEYWORD_IMPORT = new SpiceDbTokenType("IMPORT");
    IElementType KEYWORD_FROM = new SpiceDbTokenType("FROM");
    IElementType KEYWORD_NIL = new SpiceDbTokenType("NIL");

    // Operators
    IElementType OP_UNION = new SpiceDbTokenType("OP_UNION");
    IElementType OP_INTERSECTION = new SpiceDbTokenType("OP_INTERSECTION");
    IElementType OP_EXCLUSION = new SpiceDbTokenType("OP_EXCLUSION");
    IElementType OP_ARROW = new SpiceDbTokenType("OP_ARROW");
    IElementType OP_TYPE_UNION = new SpiceDbTokenType("OP_TYPE_UNION");
    IElementType OP_ASSIGN = new SpiceDbTokenType("OP_ASSIGN");

    // Punctuation
    IElementType LBRACE = new SpiceDbTokenType("LBRACE");
    IElementType RBRACE = new SpiceDbTokenType("RBRACE");
    IElementType LPAREN = new SpiceDbTokenType("LPAREN");
    IElementType RPAREN = new SpiceDbTokenType("RPAREN");
    IElementType COLON = new SpiceDbTokenType("COLON");
    IElementType HASH = new SpiceDbTokenType("HASH");
    IElementType WILDCARD = new SpiceDbTokenType("WILDCARD");
    IElementType COMMA = new SpiceDbTokenType("COMMA");
    IElementType DOT = new SpiceDbTokenType("DOT");
    IElementType SLASH = new SpiceDbTokenType("SLASH");

    // Comments
    IElementType LINE_COMMENT = new SpiceDbTokenType("LINE_COMMENT");
    IElementType BLOCK_COMMENT = new SpiceDbTokenType("BLOCK_COMMENT");

    // Literals and identifiers
    IElementType IDENTIFIER = new SpiceDbTokenType("IDENTIFIER");
    IElementType NUMBER = new SpiceDbTokenType("NUMBER");

    // Named declarations (identifiers that follow keywords)
    IElementType DEFINITION_NAME = new SpiceDbTokenType("DEFINITION_NAME");
    IElementType RELATION_NAME = new SpiceDbTokenType("RELATION_NAME");
    IElementType PERMISSION_NAME = new SpiceDbTokenType("PERMISSION_NAME");
    IElementType CAVEAT_NAME = new SpiceDbTokenType("CAVEAT_NAME");

    /**
     * Custom IElementType for SpiceDB tokens.
     */
    class SpiceDbTokenType extends IElementType {
        public SpiceDbTokenType(String debugName) {
            super(debugName, SpiceDbLanguage.INSTANCE);
        }
    }
}
