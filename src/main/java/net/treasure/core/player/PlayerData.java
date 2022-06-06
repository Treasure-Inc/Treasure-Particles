package net.treasure.core.player;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {
    String effectName;
    boolean effectsEnabled = true;
    boolean notificationsEnabled = true;
}