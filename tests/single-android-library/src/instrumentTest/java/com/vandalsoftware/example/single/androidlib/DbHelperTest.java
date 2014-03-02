package com.vandalsoftware.example.single.androidlib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

public class DbHelperTest extends AndroidTestCase {

    private Context mDbContext;
    private DbHelper mDbHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDbContext = new RenamingDelegatingContext(getContext(), "test_");
        mDbHelper = new DbHelper(mDbContext);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDbHelper.close();
        mDbContext.deleteDatabase("test_dictionary.db");
    }

    public void testOnUpgrade() {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int oldVersion = db.getVersion();
        final int newVersion = oldVersion + 1;
        mDbHelper.onUpgrade(db, oldVersion, newVersion);
    }
}
