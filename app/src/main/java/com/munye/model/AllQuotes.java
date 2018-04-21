package com.munye.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akash on 1/24/2017.
 */

public class AllQuotes implements Parcelable{

    private String providerId , name , picture , rateCoutn , quotation , quotationDate , currency;
    private double rate;


    public AllQuotes(){

    }

    protected AllQuotes(Parcel in) {
        providerId = in.readString();
        name = in.readString();
        picture = in.readString();
        rateCoutn = in.readString();
        quotation = in.readString();
        quotationDate = in.readString();
        currency = in.readString();
        rate = in.readDouble();
    }

    public static final Creator<AllQuotes> CREATOR = new Creator<AllQuotes>() {
        @Override
        public AllQuotes createFromParcel(Parcel in) {
            return new AllQuotes(in);
        }

        @Override
        public AllQuotes[] newArray(int size) {
            return new AllQuotes[size];
        }
    };

    public String getProviderId() {
        return providerId;
    }

    public void setProvidreId(String id) {
        this.providerId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getRateCoutn() {
        return rateCoutn;
    }

    public void setRateCoutn(String rateCoutn) {
        this.rateCoutn = rateCoutn;
    }

    public String getQuotation() {
        return quotation;
    }

    public void setQuotation(String quotation) {
        this.quotation = quotation;
    }

    public String getQuotationDate() {
        return quotationDate;
    }

    public void setQuotationDate(String quotationDate) {
        this.quotationDate = quotationDate;
    }

    public String getCurrency() {
        return currency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(providerId);
        dest.writeString(name);
        dest.writeString(picture);
        dest.writeString(rateCoutn);
        dest.writeString(quotation);
        dest.writeString(quotationDate);
        dest.writeString(currency);
        dest.writeDouble(rate);
    }
}
