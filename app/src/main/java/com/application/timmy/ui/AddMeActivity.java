package com.application.timmy.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.application.timmy.TimmyApplication;
import com.application.timmy.model.ChangeModel;
import com.application.timmy.model.PersonModel;
import com.application.timmy.storage.PreferencesManager;
import com.application.timmy.storage.TimmyData;
import com.application.timmy.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import timmy.application.com.landoftimmy.R;

/**
 * Created by mtudora on 16/06/14.
 */
public class AddMeActivity extends OfficeBaseActivity{
    private static final String TAG = "AddMeActivity";

    public static final int REQUEST_CAMERA_CODE = 400;

    private ImageView imgChosenView;
    private String imageFilePath;
    private int targetW, targetH;

    private EditText nameEdit, depEdit, deskXEdit, deskYEdit, moraleEdit, humourEdit, skillEdit, dailyMoraleEdit, dailyHumourEdit, dailySkillEdit;
    private Button addSelfie;
    private Bitmap selfieBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.set_my_details);

        imgChosenView = (ImageView) findViewById(R.id.selfiePic);
        nameEdit = (EditText) findViewById(R.id.name);
        depEdit = (EditText) findViewById(R.id.dep);
        deskXEdit = (EditText) findViewById(R.id.deskX);
        deskYEdit = (EditText) findViewById(R.id.deskY);
        moraleEdit = (EditText) findViewById(R.id.morale);
        humourEdit = (EditText) findViewById(R.id.humour);
        skillEdit = (EditText) findViewById(R.id.skill);
        dailyHumourEdit = (EditText) findViewById(R.id.dailyHumour);
        dailyMoraleEdit = (EditText) findViewById(R.id.dailyMorale);
        dailySkillEdit = (EditText) findViewById(R.id.dailySkill);
        addSelfie = (Button) findViewById(R.id.addSelfie);
        addSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonModel person = new PersonModel();

                try {
                    person.setName(nameEdit.getText().toString());
                    person.setDepartment(depEdit.getText().toString());
                    person.setDeskX(Integer.valueOf(deskXEdit.getText().toString()));
                    person.setDeskY(Integer.valueOf(deskYEdit.getText().toString()));
                    int morale = Integer.valueOf(moraleEdit.getText().toString());
                    int humour = Integer.valueOf(humourEdit.getText().toString());
                    int skill = Integer.valueOf(skillEdit.getText().toString());
                    person.setMorale(morale);
                    person.setHumour(humour);
                    person.setSkill(skill);
                    ChangeModel change = new ChangeModel();
                    change.setMorale(morale);
                    change.setHumour(humour);
                    change.setSkill(skill);
                    person.setLifeState(change);

                    change = new ChangeModel();
                    change.setMorale( Integer.valueOf(dailyMoraleEdit.getText().toString()));
                    change.setHumour(Integer.valueOf(dailyHumourEdit.getText().toString()));
                    change.setSkill( Integer.valueOf(dailySkillEdit.getText().toString()));
                    person.setDailyChange(change);

                    person.setPhotoLocalPath(imageFilePath);
                    person.setPhotoBitmap(selfieBitmap);

                    ArrayList<PersonModel> personsList = TimmyData.getInstance().getPersonsList();
                    if (personsList == null)
                        personsList = PreferencesManager.getInstance().getPersonsList();
                    if (personsList != null) {
                        person.setId(personsList.size());
                        personsList.add(person);
                    }

                    TimmyData.getInstance().setPersonsList(personsList);
                    PreferencesManager.getInstance().setPersonsList(personsList);
                    TimmyData.getInstance().parseData();

                    setResult(RESULT_OK);
                    finish();
                }catch (NumberFormatException e){

                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_CODE) {
                //set the imageview
                setPic(imageFilePath);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


    private void setPic(String path) {
        // Get the dimensions of the View
        targetW = imgChosenView.getWidth();
        targetH = imgChosenView.getHeight();
       // targetW =  (int) getResources().getDimension(R.dimen.target_picture_width);
        //targetH = (int) getResources().getDimension(R.dimen.target_picture_height);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        int sampleSize = calculateInSampleSize(imageWidth, imageHeight, targetW, targetH);
        options.inSampleSize = sampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap initialBitmap = BitmapFactory.decodeFile(path, options);

        if (initialBitmap == null)
            return;

        //selfieBitmap = getRoundedShape(initialBitmap);

        //selfieBitmap = ThumbnailUtils.extractThumbnail(initialBitmap, (int) getResources().getDimension(R.dimen.target_picture_width), (int) getResources().getDimension(R.dimen.target_picture_height));
        selfieBitmap = ThumbnailUtils.extractThumbnail(initialBitmap, targetW, targetH);

        FileOutputStream out = null;
        try {
            File file = Utils.createImageFile(TimmyApplication.getInstance().getApplicationContext(), "selfie", "dep");

            out = new FileOutputStream(file);
            imageFilePath = file.getPath();

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            initialBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (selfieBitmap != null) {
            imgChosenView.setImageBitmap(selfieBitmap);

        }
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {

        int targetWidth = 50;
        int targetHeight = 50;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }

    public static int calculateInSampleSize(
            int width, int height , int reqWidth, int reqHeight) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_one_item, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() == R.id.action_camera) {
            //take photo
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File f = null;
                try {
                    f = Utils.getOutputImageFile(this);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    imageFilePath = f.getPath();
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA_CODE);
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    imageFilePath = null;
                }
            }

            return true;
        }

        return false;
    }

 /*   public void saveBitmapFromView(ImageView v) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        System.out.println(layoutParams.width + " "+layoutParams.height);
        v.buildDrawingCache();

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        v.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        float scaleX = f[Matrix.MSCALE_X];
        float scaleY = f[Matrix.MSCALE_Y];

        if (scaleX == 0)
            scaleX = 1;
        if (scaleY == 0)
            scaleY = 1;

        float transX = f[Matrix.MTRANS_X];
        float transY = f[Matrix.MTRANS_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = v.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        //int posX = transX;
        //in

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);


        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = v.getWidth();
        int imgViewH = v.getHeight();

        int top = (int) (imgViewH - actH)/2;
        int left = (int) (imgViewW - actW)/2;

        int posX = 0; int posY = 0;
        if (transX != 0)
            posX = (int) (actW + transX);
        if (transY != 0)
            posY = (int) (actH + transY);

        savedImgPosX = (int)(Math.abs(transX)/scaleX) * sampleSizeOfPicture;
        savedImgPosY = (int)(Math.abs(transY)/scaleY) * sampleSizeOfPicture;
        savedImgWidth = (int)(imgViewW/scaleX) * sampleSizeOfPicture;
        savedImgHeight = (int)(imgViewH/scaleY) * sampleSizeOfPicture;
        //savedImgWidth = (imgViewW)* sampleSizeOfPicture;
        //savedImgHeight = (imgViewH)* sampleSizeOfPicture;

        System.out.println(transX + " "+transY+" "+left+" "+top);

       Bitmap b = v.getDrawingCache(true);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);

        Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(b, 100, 71);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Utils.getOutputImageFile(this));
            //todo retest
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            //  Bitmap bScaled = Bitmap.createScaledBitmap(b, 1831, 1299, true);
            b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileOutputStream outSmall = null;
        try {
            outSmall = new FileOutputStream(Utils.getSmallOutputImageFile(this));
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outSmall);
            outSmall.flush();
            outSmall.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

}


