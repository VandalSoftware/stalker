/*
 * Copyright (C) 2013 Vandal LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
