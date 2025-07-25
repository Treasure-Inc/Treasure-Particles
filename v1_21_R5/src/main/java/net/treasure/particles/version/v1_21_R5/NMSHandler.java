package net.treasure.particles.version.v1_21_R5;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.phys.Vec3D;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.util.nms.AbstractNMSHandler;
import net.treasure.particles.util.nms.particles.ParticleBuilder;
import net.treasure.particles.util.nms.particles.ParticleEffect;
import net.treasure.particles.version.v1_21_R5.data.NMSGenericData;
import net.treasure.particles.version.v1_21_R5.data.color.NMSDustData;
import net.treasure.particles.version.v1_21_R5.data.color.NMSDustTransitionData;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class NMSHandler extends AbstractNMSHandler {

    @Override
    public void sendParticle(ParticleBuilder builder) {
        var location = builder.location();

        var world = location.getWorld();
        if (world == null) return;

        var packet = new PacketPlayOutWorldParticles(
                builder.data(),
                builder.longDistance(),
                true,
                location.getX(),
                location.getY(),
                location.getZ(),
                builder.offsetX(),
                builder.offsetY(),
                builder.offsetZ(),
                builder.speed(),
                builder.amount());

        var filter = builder.viewers();
        for (var player : Bukkit.getOnlinePlayers()) {
            if (filter != null && !filter.test(player)) continue;
            if (!player.getWorld().equals(world)) continue;

            ((CraftPlayer) player).getHandle().g.b(packet);
        }
    }

    @Override
    public void sendParticles(List<ParticleBuilder> builders) {
        List<Packet<? super PacketListenerPlayOut>> packets = new ArrayList<>();
        Predicate<Player> filter = null;
        World world = null;

        for (var builder : builders) {
            filter = builder.viewers();

            var location = builder.location();
            world = location.getWorld();
            if (world == null) return;

            var packet = new PacketPlayOutWorldParticles(
                    builder.data(),
                    builder.longDistance(),
                    true,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    builder.offsetX(),
                    builder.offsetY(),
                    builder.offsetZ(),
                    builder.speed(),
                    builder.amount());

            packets.add(packet);
        }
        var bundle = new ClientboundBundlePacket(packets);

        for (var player : Bukkit.getOnlinePlayers()) {
            if (filter != null && !filter.test(player)) continue;
            if (!player.getWorld().equals(world)) continue;

            ((CraftPlayer) player).getHandle().g.b(bundle);
        }
    }

    @Override
    public Object getParticleParam(ParticleEffect effect) {
        return getGenericData(effect, null);
    }

    @Override
    public Object getDustData(Color color, float size) {
        return new NMSDustData(color, size).toNMS();
    }

    @Override
    public Object getColorTransitionData(Color color, Color transition, float size) {
        return new NMSDustTransitionData(color, transition, size).toNMS();
    }

    @Override
    public Object getGenericData(ParticleEffect effect, Object object) {
        return new NMSGenericData(effect, object).toNMS();
    }

    @Override
    public void strikeLightning(Location location, Predicate<Player> filter) {
        var world = location.getWorld();
        if (world == null) return;

        var lightning = new EntityLightning(EntityTypes.ay, ((CraftWorld) location.getWorld()).getHandle());
        lightning.a_(location.getX(), location.getY(), location.getZ());
        lightning.a(true);

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (filter != null && !filter.test(player)) return;
            if (!player.getWorld().equals(world)) return;

            ((CraftPlayer) player).getHandle().g.b(new PacketPlayOutSpawnEntity(lightning, 0, BlockPosition.a(location.getX(), location.getY(), location.getZ())));
        });
    }

    @Override
    public Object getTargetData(ParticleEffect effect, EffectData effectData, Color color, Location target, int duration) {
        return switch (effect) {
            case TRAIL -> new TrailParticleOption(
                    new Vec3D(target.getX(), target.getY(), target.getZ()),
                    color.asRGB(),
                    duration
            );
            case VIBRATION -> new VibrationParticleOption(
                    new BlockPositionSource(new BlockPosition(target.getBlockX(), target.getBlockY(), target.getBlockZ())),
                    duration
            );
            default -> super.getTargetData(effect, effectData, color, target, duration);
        };
    }
}