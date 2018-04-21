package com.munye.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.munye.user.R;
import com.munye.model.ProviderType;
import com.munye.utils.AndyUtils;

import java.util.List;

/**
 * Created by Akash on 1/20/2017.
 */

public class ProviderTypeAdapter extends RecyclerView.Adapter<ProviderTypeAdapter.MyViewHolder> {

    List<ProviderType> listProviderType;
    ProviderType providerType;
    Activity activity;

    public ProviderTypeAdapter(List<ProviderType> listProviderType , Activity activity){
        this.listProviderType = listProviderType;
        this.activity = activity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTypeTitle;
        ImageView imgType;
        LinearLayout llProviderTypeItem;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTypeTitle = (TextView)itemView.findViewById(R.id.tvTypeTitle);
            imgType = (ImageView)itemView.findViewById(R.id.imgType);
            llProviderTypeItem = (LinearLayout)itemView.findViewById(R.id.llProviderTypeItem);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_provider_type, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        providerType = listProviderType.get(position);
        holder.tvTypeTitle.setText(providerType.getName());
        holder.llProviderTypeItem.setBackgroundColor(Color.parseColor(AndyUtils.getColorCode(position % 6 , activity)));
        Glide.with(activity)
                .load(providerType.getPicture())
                .dontAnimate()
                .placeholder(activity.getResources().getDrawable(R.drawable.placeholder_img))
                .skipMemoryCache(true)
                .into(holder.imgType);
    }

    @Override
    public int getItemCount() {
        return listProviderType.size();
    }

}
