package com.yourcompany.gps_coordinates;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.HashMap;

/**
 * GpsCoordinatesPlugin
 */
public class GpsCoordinatesPlugin implements MethodCallHandler {
  private static final int MY_PERMISSIONS_REQUEST_LOCATION = 895;
  private static final String CHANNEL = "gustash.flutter.plugins/gps_coordinates";
  private Activity _activity;
  private GoogleApiClient _client;
  private LocationRequest _locationRequest;
  private Result _result;
  private boolean hasPermission = false;

  private GpsCoordinatesPlugin(Activity activity) {
    _activity = activity;
  }

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);
    channel.setMethodCallHandler(new GpsCoordinatesPlugin(registrar.activity()));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getGPSCoordinates")) {
      _result = result;
      connectToGoogleClient();
    } else {
      result.notImplemented();
    }
  }

  private void checkPermission() {
    int targetSdkVersion = _activity.getApplicationInfo().targetSdkVersion;
    if (targetSdkVersion >= Build.VERSION_CODES.M) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getGPSPermission();
      } else {
        hasPermission = true;
      }
    }

    if (hasPermission) {
      checkLocationEnabled();
    }
  }

  private void connectToGoogleClient() {
    ConnectionManager _connManager = new ConnectionManager();
    _client = new GoogleApiClient.Builder(_activity)
            .addConnectionCallbacks(_connManager)
            .addOnConnectionFailedListener(_connManager)
            .addApi(LocationServices.API)
            .build();
    _locationRequest = new LocationRequest()
            .setInterval(5)
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    _client.connect();
  }

  private void continueGPSOperation() {
    int targetSdkVersion = _activity.getApplicationInfo().targetSdkVersion;
    if (targetSdkVersion >= 23) {
      if (hasPermission) {
        getGPSCoordinates();
      }
    } else {
      getGPSCoordinates();
    }
  }

  private void getGPSCoordinates() {
    if (LocationServices.FusedLocationApi.getLastLocation(_client) != null) {
      Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(_client);
      returnLocation(lastKnownLocation);
    } else {
      LocationServices.FusedLocationApi.requestLocationUpdates(_client, _locationRequest,
              new MyLocationListener());
    }
  }

  private void checkLocationEnabled() {
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(_locationRequest);
    PendingResult<LocationSettingsResult> result =
            LocationServices.SettingsApi.checkLocationSettings(_client,
                    builder.build());
    result.setResultCallback(new MyLocationSettingsCallback());
  }

  private void getGPSPermission() {
    String requestedPermission = "";
    PackageManager pm = _activity.getPackageManager();

    try {
      PackageInfo packageInfo = pm.getPackageInfo(_activity.getPackageName(),
              PackageManager.GET_PERMISSIONS);
      if (packageInfo != null) {
        for (String permission : packageInfo.requestedPermissions) {
          if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            requestedPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
            break;
          } else if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestedPermission = Manifest.permission.ACCESS_FINE_LOCATION;
            break;
          }
        }
      }

      if (!requestedPermission.isEmpty()) {
        int permissionCheck = ContextCompat.checkSelfPermission(_activity,
                requestedPermission);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(_activity,
                  new String[]{ requestedPermission },
                  MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
          hasPermission = true;
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void returnLocation(Location location) {
    HashMap<String, Double> coordinates = new HashMap<>();
    coordinates.put("lat", location.getLatitude());
    coordinates.put("long", location.getLongitude());
    _client.disconnect();
    _result.success(coordinates);
  }

  private class ConnectionManager implements ConnectionCallbacks, OnConnectionFailedListener {

    @Override
    public void onConnected(@Nullable Bundle bundle) {
      checkPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
  }

  private class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
      returnLocation(location);
    }
  }

  private class MyLocationSettingsCallback implements ResultCallback<LocationSettingsResult> {

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
      final Status status = locationSettingsResult.getStatus();
      switch (status.getStatusCode()) {
        case LocationSettingsStatusCodes.SUCCESS:
          // All location settings are satisfied. The client can
          // initialize location requests here.
          continueGPSOperation();
          break;
        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
          // Location settings are not satisfied, but this can be fixed
          // by showing the user a dialog.
          _result.error("LOCATION DISABLED",
                  "This Android device has it's location disabled",
                  null);
          break;
        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
          // Location settings are not satisfied. However, we have no way
          // to fix the settings so we won't show the dialog.
          _result.error("LOCATION DISABLED",
                  "This Android device has it's location disabled",
                  null);
          break;
      }
    }
  }
}
