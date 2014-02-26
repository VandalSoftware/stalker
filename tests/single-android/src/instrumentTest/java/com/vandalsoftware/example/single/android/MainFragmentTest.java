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