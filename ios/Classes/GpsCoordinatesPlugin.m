#import "GpsCoordinatesPlugin.h"
#import <gps_coordinates/gps_coordinates-Swift.h>

@implementation GpsCoordinatesPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    [RealGpsCoordinatesPlugin registerWithRegistrar:registrar];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"getGPSCoordinates" isEqualToString:call.method]) {
        result(FlutterMethodNotImplemented);
    }
    result(FlutterMethodNotImplemented);
}

@end
