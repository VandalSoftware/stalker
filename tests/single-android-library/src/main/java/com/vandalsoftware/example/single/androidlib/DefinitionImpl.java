package com.vandalsoftware.example.single.androidlib;

class DefinitionImpl implements Definition {

    private final String mWord;
    private final String mDescription;

    public DefinitionImpl(String word, String description) {
        mWord = word;
        mDescription = description;
    }

    @Override
    public String getWord() {
        return mWord;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }
}
