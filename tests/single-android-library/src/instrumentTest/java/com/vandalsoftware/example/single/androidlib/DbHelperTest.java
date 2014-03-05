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
