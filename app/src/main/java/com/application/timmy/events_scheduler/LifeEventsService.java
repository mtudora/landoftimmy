package com.application.timmy.events_scheduler;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.application.timmy.model.PersonModel;
import com.application.timmy.storage.PreferencesManager;
import com.application.timmy.storage.TimmyData;
import com.application.timmy.ui.OfficeBaseActivity;

import java.util.ArrayList;

/**
 * Created by mtudora on 26/03/15.
 */
public class LifeEventsService extends Service{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
  //  public LifeEventsService(String name) {
      //  super(name);
    //}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.

        if (intent.getExtras() != null && intent.getBooleanExtra("started_from_alarm", false)){
            processLifeChanges();

            Intent lifeChangedDone = new Intent(OfficeBaseActivity.CHANGED_LIFE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(lifeChangedDone);
        }

        stopSelf();

        return START_NOT_STICKY;
    }

    public void processLifeChanges(){
        ArrayList<PersonModel>  personsList = TimmyData.getInstance().getPersonsList();
        if (personsList == null)
            personsList = PreferencesManager.getInstance().getPersonsList();

        if (personsList != null){
            for (PersonModel person : personsList){
                int initialHumour = person.getHumour();
                int initialMorale = person.getMorale();
                int initialSkill = person.getSkill();
                int changeMorale = person.getDailyChange().getMorale();
                int changeSkill = person.getDailyChange().getSkill();
                int changeHumour = person.getDailyChange().getHumour();

                person.setHumour(initialHumour + changeHumour);
                person.setMorale(initialMorale + changeMorale);
                person.setSkill(initialSkill + changeSkill);
                person.getLifeState().setHumour(initialHumour + changeHumour);
                person.getLifeState().setMorale(initialMorale + changeMorale);
                person.getLifeState().setSkill(initialSkill + changeSkill);
            }
        }

        TimmyData.getInstance().setPersonsList(personsList);
        PreferencesManager.getInstance().setPersonsList(personsList);
        TimmyData.getInstance().parseData();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

  /*  @Override
    protected void onHandleIntent(Intent intent) {

    }*/

    @Override
    public void onDestroy() {
        // I want to restart this service again in one hour
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent newServiceIntent = new Intent(this, LifeEventsService.class);
        newServiceIntent.putExtra("started_from_alarm", true);

        alarm.set(
                alarm.RTC_WAKEUP,
                //System.currentTimeMillis() + (1000 * 60 * 2),
                System.currentTimeMillis() + (1000 * 30),
                PendingIntent.getService(this, 0, newServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        );
    }
}
