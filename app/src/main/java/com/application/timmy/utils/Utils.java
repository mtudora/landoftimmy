package com.application.timmy.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mtudora on 25/03/15.
 */
public class Utils {
    public static String getFileNameByName(Context ctx, String name, String department){
        File directory =  getTempDirectory(ctx);
        File imageFile = new File(directory, "IMG_" +name +"_"+department+ ".png");

        if (imageFile != null && imageFile.exists())
            return imageFile.getPath();
        else{
            imageFile = new File(directory, "IMG_"+name +"_"+department +".png");

            if (imageFile != null && imageFile.exists())
                return imageFile.getPath();
        }

        return null;
    }

    public static File getTempDirectory(Context ctx) {
        File cache;

        // SD Card Mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Android/data/" + ctx.getPackageName() + "/cache/");
        }
        // Use internal storage
        else {
            cache = ctx.getCacheDir();
        }

        // Create the cache directory if it doesn't exist
        if (!cache.exists()) {
            cache.mkdirs();
        }

        return cache;
    }

    public static File createImageFile(Context ctx,  String name, String department) throws IOException {
        String imageFileName = "IMG_" +name +"_"+department;

        File albumF = getTempDirectory(ctx);
        File imageF = new File(albumF, imageFileName + ".png");

        return imageF;
    }

    public static File getOutputImageFile(Context ctx) throws IOException{
        // TODO: check the presence of SDCard

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        // File albumF = getAlbumDir(ctx);getTempDirectoryPath(ctx)
        File albumF = getTempDirectory(ctx);
        File imageF = File.createTempFile(imageFileName, ".png", albumF);

       // PostagramData.getInstance().setCurrentImageFile(imageF);

        return imageF;

    }
}
