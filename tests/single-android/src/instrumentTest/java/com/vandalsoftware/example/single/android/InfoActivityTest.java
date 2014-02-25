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
