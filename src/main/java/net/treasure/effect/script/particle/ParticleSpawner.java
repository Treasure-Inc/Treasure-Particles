package net.treasure.effect.script.particle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.color.data.ColorData;
import net.treasure.color.data.RGBColorData;
import net.treasure.color.data.RandomNoteColorData;
import net.treasure.core.TreasurePlugin;
import net.treasure.effect.TickHandler;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;
import net.treasure.effect.script.argument.type.BooleanArgument;
import net.treasure.effect.script.argument.type.FloatArgument;
import net.treasure.effect.script.argument.type.IntArgument;
import net.treasure.effect.script.argument.type.RangeArgument;
import net.treasure.effect.script.argument.type.VectorArgument;
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

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class ParticleSpawner extends Script {

    protected ParticleEffect effect;
    protected ParticleOrigin origin;
    protected VectorArgument position;
    protected ColorData colorData;
    protected ParticleData particleData;
    protected IntArgument amount;
    protected FloatArgument multiplier;
    protected RangeArgument speed, size;
    protected BooleanArgument directional = new BooleanArgument(false);

    public ParticleBuilder tick(Player player, EffectData data) {
        if (this.origin == null)
            return null;

        Location origin;

        origin = switch (this.origin) {
            case HEAD -> player.getEyeLocation();
            case FEET -> player.getLocation();
            case WORLD -> new Location(player.getWorld(), 0, 0, 0);
        };

        if (multiplier != null)
            origin = origin.add(player.getLocation().getDirection().multiply(multiplier.get(player, data)));

        var vector = position == null ? new Vector(0, 0, 0) : position.get(player, data);

        var directional = this.directional.get(player, data);
        if (directional) {
            vector = Vectors.rotateAroundAxisX(vector, player.getEyeLocation().getPitch());
            vector = Vectors.rotateAroundAxisY(vector, player.getEyeLocation().getYaw());
            origin = origin.add(player.getLocation().getDirection().add(vector));
        } else {
            origin = origin.add(vector);
        }

        ParticleBuilder builder = new ParticleBuilder(effect);

        // Particle Data
        if (particleData != null) {
            builder.setParticleData(particleData);
        } else if (effect.hasProperty(PropertyType.DUST) && colorData != null) {
            if (effect.equals(ParticleEffect.DUST_COLOR_TRANSITION) && colorData instanceof RGBColorData rgb)
                builder.setParticleData(new DustColorTransitionData(rgb.next(), rgb.tempNext(), size != null ? size.get(player, data) : 1));
            else if (size != null)
                builder.setParticleData(new DustData(colorData.next(), size.get(player, data)));
            else
                builder.setColor(colorData.next());
        } else if (effect.hasProperty(PropertyType.COLORABLE) && colorData != null) {
            if (effect.equals(ParticleEffect.NOTE) && colorData.isNote()) {
                builder.setParticleData(colorData instanceof RandomNoteColorData randomNoteColorData ? randomNoteColorData.random() : new NoteColor(colorData.index()));
            } else {
                var color = colorData.next();
                builder.setColor(color);
            }
        }

        builder.setLocation(origin);
        if (amount != null)
            builder.setAmount(amount.get(player, data));
        if (speed != null)
            builder.setSpeed(speed.get(player, data));

        return builder;
    }

    public void display(ParticleBuilder builder) {
        var playerManager = TreasurePlugin.getInstance().getPlayerManager();
        builder.display(viewer -> playerManager.getEffectData(viewer).canSeeEffects(viewer));
    }

    @Override
    public TickResult tick(Player player, EffectData data, TickHandler handler, int times) {
        return null;
    }

    @Override
    public Script clone() {
        return null;
    }
}