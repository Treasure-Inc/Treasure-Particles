package net.treasure.common;

import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.DataHolder;

import static net.treasure.common.Keys.NAMESPACE;

public class Permissions implements DataHolder {

    public static String BASE,
            ADMIN,
            CAN_SEE_EFFECTS;

    public static final String COMMAND_BASE = "%menu",
            COMMAND_ADMIN = "%admin";

    @Override
    public boolean checkVersion() {
        return true;
    }

    @Override
    public boolean initialize() {
        var inst = TreasurePlugin.getInstance();
        var config = inst.getConfig();
        var replacements = inst.getCommandManager().getCommandReplacements();

        replacements.addReplacement(COMMAND_BASE, BASE = config.getString("permissions.menu", NAMESPACE + ".menu"));
        replacements.addReplacement(COMMAND_ADMIN, ADMIN = config.getString("permissions.admin", NAMESPACE + ".admin"));

        CAN_SEE_EFFECTS = config.getString("permissions.can_see_effects", NAMESPACE + ".can_see_effects");
        return true;
    }

    @Override
    public void reload() {
        initialize();
    }

    public String replace(String key) {
        return key != null && key.startsWith("%") ? TreasurePlugin.getInstance().getConfig().getString("permissions." + key.substring(1), key) : key;
    }
}