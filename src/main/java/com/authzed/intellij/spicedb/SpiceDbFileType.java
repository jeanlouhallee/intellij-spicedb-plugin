package com.authzed.intellij.spicedb;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * File type for SpiceDB schema (.zed) files.
 */
public class SpiceDbFileType extends LanguageFileType {

    public static final SpiceDbFileType INSTANCE = new SpiceDbFileType();

    private SpiceDbFileType() {
        super(SpiceDbLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "SpiceDB Schema";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "SpiceDB schema file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "zed";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SpiceDbIcons.FILE;
    }
}
