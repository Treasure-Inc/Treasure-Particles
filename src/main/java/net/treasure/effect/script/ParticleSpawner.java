package net.treasure.effect.script;

import lombok.AllArgsConstructor;
import lombok.Builder;
import net.treasure.color.data.ColorData;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.data.EffectData;
import net.treasure.util.Vectors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.PropertyType;

@Builder
@AllArgsConstructor
public class ParticleSpawner extends Script {

    private ParticleEffect effect;
    private String x, y, z, from, offsetX, offsetY, offsetZ;
    private ColorData colorData;
    int amount;
    @Builder.Default
    public float multiplier = Float.MIN_VALUE;
    @Builder.Default
    float speed = Float.MIN_VALUE;
    @Builder.Default
    boolean direction = false;

    @Override
    public boolean tick(Player player, EffectData data, int times) {
        Location origin;
        double x = 0, y = 0, z = 0;
        double offsetX = 0, offsetY = 0, offsetZ = 0;
        if (from.equalsIgnoreCase("head"))
            origin = player.getEyeLocation();
        else if (from.equalsIgnoreCase("feet"))
            origin = player.getLocation();
        else
            return true;
        if (multiplier != Float.MIN_VALUE)
            origin = origin.add(player.getLocation().getDirection().multiply(multiplier));

        if (this.x != null) {
            try {
                x = Double.parseDouble(this.x);
            } catch (Exception e) {
                boolean negative = this.x.startsWith("-");
                x = data.getVariable(player, this.x.substring(negative ? 1 : 0)).getValue() * (negative ? -1 : 1);
            }
        }

        if (this.y != null) {
            try {
                y = Double.parseDouble(this.y);
            } catch (Exception e) {
                boolean negative = this.y.startsWith("-");
                y = data.getVariable(player, this.y.substring(negative ? 1 : 0)).getValue() * (negative ? -1 : 1);
            }
        }

        if (this.z != null) {
            try {
                z = Double.parseDouble(this.z);
            } catch (Exception e) {
                boolean negative = this.z.startsWith("-");
                z = data.getVariable(player, this.z.substring(negative ? 1 : 0)).getValue() * (negative ? -1 : 1);
            }
        }

        if (this.offsetX != null) {
            try {
                offsetX = Double.parseDouble(this.offsetX);
            } catch (Exception e) {
                boolean negative = this.offsetX.startsWith("-");
                offsetX = data.getVariable(player, this.offsetX.substring(negative ? 1 : 0)).getValue() * (negative ? -1 : 1);
            }
        }

        if (this.offsetY != null) {
            try {
                offsetY = Double.parseDouble(this.offsetY);
            } catch (Exception e) {
                boolean negative = this.offsetY.startsWith("-");
                offsetY = data.getVariable(player, this.offsetY.substring(negative ? 1 : 0)).getValue() * (negative ? -1 : 1);
            }
        }

        if (this.offsetZ != null) {
            try {
                offsetZ = Double.parseDouble(this.offsetZ);
            } catch (Exception e) {
                boolean negative = this.offsetZ.startsWith("-");
                offsetZ = data.getVariable(player, this.offsetZ.substring(negative ? 1 : 0)).getValue() * (negative ? -1 : 1);
            }
        }

        if (direction) {
            Vector vector = new Vector(x, y, z);
            vector = Vectors.rotateAroundAxisX(vector, player.getEyeLocation().getPitch());
            vector = Vectors.rotateAroundAxisY(vector, player.getEyeLocation().getYaw());
            origin = origin.add(player.getLocation().getDirection()).add(vector);
        } else {
            if (x != 0 || y != 0 || z != 0)
                origin = origin.add(x, y, z);
        }

        ParticleBuilder builder = new ParticleBuilder(effect);
        if (effect.hasProperty(PropertyType.COLORABLE) && colorData != null) {
            builder.setColor(colorData.next());
        }
        builder.setLocation(origin);
        builder.setOffset(new Vector(offsetX, offsetY, offsetZ));
        builder.setAmount(amount);
        if (speed != Float.MIN_VALUE)
            builder.setSpeed(speed);

        var playerManager = TreasurePlugin.getInstance().getPlayerManager();
        builder.display(viewer -> playerManager.getPlayerData(viewer).isEffectsEnabled());
        return true;
    }

    @Override
    public ParticleSpawner clone() {
        return new ParticleSpawner(effect, x, y, z, from, offsetX, offsetY, offsetZ, colorData, amount, multiplier, speed, direction);
    }
}