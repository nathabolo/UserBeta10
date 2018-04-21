package com.munye.parse;

/**
 * Created by Akash on 1/16/2017.
 */

public interface AsyncTaskCompleteListener {
    void onTaskCompleted(String response, int serviceCode);
}
