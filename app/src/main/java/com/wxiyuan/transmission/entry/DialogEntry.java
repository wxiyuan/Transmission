package com.wxiyuan.transmission.entry;

import android.os.Parcel;
import android.os.Parcelable;

public class DialogEntry implements Parcelable {

    private String title = null;
    private String message = null;
    private String positive = null;
    private String negative = null;

    public DialogEntry(String title, String message, String positive, String negative) {
        setTitle(title);
        setMessage(message);
        setPositive(positive);
        setNegative(negative);
    }

    public DialogEntry(Parcel in) {
        title = in.readString();
        message = in.readString();
        positive = in.readString();
        negative = in.readString();
    }

    public static final Creator<DialogEntry> CREATOR = new Creator<DialogEntry>() {
        @Override
        public DialogEntry createFromParcel(Parcel in) {
            return new DialogEntry(in);
        }

        @Override
        public DialogEntry[] newArray(int size) {
            return new DialogEntry[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPositive() {
        return positive;
    }

    public void setPositive(String positive) {
        this.positive = positive;
    }

    public String getNegative() {
        return negative;
    }

    public void setNegative(String negative) {
        this.negative = negative;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(message);
        parcel.writeString(positive);
        parcel.writeString(negative);
    }
}
