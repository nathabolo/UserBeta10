package com.munye.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munye.ActiveJobActivity;
import com.munye.MyJobsActivity;
import com.munye.user.R;
import com.munye.adapter.ActiveJobAdapter;
import com.munye.model.ActiveJob;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.DataParser;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.RecyclerViewTouchListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Akash on 1/30/2017.
 */

public class ActiveJobFragment extends BaseFragment implements AsyncTaskCompleteListener, RecyclerViewTouchListener.ClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<ActiveJob> listActiveJobs;
    private ActiveJobAdapter adapterActiveJob;
    private TextView tvNoActiveJob;
    private SwipeRefreshLayout swipeRefreshActiveJob;


    public static ActiveJobFragment newInstance(){
        return new ActiveJobFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter(Const.PushStatus.PUSH_STATUS_INTENT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(jobChangesRecevier , intentFilter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        RecyclerView recyclerViewActiveJob;
        LinearLayoutManager linearLayoutManager;

        View activeView = inflater.inflate(R.layout.fragment_active_job ,container , false);

        tvNoActiveJob = (TextView)activeView.findViewById(R.id.tvNoActiveJob);
        swipeRefreshActiveJob = (SwipeRefreshLayout)activeView.findViewById(R.id.swipeRefreshActiveJob);
        recyclerViewActiveJob = (RecyclerView)activeView.findViewById(R.id.recyclerViewActiveJob);
        recyclerViewActiveJob.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewActiveJob.setLayoutManager(linearLayoutManager);

        listActiveJobs = new ArrayList<>();

        adapterActiveJob = new ActiveJobAdapter(listActiveJobs , getActivity());
        recyclerViewActiveJob.setAdapter(adapterActiveJob);
        getActiveJobs(true);

        recyclerViewActiveJob.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerViewActiveJob , this));

        swipeRefreshActiveJob.setOnRefreshListener(this);
        swipeRefreshActiveJob.setColorSchemeColors(getActivity().getResources().getColor(R.color.color_1));
        return activeView;
    }

    private void getActiveJobs(boolean isShowProgress) {

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.ACTIVE_REQUEST);
        map.put(Const.Params.ID ,preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.TIME_ZONE , preferenceHelper.getTimeZone());

        new HttpRequester(getActivity() , map , Const.ServiceCode.ACTIVE_REQUEST , Const.httpRequestType.POST , this);
        if(isShowProgress)
            AndyUtils.showCustomProgressDialog(getActivity() , false);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        swipeRefreshActiveJob.setRefreshing(false);
        AndyUtils.generateLog(response);
        switch (serviceCode){
            case Const.ServiceCode.ACTIVE_REQUEST:
                parseActiveJobData(response);
                break;

            default:
                AndyUtils.generateLog("No request");
                break;
        }

    }

    private void parseActiveJobData(String response) {
        listActiveJobs.clear();
        listActiveJobs = dataParser.parseActiveJob(response,listActiveJobs);
        if(listActiveJobs.isEmpty()){
            tvNoActiveJob.setVisibility(View.VISIBLE);
        }
        else {
            tvNoActiveJob.setVisibility(View.GONE);
        }
        adapterActiveJob.notifyDataSetChanged();
    }

    @Override
    public void onClickRecyclerListItem(View view, int position) {

        Intent intentActiveJob = new Intent(getActivity() , ActiveJobActivity.class);
        intentActiveJob.putExtra("OBJECT" , listActiveJobs.get(position));
        intentActiveJob.putExtra("POSITION" , position);
        startActivityForResult(intentActiveJob , Const.ACTIVE_JOB_ACTION);

    }


    @Override
    public void onLongClickRecyclerListItem(View view, int position) {

    }

    private BroadcastReceiver jobChangesRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String brodcastResponse = intent.getStringExtra(Const.PushStatus.PUSH_MESSAGE);

            switch (DataParser.parsePushMessage.getPushId(brodcastResponse)){
                case Const.PushStatus.PROVIDER_ON_THE_WAY:
                case Const.PushStatus.PROVIDER_ARRIVE:
                case Const.PushStatus.PROVIDER_START_JOB:
                case Const.PushStatus.JOB_DONE:
                    getActiveJobs(false);
                    break;

                case Const.PushStatus.PROVIDER_CANCEL_JOB:
                    getActiveJobs(false);
                    ((MyJobsActivity)getActivity()).isLoadAgain = true;
                    break;

                default:
                    AndyUtils.generateLog("No action");
                    break;
            }

        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(jobChangesRecevier);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Const.ACTION_CANCEL_JOB || resultCode == Const.ACTION_FEEDBACK){
            listActiveJobs.remove(data.getIntExtra("POSITION" , 0));
            if(listActiveJobs.isEmpty())
                tvNoActiveJob.setVisibility(View.VISIBLE);
            adapterActiveJob.notifyDataSetChanged();
            ((MyJobsActivity)getActivity()).isLoadAgain = true;
        }
        else {
            AndyUtils.generateLog("No action");
        }
    }


    //Swipe to refresh...
    @Override
    public void onRefresh() {
        getActiveJobs(false);
    }
}
