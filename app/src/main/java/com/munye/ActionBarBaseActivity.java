package com.munye;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.munye.user.R;
import com.munye.dialog.CustomDialog;
import com.munye.parse.DataParser;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.PreferenceHelper;

/**
 * Created by Akash on 1/12/2017.
 */

public class ActionBarBaseActivity extends AppCompatActivity  {

    protected Toolbar toolbar;
    protected TextView tvToolbarTitle;
    protected ImageButton imgBtnToolbarBack ;
    protected ImageButton imgBtnDrawerToggle;
    protected PreferenceHelper preferenceHelper;
    protected DataParser dataParser;
    private CustomDialog customInternetDialog;
    private CustomDialog customGpsDialog;
    private CustomDialog dialogLocationPemission;
    private CustomDialog dialogNeverAskAgainLocation;
    private NetworkAndGpsReceiver networkAndGpsReceiver;
    private CustomDialog pendingAmountDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkAndGpsReceiver = new NetworkAndGpsReceiver();
        dataParser = new DataParser(this);
        preferenceHelper = PreferenceHelper.getInstance(this);
    }

    public void initToolBar() {
        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        tvToolbarTitle = (TextView)findViewById(R.id.tvToolBarTitle);
        imgBtnToolbarBack = (ImageButton)findViewById(R.id.imgBtnActionBarBack);
        imgBtnDrawerToggle = (ImageButton)findViewById(R.id.imgBtnDrawerToggle);
        imgBtnToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void initPreference(){
        preferenceHelper = PreferenceHelper.getInstance(getApplicationContext());
    }

    public void setToolBarTitle(String title){
        tvToolbarTitle.setText("");
    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.INTERNET_ACTION);
        intentFilter.addAction(Const.GPS_ACTION);
        registerReceiver(networkAndGpsReceiver , intentFilter);

    }

    protected void openGpsDialog(final Activity activity){

        if(customGpsDialog != null && customGpsDialog.isShowing()){
            return;
        }

        customGpsDialog = new CustomDialog(this , getString(R.string.dialog_message_gps) , getString(R.string.dialog_button_yes) , getString(R.string.dialog_button_no) , false) {
            @Override
            public void positiveButton() {
                startActivityForResult(new Intent(android.provider.Settings
                        .ACTION_LOCATION_SOURCE_SETTINGS), Const.ACTION_LOCATION_SOURCE_SETTINGS);
            }

            @Override
            public void negativeButton() {
                closeGpsDialog();
                activity.finishAffinity();
            }
        };
        customGpsDialog.show();

    }

    protected void closeGpsDialog(){
        if(customGpsDialog != null && customGpsDialog.isShowing()){
            customGpsDialog.dismiss();
            customGpsDialog = null;
        }
    }

    protected void openInternetDialog(final Activity activity){

        if(customInternetDialog != null && customInternetDialog.isShowing()){
            return;
        }

        customInternetDialog = new CustomDialog(this , getString(R.string.dialog_message_internet) , getString(R.string.dialog_button_yes) , getString(R.string.dialog_button_no) , false) {
            @Override
            public void positiveButton() {
                startActivityForResult(new Intent(Settings
                        .ACTION_SETTINGS), Const.ACTION_SETTINGS);
            }

            @Override
            public void negativeButton() {
                closeInternetDialog();
                activity.finishAffinity();
            }
        };
        customInternetDialog.show();


    }

    protected void closeInternetDialog(){

        if(customInternetDialog != null && customInternetDialog.isShowing()){
            customInternetDialog.dismiss();
            customInternetDialog = null;
        }

    }



    protected void openLocationPermissionDialog(){

        dialogLocationPemission = new CustomDialog(this , getString(R.string.diloag_message_location_permission) , getString(R.string.dialog_button_retry) , getString(R.string.dilaog_button_imsure) , false) {
            @Override
            public void positiveButton() {
                closeLocationPermissionDialog();
                checkLocationPermission();
            }

            @Override
            public void negativeButton() {
                closeLocationPermissionDialog();
                finishAffinity();            }
        };
        dialogLocationPemission.show();
    }


    protected void closeLocationPermissionDialog(){

        if(dialogLocationPemission != null && dialogLocationPemission.isShowing()){
            dialogLocationPemission.dismiss();
            dialogLocationPemission = null;
        }
    }



    protected void openDialogNeverAskAgain(){
        dialogNeverAskAgainLocation = new CustomDialog(this , getString(R.string.dialog_message_location_never_ask) , getString(R.string.dialog_button_setting) , getString(R.string.dialog_button_exit) , false) {
            @Override
            public void positiveButton() {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package" , getPackageName() , null);
                intent.setData(uri);
                startActivityForResult(intent, Const.ACTION_SETTINGS);
            }

            @Override
            public void negativeButton() {
                closeDialogNeverAskAgain();
                finishAffinity();
            }
        };
        dialogNeverAskAgainLocation.show();
    }


    protected void closeDialogNeverAskAgain(){
        if(dialogNeverAskAgainLocation != null && dialogNeverAskAgainLocation.isShowing()){
            dialogNeverAskAgainLocation.dismiss();
            dialogNeverAskAgainLocation = null;
        }
    }



    protected void checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission
                    .ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, Const.PERMISSION_LOCATION_REQUEST_CODE);
        }
    }


    protected void backToMapActivity(){
        startActivity(new Intent(this , MapActivity.class));
    }


    private class NetworkAndGpsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()){
                case Const.INTERNET_ACTION:
                    doInternetDialogAction(context);
                    break;

                case Const.GPS_ACTION:
                    doGpsDialogAction(context);
                    break;

                default:
                    //Default action
                    break;
            }
        }
    }


    private void doInternetDialogAction(Context context){
        if(AndyUtils.isNetworkAvailable(context)){
            closeInternetDialog();
        }
        else {
            openInternetDialog(ActionBarBaseActivity.this);
        }
    }

    private void doGpsDialogAction(Context context){
        if(AndyUtils.isGpsEnable(context)){
            closeGpsDialog();
        }
        else {
            openGpsDialog(ActionBarBaseActivity.this);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Const.PERMISSION_LOCATION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Granted...
                }
                else if(grantResults.length > 0 && ActivityCompat.shouldShowRequestPermissionRationale(this , permissions[0])){
                    openLocationPermissionDialog();
                }
                else {
                    openDialogNeverAskAgain();
                }
                break;


            default:
                AndyUtils.generateLog("No permission granted");
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Const.ACTION_SETTINGS){
            internetDialogAsPerActivityResult();
        }
    }


    private void internetDialogAsPerActivityResult(){
        closeDialogNeverAskAgain();
        checkLocationPermission();
        if(AndyUtils.isNetworkAvailable(this)){
            closeInternetDialog();
        }
        else {
            openInternetDialog(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AndyUtils.isGpsEnable(this)){
            closeGpsDialog();
        }
        else {
            openGpsDialog(this);
        }
        if(AndyUtils.isNetworkAvailable(this)){
            closeInternetDialog();
        }
        else {
            openInternetDialog(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkAndGpsReceiver);
    }

    @Override
    public void onBackPressed() {
        if(customGpsDialog != null && customGpsDialog.isShowing()){
            customGpsDialog.dismiss();
        }
        super.onBackPressed();
    }
}
