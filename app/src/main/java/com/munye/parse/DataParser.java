package com.munye.parse;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.munye.user.R;
import com.munye.SignInActivity;
import com.munye.model.ActiveJob;
import com.munye.model.AllQuotes;
import com.munye.model.CardDetail;
import com.munye.model.CountryCode;
import com.munye.model.ModelViewQuotes;
import com.munye.model.PreviousJob;
import com.munye.model.Provider;
import com.munye.model.ProviderType;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;
import com.munye.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Akash on 1/16/2017.
 */

public class DataParser {
    private Activity activity;
    private PreferenceHelper preferenceHelper;
    private static final String KEY_ERROR_MESSAGES = "error_messages";
    private static final String SUCCESS = "success";
    private static final String PROVIDER_LIST = "provider_list";
    private static final String PROVIDER_TYPE ="types";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String PHONE_CODE = "phone_code";
    private static final String ADDRESS = "address";
    private static final String TOKEN = "token";
    private static final String ID = "id";
    private static final String PICTURE = "picture";
    private static final String START_TIME = "start_time";
    private static final String JOB_TITLE = "job_title";
    private static final String DESCRIPTION = "description";
    private static final String ISSUE_IMAGE = "issue_image";
    private static final String REQUEST_TYPE = "request_type";
    private static final String QUOTES = "quotes";
    private static final String ALL_QUOTES = "all_quotes";
    private static final String QUOTATION = "quotation";
    private static final String CURRENCY = "currency";
    private static final String QUOTATION_DATE = "quotation_date";
    private static final String RATE = "rate";
    private static final String JOBS = "jobs";
    private static final String REQUEST_STATUS = "request_status";
    private static final String TOTAL = "total";
    private static final String PROVIDER = "provider";
    private static final String TYPE = "type";
    private static final String TOTAL_PAGE = "total_page";
    private static final String PREVIOUS_JOBS = "previous_jobs";
    private static final String FEEDBACK = "feedback";
    private static final String USER_GIVEN_COMMENT = "user_given_comment";
    private static final String USER_GIVEN_RATE = "user_given_rate";
    private static final String ADMIN_CHARGE = "admin_charge";
    private static final String CARD_ID = "card_id";
    private static final String LAST_FOUR = "last_four";
    private static final String CARD_TOKEN = "card_token";
    private static final String CARD_TYPE = "card_type";
    private static final String CUSTOMER_ID = "customer_id";
    private static final String PAYMENTS = "payments";
    private static final String SYMBOL = "symbol";
    private static final String INSTALLATION_SETTINGS = "installation_settings";
    private static final String PROVIDER_MAP_KEY = "provider_map_key";
    private static final String STRIPE_PUBLISHABLE_KEY = "stripe_publishable_key";
    private static final String AMOUNT = "amount";
    private static final String KEY="key";
    public static int totalPage;

    public DataParser(Activity activity){
        this.activity = activity;
        preferenceHelper = PreferenceHelper.getInstance(activity);
    }


    public boolean isSuccess(String response){
        if(TextUtils.isEmpty(response)){
            return false;
        }
        try{
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getBoolean(SUCCESS)){
                return true;
            }
            else {
                //it is use for check token validity...If token changes then user get logout from current activity..
                if (jsonObject.getJSONArray(KEY_ERROR_MESSAGES).getInt(0) == Const.SESSON_EXPIRE_ERROR) {
                    Intent i = new Intent(activity, SignInActivity.class);
                    preferenceHelper.logout();
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(i);
                    activity.finish();
                } else {
                    //Shows the error message as per server sends the error message...
                    AndyUtils.showErrorToast(jsonObject.getJSONArray(KEY_ERROR_MESSAGES).getInt(0),activity);
                    return false;
                }
            }
        }
        catch (Exception e){
            AndyUtils.generateLog("isSuccess: "+e);
        }
        return false;
    }



    public boolean isPandingPayment(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getJSONArray(KEY_ERROR_MESSAGES).getInt(0) == Const.PENDING_PAYMENT){
                return true;
            }
        }
        catch (Exception e){
            AndyUtils.generateLog("Exception in check pending payment"+e);
        }
        return false;
    }


    public void parseSettings(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            preferenceHelper.putGoogleKey(jsonObject.getJSONObject(INSTALLATION_SETTINGS).getString(PROVIDER_MAP_KEY));
            preferenceHelper.putStripeKey(jsonObject.getJSONObject(INSTALLATION_SETTINGS).getString(STRIPE_PUBLISHABLE_KEY));
            preferenceHelper.putCurrencySymbol(jsonObject.getJSONObject(INSTALLATION_SETTINGS).getString(SYMBOL));
            preferenceHelper.putPublicKey(jsonObject.getJSONObject(INSTALLATION_SETTINGS).getString(KEY));
        }
        catch (Exception e){
            AndyUtils.generateLog("Exception in parse setting"+e);
        }
    }


    //It call when call login and register service...
    public void parseUserData(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            preferenceHelper.putUserName(jsonObject.getString(NAME));
            preferenceHelper.putEmail(jsonObject.getString(EMAIL));
            preferenceHelper.putContactNo(jsonObject.getString(PHONE));
            preferenceHelper.putCountryCode(jsonObject.getString(PHONE_CODE));
            preferenceHelper.putAddress(jsonObject.getString(ADDRESS));
            preferenceHelper.putToken(jsonObject.getString(TOKEN));
            preferenceHelper.putId(jsonObject.getString(ID));
            preferenceHelper.putC_NO(jsonObject.getString("c_no"));
            preferenceHelper.putCVV(jsonObject.getString("cvv"));
            preferenceHelper.putEXP_YR(jsonObject.getString("exp_yr"));
            preferenceHelper.putCTYP(jsonObject.getString("c_typ"));
            preferenceHelper.putEXP_M(jsonObject.getString("exp_m"));
            preferenceHelper.putUserTokens(jsonObject.getString("token_bal"));
            preferenceHelper.putTokenPurchase(jsonObject.getString("token_purchase"));
            preferenceHelper.putMessage(jsonObject.getString("munye_message"));
            preferenceHelper.putProfilePicture(jsonObject.getString(PICTURE));

        }
        catch (Exception e){
            AndyUtils.generateLog("parseUserData Exception: "+e);
        }
    }


    public void parsePendingAmontDetail(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            preferenceHelper.putPendingAmount(jsonObject.getString(AMOUNT));
        }
        catch (Exception e){
            AndyUtils.generateLog("Exception in parsing pending amount  :"+e);
        }
    }


    public void parseOldRequstPay(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            AndyUtils.showErrorToast(jsonObject.getJSONArray(KEY_ERROR_MESSAGES).getInt(0),activity);
        }
        catch (Exception e){
            AndyUtils.generateLog("Exception in old request pay : "+e);
        }
    }

    //It returns nearest providers..
    public ArrayList<Provider> parseNearestProvider(String response) {
        ArrayList<Provider> listProvider = new ArrayList<>();
        if (TextUtils.isEmpty(response))
            return listProvider;
        try {
            JSONArray jsonArray = new JSONObject(response)
                    .getJSONArray(PROVIDER_LIST);
            for (int i = 0; i < jsonArray.length(); i++) {
                Provider provider = new Provider();
                provider.setId(jsonArray.getJSONObject(i).getInt(
                        Const.Params.ID));
                provider.setName(jsonArray.getJSONObject(i).getString(
                        Const.Params.NAME));
                provider.setDistance(jsonArray.getJSONObject(i).getDouble(
                        Const.Params.DISTANCE));
                provider.setDistanceUnit(jsonArray.getJSONObject(i).getString(
                        Const.Params.DISTANCE_UNIT));
                provider.setLatitude(jsonArray.getJSONObject(i).getDouble(
                        Const.CURRENT_LATITUDE));
                provider.setLongitude(jsonArray.getJSONObject(i).getDouble(
                        Const.CURRENT_LONGITUDE));
                listProvider.add(provider);
            }
        } catch (JSONException e) {
            AndyUtils.generateLog("Prase provider Exception"+e);
        }
        return listProvider;
    }


    //It retuns type of provider available....
    public ArrayList<ProviderType> parseTypesOfProviders(String response , ArrayList<ProviderType> listProviderType){

        if(TextUtils.isEmpty(response))
            return listProviderType;

        try{
            JSONArray jsonArray = new JSONObject(response).getJSONArray(PROVIDER_TYPE);

            for(int i=0 ; i<jsonArray.length() ; i++){
                ProviderType providerType = new ProviderType();
                providerType.setId(jsonArray.getJSONObject(i).getString(ID));
                providerType.setName(jsonArray.getJSONObject(i).getString(NAME));
                providerType.setPicture(jsonArray.getJSONObject(i).getString(PICTURE));
                listProviderType.add(providerType);
            }
        }
        catch (Exception e){
            AndyUtils.generateLog("Provider type parser Exception"+e);
        }

        return listProviderType;
    }




    /*This method is use for parse data of quotes*/
    public ArrayList<ModelViewQuotes> parseQuotes(String response , ArrayList<ModelViewQuotes> listQuotes){

        ArrayList<AllQuotes> listAllQuotes;
        int allQuotesSize;

        if(TextUtils.isEmpty(response)){
            AndyUtils.generateLog("RETURN ZERO");
            return listQuotes;
        }

        try {
            JSONArray jsonArray = new JSONObject(response).getJSONArray(QUOTATION);
            JSONArray quotesArray;

            for(int i=0 ; i<jsonArray.length() ; i++){
                ModelViewQuotes modelViewQuotes = new ModelViewQuotes();
                modelViewQuotes.setRequestId(jsonArray.getJSONObject(i).getString(ID));

                modelViewQuotes.setStartTime(jsonArray.getJSONObject(i).getString(START_TIME));
                modelViewQuotes.setJobTitle(jsonArray.getJSONObject(i).getString(JOB_TITLE));
                modelViewQuotes.setDescripion(jsonArray.getJSONObject(i).getString(DESCRIPTION));
                modelViewQuotes.setAddress(jsonArray.getJSONObject(i).getString(ADDRESS));
                modelViewQuotes.setIssueImage(jsonArray.getJSONObject(i).getString(ISSUE_IMAGE));
                modelViewQuotes.setRequestType(jsonArray.getJSONObject(i).getInt(REQUEST_TYPE));
                modelViewQuotes.setTotalQuote(jsonArray.getJSONObject(i).getInt(QUOTES));
                modelViewQuotes.setJobIcon(jsonArray.getJSONObject(i).getJSONObject(TYPE).getString(PICTURE));
                quotesArray = jsonArray.getJSONObject(i).getJSONArray(ALL_QUOTES);
                allQuotesSize = quotesArray.length();
                listAllQuotes = new ArrayList<>();
                for(int j=0 ; j<allQuotesSize ; j++){
                    AllQuotes allQuotes = new AllQuotes();
                    allQuotes.setName(quotesArray.getJSONObject(j).getString(NAME));
                    allQuotes.setPicture(quotesArray.getJSONObject(j).getString(PICTURE));
                    allQuotes.setQuotation(quotesArray.getJSONObject(j).getString(QUOTATION));
                    allQuotes.setCurrency(quotesArray.getJSONObject(j).getString(SYMBOL));
                    allQuotes.setQuotationDate(quotesArray.getJSONObject(j).getString(QUOTATION_DATE));
                    allQuotes.setProvidreId(quotesArray.getJSONObject(j).getString(ID));
                    allQuotes.setRate(quotesArray.getJSONObject(j).getDouble(RATE));
                    listAllQuotes.add(allQuotes);
                }
                modelViewQuotes.setListAllQuotes(listAllQuotes);
                listQuotes.add(modelViewQuotes);
            }


        }catch (Exception e){
            AndyUtils.generateLog("Exception in parse quotes "+e);
        }
        return  listQuotes;
    }




    /*Active job parse*/
    public ArrayList<ActiveJob> parseActiveJob(String response, ArrayList<ActiveJob> listActiveJob){

        if(TextUtils.isEmpty(response)){
            return listActiveJob;
        }
        try{

            JSONArray jsonArray = new JSONObject(response).getJSONArray(JOBS);

            for(int i=0 ; i<jsonArray.length() ; i++){
                ActiveJob activeJob = new ActiveJob();
                activeJob.setActiveJobId(jsonArray.getJSONObject(i).getString(ID));
                activeJob.setDate(jsonArray.getJSONObject(i).getString(START_TIME));
                activeJob.setAddress(jsonArray.getJSONObject(i).getString(ADDRESS));
                activeJob.setTitle(jsonArray.getJSONObject(i).getString(JOB_TITLE));
                activeJob.setDescription(jsonArray.getJSONObject(i).getString(DESCRIPTION));
                activeJob.setIssueImage(jsonArray.getJSONObject(i).getString(ISSUE_IMAGE));
                activeJob.setRequestType(jsonArray.getJSONObject(i).getInt(REQUEST_TYPE));
                activeJob.setRequestStatus(jsonArray.getJSONObject(i).getInt(REQUEST_STATUS));
                activeJob.setAmount(jsonArray.getJSONObject(i).getString(TOTAL));
                activeJob.setCurrency(jsonArray.getJSONObject(i).getString(SYMBOL));
                activeJob.setProviderId(jsonArray.getJSONObject(i).getJSONObject(PROVIDER).getString(ID));
                activeJob.setProviderName(jsonArray.getJSONObject(i).getJSONObject(PROVIDER).getString(NAME));
                activeJob.setProviderPicture(jsonArray.getJSONObject(i).getJSONObject(PROVIDER).getString(PICTURE));
                activeJob.setProviderRate(jsonArray.getJSONObject(i).getJSONObject(PROVIDER).getDouble(RATE));
                activeJob.setJobTypeIcon(jsonArray.getJSONObject(i).getJSONObject(TYPE).getString(PICTURE));
                activeJob.setAdminCharge(jsonArray.getJSONObject(i).getJSONObject(TYPE).getString(ADMIN_CHARGE));
                listActiveJob.add(activeJob);
            }

        }catch (Exception e){
            AndyUtils.generateLog("Active job parsing exception : "+e);
        }

        return listActiveJob;
    }


    public ArrayList<CardDetail> parseCardDetail(String response , ArrayList<CardDetail> listCard){

        int length;
        if(TextUtils.isEmpty(response)){
            return listCard;
        }
        else {
            try{
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray(PAYMENTS);
                length = jsonArray.length();

                for(int i=0 ; i<length ; i++){
                    CardDetail cardDetail = new CardDetail();
                    cardDetail.setCardId(jsonArray.getJSONObject(i).getString(CARD_ID));
                    cardDetail.setCardNo(jsonArray.getJSONObject(i).getString(LAST_FOUR));
                    cardDetail.setCardToken(jsonArray.getJSONObject(i).getString(CARD_TOKEN));
                    cardDetail.setCardType(jsonArray.getJSONObject(i).getString(CARD_TYPE));
                    cardDetail.setCustomerId(jsonArray.getJSONObject(i).getString(CUSTOMER_ID));
                    listCard.add(cardDetail);
                }
            }
            catch (Exception e){
                AndyUtils.generateLog("Exception on card parsing"+e);
            }
        }

        return listCard;
    }



    /*Use for parse previous job data.....*/
    public ArrayList<PreviousJob> parsePreviousJob(String response , ArrayList<PreviousJob> listPreviousJob){

        int length;
        if(TextUtils.isEmpty(response)){
            return listPreviousJob;
        }
        else {
            try{

                JSONObject jsonObject = new JSONObject(response);
                totalPage = jsonObject.getInt(TOTAL_PAGE);
                JSONArray jsonArray = jsonObject.getJSONArray(PREVIOUS_JOBS);
                length = jsonArray.length();

                for(int i=0 ; i<length ; i++){
                    PreviousJob previousJobs = new PreviousJob();
                    previousJobs.setPrviousJobId(jsonArray.getJSONObject(i).getString(ID));
                    previousJobs.setJobDate(jsonArray.getJSONObject(i).getString(START_TIME));
                    previousJobs.setJobTitle(jsonArray.getJSONObject(i).getString(JOB_TITLE));
                    previousJobs.setDescription(jsonArray.getJSONObject(i).getString(DESCRIPTION));
                    previousJobs.setAddress(jsonArray.getJSONObject(i).getString(ADDRESS));
                    previousJobs.setIssueImage(jsonArray.getJSONObject(i).getString(ISSUE_IMAGE));
                    previousJobs.setRequestType(jsonArray.getJSONObject(i).getInt(REQUEST_TYPE));
                    previousJobs.setRequestStatus(jsonArray.getJSONObject(i).getInt(REQUEST_STATUS));
                    previousJobs.setTotalAmount(jsonArray.getJSONObject(i).getString(TOTAL));
                    previousJobs.setCurrency(jsonArray.getJSONObject(i).getString(SYMBOL));
                    previousJobs.setProviderId(jsonArray.getJSONObject(i).getJSONObject(PROVIDER).getString(ID));
                    previousJobs.setProviderName(jsonArray.getJSONObject(i).getJSONObject(PROVIDER).getString(NAME));
                    previousJobs.setProviderPicture(jsonArray.getJSONObject(i).getJSONObject(PROVIDER).getString(PICTURE));
                    previousJobs.setProviderRating(jsonArray.getJSONObject(i).getJSONObject(PROVIDER).getDouble(RATE));
                    previousJobs.setJobTypeIcon(jsonArray.getJSONObject(i).getJSONObject(TYPE).getString(PICTURE));
                    previousJobs.setFeedback(jsonArray.getJSONObject(i).getJSONObject(FEEDBACK).getString(USER_GIVEN_COMMENT));
                    previousJobs.setUserGivenRate(jsonArray.getJSONObject(i).getJSONObject(FEEDBACK).getDouble(USER_GIVEN_RATE));

                    listPreviousJob.add(previousJobs);
                }
            }catch (Exception e){
                AndyUtils.generateLog("Exception in parsing previous jobs"+e);
            }
        }

        return listPreviousJob;
    }



    /*Use for parse push messages*/
    public static class parsePushMessage{
        public static int getPushId(String response){
            try {
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject.getInt(Const.PushStatus.PUSH_ID);

            } catch (JSONException e) {
                AndyUtils.generateLog("Push parsing id exception"+e);
            }
            return 0;
        }

        public static String getRequestId(String response){
            try {
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject.getString(Const.PushStatus.REQUEST_ID);
            } catch (JSONException e) {
                AndyUtils.generateLog("Push parsing requestId Exception : "+e);
            }
            return null;
        }
    }


    public ArrayList<CountryCode> parseCountryCode(){

        ArrayList<CountryCode> listCountry = new ArrayList<>();
        try{
            InputStream inputStream = activity.getResources().openRawResource(
                    R.raw.countrycodes);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            JSONArray countryArray = new JSONArray(result.toString());

            for(int i=0 ; i<countryArray.length() ; i++){
                CountryCode countryCode = new CountryCode();
                countryCode.setCountryCode(countryArray.getJSONObject(i).getString("phone-code"));
                countryCode.setCountryName(countryArray.getJSONObject(i).getString(NAME));
                listCountry.add(countryCode);
            }
        }
        catch (Exception e){
            AndyUtils.generateLog("Parsing exception of country code"+e);
        }

        return listCountry;
    }

}
