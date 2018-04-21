package com.munye;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.munye.user.R;
import com.munye.adapter.AcceptQuotesAdapter;
import com.munye.dialog.CustomTitleDialog;
import com.munye.model.AllQuotes;
import com.munye.model.ModelViewQuotes;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.DataParser;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.Formatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AcceptQuoteActivity extends ActionBarBaseActivity implements AcceptQuotesAdapter.MyAdapterListener, View.OnClickListener, AsyncTaskCompleteListener {

    private Bundle getObjectData;
    private ModelViewQuotes modelViewQuotes;
    private List<AllQuotes> listAllQuotes;
    private ArrayList<ModelViewQuotes> listViewQuotes;
    private AcceptQuotesAdapter acceptQuotesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewAcceptQuotes;
    private TextView tvDetailQuoteJobTitle , tvDetailQuoteJobType , tvDetailQuoteDate , tvDetailQuoteDescription , tvDetailQuoteAddress , tvDetailQuoteTotalQuote;
    private ImageView imgViewDetailQuoteJobIcon;
    private ImageButton imgButtonDeleteJob;
    private int color ;
    private boolean isDeleteQuote = false;
    private String broadCastMessage;
    private static int totalQuoteCount;
    private CustomTitleDialog deleteJobAlertDialog , dialogAcceptQuote , dialogDeleteQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_quote);
        initToolBar();
        setToolBarTitle(getString(R.string.title_view_quotes));

        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);
        tvDetailQuoteJobTitle = (TextView)findViewById(R.id.tvDetailQuoteJobTitle);
        tvDetailQuoteJobType = (TextView)findViewById(R.id.tvDetailQuoteJobType);
        tvDetailQuoteDate = (TextView)findViewById(R.id.tvDetailQuoteDate);
        tvDetailQuoteDescription = (TextView)findViewById(R.id.tvDetailQuoteDescription);
        tvDetailQuoteAddress = (TextView)findViewById(R.id.tvDetailQuoteAddress);
        tvDetailQuoteTotalQuote = (TextView)findViewById(R.id.tvDetailQuoteTotalQuote);
        imgViewDetailQuoteJobIcon = (ImageView)findViewById(R.id.imgViewDetailQuoteJobIcon);
        imgButtonDeleteJob = (ImageButton)findViewById(R.id.imgButtonDeleteJob);

        getObjectData = getIntent().getExtras();
        modelViewQuotes = getObjectData.getParcelable("OBJECT");
        color = getObjectData.getInt("POSITION") % 6;
        totalQuoteCount = modelViewQuotes.getTotalQuote();

        tvDetailQuoteJobTitle.setFocusable(true);
        tvDetailQuoteJobTitle.setText(modelViewQuotes.getJobTitle());
        tvDetailQuoteJobTitle.setTextColor(Color.parseColor(AndyUtils.getColorCode(color , this)));
        tvDetailQuoteDate.setText(Formatter.getDateInFormate(modelViewQuotes.getStartTime()));
        if(totalQuoteCount>0) {
            tvDetailQuoteTotalQuote.setText("You have a Quotation");
            tvDetailQuoteTotalQuote.setTextSize(15);
        }
        tvDetailQuoteDescription.setText(modelViewQuotes.getDescripion());
        tvDetailQuoteAddress.setText(modelViewQuotes.getAddress());
        Glide.with(this)
                .load(modelViewQuotes.getJobIcon())
                .dontAnimate()
                .skipMemoryCache(true)
                .placeholder(getResources().getDrawable(R.drawable.placeholder_img))
                .into(imgViewDetailQuoteJobIcon);
        imgViewDetailQuoteJobIcon.setColorFilter(Color.parseColor(AndyUtils.getColorCode(color, this )));

        switch (modelViewQuotes.getRequestType()){
            case 0:
                tvDetailQuoteJobType.setText(getString(R.string.txt_repair_maintenance));
                break;
            case 1:
                tvDetailQuoteJobType.setText(getString(R.string.txt_installation));
                break;
            default:
                //No services type..
                break;
        }

        imgButtonDeleteJob.setOnClickListener(this);

        listViewQuotes = new ArrayList<>();

        recyclerViewAcceptQuotes = (RecyclerView)findViewById(R.id.recyclerViewAcceptQuotes);
        recyclerViewAcceptQuotes.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(AcceptQuoteActivity.this);
        recyclerViewAcceptQuotes.setLayoutManager(linearLayoutManager);
        listAllQuotes = modelViewQuotes.getListAllQuotes();
        acceptQuotesAdapter = new AcceptQuotesAdapter(listAllQuotes , this ,this);
        recyclerViewAcceptQuotes.setAdapter(acceptQuotesAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(Const.PushStatus.PUSH_STATUS_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(listQuotesUpdateReceiver , intentFilter);
    }

    @Override
    public void btnAcceptQuote(View v, int position) {
        showAcceptQuoteDialog(position);
    }

    @Override
    public void btnDeleteQuote(View v, int position) {
        showRejectQuoteDialog(position);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imgButtonDeleteJob:
                showDeleteJobDialog();
                break;

            default:
                AndyUtils.generateLog("No click event");
                break;
        }

    }


    /*It shows delete job dialog whenever user going to delete job.*/
    private void showDeleteJobDialog(){

        deleteJobAlertDialog = new CustomTitleDialog(this , getString(R.string.dialog_title_delete_job) , getString(R.string.dialog_message_are_you_sure),
                getString(R.string.dialog_button_delete) , getString(R.string.dialog_button_cancel)) {
            @Override
            public void positiveResponse() {
                deleteJob();
                deleteJobAlertDialog.dismiss();
            }

            @Override
            public void negativeResponse() {
                deleteJobAlertDialog.dismiss();
            }
        };
        deleteJobAlertDialog.show();
    }



    /*This dialog will open whenever user click on accept quote button in quote list*/
    private void showAcceptQuoteDialog(final int position){

        dialogAcceptQuote = new CustomTitleDialog(this , getString(R.string.dialog_title_accept_quote) , getString(R.string.dialog_message_accept_quote),
                getString(R.string.dialog_button_yes) , getString(R.string.dialog_button_no)) {
            @Override
            public void positiveResponse() {
                actionQuote(listAllQuotes.get(position).getProviderId() , true);
                dialogAcceptQuote.dismiss();
            }

            @Override
            public void negativeResponse() {
                dialogAcceptQuote.dismiss();
            }
        };
        dialogAcceptQuote.show();
    }


    /*This dialog will open whenever click on reject quote*/
    private void showRejectQuoteDialog(final int position){

        dialogDeleteQuote = new CustomTitleDialog(this , getString(R.string.dialog_title_reject_quote) , getString(R.string.dialog_message_reject_quote),
                getString(R.string.dialog_button_yes) , getString(R.string.dialog_button_no)) {
            @Override
            public void positiveResponse() {
                actionQuote(listAllQuotes.get(position).getProviderId() , false);
                listAllQuotes.remove(position);
                totalQuoteCount = totalQuoteCount - 1;
                tvDetailQuoteTotalQuote.setText(String.valueOf(Formatter.getDoubleDigits(totalQuoteCount)));
                acceptQuotesAdapter.notifyItemRemoved(position);
                dialogDeleteQuote.dismiss();
            }

            @Override
            public void negativeResponse() {
                dialogDeleteQuote.dismiss();
            }
        };
        dialogDeleteQuote.show();
    }


    /*It use to receve push broadcast of add quote and cancel quote*/
    private BroadcastReceiver listQuotesUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            broadCastMessage = intent.getStringExtra(Const.PushStatus.PUSH_MESSAGE);
            switch (DataParser.parsePushMessage.getPushId(broadCastMessage)){

                case Const.PushStatus.ADD_QUOTE:
                case Const.PushStatus.CANCEL_QUOTE:
                    AndyUtils.generateLog("INSIDE SWITCH");
                    chekForUpdateList(broadCastMessage);
                    break;


                default:
                    AndyUtils.generateLog("No broadcast event");
                    break;
            }

        }
    };



    /*Use for delete job*/
    private void deleteJob() {

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.DELETE_JOB);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.REQUEST_ID , modelViewQuotes.getRequestId());

        new HttpRequester(this , map , Const.ServiceCode.DELETE_JOB , Const.httpRequestType.POST , this);
    }


    /*It use for check that add or cancel quote in current job.*/
    private void chekForUpdateList(String broadCastMessage){

        if(DataParser.parsePushMessage.getRequestId(broadCastMessage).equals(modelViewQuotes.getRequestId())){
            updateProvidedQuoteList();
        }

    }


    /*It use for update provider quote list*/
    private void updateProvidedQuoteList() {

        HashMap<String , String> map =new HashMap<>();

        map.put(Const.URL , Const.ServiceType.VIEW_UPDATE_QUOTE );
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.REQUEST_ID , modelViewQuotes.getRequestId());
        map.put(Const.Params.TIME_ZONE , preferenceHelper.getTimeZone());

        new HttpRequester(this , map , Const.ServiceCode.VIEW_UPDATE_QUOTE , Const.httpRequestType.POST ,this);

    }


    /*It use for perform action on accept or reject quote*/
    private void actionQuote(String providerId , boolean isAccept) {

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.REQUEST_ID , modelViewQuotes.getRequestId());
        map.put(Const.Params.PROVIDER_ID , providerId);

        if(isAccept){
            map.put(Const.URL , Const.ServiceType.ACCEPT_QUOTE);
            new HttpRequester(this , map , Const.ServiceCode.ACCEPT_QUOTE , Const.httpRequestType.POST , this);
            AndyUtils.showCustomProgressDialog(this , false);
        }
        else {
            map.put(Const.URL , Const.ServiceType.DELETE_QUOTE);
            new HttpRequester(this , map , Const.ServiceCode.DELETE_QUOTE , Const.httpRequestType.POST , this);
        }

    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.generateLog(response);

        switch (serviceCode){
            case Const.ServiceCode.DELETE_JOB :
                if(dataParser.isSuccess(response)){
                    setResult(Const.ACTION_DELETE_JOB);
                    finish();
                }
                break;

            case Const.ServiceCode.DELETE_QUOTE:
                if(dataParser.isSuccess(response)){
                    isDeleteQuote = true;
                }
                break;

            case Const.ServiceCode.ACCEPT_QUOTE:
                AndyUtils.removeCustomProgressDialog();
                if(dataParser.isSuccess(response)){
                    setResult(Const.ACTION_ACCEPT_QUOTE);
                    finish();
                }
                break;

            case Const.ServiceCode.VIEW_UPDATE_QUOTE:
                parseUpdatedQuotes(response);
                break;


            default:
                AndyUtils.generateLog("No action service call");
                break;
        }
    }


    /*It is use for parse updated response from the server*/
    private void parseUpdatedQuotes(String response) {
        if(dataParser.isSuccess(response)){
            listViewQuotes = dataParser.parseQuotes(response , listViewQuotes);
            listAllQuotes = new ArrayList<>();
            for(int i=0 ; i<listViewQuotes.size() ; i++){
                totalQuoteCount = listViewQuotes.get(i).getTotalQuote();
                tvDetailQuoteTotalQuote.setText(String.valueOf(Formatter.getDoubleDigits(totalQuoteCount)));
                listAllQuotes = listViewQuotes.get(i).getListAllQuotes();
            }
            acceptQuotesAdapter = new AcceptQuotesAdapter(listAllQuotes ,this ,this);
            recyclerViewAcceptQuotes.setAdapter(acceptQuotesAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        if(isDeleteQuote){
            setResult(Const.ACTION_DELETE_QUOTE);
            super.onBackPressed();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listQuotesUpdateReceiver);
    }
}
