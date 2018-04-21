package com.munye.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.munye.user.R;
import com.munye.model.CardDetail;

import java.util.List;

/**
 * Created by Akash on 2/16/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private List<CardDetail> listCard;
    CardRemoveListener cardRemoveListener;


    public interface CardRemoveListener{
        void removeCrad(View v, int position);
        void makeDefaultCard(View v , int position);
    }

    public CardAdapter(List<CardDetail> listCard , CardRemoveListener cardRemoveListener){
        this.listCard = listCard;
        this.cardRemoveListener = cardRemoveListener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvListCardNo;
        TextView tvListRemoveCard;
        LinearLayout linearLayoutCardDetail;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvListCardNo = (TextView)itemView.findViewById(R.id.tvListCardNo);
            tvListRemoveCard = (TextView)itemView.findViewById(R.id.tvListRemoveCard);
            linearLayoutCardDetail = (LinearLayout)itemView.findViewById(R.id.linearLayoutCardDetail);


            tvListRemoveCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardRemoveListener.removeCrad(v, getAdapterPosition());
                }
            });

            linearLayoutCardDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardRemoveListener.makeDefaultCard(v, getAdapterPosition());
                }
            });
        }
    }

    @Override
    public CardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_list , parent , false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardAdapter.MyViewHolder holder, int position) {

        CardDetail cardDetail = listCard.get(position);
        holder.tvListCardNo.setText("************"+cardDetail.getCardNo());

    }

    @Override
    public int getItemCount() {
        return listCard.size();
    }
}
