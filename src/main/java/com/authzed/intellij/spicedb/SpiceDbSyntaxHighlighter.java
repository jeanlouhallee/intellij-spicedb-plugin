package com.authzed.intellij.spicedb;

import com.authzed.intellij.spicedb.lexer.SpiceDbLexerAdapter;
import com.authzed.intellij.spicedb.psi.SpiceDbTokenSets;
import com.authzed.intellij.spicedb.psi.SpiceDbTypes;
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
 * Lexer-based syntax highlighter for SpiceDB schema files.
 * Name/reference coloring is layered on top by {@link SpiceDbAnnotator}.
 */
public class SpiceDbSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("SPICEDB_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey OPERATOR =
            createTextAttributesKey("SPICEDB_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);

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
    public static final TextAttributesKey STRING =
            createTextAttributesKey("SPICEDB_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey BRACES =
            createTextAttributesKey("SPICEDB_BRACES", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey PARENTHESES =
            createTextAttributesKey("SPICEDB_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey PREDEFINED_SYMBOL =
            createTextAttributesKey("SPICEDB_PREDEFINED_SYMBOL", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL);

    // Declaration names, applied by the annotator
    public static final TextAttributesKey DEFINITION_NAME =
            createTextAttributesKey("SPICEDB_DEFINITION_NAME", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey RELATION_NAME =
            createTextAttributesKey("SPICEDB_RELATION_NAME", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey PERMISSION_NAME =
            createTextAttributesKey("SPICEDB_PERMISSION_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey CAVEAT_NAME =
            createTextAttributesKey("SPICEDB_CAVEAT_NAME", DefaultLanguageHighlighterColors.INTERFACE_NAME);
    public static final TextAttributesKey TYPE_REFERENCE =
            createTextAttributesKey("SPICEDB_TYPE_REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE);

    private static final Map<IElementType, TextAttributesKey[]> TOKEN_HIGHLIGHTS = new HashMap<>();

    static {
        for (IElementType keyword : SpiceDbTokenSets.KEYWORDS.getTypes()) {
            TOKEN_HIGHLIGHTS.put(keyword, pack(KEYWORD));
        }

        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.PLUS, pack(OP_UNION));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.AMP, pack(OP_INTERSECTION));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.ARROW, pack(OP_ARROW));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.MINUS, pack(OPERATOR));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.PIPE, pack(OPERATOR));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.EQ, pack(OPERATOR));

        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.LINE_COMMENT, pack(LINE_COMMENT));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.BLOCK_COMMENT, pack(BLOCK_COMMENT));

        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.IDENTIFIER, pack(IDENTIFIER));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.NUMBER, pack(NUMBER));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.STRING, pack(STRING));

        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.LBRACE, pack(BRACES));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.RBRACE, pack(BRACES));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.LPAREN, pack(PARENTHESES));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.RPAREN, pack(PARENTHESES));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.HASH, pack(PREDEFINED_SYMBOL));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.STAR, pack(PREDEFINED_SYMBOL));
        TOKEN_HIGHLIGHTS.put(SpiceDbTypes.DOTDOTDOT, pack(PREDEFINED_SYMBOL));
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SpiceDbLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return TOKEN_HIGHLIGHTS.getOrDefault(tokenType, TextAttributesKey.EMPTY_ARRAY);
    }
}
