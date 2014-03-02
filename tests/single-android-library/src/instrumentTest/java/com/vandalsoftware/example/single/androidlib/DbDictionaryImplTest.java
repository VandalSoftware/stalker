package com.vandalsoftware.example.single.androidlib;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.test.AndroidTestCase;

public class DbDictionaryImplTest extends AndroidTestCase {

    private final Definition mTestDefinition = new Definition() {
        @Override
        public String getWord() {
            return "test word";
        }

        @Override
        public String getDescription() {
            return "test description";
        }
    };

    private DbDictionaryImpl mDbDictionary;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDbDictionary = new DbDictionaryImpl();
        mDbDictionary.init(getContext());
        assertRowCount(0);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDbDictionary.deleteAll();
        mDbDictionary.close();
    }

    public void testInsert() {
        assertTrue(mDbDictionary.insert(mTestDefinition) > 0);
        assertRowCount(1);
    }

    public void testInsertAllowsDuplicates() {
        assertTrue(mDbDictionary.insert(mTestDefinition) > 0);
        assertTrue(mDbDictionary.insert(mTestDefinition) > 0);
        assertRowCount(2);
    }

    public void testQueryForIdDefinitionDoesNotExist() {
        final long id = mDbDictionary.queryForId(mTestDefinition);
        assertFalse(id > 0);
    }

    public void testQueryForIdDefinitionExists() {
        final long rowId = ensureDefinitionExists(mTestDefinition);
        assertEquals(rowId, mDbDictionary.queryForId(mTestDefinition));
    }

    public void testUpdateInvalidId() {
        assertEquals(0, mDbDictionary.update(-1, mTestDefinition));
    }

    public void testUpdateValidId() {
        final long rowId = ensureDefinitionExists(mTestDefinition);
        assertEquals(1, mDbDictionary.update(rowId, mTestDefinition));
        assertTrue(mDbDictionary.queryForId(mTestDefinition) > 0);
    }

    public void testSaveDefinitionDoesNotExist() {
        assertTrue(mDbDictionary.save(mTestDefinition));
        assertTrue(mDbDictionary.queryForId(mTestDefinition) > 0);
        assertRowCount(1);
    }

    public void testSaveDefinitionExists() {
        final long rowId = ensureDefinitionExists(mTestDefinition);
        final Definition updatedDefinition = new Definition() {
            @Override
            public String getWord() {
                return mTestDefinition.getWord();
            }

            @Override
            public String getDescription() {
                return "updated description";
            }
        };
        assertTrue(mDbDictionary.save(updatedDefinition));
        assertEquals(rowId, mDbDictionary.queryForId(updatedDefinition));
        assertRowCount(1);
    }

    public void testDeleteDefinitionExists() {
        ensureDefinitionExists(mTestDefinition);
        assertTrue(mDbDictionary.delete(mTestDefinition));
        assertFalse(mDbDictionary.queryForId(mTestDefinition) > 0);
        assertRowCount(0);
    }

    public void testDeleteDefinitionDoesNotExist() {
        assertFalse(mDbDictionary.delete(mTestDefinition));
    }

    public long ensureDefinitionExists(Definition d) {
        final long rowId = mDbDictionary.insert(d);
        assertTrue(rowId > 0);
        return rowId;
    }

    public void assertRowCount(int rowCount) {
        final SQLiteDatabase db = mDbDictionary.getDbHelper().getReadableDatabase();
        final Cursor c = db.query(mDbDictionary.getTable(), new String[] {
                BaseColumns._ID
        }, null, null, null, null, null, null);
        assertNotNull(c);
        assertEquals(rowCount, c.getCount());
        c.close();
    }
}
