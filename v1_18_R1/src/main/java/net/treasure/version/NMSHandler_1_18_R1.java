package net.treasure.version;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
import net.treasure.common.NMSHandler;
import net.treasure.common.particles.ParticleBuilder;
import net.treasure.common.particles.ParticleEffect;
import net.treasure.version.data.NMSGenericData;
import net.treasure.version.data.color.NMSDustData;
import net.treasure.version.data.color.NMSDustTransitionData;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.craftbukkit.v1_18_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class NMSHandler_1_18_R1 extends NMSHandler {

    @Override
    public void sendParticle(ParticleBuilder builder) {
        var filter = builder.filter();

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

            ((CraftPlayer) player).getHandle().b.a(packet);
        }
    }

    @Override
    public void sendParticles(List<ParticleBuilder> builders) {
        List<Packet<PacketListenerPlayOut>> packets = new ArrayList<>();
        Predicate<Player> filter = null;

        for (var builder : builders) {
            filter = builder.filter();

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
            var p = ((CraftPlayer) player).getHandle().b;
            for (var packet : packets)
                p.a(packet);
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