//
//  RealGpsCoordinatesPlugin.swift
//  Pods
//
//  Created by Gustavo Parreira on 19/05/2017.
//
//

import Foundation
import UIKit

public class RealGpsCoordinatesPlugin: NSObject, FlutterPlugin {
    static let CHANNEL_NAME: String = "gustash.flutter.plugins/gps_coordinates";
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: CHANNEL_NAME, binaryMessenger: registrar.messenger());
        let instance = RealGpsCoordinatesPlugin();
        registrar.addMethodCallDelegate(instance, channel: channel);
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if (call.method == "getGPSCoordinates") {
            var coordinates: Dictionary<String, Double> = Dictionary();
            coordinates["lat"]  = 25.0;
            coordinates["long"] = 25.0;
            result(coordinates as NSDictionary);
        } else {
            result(FlutterMethodNotImplemented);
        }
    }
}
