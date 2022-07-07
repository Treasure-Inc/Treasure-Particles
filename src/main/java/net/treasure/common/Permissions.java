package net.treasure.common;

import net.treasure.core.TreasurePlugin;
import net.treasure.core.configuration.DataHolder;

public class Permissions implements DataHolder {

    public static String BASE,
            ADMIN,
            CHANGELOG,
            NOTIFICATION,
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

        replacements.addReplacement("basecmd", BASE = config.getString("permissions.menu", "trelytra.menu"));
        replacements.addReplacement("admincmd", ADMIN = config.getString("permissions.admin", "trelytra.admin"));
        replacements.addReplacement("changelog", CHANGELOG = config.getString("permissions.changelog", "trelytra.changelog"));
        replacements.addReplacement("notification", NOTIFICATION = config.getString("permissions.notification", "trelytra.notification"));
        replacements.addReplacement("debug", DEBUG = config.getString("permissions.debug", "trelytra.debug"));
        return true;
    }

    @Override
    public void reload() {
        initialize();
    }
}