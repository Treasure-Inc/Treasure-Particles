package net.treasure.common.particles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.function.Predicate;

// PaperMC ParticleBuilder Impl
@Getter
@Setter
@Accessors(fluent = true, chain = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class ParticleBuilder {

    private final ParticleEffect particle;
    private Predicate<Player> viewers;
    private Player source;
    private Location location;
    private int amount = 1;
    private float offsetX = 0, offsetY = 0, offsetZ = 0;
    private float extra = 1;
    private Object data;

    public ParticleBuilder copy() {
        return new ParticleBuilder(particle, viewers, source, location, amount, offsetX, offsetY, offsetZ, extra, data);
    }

    public Predicate<Player> filter() {
        return player -> (source == null || player.canSee(source) && (viewers == null || viewers.test(player)));
    }

    public ParticleBuilder location(World world, double x, double y, double z) {
        this.location = new Location(world, x, y, z);
        return this;
    }

    public ParticleBuilder offset(Vector vector) {
        this.offsetX = (float) vector.getX();
        this.offsetY = (float) vector.getY();
        this.offsetZ = (float) vector.getZ();
        return this;
    }

    public ParticleBuilder offset(double offsetX, double offsetY, double offsetZ) {
        this.offsetX = (float) offsetX;
        this.offsetY = (float) offsetY;
        this.offsetZ = (float) offsetZ;
        return this;
    }

    public ParticleBuilder speed(double speed) {
        this.extra = (float) speed;
        return this;
    }

    public float speed() {
        return extra;
    }

    public ParticleBuilder noteColor(int color) {
        this.amount = 0;
        this.offsetX = color / 24f;
        return this;
    }

    public <T> T data() {
        //noinspection unchecked
        return (T) data;
    }

    public <T> ParticleBuilder data(T data) {
        this.data = data;
        return this;
    }
}