package com.yourcompany.gps_coordinates;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;

/**
 * GpsCoordinatesPlugin
 */
public class GpsCoordinatesPlugin implements MethodCallHandler {
  private static final String CHANNEL = "gustash.flutter.plugins/gps_coordinates";
  private Context _context;
  private static GoogleApiClient _client;
  private ConnectionManager _connManager;

  public GpsCoordinatesPlugin(Context context) {
    _context = context;
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
      getGPSCoordinates(_context, result);
    } else {
      result.notImplemented();
    }
  }

  private void getGPSCoordinates(Context context, Result result) {
    _connManager = new ConnectionManager(result);
    _client = new GoogleApiClient.Builder(context)
            .addConnectionCallbacks(_connManager)
            .addOnConnectionFailedListener(_connManager)
            .addApi(LocationServices.API)
            .build();
    _client.connect();
  }

  private void returnLocation(Result result) {
    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(_client);
    if (lastLocation != null) {
      HashMap<String, Double> coordinates = new HashMap<>();
      coordinates.put("lat", lastLocation.getLatitude());
      coordinates.put("long", lastLocation.getLongitude());
      result.success(coordinates);
    }
  }

  private class ConnectionManager implements ConnectionCallbacks, OnConnectionFailedListener {
    private Result _result;

    protected ConnectionManager(Result result) {
      _result = result;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
      returnLocation(_result);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
  }
}
