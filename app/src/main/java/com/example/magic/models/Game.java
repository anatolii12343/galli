package com.example.magic.models;

import java.util.List;

/**
 * Класс, который хранит текущее состояние игры
 */
public class Game {

    // Жизни
    private int health;

    // Инвентарь
    private List<Item> items;

    // Последний переход
    private Transition lastTransition;

    // Текущий уровень
    private Level currentLevel;

    private Boolean helpForOldMan;

    private Boolean gameOver;

    private Boolean victory;

    private Boolean forestUnlocked;

    private Boolean caveUnlocked;

    public Boolean getForestUnlocked() {
        return forestUnlocked;
    }

    public void setForestUnlocked(Boolean forestUnlocked) {
        this.forestUnlocked = forestUnlocked;
    }

    public Boolean getCaveUnlocked() {
        return caveUnlocked;
    }

    public void setCaveUnlocked(Boolean caveUnlocked) {
        this.caveUnlocked = caveUnlocked;
    }

    public Boolean getVictory() {
        return victory;
    }

    public void setVictory(Boolean victory) {
        this.victory = victory;
    }

    public Boolean getGameOver() {
        return gameOver;
    }

    public void setGameOver(Boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Boolean getHelpForOldMan() {
        return helpForOldMan;
    }

    public void setHelpForOldMan(Boolean helpForOldMan) {
        this.helpForOldMan = helpForOldMan;
    }

    public int getHealth() {
        return health;
    }

    public Transition getLastTransition() {
        return lastTransition;
    }

    public void setLastTransition(Transition lastTransition) {
        this.lastTransition = lastTransition;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }


    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }
}


