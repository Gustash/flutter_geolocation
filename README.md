## :warning: Warning

This plugin is no longer supported or up to date. Please use alternatives like [geolocation](https://pub.dartlang.org/packages/geolocation) or [location](https://pub.dartlang.org/packages/location).

# gps_coordinates

This Flutter plugin allows you to get the device's current location.

## How to use

To use this plugin you don't have to worry about what platform the app is running on, but you do need a little work to get the permissions working in Android and iOS. Follow the following steps for whatever platform you're developing for.

### Steps for Android

Android apps need a location permission in their **AndroidManifest.xml** to be able to use location services. All you need to do is add 

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

or

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

to you **AndroidManifest.xml** depending on how accurate you want the returned location to be.

### Steps for iOS

iOS apps need a justification for using certain permissions. Location is one of them, therefore you need to add a reason for needing location access in your app's **Info.plist**.

To do that, just add the following keys to your **Info.plist** and a string for your justification:

* NSLocationWhenInUseUsageDescription
* NSLocationAlwaysUsageDescription

### Implementation in Flutter

After all that is done, simply add a dependency to you pubspec.yaml for gps_coordinates with the path to the folder where you cloned the project to.

Then import the package in your dart file with 

```dart
import 'package:gps_coordinates/gps_coordinates.dart';
```

and call the gpsCoordinates getter function in the GpsCoordinates class. Your implementation may differ, but the basic implementation in the example app is as follows:

```dart
_getCoordinates() async {
  Map<String, double> coordinates;
  // Platform messages may fail, so we use a try/catch PlatformException.
  try {
    coordinates = await GpsCoordinates.gpsCoordinates;
  } on PlatformException {
    Map<String, double> placeholdCoordinates = new Map();
    placeholdCoordinates["lat"] = 0.0;
    placeholdCoordinates["long"] = 0.0;
    coordinates = placeholdCoordinates;
  }

  // If the widget was removed from the tree while the asynchronous platform
  // message was in flight, we want to discard the reply rather than calling
  // setState to update our non-existent appearance.
  if (!mounted)
    return;

  setState(() {
    _coordinates = coordinates;
  });
}
```

That is all you have to do to implement in Flutter.

## :warning: Warning
This plugin was a project I started for a need to get the user's current location once, not for getting updates on location. For that reason, and until I have a reason to implement location listening, the project will serve just that purpose.

Feel free to implement location listening on your own and send a pull request. Just because I'm not going to spend the time to implement that doesn't mean that I wouldn't like this plugin to be more complete.
