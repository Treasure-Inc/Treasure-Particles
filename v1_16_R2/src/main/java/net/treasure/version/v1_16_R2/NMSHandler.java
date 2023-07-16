package net.treasure.version.v1_16_R2;

import net.minecraft.server.v1_16_R2.Packet;
import net.minecraft.server.v1_16_R2.PacketListenerPlayOut;
import net.minecraft.server.v1_16_R2.PacketPlayOutWorldParticles;
import net.treasure.util.nms.AbstractNMSHandler;
import net.treasure.util.nms.particles.ParticleBuilder;
import net.treasure.util.nms.particles.ParticleEffect;
import net.treasure.version.v1_16_R2.data.color.NMSDustTransitionData;
import net.treasure.version.v1_16_R2.data.NMSGenericData;
import net.treasure.version.v1_16_R2.data.color.NMSDustData;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.craftbukkit.v1_16_R2.CraftParticle;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class NMSHandler extends AbstractNMSHandler {

    @Override
    public void sendParticle(ParticleBuilder builder) {
        var filter = builder.viewers();

        var location = builder.location();
        var packet = new PacketPlayOutWorldParticles(
                builder.data(),
                false,
                location.getX(),
                location.getY(),
                location.getZ(),
                builder.offsetX(),
                builder.offsetY(),
                builder.offsetZ(),
                builder.speed(),
                builder.amount());

        for (var player : Bukkit.getOnlinePlayers()) {
            if (!filter.test(player)) continue;

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void sendParticles(List<ParticleBuilder> builders) {
        List<Packet<PacketListenerPlayOut>> packets = new ArrayList<>();
        Predicate<Player> filter = null;

        for (var builder : builders) {
            filter = builder.viewers();

            var location = builder.location();
            var packet = new PacketPlayOutWorldParticles(
                    builder.data(),
                    false,
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

        for (var player : Bukkit.getOnlinePlayers()) {
            if (filter != null && !filter.test(player)) continue;
            var p = ((CraftPlayer) player).getHandle().playerConnection;
            for (var packet : packets)
                p.sendPacket(packet);
        }
    }

    @Override
    public Object getParticleParam(ParticleEffect effect) {
        var bukkit = effect.bukkit();
        return bukkit == null ? null : CraftParticle.toNMS(bukkit);
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
}