package com.munye.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Akash on 1/16/2017.
 */

public class PreferenceHelper {

    public static SharedPreferences app_prefs;

    private final String DEVICE_TOKEN = "device_token";
    private final String TOKEN = "token";
    private final String ID = "id";
    private final String TIME_ZONE = "time_zone";
    private final String USER_NAME = "user_name";
    private final String PROFILE_PICTURE = "profile_picture";
    private final String EMAIL = "email";
    private final String ADDRESS = "address";
    private final String CONTACT_NO = "contact_no";
    private final String COUNTRY_CODE = "country_code";
    private final String PENDING_AMOUNT = "pending_amount";
    private final String GOOGLE_KEY = "google_key";
    private final String STRIPE_KEY = "stripe_key";
    private final String CURRENCY_SYMBOl = "currency_symbol";
    private final String PUBLICKEY="public_key";
    private final  String userToken ="token_bal";
    private final String C_NO = "c_no";
    private final String CVV = "cvv";
    private final String EXP_M="exp_m";
    private final  String EXP_YR ="exp_yr";
    private final String CTYP = "c_typ";
    private final String MUNYE_MESSAGE = "munye_message";

    private final String tokenPurchase="token_purchase";

    private static PreferenceHelper preferenceHelper;

    public PreferenceHelper(){
    }

    public static PreferenceHelper getInstance(Context context) {
        app_prefs = context.getSharedPreferences("tradesman_pref", Context.MODE_PRIVATE);
        if(preferenceHelper == null)
            preferenceHelper = new PreferenceHelper();
        return preferenceHelper;
    }

    public void putDeviceToken(String deviceToken) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(DEVICE_TOKEN, deviceToken);
        edit.commit();
    }

    public String getDeviceToken() {
        return app_prefs.getString(DEVICE_TOKEN, null);
    }

    public void putToken(String token){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(TOKEN, token);
        edit.commit();
    }

    public String getToken(){
        return app_prefs.getString(TOKEN, null);
    }

    public void putId(String id){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(ID, id);
        edit.commit();
    }

    public String getId(){
        return app_prefs.getString(ID, null);
    }

// jddjejfkgjhwefjehrgjhegrjhtgehjrgtehjrth

    public void putCVV(String cvv){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(CVV, cvv);
        edit.commit();
    }

    public String getCVV(){
        return app_prefs.getString(CVV, null);
    }

    public void putCTYP(String ctyp){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(CTYP, ctyp);
        edit.commit();
    }

    public String getCTYP(){
        return app_prefs.getString(CTYP, null);
    }

    public void putC_NO(String c_no){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(C_NO, c_no);
        edit.commit();
    }

    public String getTokenPurchase(){
        return app_prefs.getString(tokenPurchase, null);
    }

    public void putTokenPurchase(String buy_tokens){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(tokenPurchase, buy_tokens);
        edit.commit();
    }

    public String getC_NO(){
        return app_prefs.getString(C_NO, null);
    }


    public void putEXP_YR(String exp_yr){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(EXP_YR, exp_yr);
        edit.commit();
    }

    public String getEXP_YR(){
        return app_prefs.getString(EXP_YR, null);
    }


    public void putEXP_M(String exp_m){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(EXP_M, exp_m);
        edit.commit();
    }

    public String getEXP_M(){
        return app_prefs.getString(EXP_M, null);
    }



    public void putTimeZone(String timeZone){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(TIME_ZONE , timeZone);
        editor.commit();
    }

    public String getTimeZone(){
        return app_prefs.getString(TIME_ZONE , null);
    }

    public void putUserName(String name){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(USER_NAME , name );
        editor.commit();
    }

    public String getUserName(){
        return app_prefs.getString(USER_NAME , null);
    }

    public void putMessage(String msg){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(MUNYE_MESSAGE , msg );
        editor.commit();
    }

    public String getMessage(){
        return app_prefs.getString(MUNYE_MESSAGE , null);
    }

    public void putEmail(String email){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(EMAIL , email );
        editor.commit();
    }
    public void putUserTokens(String token){
        putStringData(userToken,token);
    }
    public String getUserToken(){
        return  app_prefs.getString(userToken,null);
    }
    public String getEmail(){
        return app_prefs.getString(EMAIL , null);
    }


    public void putAddress(String address){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(ADDRESS , address );
        editor.commit();
    }

    public String getAddress(){
        return app_prefs.getString(ADDRESS , null);
    }

    public void putContactNo(String contactno){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(CONTACT_NO , contactno );
        editor.commit();
    }

    public String getContactNo(){
        return app_prefs.getString(CONTACT_NO , null);
    }


    public void putProfilePicture(String profilePicture){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(PROFILE_PICTURE , profilePicture );
        editor.commit();
    }

    public String getProfilePicture(){
        return app_prefs.getString(PROFILE_PICTURE , null);
    }

    public void putCountryCode(String countryCode){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(COUNTRY_CODE , countryCode );
        editor.commit();
    }

    public String getCountryCode(){
        return app_prefs.getString(COUNTRY_CODE , null);
    }


    public void putPendingAmount(String pendingAmount){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(PENDING_AMOUNT , pendingAmount );
        editor.commit();
    }

    public String getPendingAmount(){
        return app_prefs.getString(PENDING_AMOUNT , null);
    }


    public void putGoogleKey(String googleKey){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(GOOGLE_KEY , googleKey );
        editor.commit();
    }

    public String getGoogleKey(){
        return app_prefs.getString(GOOGLE_KEY , null);
    }


    public void putStripeKey(String stripeKey){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(STRIPE_KEY , stripeKey );
        editor.commit();
    }
    private void putStringData(String key, String value){

        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getStripeKey(){
        return app_prefs.getString(STRIPE_KEY , null);
    }


    public void putCurrencySymbol(String currencySymbol){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(CURRENCY_SYMBOl , currencySymbol);
        editor.commit();
    }


    public String getPublicKey(){
        return app_prefs.getString(PUBLICKEY , "no_key_found");
    }

    public void putPublicKey(String strPublicKey){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(PUBLICKEY , strPublicKey);
        editor.commit();
    }


    public String getCurrencySymbol(){
        return app_prefs.getString(CURRENCY_SYMBOl , "$");
    }

    public void logout(){
        putId(null);
        putToken(null);
        app_prefs.edit().clear();
    }

}
