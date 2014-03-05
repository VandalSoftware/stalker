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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class DbDictionaryImpl implements Dictionary {

    private static final String[] PROJECTION = {
            DbHelper.COLUMN_ID,
            DbHelper.COLUMN_WORD,
            DbHelper.COLUMN_DESC
    };
    private static final int INDEX_ID = 0;
    private static final int INDEX_WORD = 1;
    private static final int INDEX_DESC = 2;

    private static final long INVALID_ID = -1;

    private static final String SELECTION_WORD = DbHelper.COLUMN_WORD + "=?";
    private static final String SELECTION_ID = DbHelper.COLUMN_ID + "=?";

    private DbHelper mDbHelper;

    @Override
    public void init(Context context) {
        mDbHelper = new DbHelper(context);
    }

    @Override
    public boolean save(Definition d) {
        final long id = queryForId(d);
        return insertOrUpdate(id, d);
    }

    long queryForId(Definition d) {
        final Cursor cursor = query(d);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getLong(INDEX_ID);
                }
            } finally {
                cursor.close();
            }
        }
        return INVALID_ID;
    }

    private Cursor query(Definition d) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.query(getTable(), PROJECTION, SELECTION_WORD, new String[]{d.getWord()}, null,
                null, null);
    }

    private boolean insertOrUpdate(long id, Definition d) {
        if (isValidId(id)) {
            return update(id, d) > 0;
        } else {
            return insert(d) != -1;
        }
    }

    boolean isValidId(long id) {
        return id != INVALID_ID;
    }

    long insert(Definition d) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.insert(getTable(), null, toContentValues(d));
    }

    int update(long id, Definition d) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.update(getTable(), toContentValues(d), SELECTION_ID,
                new String[]{String.valueOf(id)});
    }

    ContentValues toContentValues(Definition d) {
        final ContentValues values = new ContentValues(2);
        values.put(DbHelper.COLUMN_WORD, d.getWord());
        values.put(DbHelper.COLUMN_DESC, d.getDescription());
        return values;
    }

    @Override
    public boolean delete(Definition d) {
        final long id = queryForId(d);
        if (isValidId(id)) {
            final SQLiteDatabase db = mDbHelper.getWritableDatabase();
            final int rows = db.delete(getTable(), SELECTION_ID, new String[] {String.valueOf(id)});
            return rows > 0;
        }
        return false;
    }

    @Override
    public boolean deleteAll() {
        final int rows = mDbHelper.getWritableDatabase().delete(getTable(), null, null);
        return rows > 0;
    }

    @Override
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    DbHelper getDbHelper() {
        return mDbHelper;
    }

    String getTable() {
        return DbHelper.TABLE_DEFINITION;
    }
}
