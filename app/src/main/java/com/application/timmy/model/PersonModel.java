package com.application.timmy.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mtudora on 25/03/15.
 */
public class PersonModel implements Parcelable, Serializable {
    private String name;
    private String photoUrl;
    private String department;
    private int deskX;
    private int deskY;
    private int morale;
    private int humour;
    private int skill;
    private ChangeModel dailyChange;
    private ChangeModel lifeState;
    private String photoLocalPath;
    private transient Bitmap photoBitmap;
    private int id;

    public PersonModel() {

    }

    public PersonModel(JSONObject objectJSON) throws JSONException {
        if (objectJSON != null) {
            this.name = objectJSON.optString("name");
            this.photoUrl = objectJSON.optString("photo");
            this.department = objectJSON.optString("department");
            this.deskX = objectJSON.optInt("desk_x");
            this.deskY = objectJSON.optInt("desk_y");
            this.morale = objectJSON.optInt("morale");
            this.humour = objectJSON.optInt("humour");
            this.skill = objectJSON.optInt("skill");
            this.dailyChange = new ChangeModel(objectJSON.optJSONObject("daily_change"));
            this.lifeState = new ChangeModel();
            this.lifeState.setHumour(humour);
            this.lifeState.setMorale(morale);
            this.lifeState.setSkill(skill);
        } else {
            throw new JSONException("null json object");
        }
    }

    public PersonModel(Parcel in) {
        readFromParcel(in);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getDeskX() {
        return deskX;
    }

    public void setDeskX(int deskX) {
        this.deskX = deskX;
    }

    public int getDeskY() {
        return deskY;
    }

    public void setDeskY(int deskY) {
        this.deskY = deskY;
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

    public ChangeModel getDailyChange() {
        return dailyChange;
    }

    public void setDailyChange(ChangeModel dailyChange) {
        this.dailyChange = dailyChange;
    }

    public String getPhotoLocalPath() {
        return photoLocalPath;
    }

    public void setPhotoLocalPath(String photoLocalPath) {
        this.photoLocalPath = photoLocalPath;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChangeModel getLifeState() {
        return lifeState;
    }

    public void setLifeState(ChangeModel lifeState) {
        this.lifeState = lifeState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(photoUrl);
        dest.writeString(department);
        dest.writeInt(deskX);
        dest.writeInt(deskY);
        dest.writeInt(humour);
        dest.writeInt(morale);
        dest.writeInt(skill);
        dest.writeParcelable(dailyChange, flags);
        dest.writeParcelable(lifeState, flags);
        dest.writeInt(id);
    }

    private void readFromParcel(Parcel in) {
        // Please preserve the order
        this.name = in.readString();
        this.photoUrl = in.readString();
        this.department = in.readString();
        this.deskX = in.readInt();
        this.deskY = in.readInt();
        this.humour = in.readInt();
        this.morale = in.readInt();
        this.skill = in.readInt();
        this.dailyChange = in.readParcelable(ChangeModel.class.getClassLoader());
        this.lifeState = in.readParcelable(ChangeModel.class.getClassLoader());
        this.id = in.readInt();

    }

    public static final Parcelable.Creator<PersonModel> CREATOR = new Creator<PersonModel>() {
        public PersonModel createFromParcel(Parcel in) {
            return new PersonModel(in);
        }

        public PersonModel[] newArray(int size) {
            return new PersonModel[size];
        }
    };
}
