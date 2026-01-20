package com.authzed.intellij.spicedb;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.authzed.intellij.spicedb.psi.SpiceDbTokenTypes.*;

/**
 * Hand-written lexer for SpiceDB schema files.
 * Tracks state to distinguish declaration names from regular identifiers.
 */
public class SpiceDbLexer extends LexerBase {

    // State constants
    private static final int STATE_NORMAL = 0;
    private static final int STATE_AFTER_DEFINITION = 1;
    private static final int STATE_AFTER_RELATION = 2;
    private static final int STATE_AFTER_PERMISSION = 3;
    private static final int STATE_AFTER_CAVEAT = 4;

    private CharSequence buffer;
    private int bufferEnd;
    private int tokenStart;
    private int tokenEnd;
    private IElementType tokenType;
    private int state;

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.bufferEnd = endOffset;
        this.tokenStart = startOffset;
        this.tokenEnd = startOffset;
        this.tokenType = null;
        this.state = initialState;
        advance();
    }

    @Override
    public int getState() {
        return state;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return tokenType;
    }

    @Override
    public int getTokenStart() {
        return tokenStart;
    }

    @Override
    public int getTokenEnd() {
        return tokenEnd;
    }

    @Override
    public void advance() {
        tokenStart = tokenEnd;
        if (tokenStart >= bufferEnd) {
            tokenType = null;
            return;
        }

        char c = buffer.charAt(tokenStart);

        // Whitespace - don't change state
        if (Character.isWhitespace(c)) {
            tokenEnd = tokenStart + 1;
            while (tokenEnd < bufferEnd && Character.isWhitespace(buffer.charAt(tokenEnd))) {
                tokenEnd++;
            }
            tokenType = TokenType.WHITE_SPACE;
            return;
        }

        // Line comment - reset state
        if (c == '/' && tokenStart + 1 < bufferEnd && buffer.charAt(tokenStart + 1) == '/') {
            tokenEnd = tokenStart + 2;
            while (tokenEnd < bufferEnd && buffer.charAt(tokenEnd) != '\n' && buffer.charAt(tokenEnd) != '\r') {
                tokenEnd++;
            }
            tokenType = LINE_COMMENT;
            state = STATE_NORMAL;
            return;
        }

        // Block comment - reset state
        if (c == '/' && tokenStart + 1 < bufferEnd && buffer.charAt(tokenStart + 1) == '*') {
            tokenEnd = tokenStart + 2;
            while (tokenEnd + 1 < bufferEnd) {
                if (buffer.charAt(tokenEnd) == '*' && buffer.charAt(tokenEnd + 1) == '/') {
                    tokenEnd += 2;
                    break;
                }
                tokenEnd++;
            }
            if (tokenEnd == bufferEnd - 1) {
                tokenEnd = bufferEnd;
            }
            tokenType = BLOCK_COMMENT;
            state = STATE_NORMAL;
            return;
        }

        // Arrow operator (must check before minus)
        if (c == '-' && tokenStart + 1 < bufferEnd && buffer.charAt(tokenStart + 1) == '>') {
            tokenEnd = tokenStart + 2;
            tokenType = OP_ARROW;
            state = STATE_NORMAL;
            return;
        }

        // Single character operators and punctuation
        switch (c) {
            case '+':
                tokenEnd = tokenStart + 1;
                tokenType = OP_UNION;
                state = STATE_NORMAL;
                return;
            case '&':
                tokenEnd = tokenStart + 1;
                tokenType = OP_INTERSECTION;
                state = STATE_NORMAL;
                return;
            case '-':
                tokenEnd = tokenStart + 1;
                tokenType = OP_EXCLUSION;
                state = STATE_NORMAL;
                return;
            case '|':
                tokenEnd = tokenStart + 1;
                tokenType = OP_TYPE_UNION;
                state = STATE_NORMAL;
                return;
            case '=':
                tokenEnd = tokenStart + 1;
                tokenType = OP_ASSIGN;
                state = STATE_NORMAL;
                return;
            case '{':
                tokenEnd = tokenStart + 1;
                tokenType = LBRACE;
                state = STATE_NORMAL;
                return;
            case '}':
                tokenEnd = tokenStart + 1;
                tokenType = RBRACE;
                state = STATE_NORMAL;
                return;
            case '(':
                tokenEnd = tokenStart + 1;
                tokenType = LPAREN;
                state = STATE_NORMAL;
                return;
            case ')':
                tokenEnd = tokenStart + 1;
                tokenType = RPAREN;
                state = STATE_NORMAL;
                return;
            case ':':
                tokenEnd = tokenStart + 1;
                tokenType = COLON;
                state = STATE_NORMAL;
                return;
            case '#':
                tokenEnd = tokenStart + 1;
                tokenType = HASH;
                state = STATE_NORMAL;
                return;
            case '*':
                tokenEnd = tokenStart + 1;
                tokenType = WILDCARD;
                state = STATE_NORMAL;
                return;
            case ',':
                tokenEnd = tokenStart + 1;
                tokenType = COMMA;
                state = STATE_NORMAL;
                return;
            case '.':
                tokenEnd = tokenStart + 1;
                tokenType = DOT;
                state = STATE_NORMAL;
                return;
            case '/':
                tokenEnd = tokenStart + 1;
                tokenType = SLASH;
                state = STATE_NORMAL;
                return;
        }

        // Number
        if (Character.isDigit(c)) {
            tokenEnd = tokenStart + 1;
            while (tokenEnd < bufferEnd && Character.isDigit(buffer.charAt(tokenEnd))) {
                tokenEnd++;
            }
            tokenType = NUMBER;
            state = STATE_NORMAL;
            return;
        }

        // Identifier or keyword
        if (Character.isLetter(c) || c == '_') {
            tokenEnd = tokenStart + 1;
            while (tokenEnd < bufferEnd) {
                char ch = buffer.charAt(tokenEnd);
                if (Character.isLetterOrDigit(ch) || ch == '_') {
                    tokenEnd++;
                } else {
                    break;
                }
            }
            String word = buffer.subSequence(tokenStart, tokenEnd).toString();

            // Check if this is a name following a declaration keyword
            if (state != STATE_NORMAL) {
                tokenType = switch (state) {
                    case STATE_AFTER_DEFINITION -> DEFINITION_NAME;
                    case STATE_AFTER_RELATION -> RELATION_NAME;
                    case STATE_AFTER_PERMISSION -> PERMISSION_NAME;
                    case STATE_AFTER_CAVEAT -> CAVEAT_NAME;
                    default -> IDENTIFIER;
                };
                state = STATE_NORMAL;
                return;
            }

            // Check for keywords and set state accordingly
            tokenType = switch (word) {
                case "definition" -> {
                    state = STATE_AFTER_DEFINITION;
                    yield KEYWORD_DEFINITION;
                }
                case "relation" -> {
                    state = STATE_AFTER_RELATION;
                    yield KEYWORD_RELATION;
                }
                case "permission" -> {
                    state = STATE_AFTER_PERMISSION;
                    yield KEYWORD_PERMISSION;
                }
                case "caveat" -> {
                    state = STATE_AFTER_CAVEAT;
                    yield KEYWORD_CAVEAT;
                }
                case "with" -> {
                    state = STATE_NORMAL;
                    yield KEYWORD_WITH;
                }
                case "import" -> {
                    state = STATE_NORMAL;
                    yield KEYWORD_IMPORT;
                }
                case "from" -> {
                    state = STATE_NORMAL;
                    yield KEYWORD_FROM;
                }
                case "nil" -> {
                    state = STATE_NORMAL;
                    yield KEYWORD_NIL;
                }
                default -> {
                    state = STATE_NORMAL;
                    yield IDENTIFIER;
                }
            };
            return;
        }

        // Bad character
        tokenEnd = tokenStart + 1;
        tokenType = TokenType.BAD_CHARACTER;
        state = STATE_NORMAL;
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return bufferEnd;
    }
}
