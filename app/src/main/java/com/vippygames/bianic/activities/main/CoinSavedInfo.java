package com.vippygames.bianic.activities.main;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CoinSavedInfo implements Parcelable {
    public enum EDIT_STATUS {
        NOT_EDITED, // not edited

        ADDED_RECORD, // edited, just added(no cancel button)

        EDITED_RECORD; // edited(already applied in the past)

        public int getValue() {
            switch (this) {
                case ADDED_RECORD:
                    return 1;
                case EDITED_RECORD:
                    return 2;
                default:
                    return 0;
            }
        }

        public static EDIT_STATUS valueToStatus(int value) {
            switch (value) {
                case 1:
                    return ADDED_RECORD;
                case 2:
                    return EDITED_RECORD;
                default:
                    return NOT_EDITED;
            }
        }
    }

    private final String symbolEdtData;
    private final String allocationEdtData;
    private final EDIT_STATUS editStatus;

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

    public EDIT_STATUS getEditStatus() {
        return editStatus;
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
        this.editStatus = EDIT_STATUS.valueToStatus(in.readInt());
        this.symbolCancelEdtData = in.readString();
        this.allocationCancelEdtData = in.readString();
    }

    public CoinSavedInfo(String symbolEdtData, String allocationEdtData, EDIT_STATUS editStatus,
                         String symbolCancelEdtData, String allocationCancelEdtData) {
        this.symbolEdtData = symbolEdtData;
        this.allocationEdtData = allocationEdtData;
        this.editStatus = editStatus;
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
        parcel.writeInt(editStatus.getValue());
        parcel.writeString(symbolCancelEdtData);
        parcel.writeString(allocationCancelEdtData);
    }
}
