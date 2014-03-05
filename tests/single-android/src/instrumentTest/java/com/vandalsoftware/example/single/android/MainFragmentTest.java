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

package com.vandalsoftware.example.single.android;

import android.support.v4.app.FragmentActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

public class MainFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainFragmentTest() {
        super("com.vandalsoftware.example.single.android", MainActivity.class);
    }

    public void testGetCarParts() {
        final MainFragment fragment = new MainFragment();
        final CarPart[] carParts = fragment.getCarParts();
        assertNotNull(carParts);
    }

    public void testGetCarPartsReturnsSameCarParts() {
        final MainFragment fragment = new MainFragment();
        final CarPart[] carParts = fragment.getCarParts();
        assertEquals(carParts, fragment.getCarParts());
    }

    public void testListView() {
        final FragmentActivity activity = getActivity();
        final ListView listView = (ListView) activity.findViewById(android.R.id.list);

        final MainFragment fragment = (MainFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.container);
        final CarPart[] carParts = fragment.getCarParts();
        assertEquals(carParts.length, listView.getCount());

        int i = 0;
        for (CarPart carPart : carParts) {
            assertEquals(carPart, listView.getItemAtPosition(i));
            i++;
        }
    }
}