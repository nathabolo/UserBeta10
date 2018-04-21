package com.munye.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.munye.user.R;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Akash on 2/20/2017.
 */

public class CompressImage {

    private Activity activity;
    private int rotationAngle;
    private int requestType;
    private String imageFilePath;
    public boolean isBitmapCreated = false;

    public CompressImage(Activity activity , int requestType) {
        this.activity = activity;
        this.requestType = requestType;
    }

    public String getCompressImagePath(Uri uri) {
        imageFilePath = null;
        if(requestType == Const.CHOOSE_PHOTO){
            imageFilePath = getRealPathFromURI(uri);
        }
        else {
            imageFilePath = uri.getPath();
        }
        if (imageFilePath != null && imageFilePath.length() > 0) {
            try {
                int mobileWidth = 480;
                BitmapFactory.Options options = new BitmapFactory.Options();
                int outWidth = options.outWidth;
                int ratio = (int) ((((float) outWidth) / mobileWidth) + 0.5f);

                if (ratio == 0) {
                    ratio = 1;
                }
                ExifInterface exif = new ExifInterface(imageFilePath);

                String orientString = exif
                        .getAttribute(ExifInterface.TAG_ORIENTATION);
                int orientation = orientString != null ? Integer
                        .parseInt(orientString)
                        : ExifInterface.ORIENTATION_NORMAL;


                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotationAngle = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotationAngle = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotationAngle = 270;
                        break;

                    default:
                        rotationAngle = 0;
                        break;
                }

                options.inJustDecodeBounds = false;
                options.inSampleSize = ratio;

                Bitmap bmp = BitmapFactory.decodeFile(imageFilePath,
                        options);
                File myFile = new File(imageFilePath);
                FileOutputStream outStream = new FileOutputStream(
                        myFile);
                if (bmp != null) {
                    isBitmapCreated = true;
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100,
                            outStream);
                    outStream.flush();
                    outStream.close();

                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotationAngle,
                            (float) bmp.getWidth() / 2,
                            (float) bmp.getHeight() / 2);

                    bmp = Bitmap.createBitmap(bmp, 0, 0,
                            bmp.getWidth(), bmp.getHeight(), matrix,
                            true);

                    String path = MediaStore.Images.Media.insertImage(
                            activity.getContentResolver(), bmp, Calendar
                                    .getInstance().getTimeInMillis()
                                    + ".jpg", null);

                    return path;
                } else {
                    isBitmapCreated = false;
                    outStream.close();
                }
            } catch (OutOfMemoryError e) {
                AndyUtils.generateLog("Outmemory exception:" + e);
            } catch (FileNotFoundException e) {
                AndyUtils.generateLog("File exception" + e);
            } catch (IOException e) {
                AndyUtils.generateLog("IoException" + e);
            }
        } else {
            Toast.makeText(
                    activity,
                    activity.getResources().getString(
                            R.string.toast_error_unable_to_select_image),
                    Toast.LENGTH_LONG).show();
            return null;
        }

        return imageFilePath;
    }


    public String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null,
                null, null);

        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            try {
                int idx = cursor
                        .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            } catch (Exception e) {
                AndyUtils.showToast(activity, activity.getResources().getString(R.string.toast_error_unable_to_get_image));
                result = "";
                AndyUtils.generateLog("Exception of get real path" + e);
            }
            cursor.close();
        }
        return result;
    }


    public void beginCrop(Uri source) {

        Uri outputUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), (Calendar.getInstance().getTimeInMillis() + ".jpg")));
        Crop.of(source,outputUri).asSquare().start(activity);
    }


    public String handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            AndyUtils.generateLog("Handle crop");
            //String filePath = getRealPathFromURI(Crop.getOutput(result));
            return getRealPathFromURI(Crop.getOutput(result));

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(activity, Crop.getError(result).getMessage(),Toast.LENGTH_SHORT).show();
            return null;
        }
        return null;
    }
}
