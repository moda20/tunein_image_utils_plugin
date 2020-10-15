package com.example.tunein_image_utils_plugin;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.documentfile.provider.DocumentFile;
import androidx.palette.graphics.Palette;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.PluginRegistry;

public class PluginMethods {
    String TAG = "TuneInImagePlugin";

    public PluginMethods(Context context, Activity activity, ActivityPluginBinding activityPluginBinding) {
        this.context = context;
        this.activity = activity;
        this.activityPluginBinding = activityPluginBinding;
    }

    private Context context;
    private Activity activity;
    private ActivityPluginBinding activityPluginBinding;
    private final MediaMetadataRetriever mmr = new MediaMetadataRetriever();


    public  int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;

    }
    public List getFileMetaData(String filepath){
        System.out.println(filepath);
        List l = new ArrayList();
        mmr.setDataSource(filepath);
        l.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        l.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        try {
            l.add(mmr.getEmbeddedPicture());
        } catch (Exception e) {
            l.add("");
        }

        l.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
        l.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
        l.add(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        return l;
    }

    public  void takeCardUriPermission(String sdCardRootPath) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            File sdCard = new File(sdCardRootPath);
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            StorageVolume storageVolume = storageManager.getStorageVolume(sdCard);
            Intent intent = storageVolume.createAccessIntent(null);
            try {
                activity.startActivityForResult(intent, 4010);
            } catch (ActivityNotFoundException e) {
                Log.e("TUNE-IN ANDROID", "takeCardUriPermission: "+e);
            }
        }
    }


    public boolean savefileFromBytes(String filepath,byte[] bytes){
        try{
            if(filepath==null || bytes==null)throw new Exception("Arguments Not found");
            filepath=filepath.replace("%20"," ");
            DocumentFile documentFile = DocumentFile.fromTreeUri(context, getUri());
            String[] parts = filepath.split("/");
            for (int i = 0; i < parts.length; i++) {
                if(documentFile.findFile(parts[i])!=null){
                    documentFile=documentFile.findFile(parts[i]);
                }
            }
            if(documentFile!=null && documentFile.isFile()){
                OutputStream out = context.getContentResolver().openOutputStream(documentFile.getUri());
                out.write(bytes);
                out.close();
                return true;
            }else{
                throw new Exception("File Not Found");
            }
        }catch (Exception e){
            Log.e(TAG, "savefileFromBytes: "+e.getMessage());
            return false;
        }
    }

    public Uri getUri() {
        List<UriPermission> persistedUriPermissions = context.getContentResolver().getPersistedUriPermissions();
        if (persistedUriPermissions.size() > 0) {
            UriPermission uriPermission = persistedUriPermissions.get(0);
            return uriPermission.getUri();
        }
        return null;
    }

    public List<Integer> getDominantColorReal(String path){
        Bitmap myBitmap = BitmapFactory.decodeFile(path);
        List<Integer> colors = new ArrayList<Integer>();
        Palette newPalette =  Palette.from(myBitmap).generate();
        colors.add(newPalette.getDominantSwatch().getRgb());
        colors.add(newPalette.getDominantSwatch().getBodyTextColor());
        colors.add(newPalette.getDominantSwatch().getTitleTextColor());
        return colors;
    }
}
