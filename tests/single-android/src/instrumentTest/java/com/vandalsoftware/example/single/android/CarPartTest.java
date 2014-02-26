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
