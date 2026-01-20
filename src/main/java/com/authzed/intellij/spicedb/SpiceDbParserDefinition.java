package com.authzed.intellij.spicedb;

import com.authzed.intellij.spicedb.psi.SpiceDbFile;
import com.authzed.intellij.spicedb.psi.SpiceDbTokenSets;
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
 * Parser definition for SpiceDB schema files.
 * Uses a minimal implementation since we only need lexer-based syntax highlighting.
 */
public class SpiceDbParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(SpiceDbLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new SpiceDbLexer();
    }

    @NotNull
    @Override
    public PsiParser createParser(Project project) {
        // Minimal parser - we only need lexer-based highlighting
        return (root, builder) -> {
            var marker = builder.mark();
            while (!builder.eof()) {
                builder.advanceLexer();
            }
            marker.done(root);
            return builder.getTreeBuilt();
        };
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
        throw new UnsupportedOperationException("Not implemented for lexer-only plugin");
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new SpiceDbFile(viewProvider);
    }
}
