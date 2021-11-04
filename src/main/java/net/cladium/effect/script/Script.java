package net.cladium.effect.script;

import lombok.Getter;
import lombok.Setter;
import net.cladium.effect.player.EffectData;
import net.cladium.util.TimeKeeper;
import org.bukkit.entity.Player;

public abstract class Script {

    @Getter
    @Setter
    private int interval = -1;

    public abstract void tick(Player player, EffectData data);

    public void doTick(Player player, EffectData data) {
        if (interval > 0 && !TimeKeeper.isElapsed(interval))
            return;
        tick(player, data);
    }
}
