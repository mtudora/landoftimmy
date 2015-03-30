package com.application.timmy.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mtudora on 25/03/15.
 */
public class ChangeModel implements Parcelable, Serializable {
    private int morale;
    private int humour;
    private int skill;

    public ChangeModel() {

    }


    public ChangeModel(JSONObject objectJSON) throws JSONException {
        if (objectJSON != null) {
            this.morale = objectJSON.optInt("morale");
            this.humour = objectJSON.optInt("humour");
            this.skill = objectJSON.optInt("skill");
        } else {
            throw new JSONException("null json object");
        }
    }

    public ChangeModel(Parcel in) {
        readFromParcel(in);
    }


    public int getMorale() {
        return morale;
    }

    public void setMorale(int morale) {
        this.morale = morale;
    }

    public int getHumour() {
        return humour;
    }

    public void setHumour(int humour) {
        this.humour = humour;
    }

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(morale);
        dest.writeInt(humour);
        dest.writeInt(skill);
    }

    private void readFromParcel(Parcel in) {
        this.morale = in.readInt();
        this.humour = in.readInt();
        this.skill = in.readInt();
    }

    public static final Creator<ChangeModel> CREATOR = new Creator<ChangeModel>() {
        public ChangeModel createFromParcel(Parcel in) {
            return new ChangeModel(in);
        }

        public ChangeModel[] newArray(int size) {
            return new ChangeModel[size];
        }
    };

}
