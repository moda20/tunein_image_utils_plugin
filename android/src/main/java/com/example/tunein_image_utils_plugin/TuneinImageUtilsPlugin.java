package com.example.tunein_image_utils_plugin;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.palette.graphics.Palette;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** TuneinImageUtilsPlugin */
public class TuneinImageUtilsPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private static final String ID = "tunein_image_utils_plugin";
  private MethodChannel channel;
  private Context mContext;
  private Activity activity;
  private ActivityPluginBinding activityPluginBinding;
  private PluginMethods pluginMethods;
  public Registrar registrar;

  public TuneinImageUtilsPlugin() {

  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    // TODO: your plugin is now attached to an Activity
    activity=activityPluginBinding.getActivity();
    this.activityPluginBinding=activityPluginBinding;
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    // TODO: the Activity your plugin was attached to was destroyed to change configuration.
    // This call will be followed by onReattachedToActivityForConfigChanges().
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
    // TODO: your plugin is now attached to a new Activity after a configuration change.
  }

  @Override
  public void onDetachedFromActivity() {
    // TODO: your plugin is no longer associated with an Activity. Clean up references.
    activity=null;
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    TuneinImageUtilsPlugin newInstance = new TuneinImageUtilsPlugin();
    newInstance.initInstance(registrar.messenger(), registrar.context());
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    initInstance(flutterPluginBinding.getBinaryMessenger(), flutterPluginBinding.getApplicationContext());
  }

  private void initInstance(BinaryMessenger binaryMessenger, Context context){
    this.channel = new MethodChannel(binaryMessenger, ID);
    this.channel.setMethodCallHandler(this);
    mContext = context;
    this.pluginMethods = new PluginMethods(context,null,null);
  }

  public  void takeCardUriPermission(String sdCardRootPath) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      File sdCard = new File(sdCardRootPath);
      StorageManager storageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
      StorageVolume storageVolume = storageManager.getStorageVolume(sdCard);
      Intent intent = storageVolume.createAccessIntent(null);
      try {
        activity.startActivityForResult(intent, 4010);
      } catch (ActivityNotFoundException e) {
        Log.e("TUNE-IN ANDROID", "takeCardUriPermission: "+e);
      }
    }
  }
  @Override
  public void onMethodCall(@NonNull MethodCall methodCall, @NonNull final Result result) {

    switch (methodCall.method){
      case "getPlatformVersion":{
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      }
      case "sendToBackground":{
        activity.moveTaskToBack(true);
        result.success(true);
        break;
      }
      case "getStoragePath":{
        String path = Environment.getDataDirectory().toString();
        result.success(path);
        break;
      }
      case "getSDCardPermission":{
        PluginRegistry.ActivityResultListener newListener = new PluginRegistry.ActivityResultListener() {
          @Override
          public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 4010) {

              Uri uri = data.getData();

              mContext.grantUriPermission(mContext.getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                      Intent.FLAG_GRANT_READ_URI_PERMISSION);

              final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                      Intent.FLAG_GRANT_READ_URI_PERMISSION);

              mContext.getContentResolver().takePersistableUriPermission(uri, takeFlags);
              result.success(pluginMethods.getUri().toString());
              return true;
            }
            return true;
          }

        };

        if(activityPluginBinding!=null){
          activityPluginBinding.addActivityResultListener(newListener);
        }else{
          if(registrar!=null){
            registrar.addActivityResultListener(newListener);
          }
        }
        pluginMethods.takeCardUriPermission(mContext.getExternalCacheDirs()[1].toString(), this.activity);
        result.success(true);
        break;
      }
      case "saveFileFromBytes":{
        String filepath = (String) methodCall.argument("filepath");
        final byte[] bytes = methodCall.argument("bytes");
        boolean didSave = pluginMethods.savefileFromBytes(filepath,bytes);
        if(didSave){
          result.success(true);
        }else {
          result.error("Error occurred when saving file, check console","",null);
        }
        break;
      }
      case "getMetaData":{
        String filepath = (String) methodCall.argument("filepath");
        List l = pluginMethods.getFileMetaData(filepath);
        result.success(l);
        break;
      }
      case "getSdCardPath":{
        String removableStoragePath = null;
        try {
          android.util.Log.d("qqsdqsdqsd", mContext.getExternalCacheDirs().toString());
          removableStoragePath = mContext.getExternalCacheDirs()[1].toString();
        } catch (Exception e) {
          result.error("400",e.getMessage(),e);
          return;
        }
        result.success(removableStoragePath);
        break;
      }
      case "getColor":{
        String path = methodCall.argument("path");
        List<Integer> colors = pluginMethods.getDominantColorReal(path);
        result.success(colors);
        break;
      }
      default:{
        result.notImplemented();
        break;
      }
    }

  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
