package net.treasure.common;

import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.DataHolder;

import static net.treasure.common.Keys.NAMESPACE;

public class Permissions implements DataHolder {

    public static String BASE,
            ADMIN,
            CHANGELOG,
            NOTIFICATION,
            CAN_SEE_EFFECTS,
            DEBUG;

    @Override
    public boolean checkVersion() {
        return true;
    }

    @Override
    public boolean initialize() {
        var inst = TreasurePlugin.getInstance();
        var config = inst.getConfig();
        var replacements = inst.getCommandManager().getCommandReplacements();

        replacements.addReplacement("basecmd", BASE = config.getString("permissions.menu", NAMESPACE + ".menu"));
        replacements.addReplacement("admincmd", ADMIN = config.getString("permissions.admin", NAMESPACE + ".admin"));
        replacements.addReplacement("changelog", CHANGELOG = config.getString("permissions.changelog", NAMESPACE + ".changelog"));
        replacements.addReplacement("notification", NOTIFICATION = config.getString("permissions.notification", NAMESPACE + ".notification"));
        replacements.addReplacement("debug", DEBUG = config.getString("permissions.debug", NAMESPACE + ".debug"));

        CAN_SEE_EFFECTS = config.getString("permissions.can_see_effects", NAMESPACE + ".can_see_effects");
        return true;
    }

    @Override
    public void reload() {
        initialize();
    }
}