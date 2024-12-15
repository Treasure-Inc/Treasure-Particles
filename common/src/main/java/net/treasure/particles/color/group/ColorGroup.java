package net.treasure.particles.color.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.treasure.particles.color.scheme.ColorScheme;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@AllArgsConstructor
public class ColorGroup {

    private String key;
    private List<Option> availableOptions;

    public Option getOption(ColorScheme colorScheme) {
        return availableOptions.stream().filter(option -> option.colorScheme.equals(colorScheme)).findFirst().orElse(null);
    }

    public boolean hasOption(ColorScheme colorScheme) {
        return availableOptions.stream().anyMatch(option -> option.colorScheme.equals(colorScheme));
    }

    public boolean canUseAny(Player player) {
        return availableOptions.stream().anyMatch(option -> option.canUse(player));
    }

    @Getter
    @Accessors(fluent = true)
    @AllArgsConstructor
    public static class Option {
        private ColorScheme colorScheme;
        private String permission;

        public boolean canUse(Player player) {
            return permission == null || player.hasPermission(permission);
        }
    }
}