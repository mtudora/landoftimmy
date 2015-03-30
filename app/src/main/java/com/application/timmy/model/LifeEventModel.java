package com.application.timmy.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mtudora on 25/03/15.
 */
public class LifeEventModel implements Parcelable, Serializable {
    private String label;
    private ChangeModel change;

    public LifeEventModel(JSONObject objectJSON) throws JSONException {
        if (objectJSON != null) {
            this.label = objectJSON.optString("label");
            this.change = new ChangeModel(objectJSON.optJSONObject("changes"));
        } else {
            throw new JSONException("null json object");
        }
    }

    public LifeEventModel(Parcel in) {
        readFromParcel(in);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ChangeModel getChange() {
        return change;
    }

    public void setChange(ChangeModel change) {
        this.change = change;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeParcelable(change, flags);
    }

    private void readFromParcel(Parcel in) {
        this.label = in.readString();
        this.change = in.readParcelable(ChangeModel.class.getClassLoader());
    }

    public static final Creator<LifeEventModel> CREATOR = new Creator<LifeEventModel>() {
        public LifeEventModel createFromParcel(Parcel in) {
            return new LifeEventModel(in);
        }

        public LifeEventModel[] newArray(int size) {
            return new LifeEventModel[size];
        }
    };

    @Override
    public String toString() {
        return this.label;
    }
}
