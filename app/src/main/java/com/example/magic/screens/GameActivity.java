package com.example.magic.screens;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.magic.GameApplication;
import com.example.magic.R;
import com.example.magic.databinding.ActivityGameBinding;
import com.example.magic.models.BounceInterpolator;
import com.example.magic.models.Item;
import com.example.magic.models.Level;
import com.example.magic.models.Location;
import com.example.magic.models.Transition;
import com.example.magic.services.MediaManager;
import com.example.magic.services.StorageManager;

import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;

    public ActivityGameBinding getBinding() {
        return binding;
    }

    private MediaManager mediaManager;
    public GameViewModel viewModel;

    private StorageManager storageManager;

    private NavController navController;

    private Location currentLocation;

    private Transition eventTransition = null;

    private Level lastLevel = null;

    public void showNextLevel(Level level) {
        if (level == lastLevel) {
            return;
        }
        if (level == Level.MUM || level == Level.LAST) {
            lastLevel = level;
            return;
        }
        if (level.name.isEmpty()) {
            lastLevel = level;
            return;
        }
        lastLevel = level;
        binding.nextLevelTitle.setAlpha(0f);
        binding.nextLevelTitle.setVisibility(VISIBLE);
        binding.nextLevelTitle.animate()
                .setDuration(1000)
                .alpha(1f)
                .withEndAction(() -> {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        binding.nextLevelTitle.setVisibility(View.GONE);
                    }, 3000);
                })
                .start();

        binding.nextLevelValue.setText(level.name);
        binding.nextLevelValue.setAlpha(0f);
        binding.nextLevelValue.setVisibility(VISIBLE);
        binding.nextLevelValue.animate()
                .setDuration(1000)
                .alpha(1f)
                .withEndAction(() -> {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        binding.nextLevelValue.setVisibility(View.GONE);
                    }, 3000);
                })
                .start();
    }

    public void addToInventory() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.inventory_update);
        BounceInterpolator bounceInterpolator = new BounceInterpolator(0.2, 20);
        animation.setInterpolator(bounceInterpolator);
        binding.inventoryCard.startAnimation(animation);
    }

    public void setRightLocationVisible(int visible) {
        binding.rightLocation.setVisibility(visible);
    }

    public void setLeftLocationVisible(int visible) {
        binding.leftLocation.setVisibility(visible);
    }

    private List<Item> currentInventory = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaManager = ((GameApplication) getApplication()).getMediaManager();
        viewModel = new ViewModelProvider(this).get(GameViewModel.class);
        binding = ActivityGameBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> {
                    if (destination.getId() == R.id.caveFragment) {
                        currentLocation = Location.CAVE;
                    } else if (destination.getId() == R.id.houseFragment) {
                        currentLocation = Location.HOME;
                    } else if (destination.getId() == R.id.forestFragment) {
                        currentLocation = Location.FOREST;
                    } else if (destination.getId() == R.id.marketFragment) {
                        currentLocation = Location.MARKET;
                    }
                }
        );

        storageManager = ((GameApplication) getApplication()).getStorageManager();
        storageManager.heath.setValue(storageManager.getGame().getHealth());
        storageManager.transition.setValue(storageManager.getGame().getLastTransition());
        storageManager.level.setValue(storageManager.getGame().getCurrentLevel());
        storageManager.gameOver.setValue(storageManager.getGame().getGameOver());
        storageManager.inventory.setValue(storageManager.getGame().getItems());
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                AlertDialog dialog = builder.setTitle("Вы действительно хотите выйти?")
                        .setMessage("Прогресс будет сохранен автоматически")
                        .setPositiveButton("Выйти", (dialogInterface, i) -> {
                            dialogInterface.cancel();
                            finish();
                        })
                        .setNegativeButton("Отмена", (dialogInterface, i) -> {
                            dialogInterface.cancel();
                        })
                        .create();
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                //Show the dialog!
                dialog.show();

                //Set the dialog to immersive sticky mode
                dialog.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        binding.exit.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.rightLocation.setOnClickListener(v -> {
            binding.gameView.cancelActions();
            binding.gameView.moveToRightLocation(() -> {
                storageManager.rightLocation(currentLocation);
            });
        });

        binding.leftLocation.setOnClickListener(v -> {
            binding.gameView.cancelActions();
            binding.gameView.moveToLeftLocation(() -> {
                storageManager.leftLocation(currentLocation);
            });
        });

        binding.inventory.setOnClickListener(v -> {
            ((GameApplication) getApplication()).getMediaManager().startChest();
            navController.navigate(R.id.inventoryFragment);
        });

        viewModel.timerData.observe(this, s -> {
            if (s == null) {
                binding.timerBg.setVisibility(View.GONE);
                return;
            }
            binding.timerBg.setVisibility(VISIBLE);
            long minutes = s / 60;
            long seconds = s % 60;

            String text = minutes + ":" + seconds;
            binding.timer.setText(text);

            if (s <= 10) {
                binding.timer.setTextColor(
                        getColor(R.color.red)
                );
            } else {
                binding.timer.setTextColor(
                        getColor(R.color.white)
                );
            }

            if (s == 0) {
                storageManager.gameOver(false);
            }
        });

        storageManager.inventory.observe(this, in -> {
            if (storageManager.getGame() == null || storageManager.getGame().getGameOver()) {
                return;
            }
            if (currentInventory == null && !in.isEmpty()) {
                addToInventory();
                currentInventory = in;
            } else if (currentInventory != null && !in.equals(currentInventory)) {
                addToInventory();
                currentInventory = in;
            }

        });

        storageManager.heath.observe(
                this,
                health -> {
                    if (storageManager.getGame() == null || storageManager.getGame().getGameOver()) {
                        return;
                    }
                    if (health >= 1) {
                        binding.heartOne.setAlpha(1f);

                        if (health >= 2) {
                            binding.heartTwo.setAlpha(1f);

                            if (health >= 3) {
                                binding.heartThree.setAlpha(1f);
                            } else {
                                binding.heartThree.setAlpha(0.3f);
                            }
                        } else {
                            binding.heartTwo.setAlpha(0.3f);
                            binding.heartThree.setAlpha(0.3f);
                        }
                    } else {
                        storageManager.gameOver(false);
                    }
                });

        storageManager.level.observe(this, level -> {
            if (storageManager.getGame() == null || storageManager.getGame().getGameOver()) {
                return;
            }
            showNextLevel(level);
            if (level == Level.LAST) {
                storageManager.gameOver(true);
                viewModel.stopTimer();
            }
        });

        storageManager.gameOver.observe(this, gameOver -> {
            if (gameOver != null && gameOver) {
                GameOverFragment.getInstance(storageManager.getGame().getVictory()).show(getSupportFragmentManager(), "GAME OVER");
            }
        });

        storageManager.transition.observe(
                this,
                transition -> {
                    if (storageManager.getGame() == null || storageManager.getGame().getGameOver()) {
                        return;
                    }
                    if (transition == null || transition.getTo() == null) {
                        return;
                    }
                    Log.d("TAG", "TRANSITION " + transition.getTo() + " " + transition.getFrom());
                    if ((eventTransition != null && Objects.equals(eventTransition.getFrom(), transition.getFrom())) && Objects.equals(eventTransition.getTo(), transition.getTo())) {
                        return;
                    }

                    Log.d("TAG", "EV TRA " + eventTransition);
                    eventTransition = transition;

                    if (transition.getTo() == Location.HOME) {
                        binding.leftLocation.setVisibility(View.GONE);
                    } else {
                        binding.leftLocation.setVisibility(VISIBLE);
                    }

                    if (transition.getTo() == Location.CAVE) {
                        binding.rightLocation.setVisibility(View.GONE);
                    } else {
                        binding.rightLocation.setVisibility(VISIBLE);
                    }

                    if (transition.getTo() == Location.HOME) {
                        if (transition.getFrom() == null) {
                            binding.gameView.positionPlayerLeft();
                        } else {
                            binding.gameView.positionPlayerRight();
                        }
                        navController.navigate(R.id.houseFragment);
                    } else if (transition.getTo() == Location.MARKET) {
                        if (transition.getFrom() == Location.HOME) {
                            binding.gameView.positionPlayerLeft();
                        } else {
                            binding.gameView.positionPlayerRight();
                        }
                        navController.navigate(R.id.marketFragment);
                    } else if (transition.getTo() == Location.CAVE) {
                        if (transition.getFrom() == Location.FOREST) {
                            binding.gameView.positionPlayerLeft();
                        } else {
                            binding.gameView.positionPlayerRight();
                        }
                        navController.navigate(R.id.caveFragment);
                    } else if (transition.getTo() == Location.FOREST) {
                        if (transition.getFrom() == Location.MARKET) {
                            binding.gameView.positionPlayerLeft();
                        } else {
                            binding.gameView.positionPlayerRight();
                        }
                        navController.navigate(R.id.forestFragment);
                    }
                }
        );
    }
}
