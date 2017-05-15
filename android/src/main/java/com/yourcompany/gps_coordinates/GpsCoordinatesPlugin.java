package com.yourcompany.gps_coordinates;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import java.util.HashMap;

/**
 * GpsCoordinatesPlugin
 */
public class GpsCoordinatesPlugin implements MethodCallHandler {
  private static final String CHANNEL = "gustash.flutter.plugins/gps_coordinates";

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);
    channel.setMethodCallHandler(new GpsCoordinatesPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getGPSCoordinates")) {
      result.success(getGPSCoordinates());
    } else {
      result.notImplemented();
    }
  }

  private HashMap<String, Double> getGPSCoordinates() {
    HashMap<String, Double> coordinates = new HashMap<>();
    coordinates.put("lat", 150.0);
    coordinates.put("long", 150.0);
    return coordinates;
  }
}
