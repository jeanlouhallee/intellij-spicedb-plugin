package com.authzed.intellij.spicedb;

import com.authzed.intellij.spicedb.lexer.SpiceDbLexerAdapter;
import com.authzed.intellij.spicedb.parser.SpiceDbParser;
import com.authzed.intellij.spicedb.psi.SpiceDbFile;
import com.authzed.intellij.spicedb.psi.SpiceDbTokenSets;
import com.authzed.intellij.spicedb.psi.SpiceDbTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * Parser definition for SpiceDB schema files, backed by the Grammar-Kit
 * generated parser and JFlex lexer.
 */
public class SpiceDbParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(SpiceDbLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new SpiceDbLexerAdapter();
    }

    @NotNull
    @Override
    public PsiParser createParser(Project project) {
        return new SpiceDbParser();
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return SpiceDbTokenSets.COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return SpiceDbTokenSets.STRINGS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return SpiceDbTypes.Factory.createElement(node);
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new SpiceDbFile(viewProvider);
    }
}
