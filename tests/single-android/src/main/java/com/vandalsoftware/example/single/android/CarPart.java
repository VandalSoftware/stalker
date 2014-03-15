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

import android.os.Parcel;
import android.os.Parcelable;

public class CarPart implements Parcelable {

    public static final Parcelable.Creator<CarPart> CREATOR
            = new Parcelable.Creator<CarPart>() {
        public CarPart createFromParcel(Parcel in) {
            return new CarPart(in);
        }

        public CarPart[] newArray(int size) {
            return new CarPart[size];
        }
    };

    public final String name;
    public final String description;

    public CarPart(String name, String description) {
        this.name = name;
        this.description = description;
    }

    private CarPart(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
        out.writeString(this.description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarPart carPart = (CarPart) o;

        if (description != null ? !description.equals(carPart.description) : carPart.description != null)
            return false;
        if (name != null ? !name.equals(carPart.name) : carPart.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
