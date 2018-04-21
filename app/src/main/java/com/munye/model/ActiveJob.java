package com.munye.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akash on 1/30/2017.
 */

public class ActiveJob implements Parcelable{

    private String title , date , description , activeJobId , address , issueImage , amount , currency ,providerId , providerName , providerPicture  , jobTypeIcon , adminCharge;
    private double providerRate;
    private int requestType , requestStatus;


    public ActiveJob(){

    }

    protected ActiveJob(Parcel in) {
        title = in.readString();
        date = in.readString();
        description = in.readString();
        activeJobId = in.readString();
        address = in.readString();
        issueImage = in.readString();
        amount = in.readString();
        currency = in.readString();
        providerId = in.readString();
        providerName = in.readString();
        providerPicture = in.readString();
        jobTypeIcon = in.readString();
        providerRate = in.readDouble();
        requestType = in.readInt();
        requestStatus = in.readInt();
        adminCharge = in.readString();
    }

    public static final Creator<ActiveJob> CREATOR = new Creator<ActiveJob>() {
        @Override
        public ActiveJob createFromParcel(Parcel in) {
            return new ActiveJob(in);
        }

        @Override
        public ActiveJob[] newArray(int size) {
            return new ActiveJob[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getActiveJobId() {
        return activeJobId;
    }

    public void setActiveJobId(String activeJobId) {
        this.activeJobId = activeJobId;
    }

    public String getIssueImage() {
        return issueImage;
    }

    public void setIssueImage(String issueImage) {
        this.issueImage = issueImage;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderPicture() {
        return providerPicture;
    }

    public void setProviderPicture(String providerPicture) {
        this.providerPicture = providerPicture;
    }

    public String getJobTypeIcon() {
        return jobTypeIcon;
    }

    public void setJobTypeIcon(String jobTypeIcon) {
        this.jobTypeIcon = jobTypeIcon;
    }

    public double getProviderRate() {
        return providerRate;
    }

    public void setProviderRate(double providerRate) {
        this.providerRate = providerRate;
    }

    public String getAdminCharge() {
        return adminCharge;
    }

    public void setAdminCharge(String adminCharge) {
        this.adminCharge = adminCharge;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(activeJobId);
        dest.writeString(address);
        dest.writeString(issueImage);
        dest.writeString(amount);
        dest.writeString(currency);
        dest.writeString(providerId);
        dest.writeString(providerName);
        dest.writeString(providerPicture);
        dest.writeString(jobTypeIcon);
        dest.writeDouble(providerRate);
        dest.writeInt(requestType);
        dest.writeInt(requestStatus);
        dest.writeString(adminCharge);
    }
}
