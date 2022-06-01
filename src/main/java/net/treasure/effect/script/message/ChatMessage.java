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
public class ChatMessage extends Script implements ScriptReader<ChatMessage> {

    String message;

    @Override
    public boolean tick(Player player, EffectData data, int times) {
        MessageUtils.sendParsed(player, data.replaceVariables(player, message));
        return true;
    }

    @Override
    public Script clone() {
        return new ChatMessage(message);
    }

    @Override
    public ChatMessage read(Effect effect, String line) {
        return new ChatMessage(line);
    }
}