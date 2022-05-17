package net.treasure.effect.script.preset;

import lombok.AllArgsConstructor;
import net.treasure.effect.player.EffectData;
import net.treasure.effect.script.Script;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public class Preset extends Script {

    List<Script> scripts;

    @Override
    public void tick(Player player, EffectData data, int times) {
        for (Script script : scripts) {
            script.tick(player, data, times);
        }
    }

    @Override
    public Preset clone() {
        return new Preset(scripts);
    }
}