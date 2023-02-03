package net.treasure.effect.script.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import net.treasure.util.message.MessageUtils;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Builder
@Getter
public class Title extends Script {

    String title, subtitle;
    int fadeIn, stay, fadeOut;

    @Override
    public TickResult tick(Player player, EffectData data, TickHandler handler, int times) {
        MessageUtils.sendTitleParsed(player, data.replaceVariables(player, title), data.replaceVariables(player, subtitle), fadeIn, stay, fadeOut);
        return TickResult.NORMAL;
    }

    @Override
    public Script clone() {
        return new Title(title, subtitle, fadeIn, stay, fadeOut);
    }
}