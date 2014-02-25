package com.vandalsoftware.example.single.android;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class InfoFragmentTest extends ActivityInstrumentationTestCase2<InfoActivity> {

    public InfoFragmentTest() {
        super("com.vandalsoftware.example.single.android", InfoActivity.class);
    }

    public void testSetCarPart() {
        final CarPart carPart = new CarPart("info", "info description");
        final Intent intent = new Intent();
        intent.putExtra(InfoActivity.EXTRA_CAR_PART, carPart);
        setActivityIntent(intent);

        final FragmentActivity fragmentActivity = getActivity();
        final TextView mNameView = (TextView) fragmentActivity.findViewById(R.id.name);
        final TextView mDescView = (TextView) fragmentActivity.findViewById(R.id.description);
        assertTrue(carPart.name.equals(mNameView.getText()));
        assertTrue(carPart.description.equals(mDescView.getText()));
    }
}
