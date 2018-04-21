package com.munye.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.munye.user.R;
import com.munye.model.ModelViewQuotes;
import com.munye.utils.AndyUtils;
import com.munye.utils.Formatter;

import java.util.List;

/**
 * Created by Akash on 1/25/2017.
 */

public class ViewQuotesAdapter extends RecyclerView.Adapter<ViewQuotesAdapter.MyViewHolder> {

    private List<ModelViewQuotes> listViewQuotes;
    private Activity activity;

    public ViewQuotesAdapter(List<ModelViewQuotes> listViewQuotes , Activity activity){
        this.activity = activity;
        this.listViewQuotes = listViewQuotes;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgViewQuoteJobIcon;
        TextView tvViewQuotesJobTitle;
        TextView tvViewQuotesDate;
        TextView tvViewQuotesDescription;
        TextView tvViewQuotesTotalQutes;
        LinearLayout linerLayoutItemViewQuotes;
        ProgressBar progressBarViewQuote;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgViewQuoteJobIcon = (ImageView)itemView.findViewById(R.id.imgViewQuoteJobIcon);
            tvViewQuotesJobTitle = (TextView)itemView.findViewById(R.id.tvViewQuotesJobTitle);
            tvViewQuotesDate = (TextView)itemView.findViewById(R.id.tvViewQuotesDate);
            tvViewQuotesDescription = (TextView)itemView.findViewById(R.id.tvViewQuotesDescription);
            tvViewQuotesTotalQutes = (TextView)itemView.findViewById(R.id.tvViewQuotesTotalQutes);
            linerLayoutItemViewQuotes = (LinearLayout)itemView.findViewById(R.id.linerLayoutItemViewQuotes);
            progressBarViewQuote = (ProgressBar)itemView.findViewById(R.id.progressBarViewQuote);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view_quotes , parent , false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        ModelViewQuotes modelViewQuotes = listViewQuotes.get(position);
        holder.tvViewQuotesJobTitle.setText(modelViewQuotes.getJobTitle());
        holder.tvViewQuotesJobTitle.setTextColor(Color.parseColor(AndyUtils.getColorCode(position % 6 , activity)));
        holder.tvViewQuotesDate.setText(Formatter.getDateInFormate(modelViewQuotes.getStartTime()));
        holder.tvViewQuotesDescription.setText(modelViewQuotes.getDescripion());
        holder.tvViewQuotesTotalQutes.setText(Formatter.getDoubleDigits(modelViewQuotes.getTotalQuote()));
        Glide.with(activity)
                .load(modelViewQuotes.getJobIcon())
                .dontAnimate()
                .placeholder(activity.getResources().getDrawable(R.drawable.placeholder_img))
                .skipMemoryCache(true)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBarViewQuote.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imgViewQuoteJobIcon);
        holder.imgViewQuoteJobIcon.setColorFilter(Color.parseColor(AndyUtils.getColorCode(position % 6 , activity)));
        holder.linerLayoutItemViewQuotes.setBackgroundColor(Color.parseColor(AndyUtils.getBackgroundColor(position % 2 , activity)));

    }

    @Override
    public int getItemCount() {
        return listViewQuotes.size();
    }

}
