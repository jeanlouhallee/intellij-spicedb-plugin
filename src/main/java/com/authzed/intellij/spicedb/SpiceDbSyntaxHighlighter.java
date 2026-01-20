package com.authzed.intellij.spicedb;

import com.authzed.intellij.spicedb.psi.SpiceDbTokenTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Syntax highlighter for SpiceDB schema files.
 */
public class SpiceDbSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("SPICEDB_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey OPERATOR =
            createTextAttributesKey("SPICEDB_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    // Permission operators - distinct colors for visibility
    public static final TextAttributesKey OP_UNION =
            createTextAttributesKey("SPICEDB_OP_UNION", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey OP_INTERSECTION =
            createTextAttributesKey("SPICEDB_OP_INTERSECTION", DefaultLanguageHighlighterColors.STATIC_METHOD);
    public static final TextAttributesKey OP_ARROW =
            createTextAttributesKey("SPICEDB_OP_ARROW", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    public static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("SPICEDB_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BLOCK_COMMENT =
            createTextAttributesKey("SPICEDB_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("SPICEDB_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("SPICEDB_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey BRACES =
            createTextAttributesKey("SPICEDB_BRACES", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey PARENTHESES =
            createTextAttributesKey("SPICEDB_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey PREDEFINED_SYMBOL =
            createTextAttributesKey("SPICEDB_PREDEFINED_SYMBOL", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL);

    // Declaration names - each with distinct colors
    public static final TextAttributesKey DEFINITION_NAME =
            createTextAttributesKey("SPICEDB_DEFINITION_NAME", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey RELATION_NAME =
            createTextAttributesKey("SPICEDB_RELATION_NAME", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey PERMISSION_NAME =
            createTextAttributesKey("SPICEDB_PERMISSION_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey CAVEAT_NAME =
            createTextAttributesKey("SPICEDB_CAVEAT_NAME", DefaultLanguageHighlighterColors.INTERFACE_NAME);

    private static final Map<IElementType, TextAttributesKey[]> TOKEN_HIGHLIGHTS = new HashMap<>();

    static {
        // Keywords
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.KEYWORD_DEFINITION, pack(KEYWORD));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.KEYWORD_RELATION, pack(KEYWORD));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.KEYWORD_PERMISSION, pack(KEYWORD));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.KEYWORD_CAVEAT, pack(KEYWORD));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.KEYWORD_WITH, pack(KEYWORD));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.KEYWORD_IMPORT, pack(KEYWORD));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.KEYWORD_FROM, pack(KEYWORD));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.KEYWORD_NIL, pack(KEYWORD));

        // Permission operators - distinct colors
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.OP_UNION, pack(OP_UNION));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.OP_INTERSECTION, pack(OP_INTERSECTION));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.OP_ARROW, pack(OP_ARROW));

        // Other operators
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.OP_EXCLUSION, pack(OPERATOR));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.OP_TYPE_UNION, pack(OPERATOR));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.OP_ASSIGN, pack(OPERATOR));

        // Comments
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.LINE_COMMENT, pack(LINE_COMMENT));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.BLOCK_COMMENT, pack(BLOCK_COMMENT));

        // Literals and identifiers
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.IDENTIFIER, pack(IDENTIFIER));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.NUMBER, pack(NUMBER));

        // Declaration names
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.DEFINITION_NAME, pack(DEFINITION_NAME));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.RELATION_NAME, pack(RELATION_NAME));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.PERMISSION_NAME, pack(PERMISSION_NAME));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.CAVEAT_NAME, pack(CAVEAT_NAME));

        // Punctuation
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.LBRACE, pack(BRACES));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.RBRACE, pack(BRACES));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.LPAREN, pack(PARENTHESES));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.RPAREN, pack(PARENTHESES));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.HASH, pack(PREDEFINED_SYMBOL));
        TOKEN_HIGHLIGHTS.put(SpiceDbTokenTypes.WILDCARD, pack(PREDEFINED_SYMBOL));
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SpiceDbLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return TOKEN_HIGHLIGHTS.getOrDefault(tokenType, TextAttributesKey.EMPTY_ARRAY);
    }
}
