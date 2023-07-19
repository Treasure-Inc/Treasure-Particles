package net.treasure.version.v1_19_R3;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
import net.treasure.util.nms.AbstractNMSHandler;
import net.treasure.util.nms.particles.ParticleBuilder;
import net.treasure.util.nms.particles.ParticleEffect;
import net.treasure.version.v1_19_R3.data.NMSGenericData;
import net.treasure.version.v1_19_R3.data.color.NMSDustData;
import net.treasure.version.v1_19_R3.data.color.NMSDustTransitionData;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.craftbukkit.v1_19_R3.CraftParticle;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
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
                builder.longDistance(),
                location.getX(),
                location.getY(),
                location.getZ(),
                builder.offsetX(),
                builder.offsetY(),
                builder.offsetZ(),
                builder.speed(),
                builder.amount());

        for (var player : Bukkit.getOnlinePlayers()) {
            if (filter != null && !filter.test(player)) continue;

            ((CraftPlayer) player).getHandle().b.a(packet);
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
                    builder.longDistance(),
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

            ((CraftPlayer) player).getHandle().b.a(bundle);
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