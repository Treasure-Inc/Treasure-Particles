package net.treasure.effect.script;

import lombok.Getter;
import lombok.Setter;
import net.treasure.effect.player.EffectData;
import net.treasure.util.TimeKeeper;
import org.bukkit.entity.Player;

public abstract class Script implements Cloneable {

    @Getter
    @Setter
    private int interval = -1;

    public abstract void tick(Player player, EffectData data, int times);

    public void doTick(Player player, EffectData data, int times) {
        if (interval > 0 && !TimeKeeper.isElapsed(interval))
            return;
        tick(player, data, times);
    }

    @Override
    public abstract Script clone();
}
