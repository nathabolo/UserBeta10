package com.munye.parse;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.munye.user.R;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created by Akash on 1/16/2017.
 */

public class HttpRequester {
    public  String URL = "http://jimmiejob.com/index.php/";
    //public String URL = "http://192.168.0.102/jimmiejobs/public/";
    private Map<String, String> map;
    private AsyncTaskCompleteListener mAsynclistener;
    private int serviceCode;
    private Activity activity;
    private AsyncHttpRequest request;
    private HttpURLConnection httpURLConnection;
    private String methodType;


    public HttpRequester(Activity activity, Map<String, String> map,
                         int serviceCode, String methodType,
                         AsyncTaskCompleteListener asyncTaskCompleteListener) {
        this.map = map;
        this.serviceCode = serviceCode;
        this.methodType = methodType;
        this.activity = activity;

        // is Internet Connection Available...
        if (AndyUtils.isNetworkAvailable(activity)) {
            mAsynclistener = (AsyncTaskCompleteListener) asyncTaskCompleteListener;
            request = (AsyncHttpRequest) new AsyncHttpRequest()
                    .executeOnExecutor(Executors.newSingleThreadExecutor(),
                            map.get(URL));
        } else {
            AndyUtils.showToast(activity ,activity.getString(R.string.toast_no_interner_connection));
        }
    }

    class AsyncHttpRequest extends AsyncTask<String , Void ,String> {

        @Override
        protected String doInBackground(String... urls) {
            try{
            AndyUtils.generateLog("url  < === >  " + map.get(URL));
            map.remove(URL);
            try {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                URL url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod(methodType);

                if (methodType.equals(Const.httpRequestType.POST)) {

                    JSONObject keyValueJson = new JSONObject();

                    for (Map.Entry<String, String> entery : map.entrySet()) {
                        keyValueJson.put(entery.getKey(), entery.getValue());
                    }

                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    OutputStream os = httpURLConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(String.valueOf(keyValueJson));
                    writer.flush();
                    writer.close();
                    os.close();

                    return getResponse();


                }
                // It use for only GET response...
                else {
                    return getResponse();
                }
            } catch (Exception e) {
                AndyUtils.generateLog("Http parser exception : "+e);
            } finally {
                httpURLConnection.disconnect();
            }}catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try{
            if (mAsynclistener != null) {
                //if(TextUtils.isEmpty(response))
                    //AndyUtils.showToast(activity , response);
                mAsynclistener.onTaskCompleted(response, serviceCode);
            }
        }catch (Exception e) {
            }
            }

        private String getResponse() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line ;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                return sb.toString();
            } catch (Exception e) {
                AndyUtils.generateLog(String.valueOf(e));
                return null;
            }
        }

    }

}
