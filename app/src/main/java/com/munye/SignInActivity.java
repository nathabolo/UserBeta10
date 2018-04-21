package com.munye;

import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.munye.user.R;
import com.munye.dialog.CustomDialogWithEdittext;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.Validation;

import java.util.Calendar;
import java.util.HashMap;

public class SignInActivity extends ActionBarBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener {

    private Button  btnSignIn;
    private EditText edtEmail , edtPassword ;
    private TextView tvForgotPassword;
    private CustomDialogWithEdittext dialogForgotPassword;
    private TextView tvGotoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initToolBar();
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);
        setToolBarTitle(getString(R.string.title_sign_in));

        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        tvForgotPassword = (TextView)findViewById(R.id.tvForgotPassword);
        tvGotoRegister = (TextView)findViewById(R.id.tvGotoRegister);

        imgBtnToolbarBack.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvGotoRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imgBtnActionBarBack:
                onBackPressed();
                break;

            case R.id.btnSignIn:
                validateInputData();
                addNotification();
                //calendar();
                break;

            case R.id.tvForgotPassword:
                showForgotPasswordDialog();
                break;

            case R.id.tvGotoRegister:
                goToRegisterPage();
                break;

            default:
                AndyUtils.generateLog("default action");
                break;
        }
    }

    //A method to add a notification when users send info to the database
    private void addNotification() {

        String email = edtEmail.getText().toString();

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.jimmiejobslogo)
                        .setContentTitle("Sign in accepted")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setShowWhen(true)
                        .setWhen(System.currentTimeMillis() + 10 * 1000)
                        .setContentText("Welcome " + email + ", " + " you can now use our service");

        Intent notificationIntent = new Intent(this, DialogFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());


    }

//    private  void calendar(){
//
//        String email= "";
//
//        Calendar c = Calendar.getInstance();
//        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
//        Intent calender = new Intent(getApplicationContext(), MapActivity.class);
//        calender.putExtra("timeOfDay",timeOfDay);
//
//        if (timeOfDay >= 0 && timeOfDay < 12) {
//            Toast.makeText(this, "Good Morning " + email, Toast.LENGTH_LONG).show();
//        } else if (timeOfDay >= 12 && timeOfDay < 16) {
//            Toast.makeText(this, "Good Afternoon " + email, Toast.LENGTH_LONG).show();
//        } else if (timeOfDay >= 16 && timeOfDay < 21) {
//            Toast.makeText(this, "Good Evening " + email, Toast.LENGTH_LONG).show();
//        } else if (timeOfDay >= 21 && timeOfDay < 24) {
//            Toast.makeText(this, "Good Night " + email, Toast.LENGTH_LONG).show();
//        }
//
//
//    }


    private void goToRegisterPage(){
        startActivity(new Intent(SignInActivity.this , RegisterActivity.class));
        finish();
    }

    private void validateInputData(){
        if(!Validation.isEmailValid(edtEmail.getText().toString().trim())){
            AndyUtils.showToast(this , getString(R.string.toast_invalid_email));
        }
        else if(TextUtils.isEmpty(edtPassword.getText().toString().trim())){
            AndyUtils.showToast(this , getString(R.string.toast_blank_password));
        }
        else{
            sendLoginRequest();
        }
    }


    private void showForgotPasswordDialog(){

        dialogForgotPassword = new CustomDialogWithEdittext(this , getString(R.string.dilaog_title_forgot_password),
                getString(R.string.dilaog_hint_email) , getString(R.string.dialog_button_send) , getString(R.string.dialog_button_cancel)) {
            @Override
            public void yesUpdate(String email) {

                if(TextUtils.isEmpty(email) || !Validation.isEmailValid(email)){
                    AndyUtils.showToast(SignInActivity.this , getString(R.string.toast_invalid_email));
                }
                else{
                    forgotPasswordRequest(email);
                    dialogForgotPassword.dismiss();
                }

            }

            @Override
            public void NoUpdate() {
                dialogForgotPassword.dismiss();
            }
        };

        dialogForgotPassword.show();
    }

    private void forgotPasswordRequest(String email){

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.FORGOT_PASSWORD);
        map.put(Const.Params.USER_TYPE , Const.USER_TYPE);
        map.put(Const.Params.EMAIL , email);

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this , map , Const.ServiceCode.FORGOT_PASSWORD , Const.httpRequestType.POST , this);
    }

    private void sendLoginRequest(){
        HashMap<String,String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.LOGIN);
        map.put(Const.Params.LOGIN_BY , Const.MANUAL);
        map.put(Const.Params.APP_VERSION , Const.VERSION);
        map.put(Const.Params.EMAIL , edtEmail.getText().toString().trim());
        map.put(Const.Params.PASS, edtPassword.getText().toString().trim());
        map.put(Const.Params.DEVICE_TYPE , Const.DEVICE_TYPE_ANDROID );
        map.put(Const.Params.DEVICE_TOKEN , preferenceHelper.getDeviceToken());

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this , map , Const.ServiceCode.LOGIN ,Const.httpRequestType.POST ,this);
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode){
            case Const.ServiceCode.LOGIN :
                if(dataParser.isSuccess(response)){
                    dataParser.parseUserData(response);
                    Intent intentGotoMapFromLogin = new Intent(SignInActivity.this , MapActivity.class);
                    intentGotoMapFromLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentGotoMapFromLogin);
                }
                break;

            case Const.ServiceCode.FORGOT_PASSWORD:
                if(dataParser.isSuccess(response)){
                    AndyUtils.showToast(this , "Check your email for temporary password");
                }
                break;

            default:
                //Default action..
                break;
        }


    }
}
