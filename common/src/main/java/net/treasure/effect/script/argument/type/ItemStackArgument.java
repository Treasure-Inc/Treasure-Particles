package net.treasure.effect.script.argument.type;

import net.treasure.constants.Patterns;
import net.treasure.effect.script.reader.ReaderContext;
import net.treasure.util.logging.ComponentLogger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ItemStackArgument {

    public static ItemStack read(ReaderContext<?> context) {
        Material material = null;
        int data = 0;
        var matcher = Patterns.INNER_SCRIPT.matcher(context.value());

        while (matcher.find()) {
            String type = matcher.group("type");
            String value = matcher.group("value");
            try {
                switch (type) {
                    case "material" -> material = Material.valueOf(value.toUpperCase(Locale.ENGLISH));
                    case "data" -> data = Integer.parseInt(value);
                    default -> ComponentLogger.error(context, "Unexpected Item argument: " + type);
                }
            } catch (Exception ignored) {
                ComponentLogger.error(context, "Unexpected '" + type + "' value for item argument: " + value);
            }
        }
        if (material == null) {
            ComponentLogger.error(context, "Material cannot be null");
            return null;
        }

        var item = new ItemStack(material);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(data);
            item.setItemMeta(meta);
        }
        return item;
    }
}