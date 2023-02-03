package net.treasure.effect.script.message;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import net.treasure.util.message.MessageUtils;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage extends Script {

    String message;

    @Override
    public TickResult tick(Player player, EffectData data, TickHandler handler, int times) {
        MessageUtils.sendParsed(player, data.replaceVariables(player, message));
        return TickResult.NORMAL;
    }

    @Override
    public Script clone() {
        return new ChatMessage(message);
    }
}