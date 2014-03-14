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

import android.os.Parcel;
import android.test.AndroidTestCase;

public class CarPartTest extends AndroidTestCase {

    public void testParcelable() {
        final String name = "Windshield";
        final String description = "Protection yo!";
        final CarPart carPart = new CarPart(name, description);

        final Parcel out = Parcel.obtain();
        carPart.writeToParcel(out, 0);
        // Done writing, reset parcel for reading.
        out.setDataPosition(0);

        assertEquals(carPart, CarPart.CREATOR.createFromParcel(out));
    }
}
