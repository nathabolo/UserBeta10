package com.munye.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.munye.user.R;
import com.munye.model.PreviousJob;
import com.munye.utils.AndyUtils;
import com.munye.utils.Formatter;

import java.util.ArrayList;

/**
 * Created by Akash on 1/31/2017.
 */

public class PreviousJobAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<PreviousJob> listPreviousJobs;
    private Activity activity;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOAD = 2;
    private PreviousJob previousJob;
    private boolean isShowLoading = false;

    public PreviousJobAdapter(ArrayList<PreviousJob> listPreviousJobs, Activity activity) {

        this.listPreviousJobs = listPreviousJobs;
        this.activity = activity;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(activity).inflate(R.layout.list_item_active_job, parent, false);
            return new ItemHolder(itemView);
        } else if (viewType == TYPE_LOAD) {
            View loadingView = LayoutInflater.from(activity).inflate(R.layout.list_item_load_more, parent, false);
            return new LoadingHolder(loadingView);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof LoadingHolder) {

            LoadingHolder loadingHolder = (LoadingHolder) holder;
            if(isShowLoading){
                loadingHolder.pbarLoadMore.setVisibility(View.VISIBLE);
                loadingHolder.pbarLoadMore.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            }
            else{
                loadingHolder.pbarLoadMore.setVisibility(View.GONE);
            }


        } else if (holder instanceof ItemHolder) {

            ItemHolder itemHolder = (ItemHolder) holder;

            previousJob = listPreviousJobs.get(position);
            itemHolder.tvActiveJobTitle.setText(previousJob.getJobTitle());
            itemHolder.tvActiveJobTitle.setTextColor(Color.parseColor(AndyUtils.getColorCode(position % 6, activity)));
            itemHolder.tvActiveJobDate.setText(Formatter.getDateInFormate(previousJob.getJobDate()));
            itemHolder.tvActiveJobAmount.setText(AndyUtils.getSymbolFromHex(previousJob.getCurrency())+Formatter.formateDigitAfterPoint(previousJob.getTotalAmount()));
            itemHolder.tvActiveJobDescription.setText(previousJob.getDescription());
            itemHolder.tvtoPay.setText(activity.getString(R.string.txt_you_paid));
            Glide.with(activity)
                    .load(previousJob.getJobTypeIcon())
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .placeholder(activity.getResources().getDrawable(R.drawable.placeholder_img))
                    .into(itemHolder.imgActiveJobIcon);
            itemHolder.imgActiveJobIcon.setColorFilter(Color.parseColor(AndyUtils.getColorCode(position % 6, activity)));
            itemHolder.relativeLayoutActiveJob.setBackgroundColor(Color.parseColor(AndyUtils.getBackgroundColor(position % 2, activity)));

        }

    }

    @Override
    public int getItemCount() {
        return listPreviousJobs.size() + 1;
    }


    @Override
    public int getItemViewType(int position) {

        if (position == getItemCount() - 1) {
            return TYPE_LOAD;
        } else {
            return TYPE_ITEM;
        }
    }


    public void isShowLoadingPanel(boolean isShow) {
        this.isShowLoading = isShow;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        ImageView imgActiveJobIcon;
        TextView tvActiveJobTitle, tvActiveJobDate, tvActiveJobDescription, tvActiveJobAmount , tvtoPay;
        RelativeLayout relativeLayoutActiveJob;

        public ItemHolder(View itemView) {
            super(itemView);
            imgActiveJobIcon = (ImageView) itemView.findViewById(R.id.imgActiveJobIcon);
            tvActiveJobTitle = (TextView) itemView.findViewById(R.id.tvActiveJobTitle);
            tvActiveJobDate = (TextView) itemView.findViewById(R.id.tvActiveJobDate);
            tvActiveJobDescription = (TextView) itemView.findViewById(R.id.tvActiveJobDescription);
            tvActiveJobAmount = (TextView) itemView.findViewById(R.id.tvActiveJobAmount);
            relativeLayoutActiveJob = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutActiveJob);
            tvtoPay = (TextView)itemView.findViewById(R.id.tvtoPay);
        }
    }

    private class LoadingHolder extends RecyclerView.ViewHolder {

        ProgressBar pbarLoadMore;

        public LoadingHolder(View loadingView) {
            super(loadingView);
            pbarLoadMore = (ProgressBar)loadingView.findViewById(R.id.pbarLoadMore);
        }
    }
}
