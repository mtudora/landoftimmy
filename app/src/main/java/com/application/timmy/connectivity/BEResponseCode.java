package com.application.timmy.connectivity;

import com.application.timmy.TimmyApplication;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import timmy.application.com.landoftimmy.R;

public enum BEResponseCode implements Parcelable {

    OK(200), CREATED_NOT_CONFIRMED(206), NO_CONTENT(204), BAD_REQUEST(400), UNAUTHORIZED(401), FORBIDDEN(403), NOT_FOUND(404), UPDATE_APP(410), INTERNAL_SERVER_ERROR(500), SERVICE_NOT_AVAILABLE(
            503), INVALID_TOKEN(1), NO_RESPONSE(2), UNKNOWN_ERROR(999), PARSE_RESPONSE_ERROR(998);

    private int code;

    /**
     * Constructor
     *
     * @param code
     *            the int code
     */
    BEResponseCode(int code) {
        this.code = code;
    }

    /**
     * @return the numeric code
     */
    public int getCode() {
        return this.code;
    }

    /**
     * returns a BEResponseCode object that has the specified code
     *
     * @param code
     *            - the numeric code
     * @return BEResponseCode object with the specified code
     */
    public static BEResponseCode findByCode(int code) {
        for (BEResponseCode responseCode : BEResponseCode.values()) {
            if (code == responseCode.getCode()) {
                return responseCode;
            }
        }
        return UNKNOWN_ERROR;
    }

    /**
     * @return true if the code represents a success response, false otherwise
     */
    public boolean isCodeSuccessful() {
        if (this.getCode() >= 200 && this.getCode() < 300) {
            return true;
        }
        return false;
    }

    /**
     * @param responseCode
     *            - BEReponseCode object
     * @return true if the code represents a success response, false otherwise
     */
    public static boolean isCodeSuccessful(BEResponseCode responseCode) {
        if (responseCode.getCode() >= 200 && responseCode.getCode() < 300) {
            return true;
        }
        return false;
    }

    /**
     * @return - error's message id in strings.xml
     */
    public int getAppropriateErrorMessageId() {
        return BEResponseCode.getAppropriateErrorMessageId(this);
    }

    /**
     * @return - a string message to be displayed for the provided code
     */
    public String getAppropriateErrorMessage() {
        Resources res =  TimmyApplication.getInstance().getApplicationContext().getResources();
        return res.getString(this.getAppropriateErrorMessageId());
    }

    /**
     * @param code
     *            - the code
     * @return - a string message to be displayed for the provided code
     */
    public static int getAppropriateErrorMessageId(BEResponseCode code) {
        return 0;
    }

    @Override
    public int describeContents() {
        return BEResponseCode.class.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
    }

    private void readFromParcel(Parcel in) {
        this.code = in.readInt();
    }

    public static final Creator<BEResponseCode> CREATOR = new Creator<BEResponseCode>() {
        public BEResponseCode createFromParcel(Parcel in) {
            for (BEResponseCode code : BEResponseCode.values()) {
                if (in.readInt() == code.getCode()) {
                    return code;
                }
            }

            return BEResponseCode.UNKNOWN_ERROR;
        }

        public BEResponseCode[] newArray(int size) {
            return new BEResponseCode[size];
        }
    };

}
