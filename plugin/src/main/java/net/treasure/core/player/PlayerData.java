package net.treasure.core.player;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {
    String effectName;
    Map<String, String> colorPreferences = new HashMap<>();
    boolean effectsEnabled = true;
    boolean notificationsEnabled = true;
}