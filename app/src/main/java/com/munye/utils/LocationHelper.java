package com.munye.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Akash on 1/19/2017.
 */

public class LocationHelper implements com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private Context context;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private OnLocationReceived mLocationReceived;
    private LatLng latLong;

    //To set the request intervals...
    private final int INTERVAL = 5000; /*mili seconds*/
    private final int FAST_INTERVAL = 1000;
    private boolean isLocationReceived = false;


    public interface OnLocationReceived {
        public void onLocationReceived(LatLng latlong);

        public void onLocationReceived(Location location);

        public void onConntected(Bundle bundle);

        public void onConntected(Location location);
    }


    public LocationHelper(Context context) {
        this.context = context;
        createLocationRequest();
        setupGoogleApiClient();
        mGoogleApiClient.connect();

    }


    protected synchronized void setupGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    public void setLocationReceivedLister(OnLocationReceived mLocationReceived) {
        this.mLocationReceived = mLocationReceived;
    }

    public void onStart() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        } else {
            startPeriodicUpdates();
        }
    }

    public void onResume() {
        if (mGoogleApiClient.isConnected()) {
            startPeriodicUpdates();
        }
    }


    public void onPause() {
        if (mGoogleApiClient.isConnected()) {
            stopPeriodicUpdates();
        }
    }

    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            stopPeriodicUpdates();
        }
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onLocationChanged(Location location) {

        if (!isLocationReceived) {
            mLocationReceived.onConntected(location);
            isLocationReceived = true;
        }

        if (mLocationReceived != null) {
            mLocationReceived.onLocationReceived(location);
        }
        latLong = getLatLng(location);
        if (mLocationReceived != null && latLong != null) {
            mLocationReceived.onLocationReceived(latLong);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startPeriodicUpdates();
        if (mLocationReceived != null)
            mLocationReceived.onConntected(bundle);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void startPeriodicUpdates() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
            onLocationChanged(location);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    private void stopPeriodicUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FAST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public LatLng getLatLng(Location currentLocation) {
        if (currentLocation != null) {
            LatLng latLong = new LatLng(currentLocation.getLatitude(),
                    currentLocation.getLongitude());
            return latLong;
        } else {

            return null;
        }
    }
}
