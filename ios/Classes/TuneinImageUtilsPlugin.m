#import "TuneinImageUtilsPlugin.h"
#if __has_include(<tunein_image_utils_plugin/tunein_image_utils_plugin-Swift.h>)
#import <tunein_image_utils_plugin/tunein_image_utils_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "tunein_image_utils_plugin-Swift.h"
#endif

@implementation TuneinImageUtilsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftTuneinImageUtilsPlugin registerWithRegistrar:registrar];
}
@end
