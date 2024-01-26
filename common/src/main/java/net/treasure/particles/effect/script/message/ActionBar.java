package net.treasure.particles.effect.script.message;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.util.message.MessageUtils;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class ActionBar extends Script {

    private String message;

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        MessageUtils.sendActionBarParsed(player, data.replaceVariables(effect, message));
        return TickResult.NORMAL;
    }

    @Override
    public ActionBar clone() {
        return new ActionBar(message);
    }
}