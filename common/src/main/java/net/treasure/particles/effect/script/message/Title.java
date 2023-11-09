package net.treasure.particles.effect.script.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.util.message.MessageUtils;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Builder
@Getter
public class Title extends Script {

    String title, subtitle;
    int fadeIn, stay, fadeOut;

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
        MessageUtils.sendTitleParsed(player, data.replaceVariables(effect, title), data.replaceVariables(effect, subtitle), fadeIn, stay, fadeOut);
        return TickResult.NORMAL;
    }

    @Override
    public Script clone() {
        return new Title(title, subtitle, fadeIn, stay, fadeOut);
    }
}