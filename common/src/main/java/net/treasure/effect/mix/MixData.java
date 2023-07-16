package net.treasure.effect.mix;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.TreasureParticles;
import net.treasure.effect.Effect;
import net.treasure.effect.handler.TickHandler;
import net.treasure.gui.type.effects.EffectsGUI;
import net.treasure.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
public class MixData {
    String name;
    boolean needsColorGroup;
    String prefColorGroup;
    List<Pair<String, String>> handlers = new ArrayList<>();
    transient Effect cache;

    public Effect get() {
        if (cache != null) return cache;
        var effectManager = TreasureParticles.getEffectManager();
        var colorManager = TreasureParticles.getColorManager();

        var interval = Integer.MAX_VALUE;
        var cachingEnabled = false;
        List<String> variables = new ArrayList<>();
        List<TickHandler> tickHandlers = new ArrayList<>();

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

            if (effect.isCachingEnabled())
                cachingEnabled = true;

            variables.addAll(effect.getVariables().stream().map(p -> p.getKey() + "=" + p.getValue()).toList());
            tickHandlers.add(tickHandler);
        }

        return cache = new Effect(
                name,
                name,
                null,
                EffectsGUI.DEFAULT_ICON.item(),
                null,
                null,
                variables,
                interval,
                cachingEnabled,
                tickHandlers,
                colorGroup
        );
    }

    public void resetCache() {
        cache = null;
    }
}