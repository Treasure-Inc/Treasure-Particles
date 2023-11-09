package net.treasure.particles.effect.mix;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.effect.Effect;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.handler.TickHandler;
import net.treasure.particles.effect.script.variable.Variable;
import net.treasure.particles.gui.type.effects.EffectsGUI;
import net.treasure.particles.util.message.MessageUtils;
import net.treasure.particles.util.tuples.Pair;
import net.treasure.particles.util.tuples.Triplet;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(fluent = true)
public class MixData {
    String name;
    boolean needsColorGroup;
    String prefColorGroup;
    List<Pair<String, String>> handlers = new ArrayList<>();
    transient Effect cache;

    public Effect get(Player player) {
        if (cache != null) return cache;
        var effectManager = TreasureParticles.getEffectManager();
        var colorManager = TreasureParticles.getColorManager();
        var translations = TreasureParticles.getTranslations();

        var interval = Integer.MAX_VALUE;
        List<Triplet<String, Double, String>> variables = new ArrayList<>();
        List<TickHandler> tickHandlers = new ArrayList<>();

        Set<Effect> ready = new HashSet<>();
        ListMultimap<Effect, HandlerEvent> effects = ArrayListMultimap.create();

        var colorGroup = colorManager.getColorGroup(prefColorGroup);
        if (needsColorGroup && colorGroup == null) return null;

        for (var pair : handlers) {
            var effectKey = pair.getKey();
            var tickHandlerKey = pair.getValue();

            var effect = effectManager.get(effectKey);
            if (effect == null) return null;

            var tickHandler = effect.getTickHandler(tickHandlerKey);
            if (tickHandler == null) return null;

            if (tickHandler.interval < interval)
                interval = tickHandler.interval;

            if (effect.getInterval() < interval)
                interval = effect.getInterval();

            if (!ready.contains(effect))
                variables.addAll(effect.getVariables().stream().filter(triplet -> !triplet.x().equals(Variable.I) && !triplet.x().equals(Variable.TIMES)).map(triplet -> triplet.z(effectKey)).toList());
            tickHandlers.add(tickHandler);

            ready.add(effect);
            if (tickHandler.event != null)
                effects.put(effect, tickHandler.event);
        }

        return cache = new Effect(
                player.getName() + "/" + name,
                name,
                effects.asMap().entrySet().stream().map(entry -> MessageUtils.gui("<gray>•</gray> " + entry.getKey().getDisplayName() + "<gray>: " + entry.getValue().stream().map(e -> translations.get("events." + e.translationKey())).collect(Collectors.joining(", ")))).toArray(String[]::new),
                EffectsGUI.DEFAULT_ICON.item(),
                null,
                null,
                variables,
                interval,
                false,
                tickHandlers,
                colorGroup
        );
    }

    public void resetCache() {
        cache = null;
    }
}