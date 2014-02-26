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
