package com.munye;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.munye.dialog.CustomTitleDialog;
import com.munye.user.R;
import com.munye.dialog.CustomCountryCodeDialog;
import com.munye.model.CountryCode;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.MultiPartRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.CompressImage;
import com.munye.utils.Const;
import com.munye.utils.Validation;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class RegisterActivity extends ActionBarBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener{

    private EditText edtRegisterUserName, edtRegisterEmail, edtRegisterPassword, edtRegisterConformPassword, edtRegisterConatctNo , edtRegisterAddress;
    private Button btnRegister;
    private String userName, userEmail, userPassword, userContact , userAddress , countryCode;
    private ImageView imgUserPhoto;
    private Uri uri = null;
    private String filePath = null;
    private TextView tvCountryCodeType;
    private CustomCountryCodeDialog customCountryCodeDialog;
    private ArrayList<CountryCode> listCountryCode;
    private TextView tvGotoLogin;
    private CompressImage compressImage;
    private int verification_key_a,verification_key_b;
    private int i = 0;
    private CustomTitleDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initToolBar();
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);
        setToolBarTitle(getString(R.string.title_register));

        edtRegisterUserName = (EditText) findViewById(R.id.edtRegisterUserName);
        edtRegisterEmail = (EditText) findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword = (EditText) findViewById(R.id.edtRegisterPassword);
        edtRegisterConformPassword = (EditText) findViewById(R.id.edtRegisterConformPassword);
        edtRegisterConatctNo = (EditText) findViewById(R.id.edtRegisterConatctNo);
        edtRegisterAddress = (EditText)findViewById(R.id.edtRegisterAddress);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        imgUserPhoto = (ImageView)findViewById(R.id.imgUserPhoto);
        tvCountryCodeType = (TextView)findViewById(R.id.tvCountryCodeType);
        tvGotoLogin = (TextView)findViewById(R.id.tvGotoLogin);

        imgBtnToolbarBack.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        imgUserPhoto.setOnClickListener(this);
        tvCountryCodeType.setOnClickListener(this);
        tvGotoLogin.setOnClickListener(this);

        listCountryCode = new ArrayList<>();

        checkLocationPermission();
    }

    /*Handle click events*/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imgBtnActionBarBack:
                onBackPressed();
                break;

            case R.id.btnRegister:
                if(filePath == null) {
                }else {
                    if (isValidData()) {

                         if(Integer.parseInt(userContact.charAt(0)+"")>0){
                            userContact = "0"+userContact;

                            }

                        Random r = new Random();
                        verification_key_a = r.nextInt(2000 - 1000) * r.nextInt(2000 - 1000);
                        i=1;
                        registerUserWithKey(verification_key_a+"","");

                    }
                    break;
                }
            case R.id.imgUserPhoto:
                showPictureChooseDialog();
                break;

            case R.id.tvCountryCodeType:
                showCountryCodeDialog();
                break;

            case R.id.tvGotoLogin:
                goToLoginScreen();
                break;


            default:
                AndyUtils.generateLog("No id found");
                break;
        }
    }


    public void VerifyDetails(String msg){
        try{
        Button btnSubmit,btnSkip;
        final Dialog buyTokensDialog = new Dialog(RegisterActivity.this);
        buyTokensDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        buyTokensDialog.setContentView(R.layout.verification_dialog);
        buyTokensDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        btnSubmit = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceOk);
        btnSkip = (Button) buyTokensDialog.findViewById(R.id.btnSkip);
        Button btnCancel = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceCancel);
        final EditText key = (EditText) buyTokensDialog.findViewById(R.id.verificationKey);

        TextView txtAdminPrice = (TextView) buyTokensDialog.findViewById(R.id.msg);
        txtAdminPrice.setText(msg);

        if(i==1){
            btnSkip.setVisibility(View.INVISIBLE);
        }
        else{
            btnSkip.setVisibility(View.VISIBLE);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(i == 1){
                    buyTokensDialog.dismiss();
                    i = 2;
                    AndyUtils.showCustomProgressDialog(RegisterActivity.this, false);
                    Random r = new Random();
                    verification_key_b = r.nextInt(2000 - 1000) + 765764;
                    if(key.getText().toString().equals(verification_key_a+"")) {
                      //  registerUserWithKey(verification_key_b + "", "sms");
                        registerUserWithKey(verification_key_a+"","");
                    }else{
                        Toast.makeText(RegisterActivity.this, "Invalid Verification Key\n Please Try Again\n"+key.getText().toString()+"\n\n"+verification_key_a, Toast.LENGTH_SHORT).show();
                        AndyUtils.removeCustomProgressDialog();
                    }
                }
                else if(i == 2){

                    registerUser();
                    buyTokensDialog.dismiss();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Invalid Input\n Please Verify Your Details", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog = new CustomTitleDialog(RegisterActivity.this, "Skip Tier 2 Verification", "By Skipping Tier 2 (SMS) Verification your account will have a lower rating when viewed",
                        "Skip", "Cancel") {
                    @Override
                    public void positiveResponse() {
                        registerUser();
                    }

                    @Override
                    public void negativeResponse() {
                        exitDialog.dismiss();
                    }
                };
                exitDialog.show();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyTokensDialog.dismiss();
            }
        });
        buyTokensDialog.show();
    }catch (Exception e) {
        }
        }

    private void goToLoginScreen(){
        startActivity(new Intent(RegisterActivity.this , SignInActivity.class));
        finish();
    }


    /*It shows the country dialog*/
    private void showCountryCodeDialog() {

        listCountryCode = dataParser.parseCountryCode();
        customCountryCodeDialog = null;
        customCountryCodeDialog = new CustomCountryCodeDialog(this , listCountryCode) {
            @Override
            public void onSelectCountryCode(View view, int position) {
                TextView tvCountryCode = (TextView)view.findViewById(R.id.tvCountryCode);
                String countryCode = tvCountryCode.getText().toString().trim();
                tvCountryCodeType.setText(countryCode);
                customCountryCodeDialog.dismiss();
            }
        };

        customCountryCodeDialog.show();

    }


    /*It checks the validation of input data*/
    private boolean isValidData(){

        userName = edtRegisterUserName.getText().toString().trim();
        userEmail = edtRegisterEmail.getText().toString().trim();
        userPassword = edtRegisterPassword.getText().toString().trim();
        userContact = edtRegisterConatctNo.getText().toString().trim();
        userAddress = edtRegisterAddress.getText().toString().trim();
        countryCode = tvCountryCodeType.getText().toString().trim();

        if(Validation.isEmpty(userName)){
            AndyUtils.showToast(this,getString(R.string.toast_name_not_be_empty));
            return false;
        }

        else if(!Validation.isEmailValid(userEmail)){
            AndyUtils.showToast(this ,getString(R.string.toast_invalid_email));
            return false;
        }
        else if(Validation.isValidPasswordLength(userPassword)){
            AndyUtils.showToast(this , getString(R.string.toast_password_lenght));
            return false;
        }
        else if(!Validation.isPasswordMatch(userPassword , edtRegisterConformPassword.getText().toString().trim())){
            AndyUtils.showToast(this , getString(R.string.toast_password_not_matched));
            return false;
        }
//        else if(!Validation.isContactNoValid(userContact)){
//            AndyUtils.showToast(this , getString(R.string.toast_invalid_contact));
//            return false;
//        }

        else if(TextUtils.isEmpty(userAddress)){
            AndyUtils.showToast(this , getString(R.string.toast_enter_an_address));
            return false;
        }
        else {
            return true;
        }
    }



    //It shows the dialog to select photo from gallery or camera....
    private void showPictureChooseDialog(){
        AndyUtils.showCustomProgressDialog(this , false);
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getResources().getString(
                R.string.txt_dialog_choose_picture));
        String[] pictureDialogItems = {
                getResources().getString(R.string.txt_dialog_gallery),
                getResources().getString(R.string.txt_dialog_Camera)};

        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int type) {
                        switch (type) {
                            case 0:
                                choosePhotoFromGallery();
                                break;

                            case 1:
                                takePhotoFromCamera();
                                break;

                            default:
                                AndyUtils.generateLog("No Action");
                                break;
                        }
                    }
                });
        pictureDialog.show();
AndyUtils.removeCustomProgressDialog();
    }


    //It choose photo from gallery...
    private void choosePhotoFromGallery(){
        AndyUtils.showCustomProgressDialog(this , false);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, Const.PERMISSION_STORAGE_REQUEST_CODE);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, Const.CHOOSE_PHOTO);
        }
AndyUtils.removeCustomProgressDialog();
    }


    //It take photo from the camera....
    private void takePhotoFromCamera(){
try{
    AndyUtils.showCustomProgressDialog(this , false);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Const.PERMISSION_CAMERA_REQUEST_CODE);
        } else {
            Calendar cal = Calendar.getInstance();
            File directory = new File(Environment.getExternalStorageDirectory()+"/JimmieJobs");
            File file = new File(Environment.getExternalStorageDirectory()+"/JimmieJobs",(cal.getTimeInMillis() + ".jpg"));

            if(!directory.exists()){
                directory.mkdir();
            }

            if(file.exists()){
                file.delete();
            }
            else{
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    AndyUtils.generateLog("Exception of file");
                }
            }
            uri = Uri.fromFile(file);
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(cameraIntent, Const.TAKE_PHOTO);
        }
    AndyUtils.removeCustomProgressDialog();
    }catch (Exception e) {
    Toast.makeText(RegisterActivity.this, "Apologies the Camera Service has failed, Please try the Gallery Option", Toast.LENGTH_SHORT).show();
}
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Const.PERMISSION_STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        choosePhotoFromGallery();
                }
                break;

            case Const.PERMISSION_CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        takePhotoFromCamera();
                }
                break;

            default:
                AndyUtils.generateLog("No permission granted");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Const.CHOOSE_PHOTO && data != null) {
            compressImage = new CompressImage(this , Const.CHOOSE_PHOTO);
            compressImage.beginCrop(Uri.parse(compressImage.getCompressImagePath(data.getData())));

        } else if (requestCode == Const.TAKE_PHOTO) {

            compressImage = new CompressImage(this , Const.TAKE_PHOTO);
            if (uri != null) {
                String cameraFilePath = compressImage.getCompressImagePath(uri);
                if(compressImage.isBitmapCreated)
                    compressImage.beginCrop(Uri.parse(cameraFilePath));

            } else {
                Toast.makeText(
                        this,
                        getResources().getString(
                                R.string.toast_error_unable_to_select_image),
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == Crop.REQUEST_CROP && data != null) {
            try{
            setProfileImage(compressImage.handleCrop(resultCode, data));
            }catch (Exception e){
                Toast.makeText(RegisterActivity.this, "Photo could not be loaded Please Retry", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setProfileImage(String picturePath){
        try {
            filePath = picturePath;
            imgUserPhoto.setImageURI(Uri.parse(filePath));
        }catch (Exception e){
            Toast.makeText(RegisterActivity.this, "Photo could not be loaded at the moment you can do it later", Toast.LENGTH_SHORT).show();
        }
    }


    private void registerUserWithKey(String key, String sms){

        HashMap<String,String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.REGISTER);
        map.put(Const.Params.PICTURE, filePath);
        map.put(Const.Params.NAME , userName);
        map.put(Const.Params.EMAIL , userEmail);
        map.put(Const.Params.PASS, userPassword);
        map.put(Const.Params.CONTACT_NO , userContact);
        map.put(Const.Params.COUNTRY_CODE , countryCode);
        map.put(Const.Params.ADDRESS , userAddress);
        map.put(Const.Params.DEVICE_TYPE , Const.DEVICE_TYPE_ANDROID );
        map.put(Const.Params.DEVICE_TOKEN , preferenceHelper.getDeviceToken());
        map.put("verification_key", key);
        map.put("sms", sms);

        AndyUtils.showCustomProgressDialog(this , false);
        new MultiPartRequester(this , map , Const.ServiceCode.REGISTER , this);


    }

    private void registerUser(){
        AndyUtils.showCustomProgressDialog(this , false);
        HashMap<String,String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.REGISTER);
        map.put(Const.Params.PICTURE, filePath);
        map.put(Const.Params.NAME , userName);
        map.put(Const.Params.EMAIL , userEmail);
        map.put(Const.Params.PASS, userPassword);
        map.put(Const.Params.CONTACT_NO , userContact);
        map.put(Const.Params.COUNTRY_CODE , countryCode);
        map.put(Const.Params.ADDRESS , userAddress);
        map.put(Const.Params.DEVICE_TYPE , Const.DEVICE_TYPE_ANDROID );
        map.put(Const.Params.DEVICE_TOKEN , preferenceHelper.getDeviceToken());
        map.put("verification_key", "");

        AndyUtils.showCustomProgressDialog(this , false);
        new MultiPartRequester(this , map , Const.ServiceCode.REGISTER , this);


    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode){
            case Const.ServiceCode.REGISTER:
                if(dataParser.isSuccess(response)){
                   goToLoginScreen();

                }
                else {
                    if( i == 1){
                    VerifyDetails("Thank you for creating a JimmieJobs User Account, to complete the process please enter the verification " +
                            "key that has been sent to "+edtRegisterEmail.getText().toString());
                }else if(i == 2){
                        VerifyDetails("Please enter the final verification key that has been sent to "+edtRegisterConatctNo.getText().toString()+"\n\n" +
                                "You may also Skip this Tier 2 Verification by Clicking the Skip Button");
                    }
                }
                break;

            default:
                AndyUtils.generateLog("No service");
                break;
        }
    }
    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {

        Mail m;
        RegisterActivity activity;

        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    //Toast.makeText(activity, "Sent", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(activity, "Failed to send", Toast.LENGTH_SHORT).show();
                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
//                Toast.makeText(activity, "Authentication failed", Toast.LENGTH_SHORT).show();
                return false;
            } catch (MessagingException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Email failed");
                e.printStackTrace();
                Toast.makeText(activity, "Failed to send", Toast.LENGTH_SHORT).show();
                return false;
            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(activity, "Unexpected error", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }
}
