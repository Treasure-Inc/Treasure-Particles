package net.treasure.effect.script.conditional;

import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;
import org.bukkit.entity.Player;

public interface Predicate {
    boolean test(Player player, Effect effect, EffectData data);
}