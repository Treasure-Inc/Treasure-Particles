package net.treasure.particles.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.treasure.particles.TreasureParticles;
import net.treasure.particles.constants.Keys;
import net.treasure.particles.locale.Translations;
import net.treasure.particles.player.PlayerManager;
import net.treasure.particles.util.message.MessageUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Expansions extends PlaceholderExpansion {

    final PlayerManager playerManager = TreasureParticles.getPlayerManager();

    @Override
    @Nullable
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return null;

        var data = playerManager.getEffectData(player);
        if (data == null) return null;

        return switch (params) {
            case "in_use" -> String.valueOf(data.isEnabled());
            case "current_effect" ->
                    data.getCurrentEffect() != null ? data.getCurrentEffect().getParsedDisplayName() : MessageUtils.parseLegacy(Translations.PAPI_CURRENT_EFFECT_NULL);
            case "current_effect_raw" ->
                    data.getCurrentEffect() != null ? data.getCurrentEffect().getDisplayName() : MessageUtils.parseLegacy(Translations.PAPI_CURRENT_EFFECT_NULL);
            case "effects_visibility" ->
                    MessageUtils.parseLegacy(data.isEffectsEnabled() ? Translations.PAPI_ENABLED : Translations.PAPI_DISABLED);
            case "notifications" ->
                    MessageUtils.parseLegacy(data.isNotificationsEnabled() ? Translations.PAPI_ENABLED : Translations.PAPI_DISABLED);
            default -> null;
        };
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return Keys.NAMESPACE;
    }

    @Override
    public @NotNull String getAuthor() {
        return "ItsZypec";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
}