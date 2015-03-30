package com.application.timmy.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.application.timmy.TimmyApplication;
import com.application.timmy.connectivity.BECommunicator;
import com.application.timmy.connectivity.BEResponseCode;
import com.application.timmy.connectivity.BackendService;
import com.application.timmy.connectivity.ConnectivityException;
import com.application.timmy.events_scheduler.LifeEventsService;
import com.application.timmy.model.LifeEventModel;
import com.application.timmy.model.PersonModel;
import com.application.timmy.storage.PreferencesManager;
import com.application.timmy.storage.TimmyData;
import com.application.timmy.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timmy.application.com.landoftimmy.R;


public class OfficeMapActivity extends OfficeBaseActivity {
        public static final int REQUEST_CODE_UPDATE_DATA = 555;

        /** Decoded bitmap image */
        private Bitmap mBitmap;

        private OfficeMapView officeMapView;
        private ArrayList<PersonModel> personsList;
        private ArrayList<LifeEventModel> lifeEventsList;

        private int targetPicW, targetPicH;

        private ArrayList<DownloadFileAsync> asyncTasks;
        protected ProgressDialog mProgressDownload;
        private LinearLayout personMenuLayout;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.map_view);

            targetPicW =  (int) getResources().getDimension(R.dimen.target_picture_width);
            targetPicH = (int) getResources().getDimension(R.dimen.target_picture_height);

            asyncTasks = new ArrayList<DownloadFileAsync>();

            officeMapView = (OfficeMapView)findViewById(R.id.officeMapView);
         //   personMenuLayout = (LinearLayout) findViewById(R.id.personMenuLayout);

        //    officeMapView.setData(personMenuLayout);

            if (PreferencesManager.getInstance().getPersonsList() == null)
                new GetOfficeConfigurationTask(this).execute();

            startService(new Intent(this, LifeEventsService.class));

            processImages();
        }


        @Override
        protected void onDestroy() {
            super.onDestroy();

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() == R.id.action_add_person) {
            Intent newIntent = new Intent(OfficeMapActivity.this, AddMeActivity.class);
            startActivityForResult(newIntent, REQUEST_CODE_UPDATE_DATA);
            return true;
        }
        // Handle presses on the action bar items
        if (item.getItemId() == R.id.action_show) {
            Intent newIntent = new Intent(OfficeMapActivity.this, LifeDisplayActivity.class);
            //Intent newIntent = new Intent(OfficeMapActivity.this, LifeDepartmentGraph.class);
            startActivity(newIntent);
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE_UPDATE_DATA){
                officeMapView.onDataListUpdated(TimmyData.getInstance().getPersonsList());
            }
        }
    }

    @Override
    public void onDataItemUpdated(Object data) {
        if (officeMapView != null){
            processImages();

        }
    }

    public void processImages(){
        if (TimmyData.getInstance().getPersonsList() != null)
            personsList = TimmyData.getInstance().getPersonsList();
        else
        if (PreferencesManager.getInstance().getPersonsList() != null )
            personsList = PreferencesManager.getInstance().getPersonsList();
        else
            return;

        if (personsList != null) {
            for (PersonModel personModel : personsList) {
                String filePath = personModel.getPhotoLocalPath();

                if (personModel.getPhotoBitmap() == null && filePath != null) {
                    personModel.setPhotoLocalPath(filePath);

                    File imgFile = new File(filePath);
                    if (imgFile.exists()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(filePath, options);
                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;

                        int sampleSize = calculateInSampleSize(imageWidth, imageHeight, targetPicW, targetPicH);
                        options.inSampleSize = sampleSize;
                        // Decode bitmap with inSampleSize set
                        options.inJustDecodeBounds = false;

                        Bitmap personBitmap = BitmapFactory.decodeFile(filePath, options);

                        personModel.setPhotoBitmap(personBitmap);
                    }
                }
            }
        }

        PreferencesManager.getInstance().setPersonsList(personsList);
        TimmyData.getInstance().setPersonsList(personsList);
        TimmyData.getInstance().parseData();
        officeMapView.onDataListUpdated(personsList);
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

    private class GetOfficeConfigurationTask extends AsyncTask<Void, Void, ArrayList<PersonModel>> {
            private Context mContext;
            private ProgressDialog mProgressdialog;

            public GetOfficeConfigurationTask(Context context) {
                mContext = context;

                mProgressdialog = new ProgressDialog(context);
                mProgressdialog.setMessage("Please wait");
                mProgressdialog.setCancelable(false);

            }

            @Override
            protected void onPreExecute() {
                mProgressdialog.show();
            }

            @Override
            protected ArrayList<PersonModel> doInBackground(Void... params) {
                downloadData();

                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<PersonModel> persons) {
                mProgressdialog.dismiss();

                PreferencesManager.getInstance().setPersonsList(personsList);
                PreferencesManager.getInstance().setLifeEventsList(lifeEventsList);

                TimmyData.getInstance().setLifeEventsList(lifeEventsList);
                TimmyData.getInstance().setPersonsList(personsList);

                TimmyData.getInstance().parseData();

                if (officeMapView != null){
                    officeMapView.onDataListUpdated(personsList);
                }

                downloadAndSaveImages();
            }
        }

        public void downloadData(){
            //get the persons and life events list from the server
            try {
                if (personsList == null)
                    personsList = new ArrayList<PersonModel>();
                if (lifeEventsList == null)
                    lifeEventsList = new ArrayList<LifeEventModel>();

                JSONObject officeConfiguration = BackendService.getInstance().getOfficeConfiguration();
                JSONArray peopleArray = officeConfiguration.getJSONArray("people");
                for (int i = 0; i < peopleArray.length(); i++){
                    PersonModel person = new PersonModel(peopleArray.getJSONObject(i));
                    person.setId(i);
                    if (personsList != null && person != null)
                        personsList.add(person);
                }

                JSONArray lifeEventsArray = officeConfiguration.getJSONArray("life_events");
                for (int i = 0; i < lifeEventsArray.length(); i++){
                    LifeEventModel lifeEvent = new LifeEventModel(lifeEventsArray.getJSONObject(i));
                    lifeEventsList.add(lifeEvent);
                }

            } catch (ConnectivityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    public void downloadAndSaveImages(){
        //Intent intent = new Intent(this, DownloaderService.class);
        //startService(intent);

        if (personsList == null){
            return;
        }

        int nrOfPersons = personsList.size();
        for (int i = 0; i < nrOfPersons; i++) {
            PersonModel personModel = personsList.get(i);
            String filePath = Utils.getFileNameByName(this, personModel.getName(), personModel.getDepartment());

            if (filePath == null) {
                DownloadFileAsync dwdTask = new DownloadFileAsync(personModel.getPhotoUrl(), personModel, i, nrOfPersons);

                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
                    dwdTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    dwdTask.execute();
                }
            }else {
                if (filePath != null) {
                    personModel.setPhotoLocalPath(filePath);

                    File imgFile = new File(filePath);
                    if (imgFile.exists()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(filePath, options);
                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;

                        int sampleSize = calculateInSampleSize(imageWidth, imageHeight, targetPicW, targetPicH);
                        options.inSampleSize = sampleSize;
                        // Decode bitmap with inSampleSize set
                        options.inJustDecodeBounds = false;

                        Bitmap personBitmap = BitmapFactory.decodeFile(filePath, options);

                        personModel.setPhotoBitmap(personBitmap);
                    }
                }
            }
        }


        PreferencesManager.getInstance().setPersonsList(personsList);

    }

    //              Async  Task
    class DownloadFileAsync extends AsyncTask<String, String, Boolean> {
        String url;
        PersonModel personModel;
        int count, totalCount;

        public DownloadFileAsync(String url, PersonModel personModel, int count, int totalCount) {
            this.url = url;
            this.personModel = personModel;
            this.count = count;
            this.totalCount = totalCount;
        }

        @Override
        protected Boolean doInBackground(String... aurl) {
            URL url = null;
            try {
                url = new URL(this.url);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;

                int sampleSize = calculateInSampleSize(imageWidth, imageHeight, targetPicW, targetPicH);
                options.inSampleSize = sampleSize;
                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;

                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
                personModel.setPhotoBitmap(bitmap);

                FileOutputStream out = null;
                try {
                    File file = Utils.createImageFile(TimmyApplication.getInstance().getApplicationContext(), personModel.getName(), personModel.getDepartment());

                    out = new FileOutputStream(file);
                    personModel.setPhotoLocalPath(file.getPath());

                    options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;

                    if (bitmap != null)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            if (mProgressDownload == null) {
                mProgressDownload = new ProgressDialog(OfficeMapActivity.this);
                mProgressDownload.setMessage("Synchronizing pictures");
                mProgressDownload.setIndeterminate(false);
                mProgressDownload.setProgress(0);
                mProgressDownload.setMax(personsList.size());
                mProgressDownload.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDownload.setCancelable(false);
            }

            if (asyncTasks.size() == 0) {
                if (!mProgressDownload.isShowing())
                    mProgressDownload.show();
            }

            asyncTasks.add(this);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);
            mProgressDownload.setProgress(Integer.parseInt(progress[0]));
        }



        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);

            asyncTasks.remove(this);
            if (asyncTasks.size() == 0) {
                PreferencesManager.getInstance().setPersonsList(personsList);
                mProgressDownload.dismiss();
            }
        }
    }



    public static int calculateInSampleSize(
            int height, int width , int reqWidth, int reqHeight) {
        // Raw height and width of image
        //  final int height = options.outHeight;
        //  final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
