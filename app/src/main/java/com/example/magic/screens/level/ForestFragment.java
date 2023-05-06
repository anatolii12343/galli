package com.example.magic.screens.level;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.magic.GameApplication;
import com.example.magic.databinding.FragmentForestBinding;
import com.example.magic.models.Item;
import com.example.magic.models.Level;
import com.example.magic.models.action.Action;
import com.example.magic.models.action.NextLevelAction;
import com.example.magic.models.action.NpcMessage;
import com.example.magic.models.action.RunnableAction;
import com.example.magic.models.action.UserMessage;
import com.example.magic.screens.GameActivity;
import com.example.magic.screens.GameView;
import com.example.magic.services.StorageManager;

import java.util.List;

public class ForestFragment extends Fragment {

    private FragmentForestBinding binding;

    private StorageManager storageManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storageManager = ((GameApplication) getActivity().getApplication()).getStorageManager();
        storageManager.level.observe(getViewLifecycleOwner(), l -> {
            GameActivity activity = ((GameActivity) getActivity());
            GameView gameView = activity.getBinding().gameView;
            if (storageManager.getGame().getCaveUnlocked()) {
                activity.setRightLocationVisible(View.VISIBLE);
            } else {
                activity.setRightLocationVisible(View.GONE);
            }
            if (l == Level.EGG) {
                binding.egg.setVisibility(View.VISIBLE);
                binding.egg.setOnClickListener(v2 -> {
                    List<Action> eggActions = List.of(
                            new RunnableAction(() -> {
                                storageManager.addToInventory(Item.EGG);
                                storageManager.nextLevel();
                                gameView.displayNpcMessage("А я думаю, чье это?", binding.yaga.getX(), binding.yaga.getY());
                            })
                    );
                    gameView.npcMove(binding.egg.getX(), binding.egg.getY() + binding.egg.getHeight(), binding.egg.getWidth(), binding.egg.getHeight(), () -> {
                        binding.egg.setVisibility(View.GONE);
                        gameView.setUpActions(eggActions);
                    });
                });
            } else {
                binding.egg.setVisibility(View.GONE);
            }
            binding.yaga.setOnClickListener(v -> {
                if (l == Level.BABA_YAGA) {
                    List<Action> actions = List.of(
                            new NpcMessage("Привет, дитя, что ты тут забыл?", binding.yaga.getX(), binding.yaga.getY()),
                            new UserMessage("Привет, Баба Яга, я лекарство ищу...", binding.yaga.getX()),
                            new UserMessage("для моей бабушки...", binding.yaga.getX()),
                            new UserMessage("Ты случайно не знаешь где его найти? ", binding.yaga.getX()),
                            new NpcMessage("Знаю, но прежде чем я скажу...", binding.yaga.getX(), binding.yaga.getY()),
                            new NpcMessage("принеси мне кое-что...", binding.yaga.getX(), binding.yaga.getY()),
                            new NpcMessage("А что - догадайся сам.", binding.yaga.getX(), binding.yaga.getY()),
                            new NextLevelAction()
                    );
                    gameView.npcMove(binding.yaga.getX(), binding.yaga.getY() + binding.yaga.getHeight(), binding.yaga.getWidth(), binding.yaga.getHeight(), () -> {
                        gameView.setUpActions(actions);
                    });
                } else if (l == Level.GIVE_SNUP) {
                    List<Action> actions = List.of(
                            new UserMessage("Вот, держи, что ты просила.", binding.yaga.getX()),
                            new NpcMessage("Спасибо большое.", binding.yaga.getX(), binding.yaga.getY()),
                            new NpcMessage("Лекарство ты можешь найти...", binding.yaga.getX(), binding.yaga.getY()),
                            new NpcMessage("у кощея бессмертного в пещере.", binding.yaga.getX(), binding.yaga.getY()),
                            new RunnableAction(() -> {
                                activity.viewModel.stopTimer();
                                storageManager.nextLevel();
                                storageManager.unlockCave();
                                storageManager.removeForInventory(Item.SNUP);
                            })
                    );
                    gameView.npcMove(binding.yaga.getX(), binding.yaga.getY() + binding.yaga.getHeight(), binding.yaga.getWidth(), binding.yaga.getHeight(), () -> {
                        gameView.setUpActions(actions);
                    });
                }

            });
        });
    }
}
