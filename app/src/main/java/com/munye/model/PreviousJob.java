package com.munye.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akash on 1/31/2017.
 */

public class PreviousJob implements Parcelable{
    private int totalPages , pageNo , requestType , requestStatus;
    private String prviousJobId , jobDate , jobTitle , description , address , issueImage , totalAmount , currency , providerId , providerName ,providerPicture , jobTypeIcon , feedback;
    private double providerRating , userGivenRate;

    public PreviousJob(){

    }

    protected PreviousJob(Parcel in) {
        totalPages = in.readInt();
        pageNo = in.readInt();
        requestType = in.readInt();
        requestStatus = in.readInt();
        prviousJobId = in.readString();
        jobDate = in.readString();
        jobTitle = in.readString();
        description = in.readString();
        address = in.readString();
        issueImage = in.readString();
        totalAmount = in.readString();
        currency = in.readString();
        providerId = in.readString();
        providerName = in.readString();
        providerPicture = in.readString();
        jobTypeIcon = in.readString();
        feedback = in.readString();
        providerRating = in.readDouble();
        userGivenRate = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(totalPages);
        dest.writeInt(pageNo);
        dest.writeInt(requestType);
        dest.writeInt(requestStatus);
        dest.writeString(prviousJobId);
        dest.writeString(jobDate);
        dest.writeString(jobTitle);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeString(issueImage);
        dest.writeString(totalAmount);
        dest.writeString(currency);
        dest.writeString(providerId);
        dest.writeString(providerName);
        dest.writeString(providerPicture);
        dest.writeString(jobTypeIcon);
        dest.writeString(feedback);
        dest.writeDouble(providerRating);
        dest.writeDouble(userGivenRate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PreviousJob> CREATOR = new Creator<PreviousJob>() {
        @Override
        public PreviousJob createFromParcel(Parcel in) {
            return new PreviousJob(in);
        }

        @Override
        public PreviousJob[] newArray(int size) {
            return new PreviousJob[size];
        }
    };


    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
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

    public String getPrviousJobId() {
        return prviousJobId;
    }

    public void setPrviousJobId(String prviousJobId) {
        this.prviousJobId = prviousJobId;
    }

    public String getJobDate() {
        return jobDate;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIssueImage() {
        return issueImage;
    }

    public void setIssueImage(String issueImage) {
        this.issueImage = issueImage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderPicture() {
        return providerPicture;
    }

    public void setProviderPicture(String providerPicture) {
        this.providerPicture = providerPicture;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getJobTypeIcon() {
        return jobTypeIcon;
    }

    public void setJobTypeIcon(String jobTypeIcon) {
        this.jobTypeIcon = jobTypeIcon;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public double getProviderRating() {
        return providerRating;
    }

    public void setProviderRating(double providerRating) {
        this.providerRating = providerRating;
    }

    public double getUserGivenRate() {
        return userGivenRate;
    }

    public void setUserGivenRate(double userGivenRate) {
        this.userGivenRate = userGivenRate;
    }
}
