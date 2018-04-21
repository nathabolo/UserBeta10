package com.munye;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.munye.user.R;
import com.munye.adapter.CardAdapter;
import com.munye.dialog.CustomTitleDialog;
import com.munye.model.CardDetail;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.stripe.android.model.Card;
import com.munye.utils.RSACipher;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class PaymentActivity extends ActionBarBaseActivity implements AsyncTaskCompleteListener, View.OnClickListener, CardAdapter.CardRemoveListener {

    private RecyclerView recyclerViewCardList;
    private LinearLayoutManager linearLayoutManager;
    private CardAdapter adapterCard;
    private ArrayList<CardDetail> listCard;
    private ArrayList<CardDetail> listAdapterCard;
    private CardView cradDefaultCard;
    private TextView tvRemoveDefaultCard;
    private TextView tvDefaultCardNo;
    private TextView tvNoSelectedCard;
    private TextView tvNoOtherCrads;
    private CustomTitleDialog dialogCardDelete;
    private Button btnAddNewCard;
    private Button btnSkipCard;
    private Bundle data;

    //Dialog part
    private ImageView imgViewCloseDialog;
    private EditText edtCreditCardNo , edtCardMonth , edtCardYear , edtCardCvv;
    private Button btnSubmitCard;
    private Dialog dialogAddCard;
    private String cardType;
    private static final String AMERICAN_EXPRESS = "American Express";
    private static final String DISCOVER = "Discover";
    private static final String JCB = "JCB";
    private static final String DINERS_CLUB = "Diners Club";
    private static final String VISA = "Visa";
    private static final String MASTERCARD = "MasterCard";
    private static final String UNKNOWN = "Unknown";
    private static final int ACTION_GET_CARD = 1;
    private static final int ACTION_ADD_CARD = 2;
    private static final int ACTION_DELETE_CARD = 3;
    private static final Pattern CODE_PATTERN = Pattern.compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
    private String ctPayToken;
    private String ctCVV;
    private RSACipher rsaCipher;
    private String expiryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initToolBar();
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);
        setToolBarTitle(getString(R.string.title_payment));

        cradDefaultCard = (CardView)findViewById(R.id.cradDefaultCard);
        tvRemoveDefaultCard = (TextView)findViewById(R.id.tvRemoveDefaultCard);
        tvNoSelectedCard = (TextView)findViewById(R.id.tvNoSelectedCard);
        tvNoOtherCrads = (TextView)findViewById(R.id.tvNoOtherCrads);
        tvDefaultCardNo = (TextView)findViewById(R.id.tvDefaultCardNo);
        btnAddNewCard = (Button)findViewById(R.id.btnAddNewCard);
        btnSkipCard = (Button)findViewById(R.id.btnSkipCard);


        listCard = new ArrayList<>();
        listAdapterCard = new ArrayList<>();
        recyclerViewCardList = (RecyclerView)findViewById(R.id.recyclerViewCardList);
        recyclerViewCardList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(PaymentActivity.this);
        recyclerViewCardList.setLayoutManager(linearLayoutManager);

        adapterCard = new CardAdapter(listAdapterCard , this);
        recyclerViewCardList.setAdapter(adapterCard);

        getAllSaveCards();

        tvRemoveDefaultCard.setOnClickListener(this);
        btnAddNewCard.setOnClickListener(this);
        btnSkipCard.setOnClickListener(this);

        data = getIntent().getExtras();
//        }

    }


    private void getAllSaveCards(){
        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.GET_CARD);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this, map , Const.ServiceCode.GET_CARD , Const.httpRequestType.POST , this);
    }


    /*It is use to parse response of card . */
    private void parseAvailableCards(String response , int action){

        if(dataParser.isSuccess(response)){
            if(action == ACTION_ADD_CARD){
                AndyUtils.showToast(PaymentActivity.this , "Card successfully added.");
            }
            else if(action == ACTION_DELETE_CARD){
                AndyUtils.showToast(PaymentActivity.this , "Card successfully deleted.");
            }
            listCard.clear();
            listCard = dataParser.parseCardDetail(response , listCard);
            if(listCard.isEmpty()){
                cradDefaultCard.setVisibility(View.GONE);
                tvNoSelectedCard.setVisibility(View.VISIBLE);
            }
            else {
                cradDefaultCard.setVisibility(View.VISIBLE);
                tvNoSelectedCard.setVisibility(View.GONE);
                tvDefaultCardNo.setText("************"+listCard.get(0).getCardNo());
            }

            if(listCard.size()<2){
                recyclerViewCardList.setVisibility(View.GONE);
                tvNoOtherCrads.setVisibility(View.VISIBLE);
            }
            else {
                listAdapterCard.clear();
                for(int i=1 ; i<listCard.size() ; i++){
                    listAdapterCard.add(listCard.get(i));
                }
                recyclerViewCardList.setVisibility(View.VISIBLE);
                tvNoOtherCrads.setVisibility(View.GONE);
                adapterCard.notifyDataSetChanged();
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvRemoveDefaultCard:
                showDeleteCardDialog( 0 , true);
                break;

            case R.id.btnAddNewCard:
                openAddCardDialog();
                break;

            case R.id.imgViewCloseDialog:
                closeAddCardDialog();
                break;

            case R.id.btnSubmitCard:
                if(isValidCardData())
                    saveCard();
                break;

            case R.id.btnSkipCard:
                goToMapActivity();
                break;

            default:
                //Default action here..
                break;
        }
    }




    private void deleteCard(int position , boolean isDefault){

        HashMap<String , String> map = new HashMap<>();
        map.put(Const.URL , Const.ServiceType.DELETE_CARD);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        if(isDefault){
            map.put(Const.Params.CUSTOMER_ID , listCard.get(0).getCustomerId());
        }
        else{
            map.put(Const.Params.CUSTOMER_ID , listCard.get(position + 1).getCustomerId());
        }


        new HttpRequester(this , map , Const.ServiceCode.DELETE_CARD , Const.httpRequestType.POST , this );
        AndyUtils.showCustomProgressDialog(this , false);

    }



    //It use for get click event of recyclerview components....
    @Override
    public void removeCrad(View v, int position) {

        showDeleteCardDialog(position , false);
    }

    @Override
    public void makeDefaultCard(View v, int position) {
        setCardAsDefault(position + 1);
    }



    //Use for show delete card alert dialog
    private void showDeleteCardDialog(final int position , final boolean isDefault){

        dialogCardDelete = new CustomTitleDialog(this ,getString(R.string.dialog_title_delete_card)  , getString(R.string.dialog_message_are_you_sure) , getString(R.string.dialog_button_yes),
                getString(R.string.dialog_button_no)) {
            @Override
            public void positiveResponse() {
                deleteCard(position , isDefault);
                dialogCardDelete.dismiss();
            }

            @Override
            public void negativeResponse() {
                dialogCardDelete.dismiss();
            }
        };
        dialogCardDelete.show();
    }



    private void openAddCardDialog(){
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
        imgViewCloseDialog.setOnClickListener(this);
        btnSubmitCard.setOnClickListener(this);


        dialogAddCard.show();
    }


    private void closeAddCardDialog(){
        if(dialogAddCard != null && dialogAddCard.isShowing()){
            dialogAddCard.dismiss();
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



    private boolean isValidCardData(){
        if(TextUtils.isEmpty(edtCreditCardNo.getText().toString().trim())){
            AndyUtils.showToast(this , getString(R.string.toast_enter_card_no));
            return false;
        }
        else if(TextUtils.isEmpty(edtCardMonth.getText().toString().trim())){
            AndyUtils.showToast(this , getString(R.string.toast_card_month_not_empty));
            return false;
        }
        else if(TextUtils.isEmpty(edtCardYear.getText().toString().trim())){
            AndyUtils.showToast(this , getString(R.string.toast_card_year_not_empty));
            return false;
        }
        else if(TextUtils.isEmpty(edtCardCvv.getText().toString().trim())){
            AndyUtils.showToast(this , getString(R.string.toast_card_cvv_not_empty));
            return false;
        }
        else {
            return true;
        }
    }


    private void saveCard(){
        AndyUtils.showCustomProgressDialog(this , false);

        Card card = new Card(edtCreditCardNo.getText().toString() , Integer.parseInt(edtCardMonth.getText().toString()),
                Integer.parseInt(edtCardYear.getText().toString()) , edtCardCvv.getText().toString());

        boolean validation = card.validateCard();

        if(validation){
            rsaCipher=new RSACipher();
            try {
                PublicKey publicKey= rsaCipher.loadPublicKey(preferenceHelper.getPublicKey());
                ctPayToken=rsaCipher.RSAEncrypt(card.getNumber(),publicKey);
                String expMonth= String.valueOf(card.getExpMonth());
                String expYear= String.valueOf(card.getExpYear()+2000);
                expiryDate= expMonth+expYear;
                ctCVV=rsaCipher.RSAEncrypt(card.getCVC(),publicKey);
                addCard();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
        else if(!card.validateNumber()){
            edtCreditCardNo.requestFocus();
            AndyUtils.showToast(this , getString(R.string.toast_card_no_invalid));
            AndyUtils.removeCustomProgressDialog();
        }
        else if(!card.validateExpMonth()){
            edtCardMonth.requestFocus();
            AndyUtils.showToast(this , getString(R.string.toast_month_invalid));
            AndyUtils.removeCustomProgressDialog();
        }
        else if(!card.validateExpYear()){
            edtCardYear.requestFocus();
            AndyUtils.showToast(this , getString(R.string.toast_year_invalid));
            AndyUtils.removeCustomProgressDialog();
        }
        else if(!card.validateCVC()){
            edtCardCvv.requestFocus();
            AndyUtils.showToast(this , getString(R.string.toast_cvv_invalid));
            AndyUtils.removeCustomProgressDialog();
        }
        else {
            AndyUtils.showToast(this , getString(R.string.toast_card_invalid));
            AndyUtils.removeCustomProgressDialog();
        }
    }




    private void addCard(){

        HashMap<String ,String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.ADD_CARD);
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.PAYMENT_TOKEN ,ctPayToken);
        map.put(Const.Params.EXPDATE ,expiryDate);
        map.put(Const.Params.CVV ,ctCVV);
        map.put(Const.Params.CARD_NO , edtCreditCardNo.getText().toString());

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this , map , Const.ServiceCode.ADD_CARD , Const.httpRequestType.POST , this);
    }


    private void setCardAsDefault(int position){

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.GET_CARD);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.CUSTOMER_ID , listCard.get(position).getCustomerId());

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this , map , Const.ServiceCode.GET_CARD ,Const.httpRequestType.POST, this);
    }



    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode){
            case Const.ServiceCode.GET_CARD:
                parseAvailableCards(response , ACTION_GET_CARD);
                break;


            case Const.ServiceCode.ADD_CARD:
                closeAddCardDialog();
                parseAvailableCards(response , ACTION_ADD_CARD);
                break;


            case Const.ServiceCode.DELETE_CARD:
                parseAvailableCards(response , ACTION_DELETE_CARD);
                break;

            default:
                //Default actions...
                break;

        }
    }


    private void goToMapActivity(){
        startActivity(new Intent(PaymentActivity.this , MapActivity.class));
        finish();
    }
}
