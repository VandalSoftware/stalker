/*
 * Copyright (C) 2014 Vandal LLC
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
