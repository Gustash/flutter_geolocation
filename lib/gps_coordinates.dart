import 'dart:async';

import 'package:flutter/services.dart';

class GpsCoordinates {
  static const MethodChannel _channel =
    const MethodChannel('gustash.flutter.plugins/gps_coordinates');

  static Future<Map<String, double>> get gpsCoordinates =>
    _channel.invokeMethod('getGPSCoordinates');
}
