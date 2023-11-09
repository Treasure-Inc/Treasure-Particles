package net.treasure.particles.permission;

import net.treasure.particles.TreasureParticles;
import net.treasure.particles.configuration.ConfigurationGenerator;
import net.treasure.particles.configuration.DataHolder;
import net.treasure.particles.constants.Keys;

public class Permissions implements DataHolder {

    public static String BASE,
            MIXER,
            ADMIN,
            CAN_SEE_EFFECTS,
            ACCESS_ALL_EFFECTS;

    public static final String COMMAND_BASE = "%base",
            COMMAND_MIXER = "%mixer",
            COMMAND_ADMIN = "%admin";

    public static boolean
            MIX_LIMIT_ENABLED = false,
            MIX_EFFECT_LIMIT_ENABLED = false,
            EFFECTS_VISIBILITY_PERMISSION = false,
            ALWAYS_CHECK_PERMISSION = true;

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public ConfigurationGenerator getGenerator() {
        return null;
    }

    @Override
    public boolean checkVersion() {
        return true;
    }

    @Override
    public boolean initialize() {
        var config = TreasureParticles.getConfig();
        var replacements = TreasureParticles.getCommandManager().getCommandReplacements();

        replacements.addReplacement(COMMAND_BASE, BASE = config.getString("permissions.base", Keys.NAMESPACE + ".base"));
        replacements.addReplacement(COMMAND_MIXER, MIXER = config.getString("permissions.mixer", Keys.NAMESPACE + ".mixer"));
        replacements.addReplacement(COMMAND_ADMIN, ADMIN = config.getString("permissions.admin", Keys.NAMESPACE + ".admin"));

        CAN_SEE_EFFECTS = config.getString("permissions.can_see_effects", Keys.NAMESPACE + ".can_see_effects");
        ACCESS_ALL_EFFECTS = config.getString("permissions.access_all_effects", Keys.NAMESPACE + ".access_all_effects");

        ALWAYS_CHECK_PERMISSION = config.getBoolean("always-check-effect-permission", ALWAYS_CHECK_PERMISSION);
        EFFECTS_VISIBILITY_PERMISSION = config.getBoolean("permissions.effects-visibility-permission", EFFECTS_VISIBILITY_PERMISSION);
        MIX_LIMIT_ENABLED = config.getBoolean("permissions.mix-limit-enabled", MIX_LIMIT_ENABLED);
        MIX_EFFECT_LIMIT_ENABLED = config.getBoolean("permissions.mix-effect-limit-enabled", MIX_EFFECT_LIMIT_ENABLED);
        return true;
    }

    @Override
    public void reload() {
        initialize();
    }

    public String replace(String key) {
        return key != null && key.startsWith("%") ? TreasureParticles.getConfig().getString("permissions." + key.substring(1), key) : key;
    }
}