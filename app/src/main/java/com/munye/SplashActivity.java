package com.munye;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.munye.user.R;
import com.munye.parse.AsyncTaskCompleteListener;
import com.munye.parse.HttpRequester;
import com.munye.utils.AndyUtils;
import com.munye.utils.Const;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.munye.SplashActivity.AppStatus.context;

public class SplashActivity extends ActionBarBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener {

    String server_url = "http://jimmiejob.com/index.php/";
    private Button btnSplashSignIn, btnSplashRegister;
    private Timer checkInternetTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferenceHelper.putTimeZone(getTimeZone());
        btnSplashSignIn = (Button) findViewById(R.id.btnSplashSignIn);
        btnSplashRegister = (Button) findViewById(R.id.btnSplashRegister);
        btnSplashSignIn.setOnClickListener(this);
        btnSplashRegister.setOnClickListener(this);

        if (!isNotServerRunning(SplashActivity.this))
            buildDialog(SplashActivity.this).show();
        else {

        }

        if (AppStatus.getInstance(this).isOnline()) {

            // Toast.makeText(getApplicationContext(), "Online", Toast.LENGTH_SHORT).show();

        } else {

            //  Toast.makeText(getApplicationContext(), "Offline", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        stopInternetCheck();
        checkInternetStatus();
        if (!AndyUtils.isNetworkAvailable(this))
            startInternetCheck();

    }

    private void checkInternetStatus() {
        if (AndyUtils.isNetworkAvailable(this) && preferenceHelper.getId() == null) {
            stopInternetCheck();
            getSettings();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnSplashRegister.setVisibility(View.VISIBLE);
                    btnSplashSignIn.setVisibility(View.VISIBLE);
                }
            });
            closeInternetDialog();
        } else if (AndyUtils.isNetworkAvailable(this) && preferenceHelper.getId() != null) {
            getSettings();
        } else {
            openInternetDialog(this);
        }
    }

    private void startInternetCheck() {

        checkInternetTimer = new Timer();
        TimerTask taskCheckInterner = new TimerTask() {
            @Override
            public void run() {
                checkInternetStatus();
            }
        };
        checkInternetTimer.scheduleAtFixedRate(taskCheckInterner, 0, 5000);
    }

    private void stopInternetCheck() {
        if (checkInternetTimer != null) {
            checkInternetTimer.cancel();
            checkInternetTimer.purge();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopInternetCheck();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSplashSignIn:
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                break;

            case R.id.btnSplashRegister:
                startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
                break;

            default:
                AndyUtils.generateLog("No action");
                break;
        }
    }

    private String getTimeZone() {
        return java.util.TimeZone.getDefault().getID();
    }

    private void getSettings() {
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.URL, Const.ServiceType.GET_SETTINGS);

        new HttpRequester(this, map, Const.ServiceCode.GET_SETTINGS, Const.httpRequestType.POST, this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        if (serviceCode == Const.ServiceCode.GET_SETTINGS && dataParser.isSuccess(response)) {
            dataParser.parseSettings(response);
            if (preferenceHelper.getId() != null) {
                stopInternetCheck();
                startActivity(new Intent(this, MapActivity.class));
                finish();
            }
        }
    }

    public boolean isNotServerRunning(final Context context) {
        final RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Toast.makeText(SplashActivity.this, "Server is up and running", Toast.LENGTH_LONG).show();
                        requestQueue.stop();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                buildDialog(context).show();

                volleyError.printStackTrace();
                requestQueue.stop();

            }
        });

        requestQueue.add(stringRequest);

        return true;
    }

    public AlertDialog.Builder buildDialog(Context cxt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
        builder.setTitle("Server Connection Failed...!");
        builder.setMessage("Please wait for the server to respond or try again later...!");

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopInternetCheck();
    }

    public static class AppStatus {

        static Context context;
        private static AppStatus instance = new AppStatus();
        ConnectivityManager connectivityManager;
        NetworkInfo wifiInfo, mobileInfo;
        boolean connected = false;

        public static AppStatus getInstance(Context ctx) {
            context = ctx.getApplicationContext();
            return instance;
        }

        public boolean isOnline() {
            try {
                connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                connected = networkInfo != null && networkInfo.isAvailable() &&
                        networkInfo.isConnected();
                return connected;


            } catch (Exception e) {
                System.out.println("CheckConnectivity Exception: " + e.getMessage());

            }
            return connected;
        }
    }
}
