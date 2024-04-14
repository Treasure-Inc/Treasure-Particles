package net.treasure.particles.effect.script.visual;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.data.PlayerEffectData;
import net.treasure.particles.effect.handler.HandlerEvent;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.effect.script.argument.type.VectorArgument;
import net.treasure.particles.effect.script.particle.config.LocationOrigin;
import net.treasure.particles.util.nms.particles.Particles;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true, chain = true)
public class Lightning extends Script {

    private LocationOrigin origin;
    private VectorArgument position;

    @Override
    public TickResult tick(EffectData data, HandlerEvent event, int times) {
        Location origin = null;
        var player = data instanceof PlayerEffectData playerEffectData ? playerEffectData.player : null;
        if (player != null && player.getGameMode() == GameMode.SPECTATOR) return TickResult.NORMAL;

        var entity = switch (event) {
            case STATIC -> {
                origin = data.getLocation().clone();
                yield null;
            }
            case ELYTRA, STANDING, MOVING, SNEAKING, TAKE_DAMAGE -> player;
            case MOB_KILL, PLAYER_KILL, PROJECTILE, MOB_DAMAGE, PLAYER_DAMAGE, RIDE_VEHICLE ->
                    data instanceof PlayerEffectData playerEffectData ? playerEffectData.getTargetEntity() : null;
        };
        if (entity == null && origin == null) return null;

        if (origin == null)
            origin = this.origin.getLocation(entity);

        if (this.position != null)
            origin.add(this.position.get(this, data));

        var playerManager = TreasureParticles.getPlayerManager();
        Predicate<Player> filter = viewer -> {
            var d = playerManager.getEffectData(viewer);
            return d != null && d.canSeeEffects() && (player == null || viewer.canSee(player));
        };

        Particles.NMS.strikeLightning(origin, filter);
        return TickResult.NORMAL;
    }

    @Override
    public Script clone() {
        return null;
    }
}
