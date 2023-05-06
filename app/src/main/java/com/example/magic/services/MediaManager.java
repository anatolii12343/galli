package com.example.magic.services;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.magic.R;

public class MediaManager {

    private Context context;

    private SoundPool soundPool;


    private int stepsId;
    private int stepsStreamId;

    private int chestId;
    private int chestStreamId;

    private MediaPlayer mediaPlayer;

    public MediaManager(Context context) {
        this.context = context;
        soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        stepsId = soundPool.load(context, R.raw.steps, 1);
        chestId = soundPool.load(context, R.raw.open_inventory, 1);
    }

    public void startBackgroundMusic() {
        mediaPlayer = MediaPlayer.create(context, R.raw.music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void stopBackgroundMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void startSteps() {
        soundPool.stop(stepsStreamId);
        stepsStreamId = soundPool.play(stepsId, 1f, 1f, 1, 0, 1);
    }

    public void stopSteps() {
        soundPool.pause(stepsStreamId);
    }

    public void startChest() {
        soundPool.stop(chestStreamId);
        chestStreamId = soundPool.play(chestId, 1f, 1f, 1, 0, 1);
    }
}
