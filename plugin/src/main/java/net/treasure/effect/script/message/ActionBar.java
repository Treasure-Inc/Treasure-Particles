package net.treasure.effect.script.message;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import net.treasure.util.message.MessageUtils;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class ActionBar extends Script {

    String message;

    @Override
    public TickResult tick(Player player, EffectData data, int times) {
        MessageUtils.sendActionBarParsed(player, data.replaceVariables(message));
        return TickResult.NORMAL;
    }

    @Override
    public ActionBar clone() {
        return new ActionBar(message);
    }
}