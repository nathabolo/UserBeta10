package com.munye.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.munye.user.R;

/**
 * Created by Akash on 1/16/2017.
 */

public class AndyUtils {

    private static Dialog dialog;

    public static void showToast(Context context , String message){
        Toast.makeText(context , message , Toast.LENGTH_LONG).show();
    }

    public static void generateLog(String logMessage){
        Log.d("tag", logMessage);
    }



    public static void showErrorToast(int id , Context context){

        String message;
        String errorCode = Const.ERROR_CODE_PRIFIX + id;

        message = context.getResources().getString(context.getResources().getIdentifier(errorCode, "string", context.getPackageName()));
        Toast.makeText(context , message , Toast.LENGTH_LONG).show();
    }


    public static String getColorCode(int type , Context context){
        String color;
        String colorCode = Const.COLOR_CODE_PRIFIX + type;
        color = context.getResources().getString(context.getResources().getIdentifier(colorCode , "color" , context.getPackageName()));
        return color;
    }


    public static String getBackgroundColor(int type , Context context){
        String color;
        String colorCode = Const.BACKGROUND_COLOR_CODE_PRIFIX + type;
        color = context.getResources().getString(context.getResources().getIdentifier(colorCode , "color" , context.getPackageName()));
        return color;
    }

    public static String getSymbolFromHex(String hexValue){
        return String.valueOf(Html.fromHtml(hexValue));
    }

    public static void showCustomProgressDialog(Context context , boolean isCancelable){

        if(dialog != null && dialog.isShowing())
            return;

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialog_view);
        ImageView imgPogrssDialog = (ImageView)dialog.findViewById(R.id.imgPogrssDialog);
        imgPogrssDialog.setAnimation(AnimationUtils.loadAnimation(context , R.anim.rotation_animation));
        dialog.setCancelable(isCancelable);
        dialog.show();

    }

    public static void removeCustomProgressDialog(){
        try{
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
            dialog = null;
        }
    }catch (Exception e) {
        }
        }


    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = actNetworkInfo != null && actNetworkInfo.isConnectedOrConnecting();
        return isConnected;
    }

    public static boolean isGpsEnable(Context context){
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }


    public static boolean hasAnyPrifix(String number, String... prefixes){
        if (number == null) {
            return false;
        }
        for (String prefix : prefixes) {
            if (number.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
