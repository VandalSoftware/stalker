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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainFragment extends Fragment implements ListView.OnItemClickListener {

    public MainFragment() {
    }

    private CarPart[] mCarParts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        final ArrayAdapter<CarPart> adapter = new ArrayAdapter<CarPart>(getActivity(),
                android.R.layout.simple_list_item_1, getCarParts()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final TextView textView;
                if (convertView == null) {
                    textView = (TextView) LayoutInflater.from(getActivity()).inflate(
                            android.R.layout.simple_list_item_1, parent, false);
                } else {
                    textView = (TextView) convertView;
                }
                textView.setText(getItem(position).name);
                return textView;
            }
        };
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getActivity() != null) {
            final CarPart carPart = (CarPart) parent.getItemAtPosition(position);
            final Intent intent = new Intent(getActivity(), InfoActivity.class);
            intent.putExtra(InfoActivity.EXTRA_CAR_PART, carPart);
            startActivity(intent);
        }
    }

    CarPart[] getCarParts() {
        if (mCarParts == null) {
            final CarPart[] carParts = new CarPart[]{
                    new CarPart("Bumper", "Bumper description"),
                    new CarPart("Body", "Body description"),
                    new CarPart("Exhaust", "Exhaust description"),
                    new CarPart("Hood", "Hood description"),
                    new CarPart("Muffler", "Muffler description")
            };
            mCarParts = carParts;
        }
        return mCarParts;
    }
}
