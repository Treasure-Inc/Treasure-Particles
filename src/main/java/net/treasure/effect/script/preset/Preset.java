package net.treasure.effect.script.preset;

import lombok.AllArgsConstructor;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public class Preset extends Script {

    List<Script> scripts;

    @Override
    public TickResult tick(Player player, EffectData data, TickHandler handler, int times) {
        for (var script : scripts) {
            var result = script.tick(player, data, handler, times);
            if (result != TickResult.NORMAL)
                return result;
        }
        return TickResult.NORMAL;
    }

    @Override
    public Preset clone() {
        return new Preset(scripts);
    }
}