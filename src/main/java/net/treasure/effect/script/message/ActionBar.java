package net.treasure.effect.script.message;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.treasure.effect.Effect;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.ScriptReader;
import net.treasure.util.message.MessageUtils;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class ActionBar extends Script implements ScriptReader<ActionBar> {

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

    @Override
    public ActionBar read(Effect effect, String line) {
        return new ActionBar(line);
    }
}