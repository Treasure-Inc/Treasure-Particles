package net.treasure.particles.effect.script.message;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.data.PlayerEffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.util.message.MessageUtils;

@AllArgsConstructor
@NoArgsConstructor
public class ActionBar extends Script {

    private String message;

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        if (data instanceof PlayerEffectData playerEffectData)
            MessageUtils.sendActionBarParsed(playerEffectData.player, data.replaceVariables(effect, message));
        return TickResult.NORMAL;
    }

    @Override
    public ActionBar clone() {
        return new ActionBar(message);
    }
}