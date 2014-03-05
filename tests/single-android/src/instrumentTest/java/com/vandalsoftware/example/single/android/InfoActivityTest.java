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

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.test.ActivityInstrumentationTestCase2;

public class InfoActivityTest extends ActivityInstrumentationTestCase2<InfoActivity> {

    public InfoActivityTest() {
        super("com.vandalsoftware.example.single.android", InfoActivity.class);
    }

    public void testOnCreateSetsTitleToCarPartName() {
        final CarPart carPart = new CarPart("info", "info description");
        final Intent intent = new Intent();
        intent.putExtra(InfoActivity.EXTRA_CAR_PART, carPart);
        setActivityIntent(intent);

        final FragmentActivity fragmentActivity = getActivity();
        assertEquals(carPart.name, fragmentActivity.getTitle());
    }
}
