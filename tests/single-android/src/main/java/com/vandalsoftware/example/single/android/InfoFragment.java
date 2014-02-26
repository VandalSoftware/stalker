package com.vandalsoftware.example.single.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InfoFragment extends Fragment {

    public static final String EXTRA_CAR_PART = "car_part";

    private CarPart mCarPart;
    private TextView mNameTextView;
    private TextView mDescTextView;

    public InfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_info, container, false);
        mNameTextView = (TextView) rootView.findViewById(R.id.name);
        mDescTextView = (TextView) rootView.findViewById(R.id.description);

        setCarPart((CarPart) getArguments().getParcelable(EXTRA_CAR_PART));
        return rootView;
    }

    public void setCarPart(CarPart carPart) {
        mCarPart = carPart;
        if (mNameTextView != null) {
            mNameTextView.setText(carPart.name);
            mDescTextView.setText(carPart.description);
        }
    }
}
