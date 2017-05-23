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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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
  private ConnectionManager _connManager;
  private int targetSdkVersion = 0;
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
      targetSdkVersion = _activity.getApplicationInfo().targetSdkVersion;
      if (targetSdkVersion >= 23) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          getGPSPermission();
        } else {
          hasPermission = true;
        }
        if (hasPermission) {
          getGPSCoordinates(_activity);
        }
      } else {
        getGPSCoordinates(_activity);
      }
    } else {
      result.notImplemented();
    }
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

  private void getGPSCoordinates(Context context) {
    _connManager = new ConnectionManager();
    _client = new GoogleApiClient.Builder(context)
            .addConnectionCallbacks(_connManager)
            .addOnConnectionFailedListener(_connManager)
            .addApi(LocationServices.API)
            .build();
    _locationRequest = new LocationRequest()
            .setInterval(5)
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    _client.connect();
  }

  private void returnLocation(Location location) {
    HashMap<String, Double> coordinates = new HashMap<>();
    coordinates.put("lat", location.getLatitude());
    coordinates.put("long", location.getLongitude());
    _result.success(coordinates);
  }

  private class ConnectionManager implements ConnectionCallbacks, OnConnectionFailedListener {

    @Override
    public void onConnected(@Nullable Bundle bundle) {
      if (LocationServices.FusedLocationApi.getLastLocation(_client) != null) {
        Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(_client);
        returnLocation(lastKnownLocation);
      } else {
        LocationServices.FusedLocationApi.requestLocationUpdates(_client, _locationRequest,
                new MyLocationListener());
      }
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
}
