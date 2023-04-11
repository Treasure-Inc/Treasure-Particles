package net.treasure.effect.script.sound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class PlaySound extends Script {

    private String sound;
    private SoundCategory category = SoundCategory.MASTER;
    private boolean clientSide = true;
    float volume = 1, pitch = 1;

    @Override
    public TickResult tick(Player player, EffectData data, TickHandler handler, int times) {
        if (clientSide)
            player.playSound(player.getLocation(), sound, category, volume, pitch);
        else
            player.getWorld().playSound(player.getLocation(), sound, category, volume, pitch);
        return TickResult.NORMAL;
    }

    @Override
    public PlaySound clone() {
        return new PlaySound(sound, category, clientSide, volume, pitch);
    }
}