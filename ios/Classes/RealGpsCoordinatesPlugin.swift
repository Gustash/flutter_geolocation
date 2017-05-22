//
//  RealGpsCoordinatesPlugin.swift
//  Pods
//
//  Created by Gustavo Parreira on 19/05/2017.
//
//

import Foundation
import UIKit
import CoreLocation

public class RealGpsCoordinatesPlugin: NSObject, FlutterPlugin, CLLocationManagerDelegate {
    static let CHANNEL_NAME: String = "gustash.flutter.plugins/gps_coordinates";
    var manager: CLLocationManager!;
    var coordinates: NSDictionary = [:];
    var result: FlutterResult? = nil;
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: CHANNEL_NAME, binaryMessenger: registrar.messenger());
        let instance = RealGpsCoordinatesPlugin();
        registrar.addMethodCallDelegate(instance, channel: channel);
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if (call.method == "getGPSCoordinates") {
            self.result = result;
            
            if CLLocationManager.locationServicesEnabled() {
                manager = CLLocationManager();
                manager.requestAlwaysAuthorization();
                manager.requestWhenInUseAuthorization();
                manager.delegate = self;
                manager.desiredAccuracy = kCLLocationAccuracyBest;
                manager.startUpdatingLocation();
            }
            
            //var coordinates: Dictionary<String, Double> = Dictionary();
            //coordinates["lat"]  = 25.0;
            //coordinates["long"] = 25.0;
            //result(coordinates as NSDictionary);
        } else {
            result(FlutterMethodNotImplemented);
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let userLocation: CLLocation = locations[0];
        let long                     = userLocation.coordinate.longitude;
        let lat                      = userLocation.coordinate.latitude;
        buildDict(lat: lat, long: long);
        returnResult();
    }
    
    func buildDict(lat: Double, long: Double) {
        var coordinates: Dictionary<String, Double> = Dictionary();
        coordinates["lat"]  = lat;
        coordinates["long"] = long;
        self.coordinates = coordinates as NSDictionary;
    }
    
    func returnResult() {
        manager.stopUpdatingLocation();
        self.result!(self.coordinates);
    }
}
