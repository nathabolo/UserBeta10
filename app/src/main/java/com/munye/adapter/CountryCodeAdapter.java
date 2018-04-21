package com.munye.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munye.user.R;
import com.munye.model.CountryCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akash on 2/3/2017.
 */

public class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.MyViewHolder> {

    private List<CountryCode> lisCountryCode;
    private CountryCode countryCode;

    public CountryCodeAdapter(List<CountryCode> lisCountryCode){
        this.lisCountryCode = lisCountryCode;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCountryCode , tvCountryName;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvCountryCode = (TextView)itemView.findViewById(R.id.tvCountryCode);
            tvCountryName = (TextView)itemView.findViewById(R.id.tvCountryName);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_country_code ,parent ,  false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        countryCode = lisCountryCode.get(position);
        holder.tvCountryCode.setText(countryCode.getCountryCode());
        holder.tvCountryName.setText(countryCode.getCountryName());

    }

    @Override
    public int getItemCount() {
        return lisCountryCode.size();
    }


    public void setFilter(List<CountryCode> countryModels) {
        lisCountryCode = new ArrayList<>();
        lisCountryCode.addAll(countryModels);
        notifyDataSetChanged();
    }

}
