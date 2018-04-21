package com.munye;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.munye.dialog.CustomTitleDialog;
import com.munye.user.R;
import com.munye.dialog.CustomCountryCodeDialog;
import com.munye.dialog.CustomDialogWithEdittext;
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

public class UpdateProfileActivity extends ActionBarBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener {

    private ImageView imgUpdateUserPhoto;
    private TextView tvUpdateUserName , tvUpdateCountryCode,tvTokenBalance;
    private EditText edtUpdateUserName;
    private EditText edtUpdateEmail;
    private EditText edtUpdateNewPassword;
    private EditText edtUpdateAddress;
    private EditText edtUpdateContact;
    private EditText edtUpdateConfirmPassword;
    private Button btnUpdateProfile;
    private CustomCountryCodeDialog dialogCustomCountryCode;
    private ArrayList<CountryCode> listCountryCode;
    private String userUpdateName;
    private String userUpdateEmail;
    private String userUpdatePassword;
    private String userUpdateAddress;
    private String userUpdateContactNo;
    private String userCountryCode;
    private String userOldPassword;
    private CustomDialogWithEdittext dialogVerifyAccount;
    private String filePath = null;
    private CompressImage compressImage;
    private Uri uri;
    private boolean isDelete;
    private ProgressBar pbarProfileImage;
    public int tokens;
    private CallbackManager callbackManager;
    public int i = 0;
    private  int ii = 0;
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    private CustomTitleDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        initToolBar();
        setToolBarTitle("Profile");
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);
        tokens = 100;

        imgUpdateUserPhoto = (ImageView)findViewById(R.id.imgUpdateUserPhoto);
        tvUpdateUserName = (TextView)findViewById(R.id.tvUpdateUserName);
        tvUpdateUserName.setEnabled(false);
        tvTokenBalance = (TextView)findViewById(R.id.tvTokenBalance);
        tvUpdateCountryCode = (TextView)findViewById(R.id.tvUpdateCountryCode);
        edtUpdateUserName = (EditText)findViewById(R.id.edtUpdateUserName);
        edtUpdateEmail = (EditText)findViewById(R.id.edtUpdateEmail);
        edtUpdateNewPassword = (EditText)findViewById(R.id.edtUpdateNewPassword);
        edtUpdateConfirmPassword = (EditText)findViewById(R.id.edtUpdateConfirmPassword);
        edtUpdateAddress = (EditText)findViewById(R.id.edtUpdateAddress);
        edtUpdateContact = (EditText)findViewById(R.id.edtUpdateContact);
        btnUpdateProfile = (Button)findViewById(R.id.btnUpdateProfile);
        pbarProfileImage = (ProgressBar)findViewById(R.id.pbarProfileImage);



        setupAllRequiredData();
        listCountryCode = new ArrayList<>();
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            String j =(String) b.get("share");
            if(j.equals("share")) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(UpdateProfileActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(UpdateProfileActivity.this);
                }
                builder.setTitle("Share JimmieJobs On Facebook")
                        .setMessage("By Sharing JimmieJobs with Your Friends You Earn Free Tokens")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                shareYourContentOnFacebook();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }

            else if(j.equals("buy_tokens")) {
                int amnt =(int) b.get("amnt");
                String c_no =(String) b.get("c_no");
                String cvv =(String) b.get("cvv");
                String exp_yr =(String) b.get("exp_yr");
                String exp_m =(String) b.get("exp_m");
                String c_typ =(String) b.get("c_typ");
                i = 1;
                HashMap<String , String> map = new HashMap<>();
                map.put(Const.URL , Const.ServiceType.UPDATE_PROFILE);
                map.put(Const.Params.ID , preferenceHelper.getId());
                map.put(Const.Params.TOKEN , preferenceHelper.getToken());
                map.put(Const.Params.NAME , preferenceHelper.getUserName());
                map.put(Const.Params.CONTACT_NO , preferenceHelper.getContactNo());
                map.put(Const.Params.COUNTRY_CODE , preferenceHelper.getCountryCode());
                map.put(Const.Params.EMAIL , preferenceHelper.getEmail());
                map.put(Const.Params.ADDRESS , preferenceHelper.getAddress());
                map.put("add_card" , "buy_tokens");
                map.put("buy" , amnt+"");
                map.put("c_no" , c_no);
                map.put("cvv" , cvv);
                map.put("exp_yr" , exp_yr);
                map.put("exp_m" , exp_m);
                map.put("c_typ" , c_typ);
                if(!TextUtils.isEmpty(filePath)){
                    map.put(Const.Params.PICTURE , filePath);
                }
                if(!TextUtils.isEmpty(userUpdatePassword)){
                    map.put(Const.Params.NEW_PASS, "");
                    map.put(Const.Params.OLD_PASS, "");
                }

                AndyUtils.showCustomProgressDialog(UpdateProfileActivity.this , true);
                new MultiPartRequester(UpdateProfileActivity.this , map , Const.ServiceCode.UPDATE_PROFILE , UpdateProfileActivity.this);

            }

            else if(j.equals("idImage")) {
                ii=1;
                count++;
                final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
                File newdir = new File(dir);
                newdir.mkdirs();
                String file = dir+count+".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                }
                catch (IOException e)
                {
                }

                Uri outputFileUri = Uri.fromFile(newfile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }

            else if(j.equals("add_card")){
                String c_no =(String) b.get("c_no");
                String cvv =(String) b.get("cvv");
                String exp_yr =(String) b.get("exp_yr");
                String exp_m =(String) b.get("exp_m");
                String c_typ =(String) b.get("c_typ");
                HashMap<String , String> map = new HashMap<>();
                map.put(Const.URL , Const.ServiceType.UPDATE_PROFILE);
                map.put(Const.Params.ID , preferenceHelper.getId());
                map.put(Const.Params.TOKEN , preferenceHelper.getToken());
                map.put(Const.Params.NAME , preferenceHelper.getUserName());
                map.put(Const.Params.CONTACT_NO , preferenceHelper.getContactNo());
                map.put(Const.Params.COUNTRY_CODE , preferenceHelper.getCountryCode());
                map.put(Const.Params.EMAIL , preferenceHelper.getEmail());
                map.put(Const.Params.ADDRESS , preferenceHelper.getAddress());
                map.put("add_card" , "add_card");
                map.put("c_no" , c_no);
                map.put("cvv" , cvv);
                map.put("exp_yr" , exp_yr);
                map.put("exp_m" , exp_m);
                map.put("c_typ" , c_typ);
                if(!TextUtils.isEmpty(filePath)){
                    map.put(Const.Params.PICTURE , filePath);
                }
                if(!TextUtils.isEmpty(userUpdatePassword)){
                    map.put(Const.Params.NEW_PASS, "");
                    map.put(Const.Params.OLD_PASS, "");
                }

                AndyUtils.showCustomProgressDialog(UpdateProfileActivity.this , true);
                new MultiPartRequester(UpdateProfileActivity.this , map , Const.ServiceCode.UPDATE_PROFILE , UpdateProfileActivity.this);
            }

            else if(j.equals("pay")){
                i = 3;
                String amnt =(String) b.get("amnt");
                String id =(String) b.get("id");
                String c_no =(String) b.get("c_no");
                String cvv =(String) b.get("cvv");
                String exp_yr =(String) b.get("exp_yr");
                String exp_m =(String) b.get("exp_m");
                String c_typ =(String) b.get("c_typ");
                HashMap<String , String> map = new HashMap<>();
                map.put(Const.URL , Const.ServiceType.UPDATE_PROFILE);
                map.put(Const.Params.ID , preferenceHelper.getId());
                map.put(Const.Params.TOKEN , preferenceHelper.getToken());
                map.put(Const.Params.NAME , preferenceHelper.getUserName());
                map.put(Const.Params.CONTACT_NO , preferenceHelper.getContactNo());
                map.put(Const.Params.COUNTRY_CODE , preferenceHelper.getCountryCode());
                map.put(Const.Params.EMAIL , preferenceHelper.getEmail());
                map.put(Const.Params.ADDRESS , preferenceHelper.getAddress());
                map.put("pay" , "pay");
                map.put("amnt" , amnt);
                map.put("job_id" , id);
                map.put("c_no" , c_no);
                map.put("cvv" , cvv);
                map.put("exp_yr" , exp_yr);
                map.put("exp_m" , exp_m);
                map.put("c_typ" , c_typ);
                if(!TextUtils.isEmpty(filePath)){
                    map.put(Const.Params.PICTURE , filePath);
                }
                if(!TextUtils.isEmpty(userUpdatePassword)){
                    map.put(Const.Params.NEW_PASS, "");
                    map.put(Const.Params.OLD_PASS, "");
                }

                AndyUtils.showCustomProgressDialog(UpdateProfileActivity.this , true);
                new MultiPartRequester(UpdateProfileActivity.this , map , Const.ServiceCode.UPDATE_PROFILE , UpdateProfileActivity.this);
            }


        }

    }

    private void shareYourContentOnFacebook() {

        callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                HashMap<String , String> map = new HashMap<>();
                map.put(Const.URL , Const.ServiceType.UPDATE_PROFILE);
                map.put(Const.Params.ID , preferenceHelper.getId());
                map.put(Const.Params.TOKEN , preferenceHelper.getToken());
                map.put(Const.Params.NAME , preferenceHelper.getUserName());
                map.put(Const.Params.CONTACT_NO , preferenceHelper.getContactNo());
                map.put(Const.Params.COUNTRY_CODE , preferenceHelper.getCountryCode());
                map.put(Const.Params.EMAIL , preferenceHelper.getEmail());
                map.put(Const.Params.ADDRESS , preferenceHelper.getAddress());
                map.put("add_token" , "add_token");
                if(!TextUtils.isEmpty(filePath)){
                    map.put(Const.Params.PICTURE , filePath);
                }
                if(!TextUtils.isEmpty(userUpdatePassword)){
                    map.put(Const.Params.NEW_PASS, "");
                    map.put(Const.Params.OLD_PASS, "");
                }

                AndyUtils.showCustomProgressDialog(UpdateProfileActivity.this , true);
                new MultiPartRequester(UpdateProfileActivity.this , map , Const.ServiceCode.UPDATE_PROFILE , UpdateProfileActivity.this);
            }

            @Override
            public void onCancel() {
                Log.d(this.getClass().getSimpleName(), "sharing cancelled");
                //add your code to handle cancelled sharing
                Toast.makeText(UpdateProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(this.getClass().getSimpleName(), "sharing error");
                //add your code to handle sharing error
                Toast.makeText(UpdateProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {

            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Share JimmieJobs To Earn Tokens")
                    .setContentDescription("JimmieJobs")
                    .setContentUrl(Uri.parse("https://www.facebook.com/jimmiejobs/"))
                    .build();

            shareDialog.show(shareLinkContent);

        }

    }


    private void setupAllRequiredData() {
        tvTokenBalance.setText(preferenceHelper.getUserToken()+" Token(s)");
        tvUpdateUserName.setText(preferenceHelper.getUserName());
        edtUpdateUserName.setText(preferenceHelper.getUserName());
        edtUpdateEmail.setText(preferenceHelper.getEmail());
        edtUpdateContact.setText(preferenceHelper.getContactNo());
        edtUpdateAddress.setText(preferenceHelper.getAddress());
        tvUpdateCountryCode.setText(preferenceHelper.getCountryCode());
        AndyUtils.generateLog(""+preferenceHelper.getProfilePicture());
        Glide.with(this)
                .load(preferenceHelper.getProfilePicture())
                .asBitmap()
                .placeholder(getResources().getDrawable(R.drawable.default_icon))
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        pbarProfileImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        pbarProfileImage.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(new BitmapImageViewTarget(imgUpdateUserPhoto){
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable cirimage = RoundedBitmapDrawableFactory.create(getResources(),resource);
                        cirimage.setCircular(true);
                        imgUpdateUserPhoto.setImageDrawable(cirimage);
                    }
                });
        tvUpdateCountryCode.setOnClickListener(this);
        btnUpdateProfile.setOnClickListener(this);
        imgUpdateUserPhoto.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tvUpdateCountryCode:
                openCountryCodeDialog();
                break;

            case R.id.btnUpdateProfile:
                if(isValidData()){
                    exitDialog = new CustomTitleDialog(UpdateProfileActivity.this , "Update Profile" , "Are you sure you want to update your Profile ?" ,
                            "YES", "NO") {

                        public void positiveResponse() {
                            showOldPasswordDialog("");
                            exitDialog.dismiss();
                        }

                        public void negativeResponse() {
                            exitDialog.dismiss();
                        }
                    };
                    exitDialog.show();

                }
                break;

            case R.id.imgUpdateUserPhoto:
                showPictureChooseDialog();
                break;

            default:
                AndyUtils.generateLog("no Action");
                break;
        }

    }

    private void showOldPasswordDialog(String add_token) {

        if(TextUtils.isEmpty(userUpdatePassword)){
            updateProfile(add_token);
        }
        else {
            dialogVerifyAccount = null;
            dialogVerifyAccount = new CustomDialogWithEdittext(this ,getString(R.string.dilaog_title_verify_account),
                    getString(R.string.dialog_hint_current_password), getString(R.string.dialog_button_yes) , getString(R.string.dialog_button_no)) {
                @Override
                public void yesUpdate(String currentPass) {
                    if (TextUtils.isEmpty(currentPass)) {
                        AndyUtils.showToast(UpdateProfileActivity.this, "Please enter a password.");
                    } else {
                        userOldPassword = currentPass;
                        updateProfile("");
                        dialogVerifyAccount.dismiss();
                    }
                }

                @Override
                public void NoUpdate() {
                    dialogVerifyAccount.dismiss();
                }
            };
            dialogVerifyAccount.show();
        }
    }

    public void updateProfile(String update_type) {

        HashMap<String , String> map = new HashMap<>();
        map.put(Const.URL , Const.ServiceType.UPDATE_PROFILE);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.NAME , userUpdateName);
        map.put(Const.Params.CONTACT_NO , userUpdateContactNo);
        map.put(Const.Params.COUNTRY_CODE , userCountryCode);
        map.put(Const.Params.EMAIL , userUpdateEmail);
        map.put(Const.Params.ADDRESS , userUpdateAddress);
        map.put("add_token" , update_type);
        if(!TextUtils.isEmpty(filePath)){
            map.put(Const.Params.PICTURE , filePath);
        }
        if(!TextUtils.isEmpty(userUpdatePassword)){
            map.put(Const.Params.NEW_PASS, userUpdatePassword);
            map.put(Const.Params.OLD_PASS, userOldPassword);
        }

        AndyUtils.showCustomProgressDialog(this , true);
        new MultiPartRequester(this , map , Const.ServiceCode.UPDATE_PROFILE , this);

    }


    private boolean isValidData() {

        userUpdateName = edtUpdateUserName.getText().toString().trim();
        userUpdateAddress = edtUpdateAddress.getText().toString().trim();
        userUpdateEmail = edtUpdateEmail.getText().toString().trim();
        userUpdatePassword = edtUpdateNewPassword.getText().toString().trim();
        userUpdateContactNo = edtUpdateContact.getText().toString().trim();
        userCountryCode = tvUpdateCountryCode.getText().toString().trim();

        if(TextUtils.isEmpty(userUpdateName)){
            AndyUtils.showToast(this , getString(R.string.toast_name_not_be_empty));
            return false;
        }
        else if(!Validation.isEmailValid(userUpdateEmail)){
            AndyUtils.showToast(this , getString(R.string.toast_invalid_email));
            return false;
        }
        else if(!TextUtils.isEmpty(userUpdatePassword) && Validation.isValidPasswordLength(userUpdatePassword)){
            AndyUtils.showToast(this , getString(R.string.toast_password_lenght));
            return false;
        }
        else if(!TextUtils.isEmpty(userUpdatePassword) && !Validation.isPasswordMatch(userUpdatePassword , edtUpdateConfirmPassword.getText().toString().trim())){
            AndyUtils.showToast(this , getString(R.string.toast_password_not_matched));
            edtUpdateConfirmPassword.setText("");
            return false;
        }
        else if(!Validation.isContactNoValid(userUpdateContactNo)){
            AndyUtils.showToast(this , getString(R.string.toast_invalid_contact));
            return false;
        }
        else if(TextUtils.isEmpty(userUpdateAddress)){
            AndyUtils.showToast(this , getString(R.string.toast_enter_an_address));
            return false;
        }
        else {
            return true;
        }
    }

    private void openCountryCodeDialog() {
        dialogCustomCountryCode = null;
        listCountryCode = dataParser.parseCountryCode();
        dialogCustomCountryCode = new CustomCountryCodeDialog(this ,listCountryCode ) {
            @Override
            public void onSelectCountryCode(View view, int position) {
                TextView tvCountryCode = (TextView)view.findViewById(R.id.tvCountryCode);
                String code = tvCountryCode.getText().toString().trim();
                tvUpdateCountryCode.setText(code);
                dialogCustomCountryCode.dismiss();
            }
        };
        dialogCustomCountryCode.show();
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        if(serviceCode == Const.ServiceCode.UPDATE_PROFILE && dataParser.isSuccess(response)){
            dataParser.parseUserData(response);
            if(i == 1) {
//                boolean x = preferenceHelper.getTokenPurchase().contains("Request successfully processed");
//                //AndyUtils.showToast(this, x ? "Transaction Successful" : "Transaction Failed");
//                exitDialog = new CustomTitleDialog(UpdateProfileActivity.this , "JimmieJobs Tokens" , x ? "Transaction Successful" : "Transaction Failed" ,
//                        "OK", "") {
//
//                    public void positiveResponse() {
//
//                        Intent show_map = new Intent(UpdateProfileActivity.this,MapActivity.class);
//                        startActivity(show_map);
//                    }
//
//                    public void negativeResponse() {
//                        exitDialog.dismiss();
//                    }
//                };

                //Temp Solution till PAYMENT API IS UP


                exitDialog.show();

                i = 0;
            }
            if(i == 3) {
                boolean x = preferenceHelper.getTokenPurchase().contains("Request successfully processed");
                //AndyUtils.showToast(this, x ? "Payment Successful": "Payment Failed");

                exitDialog = new CustomTitleDialog(UpdateProfileActivity.this , "JimmieJobs Payment" , x ? "Payment Successful": "Payment Failed" ,
                        "OK", "") {

                    public void positiveResponse() {
                        Intent show_map = new Intent(UpdateProfileActivity.this,MapActivity.class);
                        startActivity(show_map);
                    }

                    public void negativeResponse() {
                        exitDialog.dismiss();
                    }
                };
                exitDialog.show();


                i = 0;
            }
            else{
                //AndyUtils.showToast(this, "Profile successfully updated.");
            }
            setupAllRequiredData();
            //onBackPressed();
        }
    }


    /*Camera action here*/

    //It shows the dialog to select photo from gallery or camera....
    private void showPictureChooseDialog(){

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

    }


    //It choose photo from gallery...
    private void choosePhotoFromGallery(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, Const.PERMISSION_STORAGE_REQUEST_CODE);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, Const.CHOOSE_PHOTO);
        }

    }


    //It take photo from the camera....
    private void takePhotoFromCamera(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Const.PERMISSION_CAMERA_REQUEST_CODE);
        } else {
            Calendar cal = Calendar.getInstance();
            File file = new File(Environment.getExternalStorageDirectory(),(cal.getTimeInMillis() + ".jpg"));

            if(file.exists()){
                isDelete = file.delete();
                AndyUtils.generateLog("File operations"+isDelete);
            }
            else{
                try {
                    isDelete = file.createNewFile();
                    AndyUtils.generateLog("File operations"+isDelete);
                } catch (IOException e) {
                    AndyUtils.generateLog("Exception of file"+e);
                }
            }
            uri = Uri.fromFile(file);
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(cameraIntent, Const.TAKE_PHOTO);
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
        try{
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Const.CHOOSE_PHOTO && data != null) {
            compressImage = new CompressImage(this , Const.CHOOSE_PHOTO);
            compressImage.beginCrop(Uri.parse(compressImage.getCompressImagePath(data.getData())));

        }
        else if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Toast.makeText(UpdateProfileActivity.this, "Test", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == Const.TAKE_PHOTO) {
            if (ii == 1) {
                String cameraFilePath = compressImage.getCompressImagePath(uri);
                Toast.makeText(UpdateProfileActivity.this, cameraFilePath, Toast.LENGTH_SHORT).show();
            } else {
                compressImage = new CompressImage(this, Const.TAKE_PHOTO);
                if (uri != null) {
                    String cameraFilePath = compressImage.getCompressImagePath(uri);
                    if (compressImage.isBitmapCreated)
                        compressImage.beginCrop(Uri.parse(cameraFilePath));

                } else {
                    Toast.makeText(
                            this,
                            getResources().getString(
                                    R.string.toast_error_unable_to_select_image),
                            Toast.LENGTH_LONG).show();
                }
            }
        }else if (requestCode == Crop.REQUEST_CROP && data != null) {
                setCropedImage(compressImage.handleCrop(resultCode, data));
                //setupAllRequiredData();
            }

    }catch (Exception e) {

        }
        }



    private void setCropedImage(String picturePath){
        filePath = picturePath;
        Glide.with(this)
                .load(picturePath)
                .asBitmap()
                .placeholder(getResources().getDrawable(R.drawable.user_register))
                .into(new BitmapImageViewTarget(imgUpdateUserPhoto){
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable cirimage = RoundedBitmapDrawableFactory.create(getResources(),resource);
                        cirimage.setCircular(true);
                        imgUpdateUserPhoto.setImageDrawable(cirimage);
                    }
                });
    }
}
