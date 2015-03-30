package com.application.timmy.storage;

import com.application.timmy.model.ChangeModel;
import com.application.timmy.model.LifeEventModel;
import com.application.timmy.model.PersonModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mtudora on 25/03/15.
 */
public class TimmyData {
    private static TimmyData instance = null;

    private PreferencesManager mPreferencesManager;

    private ArrayList<PersonModel> personsList;
    private ArrayList<LifeEventModel> lifeEventsList;
    private HashMap<String, ChangeModel> departmentMap;

    private TimmyData(){
        mPreferencesManager = PreferencesManager.getInstance();
        loadFromPreferences();
    }

    public static synchronized TimmyData getInstance(){
        if (instance == null){
            instance = new TimmyData();
        }

        return instance;
    }

    private void loadFromPreferences() {
        personsList = mPreferencesManager.getPersonsList();
        lifeEventsList = mPreferencesManager.getLifeEventsList();

    }

    public void parseData(){
        if (personsList != null) {
            departmentMap = new HashMap<>();
            for (PersonModel person : personsList) {
                if (!departmentMap.containsKey(person.getDepartment())) {
                    ChangeModel change = new ChangeModel();
                    change.setSkill(person.getSkill());
                    change.setHumour(person.getHumour());
                    change.setMorale(person.getMorale());
                    departmentMap.put(person.getDepartment(), change);
                } else {
                    ChangeModel change = departmentMap.get(person.getDepartment());
                    change.setSkill(change.getSkill() + person.getSkill());
                    change.setHumour(change.getHumour() + person.getHumour());
                    change.setMorale(change.getMorale() + person.getMorale());
                    departmentMap.put(person.getDepartment(), change);
                }
            }
        }
    }

    public synchronized void clearData() {
        mPreferencesManager.flushAllData();
        resetAttributes();
    }

    private void resetAttributes() {
        personsList = null;
        lifeEventsList = null;
    }

    public ArrayList<PersonModel> getPersonsList() {
        return personsList;
    }

    public void setPersonsList(ArrayList<PersonModel> personsList) {
        this.personsList = personsList;
    }

    public ArrayList<LifeEventModel> getLifeEventsList() {
        return lifeEventsList;
    }

    public void setLifeEventsList(ArrayList<LifeEventModel> lifeEventsList) {
        this.lifeEventsList = lifeEventsList;
    }

    public HashMap<String, ChangeModel> getDepartmentMap() {
        return departmentMap;
    }

    public void setDepartmentMap(HashMap<String, ChangeModel> departmentMap) {
        this.departmentMap = departmentMap;
    }
}
