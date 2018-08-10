package com.inkredibles.wema20.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;

import java.io.File;

@ParseClassName("NewFile")
public class NewFile extends ParseFile {

    public NewFile(File file) {
        super(file);
    }

}
