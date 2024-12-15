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
import net.treasure.particles.effect.script.variable.data.VariableData;
import net.treasure.particles.gui.type.effects.EffectsGUI;
import net.treasure.particles.util.message.MessageUtils;
import net.treasure.particles.util.tuples.Pair;
import org.bukkit.ChatColor;
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

    private String name;
    private boolean needsColorGroup;
    private String prefColorGroup;
    private List<Pair<String, String>> handlers = new ArrayList<>();
    private transient Effect cache;

    public Effect get(Player player) {
        if (cache != null) return cache;
        var effectManager = TreasureParticles.getEffectManager();
        var colorManager = TreasureParticles.getColorManager();
        var translations = TreasureParticles.getTranslations();

        var interval = Integer.MAX_VALUE;
        List<VariableData> variables = new ArrayList<>();
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
                variables.addAll(effect.getVariables().stream()
                        .filter(data -> !data.getName().equals(Variable.I) && !data.getName().equals(Variable.TIMES))
                        .map(data -> data.setEffect(effectKey))
                        .toList()
                );

            tickHandlers.add(tickHandler);

            ready.add(effect);
            if (tickHandler.event != null)
                effects.put(effect, tickHandler.event);
        }

        var details = effects.asMap().entrySet().stream().map(entry -> MessageUtils.gui("<gray>•</gray> " + entry.getKey().getDisplayName() + "<!b><gray>: " + entry.getValue().stream().map(e -> translations.get("events." + e.translationKey())).collect(Collectors.joining(", ")))).toArray(String[]::new);
        var hasDetails = details.length > 0;

        var description = new String[1 + (hasDetails ? 1 + details.length : 0)];
        description[0] = MessageUtils.gui("<dark_gray>Custom Mix");

        if (hasDetails) {
            description[1] = ChatColor.AQUA.toString();
            System.arraycopy(details, 0, description, 2, details.length);
        }

        return cache = new Effect(
                player.getName() + "/" + name,
                "<gold>" + name,
                description,
                EffectsGUI.DEFAULT_ICON.item(),
                null,
                null,
                false,
                variables,
                interval,
                false,
                tickHandlers,
                colorGroup,
                false
        );
    }

    public void resetCache() {
        cache = null;
    }
}