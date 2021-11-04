package net.cladium.effect.script;

import lombok.Builder;
import net.cladium.color.player.ColorData;
import net.cladium.effect.player.EffectData;
import net.cladium.util.Vectors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.PropertyType;

@Builder
public class ParticleSpawner extends Script {

    private ParticleEffect effect;
    private String x, y, z, from;
    private ColorData colorData;
    int amount;
    float speed, multiplier;
    boolean direction;

    @Override
    public void tick(Player player, EffectData data) {
        Location origin = null;
        double x = 0, y = 0, z = 0;
        if (from.equalsIgnoreCase("head"))
            origin = player.getEyeLocation();
        else if (from.equalsIgnoreCase("feet"))
            origin = player.getLocation();

        if (origin == null) return;

        if (this.x != null) {
            try {
                x = Double.parseDouble(this.x);
            } catch (Exception e) {
                boolean negative = this.x.startsWith("-");
                x = data.getVariable(this.x.substring(negative ? 1 : 0)).getValue() * (negative ? -1 : 1);
            }
        }

        if (this.y != null) {
            try {
                y = Double.parseDouble(this.y);
            } catch (Exception e) {
                boolean negative = this.y.startsWith("-");
                y = data.getVariable(this.y.substring(negative ? 1 : 0)).getValue() * (negative ? -1 : 1);
            }
        }

        if (this.z != null) {
            try {
                z = Double.parseDouble(this.z);
            } catch (Exception e) {
                boolean negative = this.z.startsWith("-");
                z = data.getVariable(this.z.substring(negative ? 1 : 0)).getValue() * (negative ? -1 : 1);
            }
        }

        if (direction) {
            Vector vector = new Vector(x, y, z);
            vector = Vectors.rotateAroundAxisX(vector, player.getEyeLocation().getPitch());
            vector = Vectors.rotateAroundAxisY(vector, player.getEyeLocation().getYaw());
            origin = origin.add(player.getLocation().getDirection()).add(vector);
        }

        ParticleBuilder builder = new ParticleBuilder(effect);
        if (effect.hasProperty(PropertyType.COLORABLE) && colorData != null) {
            // TODO renklerle ilgili bir problem var kesinlikle (? hala var mı)
            builder.setColor(colorData.next());
        }
        builder.setLocation(direction ? origin : origin.add(x, y, z));
        builder.setAmount(amount);
        if (speed != -5)
            builder.setSpeed(speed);
        builder.display();
    }
}
