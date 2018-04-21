package com.munye.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munye.MyJobsActivity;
import com.munye.PreviousJobInfoActivity;
import com.munye.user.R;
import com.munye.adapter.PreviousJobAdapter;
import com.munye.model.PreviousJob;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.PreferenceHelper;
import com.munye.utils.RecyclerViewTouchListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Akash on 1/30/2017.
 */

public class PreviousJobFragment extends BaseFragment implements AsyncTaskCompleteListener, RecyclerViewTouchListener.ClickListener {

    private RecyclerView recyclerViewPreviousJob;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<PreviousJob> listPreviousJobs;
    private PreviousJobAdapter adapterPreviousJob;
    private PreferenceHelper preferenceHelper;
    private static int pageNo;
    private static int totalPages;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private TextView tvNoPreviousJob;
    private boolean isClearList = false;
    private boolean isFirstLoad = true;


    public static PreviousJobFragment newInstanse(){
        PreviousJobFragment previousJobFragment = new PreviousJobFragment();
        return previousJobFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View previousView = inflater.inflate(R.layout.fragment_previous_job , container , false);
        preferenceHelper = PreferenceHelper.getInstance(getActivity());

        tvNoPreviousJob = (TextView)previousView.findViewById(R.id.tvNoPreviousJob);

        recyclerViewPreviousJob = (RecyclerView)previousView.findViewById(R.id.recyclerViewPreviousJob);
        recyclerViewPreviousJob.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewPreviousJob.setLayoutManager(linearLayoutManager);

        listPreviousJobs = new ArrayList<>();

        adapterPreviousJob = new PreviousJobAdapter(listPreviousJobs , getActivity());
        recyclerViewPreviousJob.setAdapter(adapterPreviousJob);
        pageNo = 1;
        recyclerViewPreviousJob.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerViewPreviousJob , this));
        recyclerViewPreviousJob.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);
                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                pastVisiblesItems =  ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && (visibleItemCount + totalItemCount)>adapterPreviousJob.getItemCount()){
                    if(totalItemCount != 0 && visibleItemCount + pastVisiblesItems >=totalItemCount ){
                        if(totalPages >= pageNo ){
                            getPreviousJobs(pageNo , false , false);
                            pageNo +=1;
                        }
                    }
                }


            }
        });

        return previousView;
    }

    private void getPreviousJobs(int pageno , boolean isClear, boolean isShowProgress) {

        this.isClearList = isClear;

        HashMap<String , String> map = new HashMap<>();

        map.put(Const.URL , Const.ServiceType.PREVIOUS_JOB);
        map.put(Const.Params.ID , preferenceHelper.getId());
        map.put(Const.Params.TOKEN , preferenceHelper.getToken());
        map.put(Const.Params.TIME_ZONE , preferenceHelper.getTimeZone());
        map.put(Const.Params.PAGE , String.valueOf(pageno));

        if(isShowProgress)
            AndyUtils.showCustomProgressDialog(getActivity() , false);
        new HttpRequester(getActivity() , map , Const.ServiceCode.PREVIOUS_JOB , Const.httpRequestType.POST , this);

        AndyUtils.generateLog("PAGE"+pageno);

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();

        AndyUtils.generateLog("RESPONSE"+response);
        switch (serviceCode){
            case Const.ServiceCode.PREVIOUS_JOB:
                if(dataParser.isSuccess(response)){
                    parsePreviousJobData(response);
                }
        }
    }

    private void parsePreviousJobData(String response ) {

        if(isClearList){
            listPreviousJobs.clear();
            isClearList = false;
        }

        listPreviousJobs = dataParser.parsePreviousJob(response , listPreviousJobs);
        totalPages = dataParser.totalPage;
        if(listPreviousJobs.isEmpty()){
            tvNoPreviousJob.setVisibility(View.VISIBLE);
        }
        else {
            tvNoPreviousJob.setVisibility(View.GONE);
        }
        adapterPreviousJob.notifyDataSetChanged();
        if(isFirstLoad){
            pageNo += 1;
            isFirstLoad = false;
        }
        if(totalPages >= pageNo){
            adapterPreviousJob.isShowLoadingPanel(true);
        }
        else {
            adapterPreviousJob.isShowLoadingPanel(false);
        }

    }

    @Override
    public void onClickRecyclerListItem(View view, int position) {



            Intent intentPreviousJobInfo = new Intent(getActivity() , PreviousJobInfoActivity.class);
            intentPreviousJobInfo.putExtra("OBJECTDATA" , listPreviousJobs.get(position));
            intentPreviousJobInfo.putExtra("POSITION" , position);
            startActivityForResult(intentPreviousJobInfo , Const.PREVIOUS_JOB_ACTION);

    }

    @Override
    public void onLongClickRecyclerListItem(View view, int position) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser && ((MyJobsActivity)getActivity()).isLoadAgain){
            isFirstLoad = true;
            pageNo = 1;
            getPreviousJobs(pageNo , true ,true);
            ((MyJobsActivity)getActivity()).isLoadAgain = false;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Const.ACTION_RETRY_REQUEST){
            listPreviousJobs.remove(data.getIntExtra("POSITION" , 0));
            adapterPreviousJob.notifyDataSetChanged();
        }
    }
}
