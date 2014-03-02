package com.vandalsoftware.example.single.androidlib;

public class DbDictionaryFactory implements DictionaryFactory {

    @Override
    public Dictionary newDictionary() {
        return new DbDictionaryImpl();
    }
}
