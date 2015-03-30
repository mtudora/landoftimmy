package com.application.timmy.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

import com.application.timmy.TimmyApplication;
import com.application.timmy.model.LifeEventModel;
import com.application.timmy.model.PersonModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

// TODO: encrypt stored data
public class PreferencesManager {

    private final static String SHARED_PREFS_NAME = "com.application.timmy.prefs";


    private final static String PERSONS = "persons";
    private final static String LIFE_EVENTS = "life_events";

    private static PreferencesManager instance = null;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferencesEditor;

    private PreferencesManager() {
        if (TimmyApplication.getInstance() != null && TimmyApplication.getInstance().getApplicationContext() != null) {
            sharedPreferences = TimmyApplication.getInstance().getApplicationContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            preferencesEditor = sharedPreferences.edit();
        }
    }


    public ArrayList<PersonModel> getPersonsList() {
        String serializedObject = sharedPreferences.getString(PERSONS, null);
        ArrayList<PersonModel> result = null;
        if (serializedObject != null) {
            Object deserializedObject = PreferencesManager.stringToObject(serializedObject);
            if (deserializedObject instanceof ArrayList<?>) {
                result = (ArrayList<PersonModel>) deserializedObject;
            }
        }
        return result;
    }

    public void setPersonsList(ArrayList<PersonModel> eventsList) {
        preferencesEditor.putString(PERSONS, objectToString(eventsList));
        preferencesEditor.commit();
    }

    public ArrayList<LifeEventModel> getLifeEventsList() {
        String serializedObject = sharedPreferences.getString(LIFE_EVENTS, null);
        ArrayList<LifeEventModel> result = null;
        if (serializedObject != null) {
            Object deserializedObject = PreferencesManager.stringToObject(serializedObject);
            if (deserializedObject instanceof ArrayList<?>) {
                result = (ArrayList<LifeEventModel>) deserializedObject;
            }
        }
        return result;
    }

    public void setLifeEventsList(ArrayList<LifeEventModel> eventsList) {
        preferencesEditor.putString(LIFE_EVENTS, objectToString(eventsList));
        preferencesEditor.commit();
    }

    /**
     * returns the instance of the PreferencesManager
     * 
     * @param
     *            - the context
     * @return - the instance of the PreferencesManager singleton
     */
    public static synchronized PreferencesManager getInstance() {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }

    private static String objectToString(Serializable object) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(out).writeObject(object);
            byte[] data = out.toByteArray();
            out.close();

            out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out, 0);
            b64.write(data);
            b64.close();
            out.close();

            return new String(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object stringToObject(String encodedObject) {
        try {
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(encodedObject.getBytes());
            Base64InputStream base64Stream = new Base64InputStream(arrayInputStream, 0);
            ObjectInputStream objectInputStream = new ObjectInputStream(base64Stream);
            return objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void flushAllData() {
        preferencesEditor.clear();
        preferencesEditor.commit();
    }


}
