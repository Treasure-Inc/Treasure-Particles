package net.treasure.particles.effect.script.conditional;

import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.data.EffectData;
import org.bukkit.entity.Player;

public interface Predicate {
    boolean test(Player player, Effect effect, EffectData data);
}