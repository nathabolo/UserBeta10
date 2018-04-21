package com.munye.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.munye.user.R;
import com.munye.model.AllQuotes;
import com.munye.utils.AndyUtils;
import com.munye.utils.Formatter;

import java.util.List;

/**
 * Created by Akash on 1/25/2017.
 */

public class AcceptQuotesAdapter extends RecyclerView.Adapter<AcceptQuotesAdapter.MyViewHolder> {

    List<AllQuotes> listAllQuotes;
    AllQuotes allQuotes;
    Activity activity;
    MyAdapterListener onClickListener;

    public interface MyAdapterListener {

        void btnAcceptQuote(View v, int position);
        void btnDeleteQuote(View v, int position);
    }

    public AcceptQuotesAdapter(List<AllQuotes> listAll , Activity activity , MyAdapterListener listener){
        this.listAllQuotes = listAll;
        this.activity = activity;
        this.onClickListener = listener;

    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgViewProviderImageAcceptQuotes;
        TextView tvProviderNameAcceptQuote , tvQuotationDateProvider , tvQuoteAmount;
        RatingBar ratingBarProviderRate;
        ImageButton imgBtnAcceptQuote , imgBtnDeleteQuote;
        RelativeLayout relativeLayoutAcceptQuote;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgViewProviderImageAcceptQuotes = (ImageView)itemView.findViewById(R.id.imgViewProviderImageAcceptQuotes);
            tvProviderNameAcceptQuote = (TextView)itemView.findViewById(R.id.tvProviderNameAcceptQuote);
            tvQuotationDateProvider = (TextView)itemView.findViewById(R.id.tvQuotationDateProvider);
            tvQuoteAmount = (TextView)itemView.findViewById(R.id.tvQuoteAmount);
            ratingBarProviderRate = (RatingBar)itemView.findViewById(R.id.ratingBarProviderRate);
            imgBtnAcceptQuote = (ImageButton)itemView.findViewById(R.id.imgBtnAcceptQuote);
            imgBtnDeleteQuote = (ImageButton)itemView.findViewById(R.id.imgBtnDeleteQuote);
            relativeLayoutAcceptQuote = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutAcceptQuote);

            imgBtnAcceptQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.btnAcceptQuote(v , getAdapterPosition());
                }
            });

            imgBtnDeleteQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.btnDeleteQuote(v , getAdapterPosition());
                }
            });

        }

    }


    @Override
    public AcceptQuotesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_accept_quotes , parent , false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AcceptQuotesAdapter.MyViewHolder holder, int position) {

        allQuotes = listAllQuotes.get(position);
        holder.tvProviderNameAcceptQuote.setText(allQuotes.getName());
        holder.tvQuotationDateProvider.setText(Formatter.getDateInFormate(allQuotes.getQuotationDate()));
        holder.tvQuoteAmount.setText(AndyUtils.getSymbolFromHex(allQuotes.getCurrency())+Formatter.formateDigitAfterPoint(allQuotes.getQuotation()));
        holder.ratingBarProviderRate.setRating((float) allQuotes.getRate());
        holder.relativeLayoutAcceptQuote.setBackgroundColor(Color.parseColor(AndyUtils.getBackgroundColor(position % 2 , activity)));
        Glide.with(activity)
                .load(allQuotes.getPicture())
                .asBitmap()
                .placeholder(activity.getResources().getDrawable(R.drawable.default_icon))
                .into(new BitmapImageViewTarget(holder.imgViewProviderImageAcceptQuotes){
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable cirimage = RoundedBitmapDrawableFactory.create(activity.getResources(),resource);
                        cirimage.setCircular(true);
                        holder.imgViewProviderImageAcceptQuotes.setImageDrawable(cirimage);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return listAllQuotes.size();
    }


}
