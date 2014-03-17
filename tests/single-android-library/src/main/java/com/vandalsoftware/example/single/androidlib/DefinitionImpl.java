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

package com.vandalsoftware.example.single.androidlib;

class DefinitionImpl implements Definition {

    private final String mWord;
    private final String mDescription;

    public DefinitionImpl(String word, String description) {
        mWord = word;
        mDescription = description;
    }

    @Override
    public String getWord() {
        return mWord;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }
}
