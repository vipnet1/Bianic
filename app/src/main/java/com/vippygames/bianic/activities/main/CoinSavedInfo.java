package com.vippygames.bianic.activities.main;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CoinSavedInfo implements Parcelable {
    private final String symbolEdtData;
    private final String allocationEdtData;
    private final int isEdited;

    // empty unless edited record
    private final String symbolCancelEdtData;

    // empty unless edited record
    private final String allocationCancelEdtData;

    public String getSymbolEdtData() {
        return symbolEdtData;
    }

    public String getAllocationEdtData() {
        return allocationEdtData;
    }

    public int getIsEdited() {
        return isEdited;
    }

    public String getSymbolCancelEdtData() {
        return symbolCancelEdtData;
    }

    public String getAllocationCancelEdtData() {
        return allocationCancelEdtData;
    }

    protected CoinSavedInfo(Parcel in) {
        this.symbolEdtData = in.readString();
        this.allocationEdtData = in.readString();
        this.isEdited = in.readInt();
        this.symbolCancelEdtData = in.readString();
        this.allocationCancelEdtData = in.readString();
    }

    public CoinSavedInfo(String symbolEdtData, String allocationEdtData, int isEdited,
                         String symbolCancelEdtData, String allocationCancelEdtData) {
        this.symbolEdtData = symbolEdtData;
        this.allocationEdtData = allocationEdtData;
        this.isEdited = isEdited;
        this.symbolCancelEdtData = symbolCancelEdtData;
        this.allocationCancelEdtData = allocationCancelEdtData;
    }

    public static final Creator<CoinSavedInfo> CREATOR = new Creator<CoinSavedInfo>() {
        @Override
        public CoinSavedInfo createFromParcel(Parcel in) {
            return new CoinSavedInfo(in);
        }

        @Override
        public CoinSavedInfo[] newArray(int size) {
            return new CoinSavedInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(symbolEdtData);
        parcel.writeString(allocationEdtData);
        parcel.writeInt(isEdited);
        parcel.writeString(symbolCancelEdtData);
        parcel.writeString(allocationCancelEdtData);
    }
}
