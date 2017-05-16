#import "GpsCoordinatesPlugin.h"

@implementation GpsCoordinatesPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"gps_coordinates"
            binaryMessenger:[registrar messenger]];
  GpsCoordinatesPlugin* instance = [[GpsCoordinatesPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    result(FlutterMethodNotImplemented);
}

@end
