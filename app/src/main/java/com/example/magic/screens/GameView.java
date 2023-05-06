package com.example.magic.screens;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.magic.GameApplication;
import com.example.magic.R;
import com.example.magic.databinding.DialogChooseBinding;
import com.example.magic.models.action.Action;
import com.example.magic.models.action.AddToInventory;
import com.example.magic.models.action.ChooseAction;
import com.example.magic.models.action.NextLevelAction;
import com.example.magic.models.action.NpcMessage;
import com.example.magic.models.action.RemoveFromInventory;
import com.example.magic.models.action.RunnableAction;
import com.example.magic.models.action.UserMessage;
import com.example.magic.services.MediaManager;
import com.example.magic.services.StorageManager;

import java.util.ArrayList;
import java.util.List;

public class GameView extends FrameLayout {

    private static final int EDGE_MARGIN_X = 200;
    private static final int EDGE_MARGIN_Y = 50;

    private LottieAnimationView player;

    private boolean firstLayout = true;

    private TextView dialog;

    private int viewHeight;

    private int viewWidth;

    private ViewPropertyAnimator movementAnimation;

    private ViewPropertyAnimator rotateAnimation;

    private ViewPropertyAnimator dialogAnimation;

    private FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

    private FrameLayout.LayoutParams lpDialog = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

    private MediaManager mediaManager;

    private StorageManager storageManager;

    private List<Action> actions = new ArrayList<>();
    public boolean isActionInProgress() {
        return currentAction < actions.size();
    }

    private int currentAction = 0;

    public GameView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mediaManager = ((GameApplication) context.getApplicationContext()).getMediaManager();
        storageManager = ((GameApplication) context.getApplicationContext()).getStorageManager();
        player = new LottieAnimationView(context, attrs);
        player.setRepeatCount(LottieDrawable.INFINITE);
        player.setAnimation("walking.json");
        player.setScaleX(1.2f);
        player.setScaleY(1.2f);
        player.setLayoutParams(lp);

        float playerHeight = 300 * 1.2f;
        float playerWidth = 200 * 1.2f;

        player.getLayoutParams().height = (int) playerHeight;
        player.getLayoutParams().width = (int) playerWidth;

        dialog = new TextView(context, attrs);
        dialog.setLayoutParams(lpDialog);

        int padding = (int) context.getResources().getDimension(R.dimen.padding12);
        dialog.setMaxHeight(300);
        dialog.setMaxWidth(400);
        dialog.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded, context.getTheme()));
        dialog.setPadding(padding, padding, padding, padding);
        dialog.setVisibility(GONE);

        addView(player);
        addView(dialog);
    }

    public void positionPlayerLeft() {
        cancelMovementAnimation();
        player.setX(EDGE_MARGIN_X);
        player.setY(viewHeight - player.getHeight() - EDGE_MARGIN_Y);

        smoothRotate(true);
    }

    public void moveToLeftLocation(Runnable onMoved) {
        move(0f + EDGE_MARGIN_X, player.getY(), onMoved);
    }

    public void moveToRightLocation(Runnable onMoved) {
        move(viewWidth - EDGE_MARGIN_X - player.getWidth(), player.getY(), onMoved);
    }

    public void positionPlayerRight() {
        cancelMovementAnimation();
        player.setX(viewWidth - EDGE_MARGIN_X - player.getWidth());
        player.setY(viewHeight - player.getHeight() - EDGE_MARGIN_Y);
        smoothRotate(false);
    }

    public void smoothRotate(boolean left) {
        float rotation = 0f;
        if (!left) {
            rotation = -180f;
        }

        cancelRotateAnimation();
        rotateAnimation = player.animate()
                .rotationY(rotation)
                .setDuration(100);
        rotateAnimation.start();
    }

    private void cancelRotateAnimation() {
        if (rotateAnimation != null) {
            rotateAnimation.cancel();
        }
        rotateAnimation = null;
    }

    private void cancelMovementAnimation() {
        if (movementAnimation != null) {
            movementAnimation.cancel();
        }
        movementAnimation = null;
    }

    float maxHeight;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        viewHeight = getHeight();
        viewWidth = getWidth();
        if (firstLayout) {
            positionPlayerLeft();
        }
        firstLayout = false;

        maxHeight = 0.90f * viewHeight;
        setOnTouchListener(
                new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            if (isActionInProgress()) {
                                play();
                                return false;
                            }
                            actions = new ArrayList<>();
                            currentAction = 0;
                            dialog.setVisibility(GONE);
                            float y = motionEvent.getY();
                            if (y < maxHeight) {
                                y = maxHeight;
                            }

                            move(motionEvent.getX() - player.getWidth() / 2f, y - player.getHeight(), () -> {
                            });
                        }
                        return false;
                    }
                }
        );
    }

    public void setUpActions(List<Action> actions) {
        if (this.actions.isEmpty()) {
            this.actions = actions;
            currentAction = 0;
            play();
        }
    }

    public void cancelActions() {
        this.actions = new ArrayList<>();
        currentAction = 0;
        play();
    }

    private void play() {
        dialog.setVisibility(GONE);
        if (actions.isEmpty()) {
            return;
        }
        Action action = actions.get(currentAction);
        if (action instanceof UserMessage) {
            UserMessage message = ((UserMessage) action);
            displayPlayerMessage(message.getText(), message.getNpcCenter());
        } else if (action instanceof NpcMessage) {
            NpcMessage message = ((NpcMessage) action);
            displayNpcMessage(message.getText(), message.getX(), message.getY());
        } else if (action instanceof NextLevelAction) {
            // отображать надпись новый уровень
            storageManager.nextLevel();
        }else if (action instanceof RemoveFromInventory) {
            storageManager.removeForInventory(((RemoveFromInventory) action).getItem());
        } else if (action instanceof RunnableAction) {
            ((RunnableAction) action).getRunnable().run();
        } else if (action instanceof ChooseAction) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            DialogChooseBinding binding = DialogChooseBinding.inflate(
                    LayoutInflater.from(getContext()), this, false
            );

            ChooseAction chooseAction = (ChooseAction) action;
            binding.title.setText(chooseAction.getTitle());
            binding.first.setText(chooseAction.getFirstTitle());
            binding.secoond.setText(chooseAction.getSecondTitle());

            builder.setView(binding.getRoot());

            AlertDialog dialog1 = builder.show();
            dialog1.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog1.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            binding.first.setOnClickListener(v ->
            {
                chooseAction.getFirstAction().run();
                dialog1.cancel();
            });
            binding.secoond.setOnClickListener(v -> {
                chooseAction.getSecondAction().run();
                dialog1.cancel();
            });
        }
        currentAction++;
    }

    public void displayPlayerMessage(String text, float npcX) {
        float x = player.getX() + player.getWidth();
        float y = player.getY();

        dialog.setText(text);
        dialog.setX(x);
        dialog.setY(y);

        if (dialogAnimation != null) {
            dialogAnimation.cancel();
        }

        dialog.setAlpha(0f);
        dialog.setVisibility(VISIBLE);
        dialogAnimation = dialog.animate()
                .setDuration(1000)
                .alpha(1f);
        dialogAnimation.start();
    }

    public void displayNpcMessage(String text, float x, float y) {
        dialog.setText(text);
        dialog.setX(x);
        dialog.setY(y);

        if (dialogAnimation != null) {
            dialogAnimation.cancel();
        }
        dialog.setAlpha(0f);
        dialog.setVisibility(VISIBLE);
        dialogAnimation = dialog.animate()
                .setDuration(1000)
                .alpha(1f);
        dialogAnimation.start();
    }

    public void move(float x, float y, Runnable onMoved) {
        if (dialogAnimation != null) {
            dialogAnimation.cancel();
        }
        float centerPlayer = player.getX() + player.getWidth() / 2f;

        boolean left = x >= centerPlayer;
        smoothRotate(left);


        movementAnimation = player.animate()
                .x(x)
                .y(y)
                .setDuration(1500)
                .withStartAction(() -> {
                    mediaManager.startSteps();
                    player.playAnimation();
                })
                .withEndAction(() -> {
                    mediaManager.stopSteps();
                    player.cancelAnimation();
                    onMoved.run();
                });
        movementAnimation.start();
    }

    public void npcMove(float x, float y, float width, float height, Runnable onMoved) {
        if (isActionInProgress()) {
            return;
        }
        float centerPlayer = player.getX() + player.getWidth() / 2f;
        boolean left = x >= centerPlayer;

        smoothRotate(left);

        // Если ближайшая сторона npc - правая
        if (!left) {
            x += width;
        } else {
            x -= player.getWidth() - 25;
        }

        movementAnimation = player.animate()
                .x(x)
                .y(y - player.getHeight())
                .setDuration(1500)
                .withStartAction(() -> {
                    mediaManager.startSteps();
                    player.playAnimation();
                })
                .withEndAction(() -> {
                    mediaManager.stopSteps();
                    player.cancelAnimation();
                    onMoved.run();
                });

        movementAnimation.start();
    }
}
