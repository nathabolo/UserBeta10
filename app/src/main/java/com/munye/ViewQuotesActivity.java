package com.munye;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.munye.user.R;
import com.munye.adapter.ViewQuotesAdapter;
import com.munye.model.ModelViewQuotes;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.DataParser;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.RecyclerViewTouchListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewQuotesActivity extends ActionBarBaseActivity implements AsyncTaskCompleteListener, RecyclerViewTouchListener.ClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerViewQuotes;
    private LinearLayoutManager linearLayoutManager;
    private ViewQuotesAdapter adapterViewQuotes;
    private ArrayList<ModelViewQuotes> listModelViewQuotes;
    private String broadCastResponse;
    public TextView tvNoQuotes ;
    private SwipeRefreshLayout swipeRefreshViewQutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quotes);
        initToolBar();
        setToolBarTitle(getString(R.string.title_view_quotes));
        imgBtnDrawerToggle.setVisibility(View.INVISIBLE);

        tvNoQuotes = (TextView)findViewById(R.id.tvNoQuotes);

        listModelViewQuotes = new ArrayList<>();
        recyclerViewQuotes = (RecyclerView)findViewById(R.id.recyclerViewQuotes);
        recyclerViewQuotes.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(ViewQuotesActivity.this);
        recyclerViewQuotes.setLayoutManager(linearLayoutManager);

        adapterViewQuotes = new ViewQuotesAdapter(listModelViewQuotes , this);
        recyclerViewQuotes.setAdapter(adapterViewQuotes);

        getQuotes(true);

        recyclerViewQuotes.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerViewQuotes , this));

        IntentFilter intentFilter = new IntentFilter(Const.PushStatus.PUSH_STATUS_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(quoteUpdateReceiver , intentFilter);


        swipeRefreshViewQutes = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshViewQutes);
        swipeRefreshViewQutes.setOnRefreshListener(this);
        swipeRefreshViewQutes.setColorSchemeColors(getResources().getColor(R.color.color_1));

        startService(new Intent(this, RealTimeService.class));
        final Handler handler=new Handler();

        final Runnable updateTask=new Runnable() {
            @Override
            public void run() {
                getQuotes(false);
                handler.postDelayed(this,6000);
            }
        };
        handler.postDelayed(updateTask,6000);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*It is use to handle click event of list item*/
    @Override
    public void onClickRecyclerListItem(View view, int position) {
        Intent intentAcceptQuoteActivity = new Intent(ViewQuotesActivity.this , AcceptQuoteActivity.class);
        intentAcceptQuoteActivity.putExtra("OBJECT" , listModelViewQuotes.get(position));
        intentAcceptQuoteActivity.putExtra("POSITION" , position);
        startActivityForResult(intentAcceptQuoteActivity , Const.QUOTE_ACTION);
    }



    @Override
    public void onLongClickRecyclerListItem(View view, int position) {

    }


    private void getQuotes(boolean isShowLoading){
        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.VIEW_QUOTES);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.TIME_ZONE , preferenceHelper.getTimeZone());

        new HttpRequester(this , map , Const.ServiceCode.VIEW_QUOTES , Const.httpRequestType.POST , this);
        if(isShowLoading)
            AndyUtils.showCustomProgressDialog(this , false);

        //Toast.makeText(ViewQuotesActivity.this, ""+listModelViewQuotes.size(), Toast.LENGTH_SHORT).show();
//        if(listModelViewQuotes.size()>0){
//            Toast.makeText(ViewQuotesActivity.this, "You have a Quote from JimmieJobs", Toast.LENGTH_SHORT).show();
//            NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//            Notification notify=new Notification.Builder
//                    (getApplicationContext()).setContentTitle("JimmieJobs").setContentText("JimmieJobs Has a Job for You").
//                    setContentTitle("JimmieJobs Provider").setSmallIcon(R.drawable.app_icon).build();
//            notify.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
//
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
//
//            notify.flags |= Notification.FLAG_AUTO_CANCEL;
//            notif.notify(0, notify);
//        }

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        swipeRefreshViewQutes.setRefreshing(false);
        switch (serviceCode){
            case Const.ServiceCode.VIEW_QUOTES:
                if(dataParser.isSuccess(response)){
                    listModelViewQuotes.clear();
                    listModelViewQuotes = dataParser.parseQuotes(response ,listModelViewQuotes);
                    if(listModelViewQuotes.isEmpty() )
                        tvNoQuotes.setVisibility(View.VISIBLE);
                    else
                        tvNoQuotes.setVisibility(View.GONE);
                    adapterViewQuotes.notifyDataSetChanged();
                }
                break;

            default:
                AndyUtils.generateLog("No service called");
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            case Const.ACTION_DELETE_JOB:
            case Const.ACTION_ACCEPT_QUOTE:
            case Const.ACTION_DELETE_QUOTE:
                //getQuotes(false);
                onBackPressed();
                break;

            default:
                AndyUtils.generateLog("No action performed");
                break;
        }
    }

    private BroadcastReceiver quoteUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            broadCastResponse = intent.getStringExtra(Const.PushStatus.PUSH_MESSAGE);
            int pushId;
            pushId = DataParser.parsePushMessage.getPushId(broadCastResponse);

            if(pushId == Const.PushStatus.ADD_QUOTE || pushId == Const.PushStatus.CANCEL_QUOTE){
                AndyUtils.generateLog("GET QUOTE CALL BY RECEIVER");
                getQuotes(true);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(quoteUpdateReceiver);
    }

    @Override
    public void onRefresh() {
        getQuotes(false);
    }


    @Override
    public void onBackPressed() {
        if(this.isTaskRoot())
            backToMapActivity();
        super.onBackPressed();
    }
}
