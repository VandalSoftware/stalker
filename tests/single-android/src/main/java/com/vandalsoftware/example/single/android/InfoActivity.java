package com.vandalsoftware.example.single.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class InfoActivity extends FragmentActivity {

    public static final String EXTRA_CAR_PART = InfoFragment.EXTRA_CAR_PART;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        final CarPart carPart = getIntent().getParcelableExtra(EXTRA_CAR_PART);
        setTitle(carPart.name);

        if (savedInstanceState == null) {
            final InfoFragment fragment = new InfoFragment();
            final Bundle args = new Bundle();
            args.putParcelable(InfoFragment.EXTRA_CAR_PART, carPart);
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }
}
