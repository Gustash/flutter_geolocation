import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:gps_coordinates/gps_coordinates.dart';

void main() {
  runApp(new MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'Flutter Demo',
      theme: new ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see
        // the application has a blue toolbar. Then, without quitting
        // the app, try changing the primarySwatch below to Colors.green
        // and then invoke "hot reload" (press "r" in the console where
        // you ran "flutter run", or press Run > Hot Reload App in IntelliJ).
        // Notice that the counter didn't reset back to zero -- the application
        // is not restarted.
        primarySwatch: Colors.blue,
      ),
      home: new MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  // This widget is the home page of your application. It is stateful,
  // meaning that it has a State object (defined below) that contains
  // fields that affect how it looks.

  // This class is the configuration for the state. It holds the
  // values (in this case the title) provided by the parent (in this
  // case the App widget) and used by the build method of the State.
  // Fields in a Widget subclass are always marked "final".

  final String title;

  @override
  _MyHomePageState createState() => new _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  Map<String, double> _coordinates = new Map();

  @override
  initState() {
    super.initState();
    _coordinates["lat"] = _coordinates["long"] = 0.0;
  }

  // Platform messages are asynchronous, so we initialize in an async method.
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

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(title: const Text('App Name')),
      body: new Padding(
        padding: const EdgeInsets.all(18.0),
        child: new Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            new Padding(
              padding: const EdgeInsets.only(bottom: 18.0),
              child: new Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  new Text(
                    _coordinates["lat"].toString()
                  ),
                  new Text(_coordinates["long"].toString())
                ]
              ),
            ),
            new RaisedButton(
                key: null,
                onPressed: buttonPressed,
                color: Colors.blue[400],
                colorBrightness: Brightness.dark,
                child: new Text("UPDATE")
            )
          ]
        ),
      )
    );
  }

  void buttonPressed() {
    _getCoordinates();
  }
}
