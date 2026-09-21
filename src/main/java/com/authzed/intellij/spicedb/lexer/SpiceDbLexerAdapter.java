package com.authzed.intellij.spicedb.lexer;

import com.intellij.lexer.FlexAdapter;

/**
 * IntelliJ lexer adapter over the JFlex-generated SpiceDB lexer.
 */
public class SpiceDbLexerAdapter extends FlexAdapter {
    public SpiceDbLexerAdapter() {
        super(new _SpiceDbLexer(null));
    }
}
