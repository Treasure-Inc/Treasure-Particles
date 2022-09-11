package net.treasure.effect.script.particle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import net.treasure.color.data.ColorData;
import net.treasure.color.data.RGBColorData;
import net.treasure.color.data.RandomNoteColorData;
import net.treasure.common.Permissions;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import net.treasure.util.Vectors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.PropertyType;
import xyz.xenondevs.particle.data.ParticleData;
import xyz.xenondevs.particle.data.color.DustColorTransitionData;
import xyz.xenondevs.particle.data.color.DustData;
import xyz.xenondevs.particle.data.color.NoteColor;

@Builder
@AllArgsConstructor
public class ParticleSpawner extends Script {

    ParticleEffect effect;
    ParticleOrigin origin;
    String x, y, z, offsetX, offsetY, offsetZ;
    ColorData colorData;
    ParticleData particleData;
    int amount;
    @Builder.Default
    float multiplier = Float.MIN_VALUE;
    @Builder.Default
    float speed = Float.MIN_VALUE;
    @Builder.Default
    float size = Float.MIN_VALUE;
    @Builder.Default
    boolean direction = false;

    @Override
    public boolean tick(Player player, EffectData data, int times) {
        if (this.origin == null)
            return true;

        Location origin;
        double x = 0, y = 0, z = 0;
        double offsetX = 0, offsetY = 0, offsetZ = 0;

        origin = switch (this.origin) {
            case HEAD -> player.getEyeLocation();
            case FEET -> player.getLocation();
            case WORLD -> new Location(player.getWorld(), 0, 0, 0);
        };

        if (multiplier != Float.MIN_VALUE)
            origin = origin.add(player.getLocation().getDirection().multiply(multiplier));

        if (this.x != null) {
            try {
                x = Double.parseDouble(this.x);
            } catch (Exception e) {
                x = Double.parseDouble(data.replaceVariables(player, this.x));
            }
        }

        if (this.y != null) {
            try {
                y = Double.parseDouble(this.y);
            } catch (Exception e) {
                y = Double.parseDouble(data.replaceVariables(player, this.y));
            }
        }

        if (this.z != null) {
            try {
                z = Double.parseDouble(this.z);
            } catch (Exception e) {
                z = Double.parseDouble(data.replaceVariables(player, this.z));
            }
        }

        if (this.offsetX != null) {
            try {
                offsetX = Double.parseDouble(this.offsetX);
            } catch (Exception e) {
                offsetX = Double.parseDouble(data.replaceVariables(player, this.offsetX));
            }
        }

        if (this.offsetY != null) {
            try {
                offsetY = Double.parseDouble(this.offsetY);
            } catch (Exception e) {
                offsetY = Double.parseDouble(data.replaceVariables(player, this.offsetY));
            }
        }

        if (this.offsetZ != null) {
            try {
                offsetZ = Double.parseDouble(this.offsetZ);
            } catch (Exception e) {
                offsetZ = Double.parseDouble(data.replaceVariables(player, this.offsetZ));
            }
        }

        var offset = new Vector(offsetX, offsetY, offsetZ);

        if (direction) {

            var direction = player.getLocation().getDirection();

            var vector = new Vector(x, y, z);
            vector = Vectors.rotateAroundAxisX(vector, player.getEyeLocation().getPitch());
            vector = Vectors.rotateAroundAxisY(vector, player.getEyeLocation().getYaw());
            origin = origin.add(direction.clone().add(vector));

            if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
                offset = Vectors.rotateAroundAxisX(offset, player.getEyeLocation().getPitch());
                offset = Vectors.rotateAroundAxisY(offset, player.getEyeLocation().getYaw());
                offset = offset.add(direction.clone().add(offset));
            }
        } else {
            origin = origin.add(x, y, z);
        }

        ParticleBuilder builder = new ParticleBuilder(effect);

        // Particle Data
        if (particleData != null) {
            builder.setParticleData(particleData);
        } else if (effect.hasProperty(PropertyType.DUST) && colorData != null && colorData instanceof RGBColorData rgb) {
            if (effect.equals(ParticleEffect.DUST_COLOR_TRANSITION))
                builder.setParticleData(new DustColorTransitionData(rgb.next(), rgb.tempNext(), size != Float.MIN_VALUE ? size : 1));
            else if (size != Float.MIN_VALUE)
                builder.setParticleData(new DustData(rgb.next(), size));
            else
                builder.setColor(rgb.next());
        } else if (effect.hasProperty(PropertyType.COLORABLE) && colorData != null) {
            if (effect.equals(ParticleEffect.NOTE) && colorData.isNote()) {
                builder.setParticleData(colorData instanceof RandomNoteColorData randomNoteColorData ? randomNoteColorData.next() : new NoteColor(colorData.index()));
            } else if (colorData instanceof RGBColorData rgb) {
                builder.setColor(rgb.next());
            }
        }

        builder.setLocation(origin);
        builder.setOffset(offset);
        builder.setAmount(amount);
        if (speed != Float.MIN_VALUE)
            builder.setSpeed(speed);

        var playerManager = TreasurePlugin.getInstance().getPlayerManager();
        builder.display(viewer -> playerManager.getPlayerData(viewer).canSeeEffects(viewer));
        return true;
    }

    @Override
    public ParticleSpawner clone() {
        return new ParticleSpawner(effect, origin, x, y, z, offsetX, offsetY, offsetZ, colorData, particleData, amount, multiplier, speed, size, direction);
    }
}