package com.application.timmy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.application.timmy.model.ChangeModel;
import com.application.timmy.model.LifeEventModel;
import com.application.timmy.model.PersonModel;
import com.application.timmy.storage.PreferencesManager;
import com.application.timmy.storage.TimmyData;
import com.application.timmy.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import timmy.application.com.landoftimmy.R;

/**
 * Created by mtudora on 29/03/15.
 */
public class ChooseLifeEventActivity extends Activity {
    private Spinner spinnerPrompt;
    private Button btnSubmit;
    private PersonModel chosenPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.choose_life_event_layout);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            chosenPerson = extras.getParcelable("chosen_person");
        }

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LifeEventModel eventModel = (LifeEventModel) spinnerPrompt.getSelectedItem();
                processLifeEvent(chosenPerson, eventModel);

                Intent newIntent = new Intent(ChooseLifeEventActivity.this, LifeDisplayActivity.class);
                ChooseLifeEventActivity.this.startActivity(newIntent);
                finish();
            }

        });

        addItemsOnSpinner();
    }

    //Whenever some big life event happens to someone in our team, everybody is a little bit influenced. For this app, let's assume
    //the impact is inversely proportional with the distance between desks by a coefficient of 2. For example, if Gabi gets married and his morale grows with 5,
    //Veronica, who is 3 meters away from Gabi, gets a morale boost of 5 * 2/3 = 3.33. Florin, who is 12 meters away from Gabi, gets a morale boost of 5 * 2/12 = 0.83.

    public void processLifeEvent(PersonModel personModel, LifeEventModel eventModel){
        ArrayList<PersonModel> personsList = PreferencesManager.getInstance().getPersonsList();

        for (PersonModel person : personsList){
            int initialHumour = person.getHumour();
            int initialMorale = person.getMorale();
            int initialSkill = person.getSkill();
            ChangeModel newChange = new ChangeModel();
            int finalHumour = 0;
            int finalMorale = 0;
            int finalSkill = 0;

            if (person.getId() != personModel.getId()){
                int distance = Math.abs(person.getDeskX() - personModel.getDeskX()) + Math.abs(person.getDeskY() - personModel.getDeskY());
                finalMorale = initialMorale + eventModel.getChange().getMorale() * Constants.LIFE_EVENT_IMPACT_ON_OTHERS / distance;
                finalHumour = initialHumour + eventModel.getChange().getHumour() * Constants.LIFE_EVENT_IMPACT_ON_OTHERS / distance;
                finalSkill = initialSkill + eventModel.getChange().getSkill() * Constants.LIFE_EVENT_IMPACT_ON_OTHERS / distance;

            }else{
                finalHumour = initialHumour + eventModel.getChange().getHumour();
                finalMorale = initialMorale + eventModel.getChange().getMorale();
                finalSkill = initialSkill + eventModel.getChange().getSkill();

            }

            person.setHumour(finalHumour);
            person.setMorale(finalMorale);
            person.setSkill(finalSkill);
            newChange.setHumour(finalHumour);
            newChange.setMorale(finalMorale);
            newChange.setSkill(finalSkill);

            person.setDailyChange(newChange);
        }

        PreferencesManager.getInstance().setPersonsList(personsList);
        TimmyData.getInstance().setPersonsList(personsList);
        TimmyData.getInstance().parseData();
    }

    // add items into spinner dynamically
    public void addItemsOnSpinner() {

        spinnerPrompt = (Spinner) findViewById(R.id.spinnerLifeEvent);
        List<LifeEventModel> lifeEventModelList = PreferencesManager.getInstance().getLifeEventsList();

        ArrayAdapter<LifeEventModel> dataAdapter = new ArrayAdapter<LifeEventModel>(this,
                android.R.layout.simple_spinner_item, lifeEventModelList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrompt.setAdapter(dataAdapter);

        spinnerPrompt.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }
}
