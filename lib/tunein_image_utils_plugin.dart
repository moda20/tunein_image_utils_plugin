// You have generated a new plugin project without
// specifying the `--platforms` flag. A plugin project supports no platforms is generated.
// To add platforms, run `flutter create -t plugin --platforms <platforms> .` under the same
// directory. You can also find a detailed instruction on how to add platforms in the `pubspec.yaml` at https://flutter.dev/docs/development/packages-and-plugins/developing-packages#plugin-platforms.

import 'dart:async';

import 'package:flutter/services.dart';

class TuneinImageUtilsPlugin {
  static const MethodChannel _channel =
      const MethodChannel('tunein_image_utils_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }


  static Future<bool> sendToBackgoround() async{
    final bool didGoBack = await _channel.invokeMethod('sendToBackground');
    return didGoBack;
  }

  static Future<String> getStoragePath() async{
    final String path = await _channel.invokeMethod('getStoragePath');
    return path;
  }

  static Future<bool> getSDCardPermission() async{
    final bool permissionGranted = await _channel.invokeMethod('getSDCardPermission');
    return permissionGranted;
  }

  static Future<bool> saveFilesFromBytes(String path, Uint8List bytes) async{
    final bool fileSaved = await _channel.invokeMethod('saveFileFromBytes',{
      "filepath":path,
      "bytes":bytes
    });
    return fileSaved;
  }

  static Future<dynamic> getFileMetadata(String path) async{
    final metaData = await _channel.invokeMethod('getMetaData', {
      "filepath":path
    });
    return metaData;
  }

  static Future<String> getSdCardPath() async{
    final String path = await _channel.invokeMethod('getSdCardPath');
    return path;
  }

  static Future<dynamic> getColor(String path) async{
    final colors = await _channel.invokeMethod('getColor',{
      "path":path
    });
    return colors;
  }
}
