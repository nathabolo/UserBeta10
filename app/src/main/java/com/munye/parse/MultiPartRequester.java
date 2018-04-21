package com.munye.parse;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.munye.user.R;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Akash on 1/17/2017.
 */

public class MultiPartRequester {
    public  String URL = "http://jimmiejob.com/index.php/";
    private Map<String, String> map;
    private AsyncTaskCompleteListener mAsynclistener;
    private int serviceCode;
    private AsyncHttpRequest request;
    private HttpURLConnection httpURLConnection;
    String boundary = "-------------" + System.currentTimeMillis();
    private static final String LINE_FEED = "\r\n";
    private static final String TWO_HYPHENS = "--";
    private DataOutputStream dos;

    public MultiPartRequester(Activity activity , Map<String , String> map , int serviceCode , AsyncTaskCompleteListener asyncTaskCompleteListener){
        this.map = map;
        this.serviceCode = serviceCode;
        this.mAsynclistener = asyncTaskCompleteListener;

        if (AndyUtils.isNetworkAvailable(activity)) {
            new AsyncHttpRequest().execute(map.get(URL));
        } else {
            AndyUtils.showToast(activity , activity.getString(R.string.toast_no_interner_connection));
        }
    }

    class AsyncHttpRequest extends AsyncTask<String , Void , String>{

        @Override
        protected String doInBackground(String... urls) {
            AndyUtils.generateLog("URL ====>"+urls[0]);
            map.remove(URL);
            try{
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                URL url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(60000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                dos = new DataOutputStream(httpURLConnection.getOutputStream());

                for(Map.Entry<String, String> entery : map.entrySet()){
                    if((entery.getKey().equalsIgnoreCase(Const.Params.PICTURE) || entery.getKey().equalsIgnoreCase(Const.Params.IMAGE)) && !TextUtils.isEmpty(entery.getValue())){
                        AndyUtils.generateLog("PICTURE FILE CREATED");
                        File f = new File(entery.getValue());
                        addFilePart(entery.getKey() , f);
                    }
                    else {
                        AndyUtils.generateLog(entery.getKey()+" : "+ entery.getValue());
                        addParameterPart(entery.getKey() , entery.getValue());
                    }
                }

                //Read the response...
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                return sb.toString();

            }
            catch (Exception e){
                AndyUtils.generateLog(""+e);

            }
            finally {
                try {
                    httpURLConnection.disconnect();
                }catch (Exception e){

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {

            if (mAsynclistener != null) {
                mAsynclistener.onTaskCompleted(response, serviceCode);
            }
        }

        private void addParameterPart(String fieldName, String value) {
            try {
                dos.writeBytes(TWO_HYPHENS + boundary + LINE_FEED);
                dos.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"" + LINE_FEED + LINE_FEED);
                dos.writeBytes(value + LINE_FEED);
            } catch (IOException e) {
                AndyUtils.generateLog("Multipart parameter : "+e);
            }
        }

        private void addFilePart(String fieldName, File uploadFile) {
            FileInputStream fStream = null;
            try {
                dos.writeBytes(TWO_HYPHENS + boundary + LINE_FEED);
                dos.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\";filename=\"" + uploadFile.getName() + "\"" + LINE_FEED);
                dos.writeBytes(LINE_FEED);

                fStream = new FileInputStream(uploadFile);
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int length = -1;

                while ((length = fStream.read(buffer)) != -1) {
                    dos.write(buffer, 0, length);
                }
                dos.writeBytes(LINE_FEED);
                dos.writeBytes(TWO_HYPHENS + boundary + TWO_HYPHENS + LINE_FEED);
            } catch (IOException e) {
                AndyUtils.generateLog("Multipart addFile : "+e);
            }
            finally {
                try {
                    if(fStream != null)
                    fStream.close();
                } catch (IOException e) {
                    AndyUtils.generateLog("file close exception"+e);
                }
            }
        }

    }

}
