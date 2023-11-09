package net.treasure.particles.effect.script.sound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlaySound extends Script {

    private String sound;
    private SoundCategory category = SoundCategory.MASTER;
    private boolean clientSide = true;
    float volume = 1, pitch = 1;

    @Override
    public TickResult tick(Player player, EffectData data, HandlerEvent event, int times) {
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