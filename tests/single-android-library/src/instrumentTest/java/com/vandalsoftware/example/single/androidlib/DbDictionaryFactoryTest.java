package com.vandalsoftware.example.single.androidlib;

import android.test.AndroidTestCase;

public class DbDictionaryFactoryTest extends AndroidTestCase {

    public void testNewDictionary() {
        final Dictionary dictionary = new DbDictionaryFactory().newDictionary();
        assertTrue(dictionary instanceof DbDictionaryImpl);
    }
}
