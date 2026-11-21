package com.bluffparty.game;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfileStore {
    private static final String PREFS = "bluff_profile";

    private final SharedPreferences preferences;

    public ProfileStore(Context context) {
        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public PlayerProfile load(int unlockedLevel) {
        int xp = preferences.getInt("xp", 0);
        int xpToNext = 500;
        return new PlayerProfile(
                preferences.getString("displayName", "Guest Player"),
                preferences.getString("playerId", "BLF-" + String.format("%06d",
                        100000 + (unlockedLevel * 73) % 899999)),
                preferences.getInt("level", Math.max(1, (unlockedLevel - 1) / 10 + 1)),
                xp,
                xpToNext,
                preferences.getInt("coins", 0),
                preferences.getInt("avatarId", 1),
                preferences.getInt("gamesPlayed", 0),
                preferences.getInt("questionsAnswered", 0),
                preferences.getInt("correctAnswers", 0),
                preferences.getInt("bestStreak", 0));
    }

    public void setDisplayName(String displayName) {
        preferences.edit().putString("displayName", displayName).apply();
    }

    public void setAvatarId(int avatarId) {
        preferences.edit().putInt("avatarId", avatarId).apply();
    }
}
