package com.munye;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.munye.user.R;
import com.stripe.android.model.Card;
import com.munye.dialog.CustomTitleDialog;
import com.munye.dialog.ImageDialog;
import com.munye.model.ActiveJob;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.DataParser;
import com.munye.parse.HttpRequester;
import com.munye.parse.MultiPartRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.Formatter;
import com.munye.utils.RSACipher;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ActiveJobActivity extends ActionBarBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener {

    private ImageView imgViewActiveJobTypeIcon , imgeViewIssueImage , imgProviderImgActiveJobActivity;
    private TextView tvTitleActiveJobActivity , tvTypeActiveJobActivity , tvDateActiveJobActivity  ,tvDescriptionActiveJobActivity , tvAmountActiveJobActivity , tvAddressActiveJobActivity;
    private TextView tvProviderNameActiveJobActivity , tvMunyeStatus;
    private RatingBar ratingActiveJobProviderRatting;
    private Bundle data;
    private ActiveJob activeJob;
    private int position;
    private String brodcastResponse , requestState;
    private Button btnFeedbackAndCancel;
    private Dialog dialogInvoice;
    private TextView tvInvoiceJobAmount;
    private TextView tvInvoiceAdminPrice;
    private TextView tvInvoiceGrandTotal;
    private TextView tvAmountPayByCard;
    private TextView tvAmountPayByCash;
    private Button btnInvoiceOk, buyTokens, btnInvoiceCancel;
    private ImageDialog imageDialog;
    private CustomTitleDialog dialogCancelJob;
    protected String filePath = null;

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
    private String ctPayToken;
    private String ctCVV;
    private RSACipher rsaCipher;
    private String expiryDate;
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    private String file;


    private double jobAmount , adminPrice , total ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_job);
        initToolBar();
        initPreference();
        setToolBarTitle(getString(R.string.title_active_job));
        try{
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);

        initRequire();
        imgeViewIssueImage.setOnClickListener(this);
        data = getIntent().getExtras();
        activeJob = data.getParcelable("OBJECT");
        position = data.getInt("POSITION") ;
        tvTitleActiveJobActivity.setText(activeJob.getTitle());
        tvTitleActiveJobActivity.setTextColor(Color.parseColor(AndyUtils.getColorCode(position % 6, this)));
        tvDateActiveJobActivity.setText(Formatter.getDateInFormate(activeJob.getDate()));
        tvDescriptionActiveJobActivity.setText(activeJob.getDescription());
        tvAmountActiveJobActivity.setText(AndyUtils.getSymbolFromHex(activeJob.getCurrency())+Formatter.formateDigitAfterPoint(activeJob.getAmount()));
        tvAddressActiveJobActivity.setText(activeJob.getAddress());
        tvProviderNameActiveJobActivity.setText(activeJob.getProviderName());

        imgViewActiveJobTypeIcon.setColorFilter(Color.parseColor(AndyUtils.getColorCode(position % 6 , this)));
        Glide.with(this)
                .load(activeJob.getJobTypeIcon())
                .placeholder(getResources().getDrawable(R.drawable.placeholder_img))
                .skipMemoryCache(true)
                .dontAnimate()
                .into(imgViewActiveJobTypeIcon);
        Glide.with(this)
                .load(activeJob.getIssueImage())
                .placeholder(getResources().getDrawable(R.drawable.no_img))
                .skipMemoryCache(true)
                .into(imgeViewIssueImage);
        Glide.with(this)
                .load(activeJob.getProviderPicture())
                .asBitmap()
                .placeholder(getResources().getDrawable(R.drawable.default_icon))
                .into(new BitmapImageViewTarget(imgProviderImgActiveJobActivity){
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable cirimage = RoundedBitmapDrawableFactory.create(getResources(),resource);
                        cirimage.setCircular(true);
                        imgProviderImgActiveJobActivity.setImageDrawable(cirimage);
                    }
                });

        ratingActiveJobProviderRatting.setRating((float) activeJob.getProviderRate());


        /*It use for get service request type*/
        switch (activeJob.getRequestType()){
            case Const.JobRequestType.TYPE_REPAIR_MAINTENANCE:
                tvTypeActiveJobActivity.setText(getString(R.string.txt_repair_maintenance));
                break;

            case Const.JobRequestType.TYPE_INSTALLATION:
                tvTypeActiveJobActivity.setText(getString(R.string.txt_installation));
                break;

            default:
                AndyUtils.generateLog("No type");
                break;
        }



        /*It use for get status of tradesman*/
        switch (activeJob.getRequestStatus()){
            case Const.RequestStatus.PROVIDER_CONFIRM:
                tvMunyeStatus.setText(getString(R.string.status_tradesman_confirm));
                showButton(getString(R.string.txt_button_cancel_job));
                break;

            case Const.RequestStatus.ON_THE_WAY:
                tvMunyeStatus.setText(getString(R.string.status_tradesman_on_the_way));
                showButton(getString(R.string.txt_button_cancel_job));
                break;

            case Const.RequestStatus.ARRIVED:
                tvMunyeStatus.setText(getString(R.string.status_tradesman_arrived));
                hideButton();
                break;

            case Const.RequestStatus.JOB_STARTED:
                tvMunyeStatus.setText(getString(R.string.status_trdesman_started));
                hideButton();
                break;

            case Const.RequestStatus.JOB_DONE:
                tvMunyeStatus.setText(getString(R.string.status_job_done));
                requestState = "Job Done";
                break;

            default:
                AndyUtils.generateLog("no status");
                break;
        }


        btnFeedbackAndCancel.setOnClickListener(this);

        IntentFilter intentFilter = new IntentFilter(Const.PushStatus.PUSH_STATUS_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(providerStatusChnageReceiver , intentFilter);

    }catch (Exception e) {
        }
        }

    private void initRequire() {
        imgViewActiveJobTypeIcon = (ImageView)findViewById(R.id.imgViewActiveJobTypeIcon);
        imgeViewIssueImage = (ImageView)findViewById(R.id.imgeViewIssueImage);
        imgProviderImgActiveJobActivity = (ImageView)findViewById(R.id.imgProviderImgActiveJobActivity);
        tvTitleActiveJobActivity = (TextView)findViewById(R.id.tvTitleActiveJobActivity);
        tvTypeActiveJobActivity = (TextView)findViewById(R.id.tvTypeActiveJobActivity);
        tvDateActiveJobActivity = (TextView)findViewById(R.id.tvDateActiveJobActivity);
        tvDescriptionActiveJobActivity = (TextView)findViewById(R.id.tvDescriptionActiveJobActivity);
        tvAmountActiveJobActivity = (TextView)findViewById(R.id.tvAmountActiveJobActivity);
        tvAddressActiveJobActivity = (TextView)findViewById(R.id.tvAddressActiveJobActivity);
        tvProviderNameActiveJobActivity = (TextView)findViewById(R.id.tvProviderNameActiveJobActivity);
        tvMunyeStatus = (TextView)findViewById(R.id.tvMunyeStatus);
        ratingActiveJobProviderRatting = (RatingBar)findViewById(R.id.ratingActiveJobProviderRatting);
        btnFeedbackAndCancel = (Button)findViewById(R.id.btnFeedbackAndCancel);
    }



    private BroadcastReceiver providerStatusChnageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            brodcastResponse = intent.getStringExtra(Const.PushStatus.PUSH_MESSAGE);

            if(DataParser.parsePushMessage.getRequestId(brodcastResponse).equals(activeJob.getActiveJobId())){
                updateProviderStatus();
            }

        }
    };

    private void updateProviderStatus() {

        switch (DataParser.parsePushMessage.getPushId(brodcastResponse)){

            case Const.PushStatus.PROVIDER_ON_THE_WAY:
                tvMunyeStatus.setText(getString(R.string.status_tradesman_on_the_way));
                break;

            case Const.PushStatus.PROVIDER_ARRIVE:
                hideButton();
                tvMunyeStatus.setText(getString(R.string.status_tradesman_arrived));
                break;

            case Const.PushStatus.PROVIDER_START_JOB:
                hideButton();
                tvMunyeStatus.setText(getString(R.string.status_trdesman_started));
                break;

            case Const.PushStatus.JOB_DONE:
                requestState = "Job Done";
                break;

            case Const.PushStatus.PROVIDER_CANCEL_JOB:
                AndyUtils.showToast(this , "This job cancelled by JimmieJobs");
                onBackPressed();
                break;

            default:
                AndyUtils.generateLog("No action");
                break;
        }
    }

    private void showButton(String text){
        btnFeedbackAndCancel.setVisibility(View.VISIBLE);
        btnFeedbackAndCancel.setText(text);
        requestState = text;
    }

    private void hideButton(){
        btnFeedbackAndCancel.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(providerStatusChnageReceiver);
    }

    private void openPaymentDialog(final String amnt, final String opt){
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
        TextView t = (TextView) dialogAddCard.findViewById(R.id.txtDisclaimer);
        t.setText("You are about to pay "+amnt+" for the job done\n\nNOTE this amount will go the JimmeJobs Account and you will be able to claim it anytime, we also do not charge Admin Fees");
        setTextWatcher();

        btnSubmitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                String x = year+"".replace("20","");
                int y = Integer.parseInt(x);
                if(cardType.equals("Unknown")){
                    Toast.makeText(ActiveJobActivity.this, "Invalid Card", Toast.LENGTH_SHORT).show();
                }
                if(Integer.parseInt(edtCardMonth.getText().toString())>12){
                    Toast.makeText(ActiveJobActivity.this, "Invalid Expiry Month", Toast.LENGTH_SHORT).show();
                }
                if(Integer.parseInt(edtCardYear.getText().toString())+2000<y){

                    Toast.makeText(ActiveJobActivity.this, "Invalid Expiry Year", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialogAddCard.dismiss();
                    Intent intentShare = new Intent(ActiveJobActivity.this, UpdateProfileActivity.class);
                    intentShare.putExtra("share", opt);
                    intentShare.putExtra("amnt", amnt);
                    intentShare.putExtra("id", activeJob.getActiveJobId());
                    intentShare.putExtra("c_no", edtCreditCardNo.getText().toString().replace("-", ""));
                    intentShare.putExtra("cvv", edtCardCvv.getText().toString());
                    intentShare.putExtra("exp_yr", edtCardYear.getText().toString());
                    intentShare.putExtra("exp_m", edtCardMonth.getText().toString());
                    intentShare.putExtra("c_typ", cardType);
                    startActivity(intentShare);
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

                if(s.length() > 0 && !CODE_PATTERN.matcher(s).matches()){
                    String input = s.toString();
                    String numberOnly = keepNumberOnly(input);
                    String code = formatCardNo(numberOnly);
                    edtCreditCardNo.removeTextChangedListener(this);
                    edtCreditCardNo.setText(code);
                    edtCreditCardNo.setSelection(code.length());
                    edtCreditCardNo.addTextChangedListener(this);
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
            } else {
                return UNKNOWN;
            }
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId()== R.id.btnFeedbackAndCancel){

            if("Job Done".equalsIgnoreCase(requestState)){
                finish();
                Intent feedBackIntent = new Intent(this , FeedbackActivity.class);
                feedBackIntent.putExtra("FEEDBACK" , activeJob);
                feedBackIntent.putExtra("position" , position);
                feedBackIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(feedBackIntent);


            }
            else {

                openPaymentDialog(tvAmountActiveJobActivity.getText().toString().replace("R",""),"pay");

            }
        }

        else if(v.getId() == R.id.btnInvoiceOk){
            if(Integer.parseInt(preferenceHelper.getUserToken())<1){
                Toast.makeText(getApplicationContext(), "Transaction Successful", Toast.LENGTH_SHORT).show();
                closeInvoiceDialog();
            }

            else {
                updateProfile();
                showInvoiceDialog();
            }

        }
        else if(v.getId() == R.id.imgeViewIssueImage){
            showImage(activeJob.getIssueImage());
        }
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

    private void showInvoiceDialog(){
        if(dialogInvoice != null && dialogInvoice.isShowing()){
            return;
        }

        dialogInvoice = new Dialog(this);
        dialogInvoice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInvoice.setContentView(R.layout.dialog_invoice);
        WindowManager.LayoutParams params = dialogInvoice.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogInvoice.getWindow().setAttributes(params);
        dialogInvoice.setCancelable(false);
        TextView tvAccBalance = (TextView)dialogInvoice.findViewById(R.id.tvAccBalance);
        buyTokens = (Button)dialogInvoice.findViewById(R.id.btnInvoiceBuyTokens) ;
        btnInvoiceCancel = (Button)dialogInvoice.findViewById(R.id.btnInvoiceCancel) ;
        tvInvoiceJobAmount = (TextView)dialogInvoice.findViewById(R.id.tvInvoiceJobAmount);
        tvInvoiceAdminPrice = (TextView)dialogInvoice.findViewById(R.id.tvInvoiceAdminPrice);
        tvInvoiceGrandTotal = (TextView)dialogInvoice.findViewById(R.id.tvInvoiceGrandTotal);
        tvAmountPayByCard = (TextView)dialogInvoice.findViewById(R.id.tvAmountPayByCard);
        tvAmountPayByCash = (TextView)dialogInvoice.findViewById(R.id.tvAmountPayByCash);
        btnInvoiceOk = (Button)dialogInvoice.findViewById(R.id.btnInvoiceOk);
        buyTokens.setVisibility(View.INVISIBLE);
        btnInvoiceOk.setOnClickListener(this);
        total = Double.parseDouble(activeJob.getAmount());
        adminPrice = Double.parseDouble(activeJob.getAdminCharge());
        jobAmount = total - adminPrice;
        if(Integer.parseInt(preferenceHelper.getUserToken())<1){
            buyTokens.setVisibility(View.VISIBLE);

            buyTokens.setText("Your Account Has Run out of Tokens. Please Buy Tokens, You can also Earn Tokens By Sharing This App with your Facebook Friends");
        }
        buyTokens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActiveJobActivity.this, "Integrate Payment System", Toast.LENGTH_SHORT).show();
                closeInvoiceDialog();
            }
        });
        btnInvoiceCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeInvoiceDialog();
            }
        });
        tvAccBalance.setText(Integer.parseInt(preferenceHelper.getUserToken())+" Tokens");
        tvInvoiceJobAmount.setText("R "+Formatter.invoiceDigitFormater(String.valueOf(jobAmount)));
        tvInvoiceAdminPrice.setText("R "+Formatter.invoiceDigitFormater(String.valueOf(adminPrice)));
        tvInvoiceGrandTotal.setText("R "+Formatter.invoiceDigitFormater(String.valueOf(total)));
        tvAmountPayByCard.setText("R "+Formatter.invoiceDigitFormater(String.valueOf(adminPrice)));
        tvAmountPayByCash.setText("R "+Formatter.invoiceDigitFormater(String.valueOf(jobAmount)));

        dialogInvoice.show();
    }



    private void closeInvoiceDialog(){
        if(dialogInvoice != null && dialogInvoice.isShowing()){
            dialogInvoice.dismiss();
            dialogInvoice = null;
        }
    }



    private void showImage(String imgUrl){
        if(!TextUtils.isEmpty(imgUrl))
        {
            imageDialog = new ImageDialog(this ,imgUrl);
            imageDialog.show();
        }
    }



    private void showCancelJobDialog(){
        dialogCancelJob  = new CustomTitleDialog(this , getString(R.string.dialog_title_cancel_job),
                getString(R.string.dialog_message_are_you_sure),getString(R.string.dialog_button_yes) , getString(R.string.dialog_button_no)) {
            @Override
            public void positiveResponse() {
                cancelJob();
                removeCancelJobDialog();
            }

            @Override
            public void negativeResponse() {
                removeCancelJobDialog();
            }
        };
        dialogCancelJob.show();
    }


    private void removeCancelJobDialog(){
        if(dialogCancelJob != null && dialogCancelJob.isShowing()){
            dialogCancelJob.dismiss();
            dialogCancelJob = null;
        }
    }




    private void cancelJob() {
        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.CANCEL_JOB);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.REQUEST_ID , activeJob.getActiveJobId());

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this , map , Const.ServiceCode.CANCEL_JOB , Const.httpRequestType.POST , this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        if(serviceCode == Const.ServiceCode.CANCEL_JOB){
            if(dataParser.isSuccess(response)){
                Intent intentBack = new Intent();
                intentBack.putExtra("POSITION" , position);
                setResult(Const.ACTION_CANCEL_JOB , intentBack);
                onBackPressed();
            }
        }

    }
}
