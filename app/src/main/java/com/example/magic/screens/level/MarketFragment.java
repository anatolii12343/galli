package com.example.magic.screens.level;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.magic.GameApplication;
import com.example.magic.databinding.FragmentMarketBinding;
import com.example.magic.models.Item;
import com.example.magic.models.Level;
import com.example.magic.models.action.Action;
import com.example.magic.models.action.AddToInventory;
import com.example.magic.models.action.ChooseAction;
import com.example.magic.models.action.NextLevelAction;
import com.example.magic.models.action.NpcMessage;
import com.example.magic.models.action.RemoveFromInventory;
import com.example.magic.models.action.RunnableAction;
import com.example.magic.models.action.UserMessage;
import com.example.magic.screens.GameActivity;
import com.example.magic.screens.GameView;
import com.example.magic.services.StorageManager;

import java.util.List;

public class MarketFragment extends Fragment {

    private FragmentMarketBinding binding;

    private StorageManager storageManager;

    private int potatoes = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMarketBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storageManager = ((GameApplication) getActivity().getApplication()).getStorageManager();

        storageManager.level.observe(getViewLifecycleOwner(), l -> {
            GameActivity activity = ((GameActivity) getActivity());
            GameView gameView = activity.getBinding().gameView;
            if (storageManager.getGame().getForestUnlocked()) {
                activity.setRightLocationVisible(View.VISIBLE);
            } else {
                activity.setRightLocationVisible(View.GONE);
            }
            if (l == Level.SHOPPING) {
                activity.viewModel.startTimer(20);
                startMimiGame();
            }
            binding.oldMan.setOnClickListener(v -> {
                if (l == Level.GO_TO_SHOPPING) {
                    List<Action> grandmaActions = List.of(
                            new NpcMessage("Бабуля прислала за продуктами?", binding.oldMan.getX(), binding.oldMan.getY()),
                            new RunnableAction(
                                    () -> {
                                        storageManager.removeForInventory(Item.PRODUCTS);
                                        storageManager.addToInventory(Item.SHOPPING_LIST);
                                        activity.viewModel.startTimer(20);
                                        storageManager.nextLevel();
                                        startMimiGame();
                                    }
                            )
                    );
                    gameView.npcMove(binding.oldMan.getX(), binding.oldMan.getY() + binding.oldMan.getHeight(), binding.oldMan.getWidth(), binding.oldMan.getHeight(), () -> {
                        gameView.setUpActions(grandmaActions);
                    });
                }  else if (l == Level.OLD_MAN) {
                    List<Action> actions = List.of(
                            new NpcMessage("Сынок, можешь взять лекарство для меня?", binding.oldMan.getX(), binding.oldMan.getY()),
                            new NpcMessage("Посмотри его у себя дома.", binding.oldMan.getX(), binding.oldMan.getY()),
                            new NpcMessage("А я тебя яблоком угощу", binding.oldMan.getX(), binding.oldMan.getY()),
                            new ChooseAction(
                                    "Помочь старику?",
                                    () -> {
                                        gameView.displayNpcMessage("Спасибо!", binding.oldMan.getX(), binding.oldMan.getY());
                                        storageManager.setHelpForOldMan(true);
                                        storageManager.nextLevel();
                                    },
                                    "Помочь",
                                    () -> {
                                        gameView.displayNpcMessage("Иди отсюда", binding.oldMan.getX(), binding.oldMan.getY());
                                        storageManager.setHelpForOldMan(false);
                                        storageManager.nextLevel();
                                    },
                                    "Отказать"
                            )
                    );
                    gameView.npcMove(binding.oldMan.getX(), binding.oldMan.getY() + binding.oldMan.getHeight(), binding.oldMan.getWidth(), binding.oldMan.getHeight(), () -> {
                        gameView.setUpActions(actions);
                    });
                } else if (l == Level.PILLS) {
                    List<Action> actions = List.of(
                            new RunnableAction(() -> {
                                gameView.displayNpcMessage("Спасибо огромное.", binding.oldMan.getX(), binding.oldMan.getY());
                                storageManager.removeForInventory(Item.PILLS);
                            }),
                            new RunnableAction(() -> {
                                storageManager.addToInventory(Item.GOLDEN_APPLE);
                                gameView.displayNpcMessage("Вот тебе золотое яблоко на всякий.", binding.oldMan.getX(), binding.oldMan.getY());
                            }),
                            new UserMessage("Извините, а вы не знаете где можно...", binding.oldMan.getX()),
                            new UserMessage("достать лекарство для моей бабушки?", binding.oldMan.getX()),
                            new NpcMessage("Да, знаю. В лесу ты найдёшь свой ответ.", binding.oldMan.getX(), binding.oldMan.getY()),
                            new RunnableAction(() -> {
                                storageManager.nextLevel();
                                storageManager.unlockForest();
                            })
                    );
                    gameView.npcMove(binding.oldMan.getX(), binding.oldMan.getY() + binding.oldMan.getHeight(), binding.oldMan.getWidth(), binding.oldMan.getHeight(), () -> {
                        gameView.setUpActions(actions);
                    });
                }
            });
        });
    }

    private void stopGame() {
        ((GameActivity) getActivity()).viewModel.stopTimer();
        storageManager.nextLevel();
    }

    private void startMimiGame() {
        GameActivity activity = ((GameActivity) getActivity());
        GameView gameView = activity.getBinding().gameView;
        binding.potato1.setVisibility(View.VISIBLE);
        binding.potato2.setVisibility(View.VISIBLE);
        binding.potato3.setVisibility(View.VISIBLE);
        binding.potato4.setVisibility(View.VISIBLE);
        binding.potato5.setVisibility(View.VISIBLE);

        binding.potato1.setOnClickListener(p -> {
            gameView.npcMove(binding.potato1.getX(), binding.potato1.getY() + binding.potato1.getHeight(), binding.potato1.getWidth(), binding.potato1.getHeight(), () -> {
                potatoes++;
                binding.potato1.setVisibility(View.GONE);
                if (potatoes == 5) {
                    stopGame();
                }
            });
        });

        binding.potato2.setOnClickListener(p -> {
            gameView.npcMove(binding.potato2.getX(), binding.potato2.getY() + binding.potato2.getHeight(), binding.potato2.getWidth(), binding.potato2.getHeight(), () -> {
                potatoes++;
                binding.potato2.setVisibility(View.GONE);
                if (potatoes == 5) {
                    stopGame();
                }
            });
        });
        binding.potato3.setOnClickListener(p -> {
            gameView.npcMove(binding.potato3.getX(), binding.potato3.getY() + binding.potato3.getHeight(), binding.potato3.getWidth(), binding.potato3.getHeight(), () -> {
                potatoes++;
                binding.potato3.setVisibility(View.GONE);
                if (potatoes == 5) {
                    stopGame();
                }
            });
        });
        binding.potato4.setOnClickListener(p -> {
            gameView.npcMove(binding.potato4.getX(), binding.potato4.getY() + binding.potato4.getHeight(), binding.potato4.getWidth(), binding.potato4.getHeight(), () -> {
                potatoes++;
                binding.potato4.setVisibility(View.GONE);
                if (potatoes == 5) {
                    stopGame();
                }
            });
        });
        binding.potato5.setOnClickListener(p -> {
            gameView.npcMove(binding.potato5.getX(), binding.potato5.getY() + binding.potato5.getHeight(), binding.potato5.getWidth(), binding.potato5.getHeight(), () -> {
                potatoes++;
                binding.potato5.setVisibility(View.GONE);
                if (potatoes == 5) {
                    stopGame();
                }
            });
        });
    }
}
