package com.application.timmy.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;


import com.application.timmy.connectivity.BEResponseCode;
import com.application.timmy.model.ChangeModel;
import com.application.timmy.model.PersonModel;
import com.application.timmy.storage.TimmyData;
import com.application.timmy.utils.Utils;

import timmy.application.com.landoftimmy.R;

public class LifeDisplayActivity extends OfficeBaseActivity {

    ExpandableListAdapter listAdapterDep, listAdapterPersons;
    ExpandableListView listViewDep, listViewPersons;
    List<String> listDataHeaderPersons, listDataHeaderDep;
    HashMap<String, ChangeModel> listDataChildDep, listDataChildPersons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.life_events_layout);

        // get the listview
        listViewDep = (ExpandableListView) findViewById(R.id.listViewDep);
        listViewPersons = (ExpandableListView) findViewById(R.id.listViewPersons);

        // preparing list data
        prepareListDataPersons();
        prepareListDataDepartment();

        listAdapterPersons = new ExpandableListAdapter(this, listDataHeaderPersons, listDataChildPersons);
        listViewPersons.setAdapter(listAdapterPersons);

        listAdapterDep = new ExpandableListAdapter(this, listDataHeaderDep, listDataChildDep);
        listViewDep.setAdapter(listAdapterDep);
    }

    /*
     * Preparing the list data
     */
    private void prepareListDataPersons() {
        listDataHeaderPersons = new ArrayList<String>();
        listDataChildPersons = new HashMap<String, ChangeModel>();

        ArrayList<PersonModel> personsList = TimmyData.getInstance().getPersonsList();

        if (personsList == null)
            return;

        int counter = 0;
        for (PersonModel person : personsList) {
            // Adding child data
            listDataHeaderPersons.add(person.getName());

            // Adding child data
            ChangeModel lifeState = person.getLifeState();
            listDataChildPersons.put(listDataHeaderPersons.get(counter), lifeState); // Header, Child data
            counter++;
        }
    }

    private void prepareListDataDepartment() {
        listDataHeaderDep = new ArrayList<String>();
        listDataChildDep = new HashMap<String, ChangeModel>();

        HashMap<String, ChangeModel> depMap = TimmyData.getInstance().getDepartmentMap();

        if (depMap == null)
            return;

        int counter = 0;

        for (String dep : depMap.keySet()){
            listDataHeaderDep.add(dep);

            // Adding child data
            ChangeModel lifeState = depMap.get(dep);
            listDataChildDep.put(listDataHeaderDep.get(counter), lifeState); // Header, Child data
            counter++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_graph, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() == R.id.action_graph) {
            Intent newIntent = new Intent(LifeDisplayActivity.this, BarChartActivityMultiDataset.class);
            startActivity(newIntent);

            return true;
        }

        return false;
    }

    @Override
    public void onDataItemUpdated(Object data) {
      //  prepareListDataPersons();
      //  prepareListDataDepartment();

        listAdapterDep.notifyDataSetChanged();
        listAdapterPersons.notifyDataSetChanged();
    }

    @Override
    public void onDataArrayUpdated(Object[] data) {

    }

    @Override
    public void onDataListUpdated(List<?> data) {

    }

    @Override
    public void onDataFailed(BEResponseCode responseCode) {

    }

}