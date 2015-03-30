package com.application.timmy.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.application.timmy.connectivity.BECommunicator;
import com.application.timmy.connectivity.BEResponseCode;
import com.application.timmy.storage.PreferencesManager;
import com.application.timmy.storage.TimmyData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtudora on 27/03/15.
 */
public class OfficeBaseActivity extends Activity implements BECommunicator {
    public static final String CHANGED_LIFE = "com.application.timmy.CHANGED_LIFE";
    private OfficeBaseActivity baseActivity;

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(CHANGED_LIFE)) {
                //TimmyData.getInstance().setPersonsList(PreferencesManager.getInstance().getPersonsList());
               //notify the two classes
                baseActivity.onDataItemUpdated(null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CHANGED_LIFE);
        bManager.registerReceiver(bReceiver, intentFilter);

        baseActivity = this;

    }

    @Override
    public void onDataItemUpdated(Object data) {

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
