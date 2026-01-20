package com.authzed.intellij.spicedb.psi;

import com.authzed.intellij.spicedb.SpiceDbFileType;
import com.authzed.intellij.spicedb.SpiceDbLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * PSI file wrapper for SpiceDB schema files.
 */
public class SpiceDbFile extends PsiFileBase {

    public SpiceDbFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, SpiceDbLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SpiceDbFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "SpiceDB Schema File";
    }
}
