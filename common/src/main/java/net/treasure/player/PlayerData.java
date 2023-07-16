package net.treasure.player;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.treasure.effect.mix.MixData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {
    String effectName;
    Map<String, String> colorPreferences = new HashMap<>();
    List<MixData> mixData = new ArrayList<>();
    boolean effectsEnabled = true;
    boolean notificationsEnabled = true;
}