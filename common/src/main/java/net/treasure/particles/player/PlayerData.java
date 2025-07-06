package net.treasure.particles.player;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.treasure.particles.effect.mix.MixData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class PlayerData {

    public String effectName;
    public String selectedMix;

    public Map<String, String> colorPreferences = new HashMap<>();
    public List<MixData> mixData = new ArrayList<>();

    public boolean effectsEnabled = true;
    public boolean notificationsEnabled = true;
}