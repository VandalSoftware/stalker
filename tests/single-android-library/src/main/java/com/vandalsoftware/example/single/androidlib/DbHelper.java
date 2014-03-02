package com.vandalsoftware.example.single.androidlib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DbHelper extends SQLiteOpenHelper {

    public static final String TABLE_DEFINITION = "defintion";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_DESC = "description";

    private static final String TAG = "DbHelper";
    private static final String DATABASE_NAME = "dictionary.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DEFINITION_CREATE_TABLE = "CREATE TABLE " + TABLE_DEFINITION + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_WORD + " TEXT NOT NULL, " +
            COLUMN_DESC + " TEXT NOT NULL);";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DEFINITION_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, "Upgrading db from " + oldVersion + " to " + newVersion);
        }
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFINITION);
        onCreate(db);
    }
}
