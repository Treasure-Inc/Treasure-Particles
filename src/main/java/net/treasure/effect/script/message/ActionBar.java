package net.treasure.effect.script.message;

import lombok.AllArgsConstructor;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import net.treasure.util.message.MessageUtils;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ActionBar extends Script {

    String message;

    @Override
    public boolean tick(Player player, EffectData data, int times) {
        MessageUtils.sendActionBarParsed(player, data.replaceVariables(player, message));
        return true;
    }

    @Override
    public ActionBar clone() {
        return new ActionBar(message);
    }
}