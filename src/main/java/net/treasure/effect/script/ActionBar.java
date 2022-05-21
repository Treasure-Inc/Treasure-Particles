package net.treasure.effect.script;

import lombok.AllArgsConstructor;
import net.treasure.effect.data.EffectData;
import net.treasure.util.message.MessageUtils;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ActionBar extends Script {

    String message;

    @Override
    public void tick(Player player, EffectData data, int times) {
        MessageUtils.sendActionBarParsed(player, data.replaceVariables(player, message));
    }

    @Override
    public ActionBar clone() {
        return new ActionBar(message);
    }
}