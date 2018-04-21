package com.munye;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.munye.user.R;
import com.stripe.android.model.Card;
import com.munye.adapter.GooglePlacesAutocompleteAdapter;
import com.munye.adapter.ProviderTypeAdapter;
import com.munye.component.CustomMapView;
import com.munye.dialog.CustomDialog;
import com.munye.dialog.CustomTitleDialog;
import com.munye.model.Provider;
import com.munye.model.ProviderType;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.parse.MultiPartRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.Formatter;
import com.munye.utils.LocationHelper;
import com.munye.utils.RSACipher;
import com.munye.utils.RecyclerViewTouchListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class MapActivity extends ActionBarBaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, LocationHelper.OnLocationReceived, OnMapReadyCallback, AdapterView.OnItemClickListener, AsyncTaskCompleteListener, RecyclerViewTouchListener.ClickListener {

    private DrawerLayout drawer;
    private AutoCompleteTextView actvplaceAutocomplete;
    private ImageButton btnMyLocation;
    private EditText searchItem;
    private ImageView imgNavHeaderUserImage ;
    private ImageView imgViewClearSearch;
    private TextView txtresults  , tvButtonSetLocation , tvNavHeaderUserName,tvTokenBalance;
    private String selectedAddress , strAddress , selectedProviderName , selectedProviderType , imageUrlProviderType , iconColor;
    private CustomMapView mapView;
    private GoogleMap map;
    private GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter;
    private LocationHelper locationHelper;
    protected Location myLocation;
    private LatLng curretLatLng , myLocationLatlng;
    private HashMap<Integer , Marker> nearProviderMarker;
    private ArrayList<Provider> listProvider;
    private ArrayList<ProviderType> listProviderType;
    private ArrayList<ProviderType> listServiceTypes;
    private MarkerOptions markerOptions;
    private Address address;
    private RecyclerView recyclerViewProviderTypeList;
    private ProviderTypeAdapter providerTypeAdapter;
    private GridLayoutManager gridLayoutManager;
    private boolean isProviderAvailable = false;
    private View viewHeader;
    private CustomTitleDialog exitDialog , logoutDialog;
    private CustomDialog pendingAmountDialog;
    private Spinner serviceSpinner;
    private String spinnerValue;
    public String selectedProviderId = "0";

    //Dialog part
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
    private String msg;
    private EditText editSubject, editText;
    private AssistStructure.ViewNode edtEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        try{

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initToolBar();
       // setToolBarTitle(getString(R.string.app_name));
        checkLocationPermission();
        serviceSpinner = (Spinner) findViewById(R.id.service_spinner1);
        searchItem = (EditText)findViewById(R.id.search_item) ;
        imgBtnToolbarBack.setVisibility(View.INVISIBLE);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        imgBtnDrawerToggle.setOnClickListener(this);
        tvToolbarTitle.setOnClickListener(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        viewHeader = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        //navigation Header items....
        imgNavHeaderUserImage = (ImageView)viewHeader.findViewById(R.id.imgNavHeaderUserImage);
        tvNavHeaderUserName = (TextView)viewHeader.findViewById(R.id.tvNavHeaderUserName);
        tvTokenBalance = (TextView)viewHeader.findViewById(R.id.tvTokenBalance);
        imgNavHeaderUserImage.setOnClickListener(this);

        //Searchview pannel..
        actvplaceAutocomplete = (AutoCompleteTextView)findViewById(R.id.tvplaceAutocomplete);
        actvplaceAutocomplete.setFocusable(false);
        btnMyLocation = (ImageButton)findViewById(R.id.btnMyLocation);
        //imgViewSelectedProviderType = (ImageView)findViewById(R.id.imgSelectedProviderType);
        // tvHireExpertText = (TextView)findViewById(R.id.tvHireExpertText);
        tvButtonSetLocation = (TextView) findViewById(R.id.tvButtonSetLocation);
        txtresults = (TextView) findViewById(R.id.txtresults);
        imgViewClearSearch = (ImageView)findViewById(R.id.imgViewClearSearch);
        searchItem = (EditText) findViewById(R.id.search_item);
       // greetings();
        startService(new Intent(this, RealTimeService.class));


        imgViewClearSearch.setOnClickListener(this);

//        btnSubmitCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveCard();
//            }
//        });

        searchItem.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                getFilteredProvider(curretLatLng, "search" , searchItem.getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }


            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


            }
        });



        serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try{

                        selectedProviderType = listProviderType.get(position).getId();
                        selectedProviderType = listProviderType.get(position).getId();
                        selectedProviderName = listProviderType.get(position).getName();
                        imageUrlProviderType = listProviderType.get(position).getPicture();
                        iconColor = AndyUtils.getColorCode(position % 6, getApplicationContext());
                        spinnerValue = listProviderType.get(position-1).getId();
                        Toast.makeText(MapActivity.this, listProviderType.get(position-1).getName(), Toast.LENGTH_SHORT).show();
                        getFilteredProvider(curretLatLng, spinnerValue, "");
                        hideProviderType();

                }catch (Exception e) {
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        //Map part...
        locationHelper = new LocationHelper(this);
        locationHelper.setLocationReceivedLister(this);
        mapView = (CustomMapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        googlePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(this , R.layout.list_item_place);
        actvplaceAutocomplete.setAdapter(googlePlacesAutocompleteAdapter);

        actvplaceAutocomplete.setOnItemClickListener(this);
        btnMyLocation.setOnClickListener(this);
        tvButtonSetLocation.setOnClickListener(this);

        nearProviderMarker = new HashMap<>();

        listProviderType = new ArrayList<>();
        getTotalTypesOfProvider();

        //Provider type Recycler view part...
        recyclerViewProviderTypeList = (RecyclerView)findViewById(R.id.recyclerViewProviderTypeList);
        recyclerViewProviderTypeList.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(MapActivity.this , 3);
        recyclerViewProviderTypeList.setLayoutManager(gridLayoutManager);
        providerTypeAdapter = new ProviderTypeAdapter(listProviderType, this);
        recyclerViewProviderTypeList.setAdapter(providerTypeAdapter);
        recyclerViewProviderTypeList.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerViewProviderTypeList, this));

        //getProvider();

       // getPandingPaymentDetail();
    }catch (Exception e) {
        }
        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /* It handles selection of drawer content */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile_update) {
            startActivity(new Intent(MapActivity.this , UpdateProfileActivity.class));
        } else if (id == R.id.nav_view_quotes) {
            startActivity(new Intent(MapActivity.this , ViewQuotesActivity.class));

        } else if (id == R.id.nav_my_jobs) {
            startActivity(new Intent(MapActivity.this , MyJobsActivity.class));

        } else if (id == R.id.help) {
            help();
            //sendEmail();


        } else if (id == R.id.nav_logout) {
            showLogoutDialog();
        }
        else if(id == R.id.nav_payment){
            //shareMunye();
            shareAppLinkViaFacebook();
            doShareLink();
        }

        else if(id == R.id.buyTokens){
            final Dialog buyTokensDialog = new Dialog(MapActivity.this);
            buyTokensDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            buyTokensDialog.setContentView(R.layout.buy_tokens);
            buyTokensDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
            Button btnSubmit = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceOk);
            Button btnCancel = (Button) buyTokensDialog.findViewById(R.id.btnInvoiceCancel);
            final EditText key = (EditText) buyTokensDialog.findViewById(R.id.verificationKey);

            TextView txtAdminPrice = (TextView) buyTokensDialog.findViewById(R.id.msg);
            //txtAdminPrice.setText("Select the Amount of Tokens you want to buy");

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                        exitDialog = new CustomTitleDialog(MapActivity.this , "Buy 5 Tokens" , "Are you sure you want to buy Tokens?\n\nTo Buys Tokens you are required to enter details of your Debit/Credit Card for payment  and proceed" ,
                                "PROCEED", "NO") {

                            public void positiveResponse() {
                                buyTokensDialog.dismiss();
                                exitDialog.dismiss();
                                openAddCardDialog(10,"buy_tokens");
                            }


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

                    exitDialog = new CustomTitleDialog(MapActivity.this , "Buy 10 Tokens" , "Are you sure you want to buy Tokens?\n\nTo Buys Tokens you are required to enter details of your Debit/Credit Card for payment  and proceed" ,
                            "PROCEED", "NO") {

                        public void positiveResponse() {
                            buyTokensDialog.dismiss();
                            exitDialog.dismiss();
                            openAddCardDialog(12,"buy_tokens");
                        }


                        public void negativeResponse() {
                            exitDialog.dismiss();
                        }
                    };
                    exitDialog.show();

                }
            });
            buyTokensDialog.show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void doShareLink() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        Intent chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_to_earn_tokens));

        // for 21+, we can use EXTRA_REPLACEMENT_EXTRAS to support the specific case of Facebook
        // (only supports a link)
        // >=21: facebook=link, other=text+link
        // <=20: all=link
        String link = "http://jimmiejob.com/index.php/";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String text = "Click to earn tokens with jimmie jobs";
            shareIntent.putExtra(Intent.EXTRA_TEXT, text + " " + link);
            Bundle facebookBundle = new Bundle();
            facebookBundle.putString(Intent.EXTRA_TEXT, link);
            Bundle replacement = new Bundle();
            replacement.putBundle("com.facebook.katana", facebookBundle);
            chooserIntent.putExtra(Intent.EXTRA_REPLACEMENT_EXTRAS, replacement);
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, link);
        }

        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(chooserIntent);

    }

    // Converting File to Base64.encode String type using Method
    public String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile= "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile =  output.toString();
        }
        catch (FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
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
                        Toast.makeText(MapActivity.this, "Invalid Card", Toast.LENGTH_SHORT).show();
                    }
                    if (Integer.parseInt(edtCardMonth.getText().toString()) > 12) {
                        Toast.makeText(MapActivity.this, "Invalid Expiry Month", Toast.LENGTH_SHORT).show();
                    }
                    if (Integer.parseInt(edtCardYear.getText().toString()) + 2000 < y) {

                        Toast.makeText(MapActivity.this, "Invalid Expiry Year", Toast.LENGTH_SHORT).show();
                    } else {

                        exitDialog = new CustomTitleDialog(MapActivity.this , "Make Payment" , "Pay for Tokens ?" ,
                                "YES", "NO") {

                            public void positiveResponse() {
                                dialogAddCard.dismiss();
                                Intent intentShare = new Intent(MapActivity.this, UpdateProfileActivity.class);
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
                    Toast.makeText(MapActivity.this, "Input Error", Toast.LENGTH_SHORT).show();
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



    private void openViewCardDialog(){
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
        edtCreditCardNo.setText(preferenceHelper.getC_NO());
        edtCardCvv.setText(preferenceHelper.getCVV());
        edtCardMonth.setText(preferenceHelper.getEXP_M());
        edtCardYear.setText(preferenceHelper.getEXP_YR());

        edtCreditCardNo.setEnabled(false);
        edtCardMonth.setEnabled(false);
        edtCardYear.setEnabled(false);
        edtCardCvv.setEnabled(false);
        edtCreditCardNo.setCompoundDrawablesWithIntrinsicBounds(null , null , null , null);
        char first = (edtCreditCardNo.getText().toString()).charAt(0);
        cardType = getCardType(first+"");
        setCardTypeDrawable(preferenceHelper.getCTYP());

        btnSubmitCard.setVisibility(View.INVISIBLE);
        setTextWatcher();
        imgViewCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddCard.dismiss();
            }
        });

        dialogAddCard.show();
    }catch (Exception e) {
          //  openAddCardDialog();
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
        /*AndyUtils.showCustomProgressDialog(this , false);*/

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

                Toast.makeText(MapActivity.this, "Card Will be Added Here", Toast.LENGTH_SHORT).show();
                AndyUtils.removeCustomProgressDialog();

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



    public void help(){
        try{
        LayoutInflater li = LayoutInflater.from(MapActivity.this);
        View promptsView = li.inflate(R.layout.dialog_help_user, null);
        final EditText x = (EditText) promptsView.findViewById(R.id.editText) ;
        Button y = (Button) promptsView.findViewById(R.id.emailSupport);
        editSubject = (EditText)findViewById(R.id.editSubject);
       editText = (EditText)findViewById(R.id.editText);

        y.setOnClickListener(this);
            //sendEmail();

            y.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(x.getText().length()<5){

                        Toast.makeText(MapActivity.this, "Send Email to Support", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MapActivity.this, "Sending Email to Support", Toast.LENGTH_SHORT).show();
                        x.setText("");
                    }

                }



            });


        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MapActivity.this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }catch (Exception e) {
        }

        }

    private void sendEmail(){

        //Getting content for email
        // String email = editTextEmail.getText().toString().trim();
        String subject = editSubject.getText().toString().trim();
        String message = editText.getText().toString().trim();

        //Creating SendMail object
        SendMail sm = new SendMail(this, subject, message);

        //Executing sendmail to send email
        sm.execute();


    }


//    private void shareMunye(){
//        try{
//        Intent intentShare = new Intent(MapActivity.this , UpdateProfileActivity.class);
//        intentShare.putExtra("share" , "share");
//        startActivity(intentShare);
//    }catch (Exception e) {
//        }
//
//        }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgBtnDrawerToggle:
            case R.id.tvToolBarTitle:
                drawer.openDrawer(GravityCompat.START);
                break;

            case R.id.btnMyLocation:
                if(myLocationLatlng != null)
                    animateCameraToPosition(myLocationLatlng , true);
                break;

            case R.id.tvButtonSetLocation:
                goToCreateJob();
                break;

            case R.id.imgNavHeaderUserImage:
                startActivity(new Intent(this,UpdateProfileActivity.class));
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.imgViewClearSearch:
                actvplaceAutocomplete.setText("");
                break;

            default:
                AndyUtils.generateLog("map onclick default");
                break;
        }

    }

//
    public void  updateProfileToken() {

        HashMap<String , String> map = new HashMap<>();
        map.put(Const.URL , Const.ServiceType.UPDATE_PROFILE);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.NAME , preferenceHelper.getUserName());
        map.put(Const.Params.CONTACT_NO , preferenceHelper.getContactNo());
        map.put(Const.Params.COUNTRY_CODE , preferenceHelper.getCountryCode());
        map.put(Const.Params.EMAIL , preferenceHelper.getEmail());
        map.put(Const.Params.ADDRESS , preferenceHelper.getAddress());
        if(!TextUtils.isEmpty("")){
            map.put(Const.Params.PICTURE , "");
        }
        if(!TextUtils.isEmpty("ganizo04")){
            map.put(Const.Params.NEW_PASS, "ganizo04");
            map.put(Const.Params.OLD_PASS, "ganizo04");
        }

        AndyUtils.showCustomProgressDialog(this , true);
        new MultiPartRequester(this , map , Const.ServiceCode.UPDATE_PROFILE , this);

    }

    private void logout() {
        try{
        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.LOGOUT);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());

        AndyUtils.showCustomProgressDialog(this , false);
        new HttpRequester(this , map , Const.ServiceCode.LOGOUT , Const.httpRequestType.POST , this);

    }catch (Exception e) {
        }
        }


    private void getPandingPaymentDetail(){

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.GET_PROFILE);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());

        new HttpRequester(this , map , Const.ServiceCode.GET_PROFILE , Const.httpRequestType.POST , this);
    }




    /*It is use for handle click event of recycler view....*/
    @Override
    public void onClickRecyclerListItem(View view, int position) {
        selectedProviderType = listProviderType.get(position).getId();
        selectedProviderName = listProviderType.get(position).getName();
        imageUrlProviderType = listProviderType.get(position).getPicture();
        iconColor = AndyUtils.getColorCode(position % 6 , getApplicationContext());
        getProvider(curretLatLng);
        hideProviderType();

    }

    @Override
    public void onLongClickRecyclerListItem(View view, int position) {
        //Handle long click event of item...
    }


    //It handle autocomplete item selection...
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        selectedAddress = parent.getItemAtPosition(position).toString().trim();
        getLatlngFromAddress(selectedAddress);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(actvplaceAutocomplete.getWindowToken(), 0);

    }

    /* It hide recyclerview and set content on search pannel as per action..*/
    private void hideProviderType(){
        if(!TextUtils.isEmpty(selectedProviderName) && !TextUtils.isEmpty(iconColor)){
//            imgViewSelectedProviderType.setColorFilter(Color.parseColor(iconColor));
//            Glide.with(this)
//                    .load(imageUrlProviderType)
//                    .skipMemoryCache(true)
//                    .placeholder(R.drawable.search_icon)
//                    .into(imgViewSelectedProviderType);
        }
        //  tvHireExpertText.setVisibility(View.GONE);
        actvplaceAutocomplete.setVisibility(View.VISIBLE);
        recyclerViewProviderTypeList.setVisibility(View.GONE);
        imgViewClearSearch.setVisibility(View.VISIBLE);

    }


    /*Map PART*/
    @Override
    public void onLocationReceived(LatLng latlong) {

    }

    @Override
    public void onLocationReceived(Location location) {

        if(location != null){
            myLocation = location;
        }

    }

    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {
        try{
        if(location != null){
            myLocation = location;
            LatLng latLang = new LatLng(location.getLatitude(),
                    location.getLongitude());
            myLocationLatlng = latLang;
            animateCameraToPosition(latLang, false);
        }

    }catch (Exception e) {
        }
        }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.map = googleMap;
        setUpMap();

    }

    private void setUpMap(){
        try{
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);
        //map.setz

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                curretLatLng = map.getCameraPosition().target;
                getAddressFromLatlng(curretLatLng , getApplicationContext());
                if(!TextUtils.isEmpty(selectedProviderType))
                    getProvider(curretLatLng);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectedProviderId = (marker.getSnippet().toString());
                profileDialog(marker.getTitle());
                return false;
            }
        });
    }catch (Exception e) {
        }
        }
    public void profileDialog(String textProfile){
        try{
        LayoutInflater li = LayoutInflater.from(MapActivity.this);
        View promptsView = li.inflate(R.layout.dialog_profile, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapActivity.this);
        TextView textViewProfile = (TextView) promptsView.findViewById(R.id.txtprofile) ;
        textViewProfile.setText(textProfile);

        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setPositiveButton("Request Quote",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                       // Toast.makeText(MapActivity.this, ""+selectedProviderId, Toast.LENGTH_SHORT).show();
                        searchItem.setText("");
                    goToCreateJob();
                    }

                });
        alertDialogBuilder.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        searchItem.setText("");
                    }

                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        // show it
        alertDialog.show();


    }catch (Exception e) {
        }
        }

    /*It use for animate camera to given location....*/
    private void animateCameraToPosition(LatLng latLng, boolean isAnimate){
        try {
            CameraUpdate cameraUpdate;
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 7);
            if (cameraUpdate != null && map != null) {
                if (isAnimate)
                    map.animateCamera(cameraUpdate);
                else
                    map.moveCamera(cameraUpdate);
            }
        } catch (Exception e) {
            AndyUtils.generateLog("CameraAnimate Exception "+e);
        }
    }


    /*This method give address from the current latlong....*/
    private void getAddressFromLatlng(final LatLng latLng , final Context context){
        try{
        actvplaceAutocomplete.setText(getString(R.string.txt_waiting_for_address));

        new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder gc = new Geocoder(context , Locale.getDefault());
                try{
                    List<Address> addressesList = gc.getFromLocation(latLng.latitude , latLng.longitude , 1);
                    if (addressesList != null && !addressesList.isEmpty()) {
                        address = addressesList.get(0);

                        StringBuilder sb = new StringBuilder();
                        if (address.getAddressLine(0) != null) {
                            if (address.getMaxAddressLineIndex() > 0) {
                                for (int i = 0; i < address
                                        .getMaxAddressLineIndex(); i++) {
                                    sb.append(address.getAddressLine(i)).append("\n");
                                }
                                sb.append(",");
                                sb.append(address.getCountryName());
                            } else {
                                sb.append(address.getAddressLine(0));
                            }
                        }
                        strAddress = sb.toString();
                        strAddress = strAddress.replace(",null", "");
                        strAddress = strAddress.replace("null", "");
                        strAddress = strAddress.replace("Unnamed", "");

                        if(context == null)
                            return;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!TextUtils.isEmpty(strAddress)){
                                    actvplaceAutocomplete.setFocusable(false);
                                    actvplaceAutocomplete.setFocusableInTouchMode(false);
                                    actvplaceAutocomplete.setText(strAddress);
                                    actvplaceAutocomplete.setFocusable(true);
                                    actvplaceAutocomplete.setFocusableInTouchMode(true);

                                }
                                else {
                                    actvplaceAutocomplete.setText("");
                                }
                            }
                        });
                    }
                }
                catch (Exception e){
                    AndyUtils.generateLog("Getting address run Exception: "+e);
                }

            }
        }).start();
    }catch (Exception e) {
        }
        }


    /*This method give latlong from the address....*/
    private void getLatlngFromAddress(String currentAddress){

        if (Geocoder.isPresent()) {
            try {

                Geocoder gc = new Geocoder(this);
                List<android.location.Address> address = gc.getFromLocationName(currentAddress, 5);

                for (android.location.Address a : address) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        curretLatLng = new LatLng(a.getLatitude() , a.getLongitude());
                        animateCameraToPosition(curretLatLng ,true);

                    }
                }
            } catch (IOException e) {
                AndyUtils.generateLog("Exception :"+e);
            }
        }
    }


    /*This method call whenever response is comes from the request....*/
    private void getProvider(LatLng latLng){

        if(spinnerValue == null) {
            if(latLng == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AndyUtils.showToast(getApplicationContext() , getString(R.string.txt_waiting_for_location));
                    }
                });
            }
            else {
                HashMap<String, String> map = new HashMap<>();

                map.put(Const.URL, Const.ServiceType.GETPROVIDER);
                map.put(Const.Params.ID, preferenceHelper.getId());
                map.put(Const.Params.TOKEN, preferenceHelper.getToken());
                map.put(Const.Params.PROVIDER_TYPE , selectedProviderType);
                map.put(Const.Params.LATITUDE, String.valueOf(latLng.latitude));
                map.put(Const.Params.LONGITUDE, String.valueOf(latLng.longitude));
                map.put(Const.Params.LONGITUDE, String.valueOf(latLng.longitude));
                map.put("search_parameter", "");
                map.put("filter", "all");
                new HttpRequester(this, map, Const.ServiceCode.GET_COMPANY_LIST, Const.httpRequestType.POST, this);
            }
        }
        else{
            getFilteredProvider(curretLatLng,spinnerValue,"");
        }


    }


    /*This method call whenever response is comes from the request....*/
    private void getFilteredProvider(LatLng latLng,  String i, String search_parameter){

        if(latLng == null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AndyUtils.showToast(getApplicationContext() , getString(R.string.txt_waiting_for_location));
                }
            });
        }
        else {
            HashMap<String, String> map = new HashMap<>();

            map.put(Const.URL, Const.ServiceType.GETPROVIDER);
            map.put(Const.Params.ID, preferenceHelper.getId());
            map.put(Const.Params.TOKEN, preferenceHelper.getToken());
            map.put(Const.Params.PROVIDER_TYPE , selectedProviderType);
            map.put(Const.Params.LATITUDE, String.valueOf(latLng.latitude));
            map.put(Const.Params.LONGITUDE, String.valueOf(latLng.longitude));
            map.put(Const.Params.LONGITUDE, String.valueOf(latLng.longitude));
            map.put("search_parameter", search_parameter);
            map.put("filter", i);
            new HttpRequester(this, map, Const.ServiceCode.GET_COMPANY_LIST, Const.httpRequestType.POST, this);
        }
    }






    private void getTotalTypesOfProvider(){

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.GET_PROVIDER_TYPE);

        new HttpRequester(this ,map , Const.ServiceCode.GET_TYPES , Const.httpRequestType.GET , this);
        AndyUtils.showCustomProgressDialog(this , false);
    }


    /*It use for getting response from request.....*/
    @Override
    public void onTaskCompleted(final String response, int serviceCode) {
try{

        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode)
        {
            case Const.ServiceCode.GET_COMPANY_LIST:
                if(dataParser.isSuccess(response)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            listProvider = new ArrayList<>();
                            listProvider = dataParser.parseNearestProvider(response);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    setProviderOnMap();
                                }
                            });
                        }
                    }).start();
                }
                break;

            case Const.ServiceCode.GET_TYPES:
                if(dataParser.isSuccess(response)){

                    listProviderType.clear();
                    listProviderType = dataParser.parseTypesOfProviders(response , listProviderType);
                    providerTypeAdapter.notifyDataSetChanged();
                    selectedProviderType = listProviderType.get(0).getId();
                    selectedProviderName = listProviderType.get(0).getName();
                    imageUrlProviderType = listProviderType.get(0).getPicture();
                    iconColor = AndyUtils.getColorCode(0 % 6 , getApplicationContext());

                    hideProviderType();

                    List<String> spinnerArray = new ArrayList<String>();
                    spinnerArray.add("SELECT CATEGORY");
                    for(int i = 0; i <listProviderType.size(); i++) {

                        spinnerArray.add(listProviderType.get(i).getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            this, android.R.layout.simple_spinner_item, spinnerArray);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    serviceSpinner.setAdapter(adapter);
                }
                break;


            case Const.ServiceCode.LOGOUT:
                if(logoutDialog != null && logoutDialog.isShowing())
                    logoutDialog.dismiss();
                preferenceHelper.logout();
                startActivity(new Intent(this , SignInActivity.class));
                finish();
                break;


            case Const.ServiceCode.GET_PROFILE:
                if(dataParser.isSuccess(response)){
                    dataParser.parsePendingAmontDetail(response);
                    checkPendingAmount();

                }

                break;

            case Const.ServiceCode.PAY_OLD_REQUEST:
                closePendingAmountDialog();
                if(dataParser.isSuccess(response)){
                    dataParser.parseOldRequstPay(response);
                }
                break;


            default:
                AndyUtils.generateLog("OnTaskComplete default");
                break;
        }


    }catch (Exception e) {
}
    }


    /*It is use for set provider pin on map...*/
    private void setProviderOnMap() {

        if (map != null) {
            txtresults.setText(listProvider.size()+" Service Provider(s) Available");
            map.clear();
            nearProviderMarker.clear();

            Provider provider;
            int size = listProvider.size();
            if (size > 0) {
                isProviderAvailable = true;
            } else {
                isProviderAvailable = false;
            }
            markerOptions = new MarkerOptions();
            markerOptions.flat(false);
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.app_icon));

//            try{
//
//                URL url = new URL("https://media-exp2.licdn.com/mpr/mpr/shrinknp_200_200/AAIA_wDGAAAAAQAAAAAAAAweAAAAJGQ5ZDY2NDEwLWM3MDgtNGU2NS1iM2VjLWY0YjBkOTY5NTY5Ng.jpg");
//                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//
//
//                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_home));
//                //  map.addMarker(new MarkerOptions()
//                 markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
//            }catch (Exception e){
//                Toast.makeText(MapActivity.this, ""+e, Toast.LENGTH_SHORT).show();
//            }

            for (int i = 0; i < size; i++) {
                provider = listProvider.get(i);
                markerOptions.title("NAME :"+listProvider.get(i).getName()+"\nDistance: "+listProvider.get(i).getDistance()+" "+listProvider.get(i).getDistanceUnit()+" Away");
                markerOptions.position(new LatLng(provider.getLatitude(), provider.getLongitude()));
                markerOptions.snippet(listProvider.get(i).getId()+"");
                nearProviderMarker.put(provider.getId(), map.addMarker(markerOptions));

            }

        }
    }



    private void goToCreateJob(){

        if(TextUtils.isEmpty(selectedProviderType)){
            AndyUtils.showToast(this , getString(R.string.toast_select_service));
        }
        else if(!isProviderAvailable){
            AndyUtils.showToast(this , getString(R.string.toast_no_provider_available));
        }
        else if(curretLatLng == null){
            AndyUtils.showToast(this , getString(R.string.toast_no_location_found));
        }
        else if(TextUtils.isEmpty(strAddress)){
            AndyUtils.showToast(this , getString(R.string.toast_no_address_found));
        }
        else {
            Intent intentCreateJob = new Intent(MapActivity.this , CreateJobActivity.class);
            intentCreateJob.putExtra("LATITUDE",String.valueOf(curretLatLng.latitude));
            intentCreateJob.putExtra("LONGITUDE" ,String.valueOf(curretLatLng.longitude));
            intentCreateJob.putExtra("ADDRESS" ,strAddress);
            intentCreateJob.putExtra("TYPE" , selectedProviderType);
            intentCreateJob.putExtra("PROVIDER_TYPE_NAME" , "SEND JOB TO HANDY MAN");
            intentCreateJob.putExtra("IMAGE" , imageUrlProviderType);
            intentCreateJob.putExtra("COLOR" , iconColor);
            intentCreateJob.putExtra("PROVIDER_ID" , selectedProviderId);
            startActivity(intentCreateJob);
        }
    }



    /*It is use to check weather any request pending for pay or not*/
    private void checkPendingAmount(){
        double pendingAmout = Double.parseDouble(preferenceHelper.getPendingAmount());
        if(pendingAmout > 0)
            showPendingAmountDialog(pendingAmout);
    }


    private void showPendingAmountDialog(double amount){
        String amountToDisplay = Formatter.invoiceDigitFormater(String.valueOf(amount))+"$";
        String content = getString(R.string.dialog_message_pending_payment)+" "+amountToDisplay;
        String buttonPay = getString(R.string.dialog_button_pay)+" "+amountToDisplay;
        pendingAmountDialog = new CustomDialog(this , content, buttonPay , getString(R.string.dialog_button_cancel) , false) {
            @Override
            public void positiveButton() {
                payOldRequest();
            }

            @Override
            public void negativeButton() {
                closePendingAmountDialog();
            }
        };
        pendingAmountDialog.show();
    }


    private void closePendingAmountDialog(){
        if(pendingAmountDialog != null && pendingAmountDialog.isShowing()){
            pendingAmountDialog.dismiss();
            pendingAmountDialog = null;
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


    public void showSerchDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_search, null);
        dialogBuilder.setView(dialogView);

        final EditText searchItem = (EditText) dialogView.findViewById(R.id.search_criteria);

        dialogBuilder.setTitle("Search");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void showExitDialog(){
        exitDialog = new CustomTitleDialog(this , getString(R.string.dialog_title_exit) , getString(R.string.dialog_message_are_you_sure) ,
                getString(R.string.dialog_button_yes), getString(R.string.dialog_button_no)) {
            @Override
            public void positiveResponse() {
                exitDialog.dismiss();
                finish();
            }

            @Override
            public void negativeResponse() {
                exitDialog.dismiss();
            }
        };
        exitDialog.show();
    }


    private void showLogoutDialog(){
        logoutDialog = new CustomTitleDialog(this , getString(R.string.dialog_title_logout) , getString(R.string.dialog_message_are_you_sure) ,
                getString(R.string.dialog_button_logout), getString(R.string.dialog_button_cancel)) {
            @Override
            public void positiveResponse() {
                logout();
            }

            @Override
            public void negativeResponse() {
                logoutDialog.dismiss();
            }
        };
        logoutDialog.show();
    }

    private String convertToBase64(String imagePath)

    {

        Bitmap bm = BitmapFactory.decodeFile(imagePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] byteArrayImage = baos.toByteArray();

        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        return encodedImage;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Toast.makeText(MapActivity.this, convertToBase64(file), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        //tvTokenBalance.setText(preferenceHelper.getUserToken()+" Tokens");

        Calendar c = Calendar.getInstance();
        final int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        // Handler which will run after 2 seconds.
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (timeOfDay >= 0 && timeOfDay < 12){

                    Toast toast = new Toast(MapActivity.this);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater li = getLayoutInflater();
                    View toastAppear = li.inflate(R.layout.customtoast_layout2, (ViewGroup) findViewById(R.id.CustomToastLayoutRoot));
                    toast.setView(toastAppear);
                    toast.show();

                    //Toast.makeText(MapActivity.this, "Good Morning, Welcome to jimmie jobs", Toast.LENGTH_LONG).show();
                } else if (timeOfDay >= 12 && timeOfDay < 16){

                    Toast toast = new Toast(MapActivity.this);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater li = getLayoutInflater();
                    View toastAppear = li.inflate(R.layout.customtoast_layout3, (ViewGroup) findViewById(R.id.CustomToastLayoutRoot));
                    toast.setView(toastAppear);
                    toast.show();

                    //Toast.makeText(MapActivity.this, "Good Afternoon, Welcome to jimmie jobs", Toast.LENGTH_LONG).show();
                } else if (timeOfDay >= 16 && timeOfDay < 21){

                    Toast toast = new Toast(MapActivity.this);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater li = getLayoutInflater();
                    View toastAppear = li.inflate(R.layout.customtoast_layout, (ViewGroup) findViewById(R.id.CustomToastLayoutRoot));
                    toast.setView(toastAppear);
                    toast.show();

                    //Toast.makeText(MapActivity.this, "Good Evening, Welcome to jimmie jobs", Toast.LENGTH_LONG).show();
                } else if (timeOfDay >= 21 && timeOfDay < 24){


                    Toast toast = new Toast(MapActivity.this);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater li = getLayoutInflater();
                    View toastAppear = li.inflate(R.layout.customtoast_layout4, (ViewGroup) findViewById(R.id.CustomToastLayoutRoot));
                    toast.setView(toastAppear);
                    toast.show();
                    //Toast.makeText(MapActivity.this, "Good Night, Welcome to jimmie jobs", Toast.LENGTH_LONG).show();
                }
            }
        }, 2000);

        locationHelper.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this)
                .load(preferenceHelper.getProfilePicture())
                .dontAnimate()
                .skipMemoryCache(true)
                .placeholder(R.drawable.default_icon).into(imgNavHeaderUserImage);
        tvNavHeaderUserName.setText(preferenceHelper.getUserName());
       // tvTokenBalance.setText(preferenceHelper.getUserToken()+" Tokens");
        mapView.onResume();
        locationHelper.onResume();
        getProvider(curretLatLng);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHelper.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationHelper.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showExitDialog();
        }
    }

    //Send a share token link to facebook
    private void shareAppLinkViaFacebook() {
        String urlToShare="http://jimmiejob.com/index.php/";
        try {
            Intent intent1 = new Intent();
            intent1.setClassName("com.facebook.katana", "com.facebook.katana.activity.composer.ImplicitShareIntentHandler");
            intent1.setAction("android.intent.action.SEND");
            intent1.setType("text/plain");
            intent1.putExtra("android.intent.extra.TEXT", urlToShare);
            startActivity(intent1);
        } catch (Exception e) {
            // If we failed (not native FB app installed), try share through SEND
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            startActivity(intent);
        }
    }

    //A method to add a notification when users send info to the database
    private void addNotification() {

        String email = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            email = edtEmail.getText().toString();
        }

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
                        .setContentText("Thank you " + email + ", " + " for sharing your tokens");

        Intent notificationIntent = new Intent(this, DialogFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }

}
