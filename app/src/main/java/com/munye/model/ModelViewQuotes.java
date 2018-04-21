package com.munye.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akash on 1/24/2017.
 */

public class ModelViewQuotes implements Parcelable{

    private String startTime , jobTitle , descripion , address , issueImage , jobIcon , requestId;
    private int requestType , totalQuote ;
    private List<AllQuotes> listAllQuotes;


    public ModelViewQuotes(){

    }

    protected ModelViewQuotes(Parcel in) {
        startTime = in.readString();
        jobTitle = in.readString();
        descripion = in.readString();
        address = in.readString();
        issueImage = in.readString();
        jobIcon = in.readString();
        requestId = in.readString();
        requestType = in.readInt();
        totalQuote = in.readInt();
        listAllQuotes = new ArrayList<>();
        in.readTypedList(listAllQuotes , AllQuotes.CREATOR);
    }

    public static final Creator<ModelViewQuotes> CREATOR = new Creator<ModelViewQuotes>() {
        @Override
        public ModelViewQuotes createFromParcel(Parcel in) {
            return new ModelViewQuotes(in);
        }

        @Override
        public ModelViewQuotes[] newArray(int size) {
            return new ModelViewQuotes[size];
        }
    };

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDescripion() {
        return descripion;
    }

    public void setDescripion(String descripion) {
        this.descripion = descripion;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIssueImage() {
        return issueImage;
    }

    public void setIssueImage(String issueImage) {
        this.issueImage = issueImage;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public int getTotalQuote() {
        return totalQuote;
    }

    public void setTotalQuote(int totalQuote) {
        this.totalQuote = totalQuote;
    }

    public String getJobIcon() {
        return jobIcon;
    }

    public void setJobIcon(String jobIcon) {
        this.jobIcon = jobIcon;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<AllQuotes> getListAllQuotes() {

        return listAllQuotes;

    }
    public void setListAllQuotes(List<AllQuotes> listAllQuotes) {
        this.listAllQuotes = listAllQuotes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startTime);
        dest.writeString(jobTitle);
        dest.writeString(descripion);
        dest.writeString(address);
        dest.writeString(issueImage);
        dest.writeString(jobIcon);
        dest.writeString(requestId);
        dest.writeInt(requestType);
        dest.writeInt(totalQuote);
        dest.writeTypedList(listAllQuotes);
    }
}
