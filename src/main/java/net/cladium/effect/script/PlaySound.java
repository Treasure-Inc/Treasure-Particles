package net.cladium.effect.script;

import lombok.Builder;
import net.cladium.effect.player.EffectData;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Builder
public class PlaySound extends Script {

    @NotNull
    private String sound;
    private boolean clientSide;
    float volume, pitch;

    @Override
    public void tick(Player player, EffectData data) {
        if (clientSide)
            player.playSound(player.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
        else
            player.getWorld().playSound(player.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
    }
}