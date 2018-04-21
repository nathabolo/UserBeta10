package com.munye.firebase;


import com.munye.utils.PreferenceHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Akash on 1/18/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private PreferenceHelper preferenceHelper;

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        storeIntoPreference(refreshToken);
    }

    private void storeIntoPreference(String token){
        preferenceHelper = PreferenceHelper.getInstance(getApplicationContext());
        preferenceHelper.putDeviceToken(token);
    }
}
