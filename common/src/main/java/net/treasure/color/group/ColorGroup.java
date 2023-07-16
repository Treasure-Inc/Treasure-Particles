package net.treasure.color.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.treasure.color.scheme.ColorScheme;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@AllArgsConstructor
public class ColorGroup {
    String key;
    List<Option> availableOptions;

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
        ColorScheme colorScheme;
        String permission;

        public boolean canUse(Player player) {
            return permission == null || player.hasPermission(permission);
        }
    }
}