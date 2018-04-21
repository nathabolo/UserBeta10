package com.munye;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.munye.dialog.CustomTitleDialog;
import com.munye.user.R;
import com.munye.dialog.CustomDialog;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.parse.MultiPartRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.CompressImage;
import com.munye.utils.Const;
import com.munye.utils.Formatter;
import com.munye.utils.RSACipher;
import com.stripe.android.model.Card;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

public class CreateJobActivity extends ActionBarBaseActivity implements View.OnClickListener, View.OnTouchListener,  AsyncTaskCompleteListener {
    private ImageView imgViewPostJobType;
    private TextView tvPostJobType;
    private EditText edtJobTitle, edtDescription, edtLocationAddress, edtImagePath;
    private RadioGroup radioGroupServiceCategory;
    private ImageButton imgBtnEditAddress, imgBtnSelectImage;
    private Typeface customRadioButtonFont;
    private Button btnPostJob;
    private Bundle data;
    private String color, jobType = "0";
    private Uri uri = null;
    protected String filePath = null;
    protected String strJobTitle;
    protected String strDescription;
    public String selectedProviderId,selectedProviderEmail;
    private boolean isDelete;
    private CompressImage compressImage;
    private CustomDialog pendingAmountPayDialog;
    private ImageView imgViewCloseDialog;
    private EditText edtCreditCardNo , edtCardMonth , edtCardYear , edtCardCvv;
    private Button btnSubmitCard;
    private Dialog dialogAddCard;
    private String cardType;
    private static final String AMERICAN_EXPRESS = "AMERICAN EXPRESS";
    private static final String DISCOVER = "Discover";
    private static final String JCB = "JCB";
    private static final String DINERS_CLUB = "Diners Club";
    private static final String VISA = "VISA";
    private static final String MASTERCARD = "MASTERCARD";
    private static final String UNKNOWN = "Unknown";
    private static final int ACTION_GET_CARD = 1;
    private static final int ACTION_ADD_CARD = 2;
    private static final int ACTION_DELETE_CARD = 3;
    private static final Pattern CODE_PATTERN = Pattern.compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
    private CustomTitleDialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        initToolBar();
        setToolBarTitle(getString(R.string.title_create_job));
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);

        imgViewPostJobType = (ImageView) findViewById(R.id.imgViewPostJobType);
        tvPostJobType = (TextView) findViewById(R.id.tvPostJobType);
        edtJobTitle = (EditText) findViewById(R.id.edtJobTitle);
        edtLocationAddress = (EditText) findViewById(R.id.edtLocationAddress);
        edtImagePath = (EditText) findViewById(R.id.edtImagePath);
        imgBtnEditAddress = (ImageButton) findViewById(R.id.imgBtnEditAddress);
        imgBtnSelectImage = (ImageButton) findViewById(R.id.imgBtnSelectImage);
        imgBtnSelectImage.setVisibility(View.INVISIBLE);
        edtImagePath.setVisibility(View.INVISIBLE);
        radioGroupServiceCategory = (RadioGroup) findViewById(R.id.radioGroupServiceCategory);

        edtDescription = (EditText) findViewById(R.id.edtDescription);
        btnPostJob = (Button) findViewById(R.id.btnPostJob);

        customRadioButtonFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold.ttf");



        btnPostJob.setOnClickListener(this);
        edtDescription.setOnTouchListener(this);
        imgBtnEditAddress.setOnClickListener(this);
        imgBtnSelectImage.setOnClickListener(this);

        data = getIntent().getExtras();
        color = data.getString("COLOR");
        tvPostJobType.setText("Request Quote From Service Provider.");
        tvPostJobType.setTextColor(Color.parseColor(color));
        Glide.with(this).load(data.getString("IMAGE")).placeholder(R.drawable.placeholder_img).into(imgViewPostJobType);
        imgViewPostJobType.setColorFilter(Color.parseColor(color));
        edtLocationAddress.setText(data.getString("ADDRESS"));
        selectedProviderEmail = (data.getString("email"));
        selectedProviderId = (data.getString("PROVIDER_ID"));



    }
//    public void disclaimer(){
//        LayoutInflater li = LayoutInflater.from(CreateJobActivity.this);
//        View promptsView = li.inflate(R.layout.dialog_disclaimer, null);
//        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(CreateJobActivity.this);
//        alertDialogBuilder.setView(promptsView);
//        alertDialogBuilder.setPositiveButton("PROCEED",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        createJob();
//                    }
//                });
//        alertDialogBuilder.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.setCancelable(false);
//        alertDialog.show();
//    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnPostJob:
                if(Integer.parseInt(preferenceHelper.getUserToken())<1){
                    showInvoiceDialog();
                    AndyUtils.generateLog("Your Account Has Run out of Tokens. Please Buy Tokens, You can also Earn Tokens By Sharing This App with your Facebook Friends.");



                }else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Send Job Request");
                    builder.setIcon(R.drawable.app_icon);
                    builder.setMessage("Sending a Job Request to The Service Provider will cost you 1 Token\n\n Are you sure you want to send the job Request?");
                    builder.setCancelable(true);
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    createJob();
                                }
                            }
                            );

                    AlertDialog alert = builder.create();
                    alert.show();


                }
                break;

            case R.id.imgBtnEditAddress:
                edtLocationAddress.setEnabled(true);
                break;

            case R.id.imgBtnSelectImage:
                //openImageSelectionDialog();
                break;

            default:
                AndyUtils.generateLog("onclick default case");
                break;

        }

    }

    private void showInvoiceDialog() {


        final Dialog dialogIBuyTokens = new Dialog(this);
        dialogIBuyTokens.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogIBuyTokens.setContentView(R.layout.user_buy_tokens_dialog);
        WindowManager.LayoutParams params = dialogIBuyTokens.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogIBuyTokens.getWindow().setAttributes(params);
        dialogIBuyTokens.setCancelable(false);
        TextView tvAccBalance = (TextView) dialogIBuyTokens.findViewById(R.id.tvTokenBalance);
        Button btnInvoiceOk = (Button) dialogIBuyTokens.findViewById(R.id.btnInvoiceOk);
        Button btnInvoiceCancel = (Button) dialogIBuyTokens.findViewById(R.id.btnInvoiceCancel);

        btnInvoiceOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(preferenceHelper.getC_NO().length()<16){
//                    AndyUtils.generateLog("Sorry, No Valid Credit/Debit Card has been registered to this Account");
//                }
//                else {
                    final Dialog buyTokensDialog = new Dialog(CreateJobActivity.this);
                    buyTokensDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    buyTokensDialog.setContentView(R.layout.buy_tokens);
                    buyTokensDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    Button btnSubmit = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceOk);
                    Button btnCancel = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceCancel);
                    final EditText key = (EditText) buyTokensDialog.findViewById(R.id.verificationKey);

                    TextView txtAdminPrice = (TextView) buyTokensDialog.findViewById(R.id.msg);
                    txtAdminPrice.setText("Select the Amount of Tokens you want to buy.");

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            openAddCardDialog(10,"buy_tokens");

                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intentShare = new Intent(CreateJobActivity.this, UpdateProfileActivity.class);

                            openAddCardDialog(20,"buy_tokens");

                        }
                    });
                    buyTokensDialog.show();

                }

           // }
        });

        btnInvoiceCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIBuyTokens.dismiss();
            }
        });
        tvAccBalance.setText("Insufficient Token Balance\nAvailable: ("+preferenceHelper.getUserToken()+" Tokens)\n\n\nAt least 1 Token is required in order to send a job to this service Provider.");

        dialogIBuyTokens.show();
    }

    private void openAddCardDialog(final int amnt,final String opt){

        try{
            if(dialogAddCard != null && dialogAddCard.isShowing()){
                return;
            }

            dialogAddCard = new Dialog(this);
            dialogAddCard.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogAddCard.setContentView(R.layout.dialog_add_card);
            WindowManager.LayoutParams params = dialogAddCard.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            dialogAddCard.getWindow().setAttributes(params);
            dialogAddCard.setCancelable(false);

            imgViewCloseDialog = (ImageView)dialogAddCard.findViewById(R.id.imgViewCloseDialog);
            edtCreditCardNo = (EditText) dialogAddCard.findViewById(R.id.edtCreditCardNo);
            edtCardMonth = (EditText) dialogAddCard.findViewById(R.id.edtCardMonth);
            edtCardYear = (EditText) dialogAddCard.findViewById(R.id.edtCardYear);
            edtCardCvv = (EditText) dialogAddCard.findViewById(R.id.edtCardCvv);
            btnSubmitCard = (Button) dialogAddCard.findViewById(R.id.btnSubmitCard);
            setTextWatcher();

            btnSubmitCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        String x = year + "".replace("20", "");
                        int y = Integer.parseInt(x);
                        if (cardType.equals("Unknown")) {
                            Toast.makeText(CreateJobActivity.this, "Invalid Card", Toast.LENGTH_SHORT).show();
                        }
                        if (Integer.parseInt(edtCardMonth.getText().toString()) > 12) {
                            Toast.makeText(CreateJobActivity.this, "Invalid Expiry Month", Toast.LENGTH_SHORT).show();
                        }
                        if (Integer.parseInt(edtCardYear.getText().toString()) + 2000 < y) {

                            Toast.makeText(CreateJobActivity.this, "Invalid Expiry Year", Toast.LENGTH_SHORT).show();
                        } else {

                            exitDialog = new CustomTitleDialog(CreateJobActivity.this , "Make Payment" , "Pay for Tokens ?" ,
                                    "YES", "NO") {

                                public void positiveResponse() {
                                    dialogAddCard.dismiss();
                                    Intent intentShare = new Intent(CreateJobActivity.this, UpdateProfileActivity.class);
                                    intentShare.putExtra("share", opt);
                                    intentShare.putExtra("amnt", amnt);
                                    intentShare.putExtra("c_no", edtCreditCardNo.getText().toString().replace("-", ""));
                                    intentShare.putExtra("cvv", edtCardCvv.getText().toString());
                                    intentShare.putExtra("exp_yr", edtCardYear.getText().toString());
                                    intentShare.putExtra("exp_m", edtCardMonth.getText().toString());
                                    intentShare.putExtra("c_typ", cardType);
                                    startActivity(intentShare);
                                }


                                public void negativeResponse() {
                                    exitDialog.dismiss();
                                }
                            };
                            exitDialog.show();


                        }
                    }catch (Exception e){
                        Toast.makeText(CreateJobActivity.this, "Input Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            imgViewCloseDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddCard.dismiss();
                }
            });
            dialogAddCard.show();
        }catch (Exception e) {
        }
    }


    private void setTextWatcher(){
        edtCreditCardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do with text..
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(TextUtils.isEmpty(s.toString())){
                    edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(null , null , null , null);
                }

                cardType = getCardType(s.toString());
                setCardTypeDrawable(cardType);

                if(edtCreditCardNo.getText().toString().length() == 19){
                    edtCardMonth.requestFocus();
                }

            }


            @Override
            public void afterTextChanged(Editable s) {
                try{
                    if(s.length() > 0 && !CODE_PATTERN.matcher(s).matches()){
                        String input = s.toString();
                        String numberOnly = keepNumberOnly(input);
                        String code = formatCardNo(numberOnly);
                        edtCreditCardNo.removeTextChangedListener(this);
                        edtCreditCardNo.setText(code);
                        edtCreditCardNo.setSelection(code.length());
                        edtCreditCardNo.addTextChangedListener(this);
                    }

                }catch (Exception e) {
                }
            }


            private String keepNumberOnly(CharSequence s){
                return s.toString().replaceAll("[^0-9]", "");
            }

            private String formatCardNo(CharSequence s){

                int groupDigits = 0;
                String cardNo = "";
                int sSize = s.length();
                for (int i = 0; i < sSize; ++i) {
                    cardNo += s.charAt(i);
                    ++groupDigits;
                    if (groupDigits == 4) {
                        cardNo += "-";
                        groupDigits = 0;
                    }
                }
                return cardNo;
            }
        });


        edtCardYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do with text
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edtCardYear.getText().toString().length() == 2){
                    edtCardCvv.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do with text
            }
        });


        edtCardMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do with text..
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(edtCardMonth.getText().toString().length() == 2){
                    edtCardYear.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do with text..
            }
        });
    }


    private void setCardTypeDrawable(String cradTypes){
        try{
            if(cradTypes.equals(VISA)){
                edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(
                        ResourcesCompat.getDrawable(getResources(), R.drawable
                                .creditcard_visa, null)
                        , null,
                        null, null);
            }
            else if(cradTypes.equals(MASTERCARD)){
                edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(
                        ResourcesCompat.getDrawable(getResources(), R.drawable
                                .creditcard_mastercard, null)
                        , null,
                        null, null);
            }
            else if(cradTypes.equals(DISCOVER)){
                edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(
                        ResourcesCompat.getDrawable(getResources(), R.drawable
                                .creditcard_discover, null)
                        , null,
                        null, null);
            }
            else if (cradTypes.equals(AMERICAN_EXPRESS)){
                edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(
                        ResourcesCompat.getDrawable(getResources(), R.drawable
                                .creditcard_amex, null)
                        , null,
                        null, null);
            }
            else {
                edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, null, null);
            }
        }catch (Exception e) {
        }
    }


    private String getCardType(String prifix){

        if(TextUtils.isEmpty(prifix)){
            return UNKNOWN;
        }
        else {
            if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_VISA)) {
                return VISA;
            } else if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_AMERICAN_EXPRESS)) {
                return AMERICAN_EXPRESS;
            } else if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_DISCOVER)) {
                return DISCOVER;
            } else if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_DINERS_CLUB)) {
                return DINERS_CLUB;
            } else if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_MASTERCARD)) {
                return MASTERCARD;
            } else if (AndyUtils.hasAnyPrifix(prifix, Card.PREFIXES_JCB)){
                return JCB;
            }


            else {
                return UNKNOWN;
            }
        }
    }

    /*It is use for handle inner scroll of description box*/
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                v.getParent().requestDisallowInterceptTouchEvent(false);
                break;

            default:
                AndyUtils.generateLog("Action");
                break;
        }
        return false;
    }



    private void openImageSelectionDialog() {

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
                                AndyUtils.generateLog("No selection");
                                break;
                        }
                    }
                });
        pictureDialog.show();

    }

    private void choosePhotoFromGallery() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, Const.PERMISSION_STORAGE_REQUEST_CODE);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, Const.CHOOSE_PHOTO);
        }

    }

    private void takePhotoFromCamera() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Const.PERMISSION_CAMERA_REQUEST_CODE);
        } else {
            Calendar cal = Calendar.getInstance();
            File file = new File(Environment.getExternalStorageDirectory(), (cal.getTimeInMillis() + ".jpg"));

            if(file.exists()){
                isDelete =  file.delete();
                AndyUtils.generateLog("File operation"+isDelete);
            }
            else {
                try {
                    isDelete = file.createNewFile();
                    AndyUtils.generateLog("File operation"+isDelete);
                } catch (IOException e) {
                    AndyUtils.generateLog("File creation exception"+e);
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
        switch (requestCode) {
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
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Const.CHOOSE_PHOTO) {
            if (data != null) {
                Uri uriChoosePhoto = data.getData();
                compressImage = new CompressImage(this , Const.CHOOSE_PHOTO);
                filePath = compressImage.getRealPathFromURI(Uri.parse(compressImage.getCompressImagePath(uriChoosePhoto)));
                edtImagePath.setText(displayFileName(filePath));

            } else {
                Toast.makeText(
                        this,
                        getResources().getString(
                                R.string.toast_error_unable_to_select_image),
                        Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == Const.TAKE_PHOTO) {
            if (uri != null) {
                compressImage = new CompressImage(this , Const.TAKE_PHOTO);
                filePath = compressImage.getRealPathFromURI(Uri.parse(compressImage.getCompressImagePath(uri)));

                if(compressImage.isBitmapCreated)
                    edtImagePath.setText(displayFileName(filePath));
            }
        } else {
            AndyUtils.showToast(this, getString(R.string.toast_error_unable_to_select_image));
        }

    }


    /*it is use to display only file name in upload imagebox*/
    private String displayFileName(String path){
        int index = filePath.lastIndexOf("/") + 1;
        return path.substring(index , path.length());
    }


    private void createJob() {
        try{
        strJobTitle = edtJobTitle.getText().toString().trim();
        strDescription = edtDescription.getText().toString().trim();
        if (TextUtils.isEmpty(strJobTitle)) {
            AndyUtils.showToast(this, getString(R.string.toast_job_title_required));
        } else if (TextUtils.isEmpty(strDescription)) {
            AndyUtils.showToast(this , getString(R.string.toast_description_required));
        } else {

            HashMap<String, String> map = new HashMap<>();

            map.put(Const.URL, Const.ServiceType.UPDATE_PROFILE);
            map.put(Const.Params.IMAGE, filePath);
            map.put(Const.Params.ID, preferenceHelper.getId());
            map.put(Const.Params.TOKEN, preferenceHelper.getToken());
            map.put(Const.Params.LATITUDE, data.getString("LATITUDE"));
            map.put(Const.Params.LONGITUDE, data.getString("LONGITUDE"));
            map.put(Const.Params.ADDRESS, edtLocationAddress.getText().toString().trim());
            map.put(Const.Params.JOB_TITLE, strJobTitle);
            map.put(Const.Params.JOB_TYPE, jobType);
            map.put(Const.Params.DESCRIPTION, edtDescription.getText().toString().trim());
            map.put(Const.Params.TYPES, data.getString("TYPE"));
            map.put(Const.Params.TIME_ZONE , preferenceHelper.getTimeZone());
            map.put("selected_provider_id" , selectedProviderId);
            map.put("add_card" , "send_request");
            map.put("",preferenceHelper.getEmail());
            AndyUtils.showCustomProgressDialog(this , false);
            new MultiPartRequester(this, map, Const.ServiceCode.CREATE_REQUEST, this);
        }

}catch (Exception e) {
    Toast.makeText(CreateJobActivity.this, ""+e, Toast.LENGTH_SHORT).show();
}
    }


    private void checkPendingAmount(){
        double pendingAmout = Double.parseDouble(preferenceHelper.getPendingAmount());
        if(pendingAmout > 0)
            showPendingAmountDialog(pendingAmout);
    }


    private void showPendingAmountDialog(double amount){
        String amountToDisplay = Formatter.invoiceDigitFormater(String.valueOf(amount))+"$";
        String content = getString(R.string.dialog_message_pending_payment)+" "+amountToDisplay;
        String buttonPay = getString(R.string.dialog_button_pay)+" "+amountToDisplay;
        pendingAmountPayDialog = new CustomDialog(this , content, buttonPay , getString(R.string.dialog_button_cancel) , false) {
            @Override
            public void positiveButton() {
                payOldRequest();
            }

            @Override
            public void negativeButton() {
                closePendingAmountDialog();
            }
        };
        pendingAmountPayDialog.show();
    }


    private void closePendingAmountDialog(){
        if(pendingAmountPayDialog != null && pendingAmountPayDialog.isShowing()){
            pendingAmountPayDialog.dismiss();
            pendingAmountPayDialog = null;
        }
    }


    private void payOldRequest(){

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.PAY_OLD_REQUEST);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());

        AndyUtils.showCustomProgressDialog(this ,false);
        new HttpRequester(this , map , Const.ServiceCode.PAY_OLD_REQUEST , Const.httpRequestType.POST , this);
    }

    public void updateProfile() {

        HashMap<String , String> map = new HashMap<>();
        map.put(Const.URL , Const.ServiceType.UPDATE_PROFILE);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.NAME , preferenceHelper.getUserName());
        map.put(Const.Params.CONTACT_NO , preferenceHelper.getContactNo());
        map.put(Const.Params.COUNTRY_CODE , preferenceHelper.getCountryCode());
        map.put(Const.Params.EMAIL , preferenceHelper.getEmail());
        map.put(Const.Params.ADDRESS , preferenceHelper.getAddress());
        if(!TextUtils.isEmpty(filePath)){
            map.put(Const.Params.PICTURE , filePath);
        }
       // if(!TextUtils.isEmpty(userUpdatePassword)){
            map.put(Const.Params.NEW_PASS, "");
            map.put(Const.Params.OLD_PASS, "");
       // }

        AndyUtils.showCustomProgressDialog(this , true);
        new MultiPartRequester(this , map , Const.ServiceCode.UPDATE_PROFILE , this);

    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
try{
        AndyUtils.removeCustomProgressDialog();
        if(serviceCode == Const.ServiceCode.UPDATE_PROFILE && dataParser.isSuccess(response)){
            dataParser.parseUserData(response);
            onBackPressed();
        }else {
            closePendingAmountDialog();
            if (serviceCode == Const.ServiceCode.CREATE_REQUEST && dataParser.isSuccess(response)) {
                updateProfile();
                AndyUtils.showToast(this, getString(R.string.toast_job_create_success));
                Intent intentMap = new Intent(CreateJobActivity.this, MapActivity.class);
                intentMap.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentMap);

            } else if (serviceCode == Const.ServiceCode.PAY_OLD_REQUEST && dataParser.isSuccess(response)) {
                dataParser.parseOldRequstPay(response);
            } else {
                if (dataParser.isPandingPayment(response)) {
                    checkPendingAmount();
                }
            }
        }

    }catch (Exception e) {
}
    }
}
