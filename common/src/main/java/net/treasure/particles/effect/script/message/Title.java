package net.treasure.particles.effect.script.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.data.PlayerEffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.util.message.MessageUtils;

@AllArgsConstructor
@Builder
@Getter
public class Title extends Script {

    private String title, subtitle;
    private int fadeIn, stay, fadeOut;

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        if (data instanceof PlayerEffectData playerEffectData)
            MessageUtils.sendTitleParsed(playerEffectData.player, data.replaceVariables(effect, title), data.replaceVariables(effect, subtitle), fadeIn, stay, fadeOut);
        return TickResult.NORMAL;
    }

    @Override
    public Script clone() {
        return new Title(title, subtitle, fadeIn, stay, fadeOut);
    }
}