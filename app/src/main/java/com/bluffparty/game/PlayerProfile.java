package com.bluffparty.game;

public class PlayerProfile {
    private final String displayName;
    private final String playerId;
    private final int level;
    private final int xp;
    private final int xpToNextLevel;
    private final int coins;
    private final int avatarId;
    private final int gamesPlayed;
    private final int questionsAnswered;
    private final int correctAnswers;
    private final int bestStreak;

    public PlayerProfile(String displayName, String playerId, int level, int xp,
                         int xpToNextLevel, int coins, int avatarId, int gamesPlayed,
                         int questionsAnswered, int correctAnswers, int bestStreak) {
        this.displayName = displayName;
        this.playerId = playerId;
        this.level = level;
        this.xp = xp;
        this.xpToNextLevel = xpToNextLevel;
        this.coins = coins;
        this.avatarId = avatarId;
        this.gamesPlayed = gamesPlayed;
        this.questionsAnswered = questionsAnswered;
        this.correctAnswers = correctAnswers;
        this.bestStreak = bestStreak;
    }

    public String getDisplayName() { return displayName; }
    public String getPlayerId() { return playerId; }
    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getXpToNextLevel() { return xpToNextLevel; }
    public int getCoins() { return coins; }
    public int getAvatarId() { return avatarId; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getQuestionsAnswered() { return questionsAnswered; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getBestStreak() { return bestStreak; }

    public int getAccuracyPercent() {
        if (questionsAnswered <= 0) return 0;
        return Math.min(100, Math.round(correctAnswers * 100f / questionsAnswered));
    }
}
