package net.treasure.effect.script;

import lombok.Builder;
import net.treasure.effect.data.EffectData;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Builder
public class PlaySound extends Script {

    @NotNull
    private String sound;
    @Builder.Default
    private boolean clientSide = true;
    @Builder.Default
    float volume = 1, pitch = 1;

    @Override
    public void tick(Player player, EffectData data, int times) {
        if (clientSide)
            player.playSound(player.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
        else
            player.getWorld().playSound(player.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public Script clone() {
        return new PlaySound(sound, clientSide, volume, pitch);
    }
}