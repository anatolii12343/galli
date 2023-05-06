package com.example.magic.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.magic.GameApplication;
import com.example.magic.R;
import com.example.magic.databinding.ActivityMainBinding;
import com.example.magic.services.MediaManager;
import com.example.magic.services.StorageManager;

public class MainActivity extends AppCompatActivity {

    private StorageManager storageManager;

    private ActivityMainBinding binding;
    private MediaManager mediaManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;

        decorView.setSystemUiVisibility(uiOptions);
        storageManager = ((GameApplication) getApplication()).getStorageManager();

        binding.loadGame.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        View.OnClickListener listener = view -> {
            storageManager.newGame();
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        };

        boolean musicEnabled = storageManager.isMusicEnabled();
        mediaManager = ((GameApplication) getApplication()).getMediaManager();

        boolean continueAvailable = storageManager.getGame() != null;
        if (continueAvailable) {
            binding.loadGame.setVisibility(View.VISIBLE);
        } else {
            binding.loadGame.setVisibility(View.GONE);
        }

        if (musicEnabled) {
            binding.music.setImageResource(R.drawable.ic_music_on);
            mediaManager.startBackgroundMusic();
        } else {
            binding.music.setImageResource(R.drawable.ic_music_off);
            mediaManager.stopBackgroundMusic();
        }
        View.OnClickListener listener2 = view -> {
            if (storageManager.isMusicEnabled()) {
                mediaManager.stopBackgroundMusic();
                binding.music.setImageResource(R.drawable.ic_music_off);
            } else {
                mediaManager.startBackgroundMusic();
                binding.music.setImageResource(R.drawable.ic_music_on);
            }
            storageManager.setMusicEnabled();
        };

        binding.startGame.setOnClickListener(listener);
        binding.music.setOnClickListener(listener2);
        binding.exit.setOnClickListener(
                v -> {
                    exit();
                }
        );
    }

    private void exit() {
        // TODO
        finish();
    }
}