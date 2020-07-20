package com.smsapp.helpme;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gamelooper on 3/30/2018.
 */

public class Details implements Parcelable {


    private String phonenumber;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phonenumber);
        dest.writeString(password);
        dest.writeString(description);
    }


    public static final Creator<Details> CREATOR = new Creator<Details>() {
        @Override
        public Details createFromParcel(Parcel in) {
            return new Details();
        }

        @Override
        public Details[] newArray(int size) {
            return new Details[size];
        }
    };

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @Override
    public String toString() {
        return "Details{" +
                "phonenumber='" + phonenumber + '\'' +
                ", password='" + password + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
