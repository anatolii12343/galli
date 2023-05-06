package com.example.magic.screens.level;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.magic.GameApplication;
import com.example.magic.databinding.FragmentHomeBinding;
import com.example.magic.models.Item;
import com.example.magic.models.Level;
import com.example.magic.models.action.Action;
import com.example.magic.models.action.NpcMessage;
import com.example.magic.models.action.RunnableAction;
import com.example.magic.models.action.UserMessage;
import com.example.magic.screens.GameActivity;
import com.example.magic.screens.GameView;
import com.example.magic.services.StorageManager;

import java.util.ArrayList;
import java.util.List;

public class HouseFragment extends Fragment {

    private FragmentHomeBinding binding;

    private GameView gameView;

    private StorageManager storageManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storageManager = ((GameApplication) getActivity().getApplication()).getStorageManager();
        storageManager.level.observe(getViewLifecycleOwner(), l -> {
            GameActivity activity = ((GameActivity) getActivity());
            GameView gameView = activity.getBinding().gameView;
            if (l == Level.SNUP) {
                binding.snup.setVisibility(View.VISIBLE);
                binding.snup.setOnClickListener(v1 -> {
                    List<Action> actions = List.of(
                            new RunnableAction(() -> {
                                storageManager.addToInventory(Item.SNUP);
                                storageManager.nextLevel();
                                binding.snup.setVisibility(View.GONE);
                            })
                    );
                    gameView.npcMove(binding.snup.getX(), binding.snup.getY() + binding.snup.getHeight() + 150, binding.snup.getWidth(), binding.snup.getHeight(), () -> {
                        gameView.setUpActions(actions);
                    });
                });
            } else {
                binding.snup.setVisibility(View.GONE);
            }
            activity.setRightLocationVisible(View.VISIBLE);
            binding.grandma.setOnClickListener(
                    v -> {
                        if (l == Level.MUM) {
                            List<Action> grandmaActions = List.of(
                                    new NpcMessage("Привет, внучок мне стало очень плохо...", binding.grandma.getX(), binding.grandma.getY()),
                                    new NpcMessage("Можешь ли ты сходить за продуктами...", binding.grandma.getX(), binding.grandma.getY()),
                                    new NpcMessage("...на рынок?", binding.grandma.getX(), binding.grandma.getY()),
                                    new NpcMessage("и принести их в дом?", binding.grandma.getX(), binding.grandma.getY()),
                                    new UserMessage("Привет. Да, хорошо, бабуль, сейчас схожу", binding.grandma.getX()),
                                    new RunnableAction(() -> {
                                        storageManager.addToInventory(Item.PRODUCTS);
                                        storageManager.nextLevel();
                                    })
                            );
                            gameView.npcMove(binding.grandma.getX(), binding.grandma.getY() + binding.grandma.getHeight(), binding.grandma.getWidth(), binding.grandma.getHeight(), () -> {
                                gameView.setUpActions(grandmaActions);
                            });
                        } else if (l == Level.HEALTH_MUM) {
                            List<Action> actions = List.of(
                                    new UserMessage("Привет, бабушка, вот тебе лекарство.", binding.grandma.getX()),
                                    new NpcMessage("Ого, мне сразу стало лучше", binding.grandma.getX(), binding.grandma.getY()),
                                    new NpcMessage("Теперь мы сможем нормально жить.", binding.grandma.getX(), binding.grandma.getY()),
                                    new RunnableAction(() -> {
                                        storageManager.removeForInventory(Item.TANTUM_VERDE);
                                        storageManager.nextLevel();
                                    })
                            );
                            gameView.npcMove(binding.grandma.getX(), binding.grandma.getY() + binding.grandma.getHeight(), binding.grandma.getWidth(), binding.grandma.getHeight(), () -> {
                                gameView.setUpActions(actions);
                            });
                        } else if (l == Level.PUT_PRODUCTS) {
                            ArrayList<Action> grandmaActions = new ArrayList<Action>() {{
                                add(new NpcMessage("Спасибо внучок за продукты!", binding.grandma.getX(), binding.grandma.getY()));
                                add(new NpcMessage("Я пока ещё полежу, а то мне совсем плохо.", binding.grandma.getX(), binding.grandma.getY()));
                                add(new UserMessage("Бабушка, не волнуйся!", binding.grandma.getX()));
                                add(new UserMessage("Я обязательно найду лекарство.", binding.grandma.getX()));
                                add(new RunnableAction(() -> {
                                    storageManager.removeForInventory(Item.SHOPPING_LIST);
                                }));
                            }};
                            if (storageManager.getGame().getHelpForOldMan()) {
                                grandmaActions.add(
                                        new UserMessage("У тебя не найдется таблеток...", binding.grandma.getX())
                                );
                                grandmaActions.add(
                                        new UserMessage("...для старика с рынка?", binding.grandma.getX())
                                );
                                grandmaActions.add(
                                        new NpcMessage("Найдутся", binding.grandma.getX(), binding.grandma.getY())
                                );
                                grandmaActions.add(
                                        new RunnableAction(() -> {
                                            storageManager.addToInventory(Item.PILLS);
                                            storageManager.setLevel(Level.PILLS);
                                        })
                                );
                            } else {
                                grandmaActions.add(
                                        new NpcMessage("Слушай, а сходи как еще...", binding.grandma.getX(), binding.grandma.getY())
                                );
                                grandmaActions.add(
                                        new NpcMessage("... проведай бабу ягу в лесу", binding.grandma.getX(), binding.grandma.getY())
                                );
                                grandmaActions.add(
                                        new NpcMessage("Только поторопись", binding.grandma.getX(), binding.grandma.getY())
                                );
                                grandmaActions.add(
                                        new RunnableAction(() -> {
                                            storageManager.setLevel(Level.BABA_YAGA);
                                            storageManager.unlockForest();
                                            activity.viewModel.startTimer(60);
                                        })
                                );
                            }
                            gameView.npcMove(binding.grandma.getX(), binding.grandma.getY() + binding.grandma.getHeight(), binding.grandma.getWidth(), binding.grandma.getHeight(), () -> {
                                gameView.setUpActions(grandmaActions);
                            });
                        } else {
                            List<Action> grandmaActions = List.of(
                                    new NpcMessage("Ты уже сходил за продуктами?", binding.grandma.getX(), binding.grandma.getY()));
                            gameView.npcMove(binding.grandma.getX(), binding.grandma.getY() + binding.grandma.getHeight(), binding.grandma.getWidth(), binding.grandma.getHeight(), () -> {
                                gameView.setUpActions(grandmaActions);
                            });
                        }
                    }
            );
        });

    }
}
