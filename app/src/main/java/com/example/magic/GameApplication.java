package com.example.magic;

import android.app.Application;

import com.example.magic.services.MediaManager;
import com.example.magic.services.StorageManager;

public class GameApplication extends Application {

    private StorageManager storageManager;

    private MediaManager mediaManager;

    @Override
    public void onCreate() {
        super.onCreate();
        storageManager = new StorageManager(this);
        mediaManager = new MediaManager(this);
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public MediaManager getMediaManager() {
        return mediaManager;
    }
}
