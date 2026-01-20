package com.authzed.intellij.spicedb;

import com.intellij.lang.Language;

/**
 * Language definition for SpiceDB schema files.
 */
public class SpiceDbLanguage extends Language {

    public static final SpiceDbLanguage INSTANCE = new SpiceDbLanguage();

    private SpiceDbLanguage() {
        super("SpiceDB");
    }
}
