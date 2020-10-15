import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:tunein_image_utils_plugin/tunein_image_utils_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('tunein_image_utils_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await TuneinImageUtilsPlugin.platformVersion, '42');
  });
}
