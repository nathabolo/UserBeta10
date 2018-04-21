package com.munye.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.munye.user.R;
import com.munye.model.ActiveJob;
import com.munye.utils.AndyUtils;
import com.munye.utils.Formatter;

import java.util.List;

/**
 * Created by Akash on 1/30/2017.
 */

public class ActiveJobAdapter extends RecyclerView.Adapter<ActiveJobAdapter.MyViewHolder> {

    private List<ActiveJob> listActiveJobs;
    private ActiveJob activeJob;
    private Activity activity;

    public ActiveJobAdapter(List<ActiveJob> listActiveJob , Activity activity){

        this.listActiveJobs = listActiveJob;
        this.activity = activity;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgActiveJobIcon;
        TextView tvActiveJobTitle , tvActiveJobDate , tvActiveJobDescription , tvActiveJobAmount;
        RelativeLayout relativeLayoutActiveJob;


        public MyViewHolder(View itemView) {
            super(itemView);

            imgActiveJobIcon = (ImageView)itemView.findViewById(R.id.imgActiveJobIcon);
            tvActiveJobTitle = (TextView)itemView.findViewById(R.id.tvActiveJobTitle);
            tvActiveJobDate = (TextView)itemView.findViewById(R.id.tvActiveJobDate);
            tvActiveJobDescription = (TextView)itemView.findViewById(R.id.tvActiveJobDescription);
            tvActiveJobAmount = (TextView)itemView.findViewById(R.id.tvActiveJobAmount);
            relativeLayoutActiveJob = (RelativeLayout)itemView.findViewById(R.id.relativeLayoutActiveJob);
        }
    }

    @Override
    public ActiveJobAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_active_job , parent , false);

        return new ActiveJobAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActiveJobAdapter.MyViewHolder holder, int position) {

        activeJob = listActiveJobs.get(position);
        holder.tvActiveJobTitle.setText(activeJob.getTitle());
        holder.tvActiveJobTitle.setTextColor(Color.parseColor(AndyUtils.getColorCode(position % 6 , activity)));
        holder.tvActiveJobDate.setText(Formatter.getDateInFormate(activeJob.getDate()));
        holder.tvActiveJobAmount.setText(AndyUtils.getSymbolFromHex(activeJob.getCurrency())+Formatter.formateDigitAfterPoint(activeJob.getAmount()));
        holder.tvActiveJobDescription.setText(activeJob.getDescription());
        Glide.with(activity)
                .load(activeJob.getJobTypeIcon())
                .skipMemoryCache(true)
                .dontAnimate()
                .placeholder(activity.getResources().getDrawable(R.drawable.placeholder_img))
                .into(holder.imgActiveJobIcon);
        holder.imgActiveJobIcon.setColorFilter(Color.parseColor(AndyUtils.getColorCode(position % 6  , activity)));
        holder.relativeLayoutActiveJob.setBackgroundColor(Color.parseColor(AndyUtils.getBackgroundColor(position % 2 , activity)));
    }

    @Override
    public int getItemCount() {
        return listActiveJobs.size();
    }


}
