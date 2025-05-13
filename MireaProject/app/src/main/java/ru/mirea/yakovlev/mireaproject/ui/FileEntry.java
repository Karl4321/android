package ru.mirea.yakovlev.mireaproject.ui;

import java.io.File;

public class FileEntry {
    private final String displayName;
    private final File fileReference;

    public FileEntry(String displayName, File fileReference) {
        this.displayName = displayName;
        this.fileReference = fileReference;
    }

    public String getDisplayName() {
        return displayName;
    }

    public File getFileReference() {
        return fileReference;
    }
}
