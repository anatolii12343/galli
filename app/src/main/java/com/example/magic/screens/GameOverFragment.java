package com.example.magic.screens;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.example.magic.GameApplication;
import com.example.magic.R;
import com.example.magic.databinding.FragmentGameOverBinding;

public class GameOverFragment extends DialogFragment {

    private FragmentGameOverBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGameOverBinding.inflate(inflater, container, false);
        requireDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        requireDialog().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        requireDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.use.setOnClickListener(v -> {
            dismiss();
            ((GameApplication)getActivity().getApplication()).getStorageManager().nullGame();
            requireActivity().finish();
        });
        boolean victory = getArguments().getBoolean("victory");
        if (victory) {
            binding.status.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_green_dark, getActivity().getTheme()));
            binding.status.setText("Вы победили!");
        } else {
            binding.status.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_dark, getActivity().getTheme()));
            binding.status.setText("Вы проиграли");
        }
    }

    public static GameOverFragment getInstance(boolean victory) {
        GameOverFragment fragment = new GameOverFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("victory", victory);
        fragment.setArguments(bundle);
        return fragment;
    }
}
