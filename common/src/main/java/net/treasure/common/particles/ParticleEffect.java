package net.treasure.common.particles;

import lombok.Getter;
import net.treasure.common.ReflectionUtils;
import org.bukkit.Particle;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.treasure.common.particles.ParticleEffect.Property.CAN_BE_COLORED;
import static net.treasure.common.particles.ParticleEffect.Property.DIRECTIONAL;
import static net.treasure.common.particles.ParticleEffect.Property.DUST;
import static net.treasure.common.particles.ParticleEffect.Property.REQUIRES_BLOCK;
import static net.treasure.common.particles.ParticleEffect.Property.REQUIRES_ITEM;
import static net.treasure.common.particles.ParticleEffect.Property.REQUIRES_WATER;
import static net.treasure.common.particles.ParticleEffect.Property.RESIZEABLE;

/**
 * @author <a href="https://github.com/ByteZ1337">ByteZ1337</a>
 */
@Getter
public enum ParticleEffect {
    /**
     * In vanilla, this particle is randomly displayed in the
     * basalt deltas nether biomes.
     * <p>
     * The movement of this particle is handled completely clientside
     * and can therefore not be influenced.
     * <p>
     * <b>Information</b>
     * <ul>
     * <li>Appearance: Gray/White square</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This Particle gets a random velocity while falling down.</li>
     * </ul>
     */
    ASH(version -> version < 16 ? "NONE" : "ash"),
    /**
     * <b>REPLACED BY {@link #BLOCK_MARKER} SINCE 1.18</b>
     * <p>
     * In vanilla, this particle is displayed by barrier blocks
     * when a player holds a barrier item in the main or off hand.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Red box with a slash through it.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    BARRIER(version -> version < 8 || version > 17 ? "NONE" : (version < 13 ? "BARRIER" : "barrier")),
    /**
     * In vanilla, this particle is displayed when a player breaks
     * a block or sprints. It's also displayed by iron golems while
     * walking.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Little piece of a texture.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle needs a block texture in order to work.</li>
     * </ul>
     */
    BLOCK_CRACK(version -> version < 8 ? "NONE" : (version < 13 ? "BLOCK_CRACK" : "block"), REQUIRES_BLOCK),
    /**
     * In vanilla, this particle is displayed when an entity hits the ground
     * after falling. It's also displayed when an armor-stand is broken.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Little piece of a texture.</li>
     * <li>Extra:<ul>
     * <li>  The velocity of this particle can be set. The amount has to be 0</li>
     * <li> This particle needs a block texture in order to work.</li></ul></li>
     * </ul>
     */
    BUBBLE_COLUMN_UP(version -> version < 13 ? "NONE" : "bubble_column_up", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed by barrier blocks when a player
     * holds a barrier item in the main- or off-hand or by the light block.
     * <p>
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Texture of the provided block.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle needs a block texture in order to work.</li>
     * </ul>
     */
    BLOCK_MARKER(version -> version < 18 ? "NONE" : "block_marker", REQUIRES_BLOCK),
    /**
     * In vanilla, this particle is displayed at the top of
     * bubble columns.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue circle.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    BUBBLE_POP(version -> version < 13 ? "NONE" : "bubble_pop", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed by campfires.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Smoke cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    CAMPFIRE_COSY_SMOKE(version -> version < 14 ? "NONE" : "campfire_cosy_smoke", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed by campfires with
     * a hay bale placed under them.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Smoke cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    CAMPFIRE_SIGNAL_SMOKE(version -> version < 14 ? "NONE" : "campfire_signal_smoke", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when an entity dies.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Large white cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    CLOUD(version -> version < 8 ? "NONE" : (version < 13 ? "CLOUD" : "cloud"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when a composter
     * is used by a player.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Green start</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    COMPOSTER(version -> version < 14 ? "NONE" : "composter"),
    /**
     * In vanilla, this particle is displayed in the crimson forest
     * nether biomes.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Pink square.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: This Particle gets a random velocity up.</li>
     * </ul>
     */
    CRIMSON_SPORE(version -> version < 16 ? "NONE" : "crimson_spore"),
    /**
     * In vanilla, this particle is displayed when a player lands
     * a critical hit on an entity or an  arrow is launched with full power.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Light brown cross.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    CRIT(version -> version < 8 ? "NONE" : (version < 13 ? "CRIT" : "crit"), DIRECTIONAL),
    /**
     * In vanilla, this particle  is displayed when a player hits
     * an entity with a sharpness sword.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Cyan star.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    CRIT_MAGIC(version -> version < 8 ? "NONE" : (version < 13 ? "CRIT_MAGIC" : "enchanted_hit"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed by magma blocks underwater.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Cyan star.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    CURRENT_DOWN(version -> version < 13 ? "NONE" : "current_down"),
    /**
     * In vanilla, this particle is displayed when a Player hits
     * an Entity by melee attack.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A dark red heart.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    DAMAGE_INDICATOR(version -> version < 9 ? "NONE" : (version < 13 ? "DAMAGE_INDICATOR" : "damage_indicator"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed as a trail of
     * dolphins.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A blue square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    DOLPHIN(version -> version < 13 ? "NONE" : "dolphin"),
    /**
     * In vanilla, this particle is displayed by the ender dragons
     * breath and ender fireballs.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A purple cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    DRAGON_BREATH(version -> version < 9 ? "NONE" : (version < 13 ? "DRAGON_BREATH" : "dragon_breath"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed randomly when a
     * lava block is above a block.
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Orange drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    DRIP_LAVA(version -> version < 8 ? "NONE" : (version < 13 ? "DRIP_LAVA" : "dripping_lava")),
    /**
     * In vanilla, this particle is displayed randomly when a
     * water block is above a block.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    DRIP_WATER(version -> version < 8 ? "NONE" : (version < 13 ? "DRIP_WATER" : "dripping_water")),
    /**
     * In vanilla, this particle is shown dripping from the
     * tip of pointed dripstones.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Orange drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    DRIPPING_DRIPSTONE_LAVA(version -> version < 17 ? "NONE" : "dripping_dripstone_lava"),
    /**
     * In vanilla, this particle is shown dripping from the
     * tip of pointed dripstones.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    DRIPPING_DRIPSTONE_WATER(version -> version < 17 ? "NONE" : "dripping_dripstone_water"),
    /**
     * In vanilla, this particle is displayed by beehives filled
     * with honey. As opposed to the {@link #FALLING_HONEY} particles,
     * this particle floats in the air before falling to the ground.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A rectangular honey drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: Spawns a {@link #LANDING_HONEY} particle after landing on a block.</li>
     * </ul>
     */
    DRIPPING_HONEY(version -> version < 15 ? "NONE" : "dripping_honey"),
    /**
     * In vanilla, this particle is displayed by crying obsidian.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A rectangular obsidian tear.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: Spawns a {@link #LANDING_OBSIDIAN_TEAR} particle after landing on a block.</li>
     * </ul>
     */
    DRIPPING_OBSIDIAN_TEAR(version -> version < 16 ? "NONE" : "dripping_obsidian_tear"),
    /**
     * In vanilla, this particle is displayed when a sculk sensor is triggered.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny colored cloud that changes color.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle supports 2 colors. It will display a fade animation between the two colors. It also
     * supports a custom size.
     * </ul>
     */
    DUST_COLOR_TRANSITION(version -> version < 17 ? "NONE" : "dust_color_transition", CAN_BE_COLORED, DUST),
    /**
     * In vanilla, this particle appears when a lightning bolt hits
     * copper blocks.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A small spark.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set.</li>
     * </ul>
     */
    ELECTRIC_SPARK(version -> version < 17 ? "NONE" : "electric_spark", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed by bookshelves near
     * an enchanting table.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A random letter from the galactic alphabet.</li>
     * <li>Speed value: Influences the spread of this particle effect.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    ENCHANTMENT_TABLE(version -> version < 8 ? "NONE" : (version < 13 ? "ENCHANTMENT_TABLE" : "enchant"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed by end rods and
     * shulker bullets.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White circle.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    END_ROD(version -> version < 9 ? "NONE" : (version < 13 ? "END_ROD" : "end_rod"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when tnt or creeper
     * explodes.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Gray ball which fades away after a few seconds.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    EXPLOSION_HUGE(version -> version < 8 ? "NONE" : (version < 13 ? "EXPLOSION_HUGE" : "explosion_emitter")),
    /**
     * In vanilla, this particle is displayed when a fireball
     * explodes or a wither skull hits a block/entity.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Gray ball which fades away after a few seconds.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    EXPLOSION_LARGE(version -> version < 8 ? "NONE" : (version < 13 ? "EXPLOSION_LARGE" : "explosion")),
    /**
     * In vanilla, this particle is displayed when either a creeper or
     * a tnt explodes.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White smoke.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    EXPLOSION_NORMAL(version -> version < 8 ? "NONE" : (version < 13 ? "EXPLOSION_NORMAL" : "poof"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed after {@link #DRIPPING_DRIPSTONE_LAVA}
     * starts falling from pointed dripstones.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Orange drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_DRIPSTONE_LAVA(version -> version < 17 ? "NONE" : "falling_dripstone_lava"),
    /**
     * In vanilla, this particle is displayed after {@link #DRIPPING_DRIPSTONE_WATER}
     * starts falling from pointed dripstones.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_DRIPSTONE_WATER(version -> version < 17 ? "NONE" : "falling_dripstone_water"),
    /**
     * In vanilla, this particle is displayed randomly by floating sand
     * and gravel.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: a circle part of a texture.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle needs a block texture in order to work.</li>
     * </ul>
     */
    FALLING_DUST(version -> version < 10 ? "NONE" : (version < 13 ? "FALLING_DUST" : "falling_dust"), REQUIRES_BLOCK),
    /**
     * In vanilla, this particle is displayed below beehives filled
     * with honey. As opposed to the {@link #DRIPPING_HONEY} particles,
     * this particle falls instantly.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A rectangular honey drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: Spawns a {@link #LANDING_HONEY} after landing on a block.</li>
     * </ul>
     */
    FALLING_HONEY(version -> version < 15 ? "NONE" : "falling_honey"),
    /**
     * In vanilla, this particle is displayed by bees that have pollen
     * and are on their way to the beehive.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_NECTAR(version -> version < 15 ? "NONE" : "falling_nectar"),
    /**
     * In vanilla, this particle is displayed below crying obsidian
     * blocks.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Purple square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_OBSIDIAN_TEAR(version -> version < 16 ? "NONE" : "falling_obsidian_tear"),
    /**
     * In vanilla, this particle is displayed below spore blossoms.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Green square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_SPORE_BLOSSOM(version -> version < 17 ? "NONE" : "falling_spore_blossom"),
    /**
     * In vanilla, this particle is displayed when a firework is
     * launched.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Sparkling white star.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    FIREWORKS_SPARK(version -> version < 8 ? "NONE" : (version < 13 ? "FIREWORKS_SPARK" : "firework"), DIRECTIONAL),
    /**
     * In vanilla, this particle is randomly displayed by torches,
     * active furnaces,spawners and magma cubes.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny flame.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    FLAME(version -> version < 8 ? "NONE" : (version < 13 ? "FLAME" : "flame"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed by exploding fireworks
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A white glow.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: The color of this flash can't be set since it's only set clientside.</li>
     * </ul>
     */
    FLASH(version -> version < 14 ? "NONE" : "flash"),
    /**
     * This particle is unused and is removed in the version 1.13.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Low opacity gray square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FOOTSTEP(version -> version > 8 && version < 13 ? "FOOTSTEP" : "NONE"),
    /**
     * In vanilla, this particle is displayed by a glow squid.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Cyan star.</li>
     * <li>Speed value: Doesn't seem to influence the particle.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0. Please note that this particle
     * is barely movable.</li>
     * </ul>
     */
    GLOW(version -> version < 17 ? "NONE" : "glow", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed by a glow squid when it gets hurt.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Cyan ink.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    GLOW_SQUID_INK(version -> version < 17 ? "NONE" : "glow_squid_ink", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when taming or
     * breeding animals.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Red heart.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    HEART(version -> version < 8 ? "NONE" : (version < 13 ? "HEART" : "heart")),
    /**
     * In vanilla, this particle is displayed when a tool is
     * broken, an egg or a splash potion hits an entity or a block, It is
     * also displayed when a player eats or an eye of ender breaks.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Little piece of a texture.</li>
     * <li>Extra:<ul>
     * <li> The velocity of this particle can be set. The amount has to be 0.</li>
     * <li> This particle needs a item texture in order to work.</li></ul></li>
     * </ul>
     */
    ITEM_CRACK(version -> version < 8 ? "NONE" : (version < 13 ? "ITEM_CRACK" : "item"), DIRECTIONAL, REQUIRES_ITEM),
    /**
     * In vanilla, this particle is displayed after a falling or
     * dripping Honey particle reaches a block.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Honey colored lines.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This Particle stays on the ground and doesn't instantly despawn.</li>
     * </ul>
     */
    LANDING_HONEY(version -> version < 15 ? "NONE" : "landing_honey"),
    /**
     * In vanilla, this particle is displayed after a falling or
     * dripping obsidian tear reaches a block.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Purple colored lines.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This Particle stays on the ground and doesn't instantly despawn.</li>
     * </ul>
     */
    LANDING_OBSIDIAN_TEAR(version -> version < 16 ? "NONE" : "landing_obsidian_tear"),
    /**
     * In vanilla, this particle is randomly displayed by lava.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Orange lava ball.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    LAVA(version -> version < 8 ? "NONE" : (version < 13 ? "LAVA" : "lava")),
    /**
     * <b>REPLACED BY {@link #BLOCK_MARKER} SINCE 1.18</b>
     * <p>
     * In vanilla, this particle is displayed by the light block.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: 1.17: four yellow stars. Since 1.18: A lightbulb</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    LIGHT(version -> version != 17 ? "NONE" : "light"),
    /**
     * In vanilla, this particle is displayed by elder guardians.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A elder guardian.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    MOB_APPEARANCE(version -> version < 8 ? "NONE" : (version < 13 ? "MOB_APPEARANCE" : "elder_guardian")),
    /**
     * In vanilla, this particle is displayed by active conduits.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue circle with a brown core.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    NAUTILUS(version -> version < 13 ? "NONE" : "nautilus", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when rightclicking
     * or activating a note block.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Colored Note.</li>
     * <li>Speed value: Causes the particle to be green when set to 0.</li>
     * <li>Extra: the offsetX parameter represents which note should be displayed. The amount has to be 0 or the color won't work.</li>
     * </ul>
     */
    NOTE(version -> version < 8 ? "NONE" : (version < 13 ? "NOTE" : "note"), CAN_BE_COLORED),
    /**
     * In vanilla, this particle is randomly displayed by nether
     * portal, endermen, ender chests, dragon eggs, endermites and end
     * gateway portals. It is also displayed when an ender pearl hits
     * a block or an entity, when an eye of ender beaks or when the player eats
     * a chorus fruit.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Purple cloud.</li>
     * <li>Speed value: Influences the spread of this particle effect.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    PORTAL(version -> version < 8 ? "NONE" : (version < 13 ? "PORTAL" : "portal"), DIRECTIONAL),
    /**
     * In vanilla, this particle is randomly displayed by active
     * redstone ore, active redstone, active redstone repeater and
     * active redstone torches. Since 1.13 it is also displayed when
     * pressing a button, activating a lever or stepping onto a pressure
     * plate
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny colored cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: offsetX, offsetY and offsetZ represent the rgb values of the particle. The amount has to be 0 or the color won't work.</li>
     * </ul>
     */
    REDSTONE(version -> version < 8 ? "NONE" : (version < 13 ? "REDSTONE" : "dust"), CAN_BE_COLORED, DUST),
    /**
     * Currently Unused in vanilla. It's pretty much the same as the normal portal
     * particle but instead of flying to the original location it flies away at the specified
     * velocity.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Purple Cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    REVERSE_PORTAL(version -> version < 16 ? "NONE" : "reverse_portal", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when oxidation is scraped off a copper block.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Cyan star.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SCRAPE(version -> version < 17 ? "NONE" : "scrape", DIRECTIONAL),
    /**
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue dust turning into a circle.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: You can change the roll of this particle.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SCULK_CHARGE(version -> version < 19 ? "NONE" : "sculk_charge", DIRECTIONAL),
    /**
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A blue circle popping.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SCULK_CHARGE_POP(version -> version < 19 ? "NONE" : "sculk_charge_pop", DIRECTIONAL),
    /**
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A blue soul.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SCULK_SOUL(version -> version < 19 ? "NONE" : "sculk_soul", DIRECTIONAL),
    /**
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A blue circle flying up.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: You can set the delay before the particle appears.</li>
     * </ul>
     */
    SHRIEK(version -> version < 19 ? "NONE" : "shriek"),
    /**
     * In vanilla, this particle is displayed by jumping slimes.
     * <p>
     * The particle originates from the nms EntitySlime class.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny part of the slimeball icon.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    SLIME(version -> version < 8 ? "NONE" : (version < 13 ? "SLIME" : "item_slime")),
    /**
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A small flame.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SMALL_FLAME(version -> version < 17 ? "NONE" : "small_flame", DIRECTIONAL),
    /**
     * In vanilla, this particle is randomly displayed by fire, furnace
     * minecarts and blazes. It's also displayed when trying to place water
     * in the nether.
     * <p>
     * The particle originates from the nms ItemBucket, EntityBlaze
     * BlockFluids and EntityMinecart classes.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Large gray cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    SMOKE_LARGE(version -> version < 8 ? "NONE" : (version < 13 ? "SMOKE_LARGE" : "large_smoke"), DIRECTIONAL),
    /**
     * In vanilla, this particle is randomly displayed by primed
     * tnt, torches, end portals, active brewing stands, monster
     * spawners or when either a dropper or dispenser gets triggered. It's
     * also displayed when taming a wild animal or an explosion occurs.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Little gray cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    SMOKE_NORMAL(version -> version < 8 ? "NONE" : (version < 13 ? "SMOKE_NORMAL" : "smoke"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed by sneezing baby pandas.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Green cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SNEEZE(version -> version < 14 ? "NONE" : "sneeze", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when a snowball
     * hits an entity or a block.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Little peace of the snowball texture.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    SNOWBALL(version -> version < 8 ? "NONE" : (version < 13 ? "SNOWBALL" : "item_snowball")),
    /**
     * In vanilla, this particle is displayed when a player sinks in powder snow.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A small white snowflake.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SNOWFLAKE(version -> version < 17 ? "NONE" : "snowflake", DIRECTIONAL),
    /**
     * This particle is unused and is merged into "poof" in 1.13.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny white cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SNOW_SHOVEL(version -> version < 8 ? "NONE" : (version < 13 ? "SNOW_SHOVEL" : "poof"), DIRECTIONAL),
    /**
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A blue explosion.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    SONIC_BOOM(version -> version < 19 ? "NONE" : "sonic_boom"),
    /**
     * In vanilla, this particle is displayed when a player walks
     * on soulsand with the soul speed enchantment.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A soul.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SOUL(version -> version < 16 ? "NONE" : "soul", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed by soul torches
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue flame.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SOUL_FIRE_FLAME(version -> version < 16 ? "NONE" : "soul_fire_flame", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when a splash potion or
     * an experience bottle hits a block or an entity. It's also displayed by
     * evokers.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White swirl.</li>
     * <li>Speed value: Causes the particle to only fly up when set to 0.</li>
     * <li>Extra: Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0</li>
     * </ul>
     */
    SPELL(version -> version < 8 ? "NONE" : (version < 13 ? "SPELL" : "effect")),
    /**
     * In vanilla, this particle is displayed when an instant splash
     * potion (e.g. instant health) hits a block or an entity.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White swirl.</li>
     * <li>Speed value: Causes the particle to only fly up when set to 0.</li>
     * <li>Extra: Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0</li>
     * </ul>
     */
    SPELL_INSTANT(version -> version < 8 ? "NONE" : (version < 13 ? "SPELL_INSTANT" : "instant_effect")),
    /**
     * In vanilla, this particle is displayed when an entity has
     * an active potion effect with the "ShowParticles" tag set to 1.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: colored swirl.</li>
     * <li>Speed value: Represents the lightness of the color.</li>
     * <li>Extra: offsetX, offsetY and offsetZ represent the rgb values of the particle. The amount has to be 0 or the color won't work.</li>
     * </ul>
     */
    SPELL_MOB(version -> version < 8 ? "NONE" : (version < 13 ? "SPELL_MOB" : "entity_effect"), CAN_BE_COLORED),
    /**
     * In vanilla, this particle is displayed when an entity has
     * an active potion effect from a nearby beacon.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: colored swirl.</li>
     * <li>Speed value: Represents the lightness of the color.</li>
     * <li>Extra: offsetX, offsetY and offsetZ represent the rgb values of the particle. The amount has to be 0 or the color won't work.</li>
     * </ul>
     */
    SPELL_MOB_AMBIENT(version -> version < 8 ? "NONE" : (version < 13 ? "SPELL_MOB_AMBIENT" : "ambient_entity_effect"), CAN_BE_COLORED),
    /**
     * In vanilla, this particle is displayed randomly by witches.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Purple cross.</li>
     * <li>Speed value: Causes the particle to only fly up when set to 0.</li>
     * <li>Extra: Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0</li>
     * </ul>
     */
    SPELL_WITCH(version -> version < 8 ? "NONE" : (version < 13 ? "SPELL_WITCH" : "witch")),
    /**
     * In vanilla, this particle is displayed by llamas while
     * attacking an entity.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SPIT(version -> version < 11 ? "NONE" : (version < 13 ? "SPIT" : "spit")),
    /**
     * In vanilla, this particle is emitted around spore blossoms.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Green square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    SPORE_BLOSSOM_AIR(version -> version < 17 ? "NONE" : "spore_blossom_air"),
    /**
     * In vanilla, this particle is displayed when a squid gets
     * damaged.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Black ink.</li>
     * <li>Speed value:Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SQUID_INK(version -> version < 13 ? "NONE" : "squid_ink", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed randomly in water.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny blue square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    SUSPENDED(version -> version < 8 ? "NONE" : (version < 13 ? "SUSPENDED" : "underwater"), REQUIRES_WATER),
    /**
     * In vanilla, this particle is displayed when a player is close
     * to bedrock or the void.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny gray square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    SUSPENDED_DEPTH(version -> version > 8 && version < 13 ? "SUSPENDED_DEPTH" : "NONE", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when a Player hits
     * multiple entities at once with a sword.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A white curve.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: The size of this particle can be set in the offsetX parameter. The amount has to be 0 and the speed has to be 1.</li>
     * </ul>
     */
    SWEEP_ATTACK(version -> version < 9 ? "NONE" : (version < 13 ? "SWEEP_ATTACK" : "sweep_attack"), RESIZEABLE),
    /**
     * In vanilla, this particle is displayed when a totem of
     * undying is used.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A green/yellow circle.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    TOTEM(version -> version < 11 ? "NONE" : (version < 13 ? "TOTEM" : "totem_of_undying"), DIRECTIONAL),
    /**
     * In vanilla, this particle is randomly displayed by mycelium
     * blocks.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny gray square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    TOWN_AURA(version -> version < 8 ? "NONE" : (version < 13 ? "TOWN_AURA" : "mycelium"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when attacking a village.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Gray cloud with a lightning.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    VILLAGER_ANGRY(version -> version < 8 ? "NONE" : (version < 13 ? "VILLAGER_ANGRY" : "angry_villager")),
    /**
     * In vanilla, this particle is displayed when trading with a
     * villager, using bone meal on crops, feeding baby animals or walking on
     * turtle eggs.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Green star.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    VILLAGER_HAPPY(version -> version < 8 ? "NONE" : (version < 13 ? "VILLAGER_HAPPY" : "happy_villager"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed in the warped forest
     * nether biome.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue square.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: This Particle gets a random velocity up.</li>
     * </ul>
     */
    WARPED_SPORE(version -> version < 16 ? "NONE" : "warped_spore"),
    /**
     * In vanilla, this particle is displayed when an Entity is
     * swimming in water, a projectile flies into the water or a fish
     * bites onto the bait.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Bubble with blue outline.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    WATER_BUBBLE(version -> version < 8 ? "NONE" : (version < 13 ? "WATER_BUBBLE" : "bubble"), DIRECTIONAL, REQUIRES_WATER),
    /**
     * In vanilla, this particle is displayed when rain hits the ground.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue droplet.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    WATER_DROP(version -> version > 8 && version < 13 ? "WATER_DROP" : "NONE"),
    /**
     * In vanilla, this particle is displayed when an Entity is
     * swimming in water, wolves shaking  off after swimming or boats.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue droplet.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    WATER_SPLASH(version -> version < 8 ? "NONE" : (version < 13 ? "WATER_SPLASH" : "splash"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when a fish bites
     * onto the bait of a fishing rod.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny blue square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    WATER_WAKE(version -> version < 8 ? "NONE" : (version < 13 ? "WATER_WAKE" : "fishing"), DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when wax is removed from a copper block.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White star.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    WAX_OFF(version -> version < 17 ? "NONE" : "wax_off", DIRECTIONAL),
    /**
     * In vanilla, this particle is displayed when wax is applied to a copper block.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Orange star.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    WAX_ON(version -> version < 17 ? "NONE" : "wax_on", DIRECTIONAL),
    /**
     * In vanilla, this particle is randomly displayed in the
     * basalt deltas nether biomes.
     * <p>
     * The movement of this particle is handled completely clientside
     * and can therefore not be influenced.
     * <p>
     * <b>Information</b>
     * <ul>
     * <li>Appearance: White square</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This Particle gets a random velocity in the -x and -z direction while falling down.</li>
     * </ul>
     */
    WHITE_ASH(version -> version < 16 ? "NONE" : "white_ash");

    public static final List<ParticleEffect> VALUES = List.of(values());
    public static final Map<String, ParticleEffect> MINECRAFT_KEYS;

    static {
        //noinspection ConstantConditions
        MINECRAFT_KEYS = Collections.unmodifiableMap(
                VALUES.stream()
                        .filter(effect -> !"NONE".equals(effect.getFieldName()))
                        .collect(Collectors.toMap(ParticleEffect::getFieldName, Function.identity()))
        );
    }

    final DoubleFunction<String> fieldNameMapper;
    final List<Property> properties;
    Particle particle;

    ParticleEffect(DoubleFunction<String> fieldNameMapper, Property... properties) {
        this.fieldNameMapper = fieldNameMapper;
        this.properties = List.of(properties);
    }

    public boolean hasProperty(Property property) {
        return property != null && properties.contains(property);
    }

    public String getFieldName() {
        return fieldNameMapper.apply(ReflectionUtils.MINECRAFT_VERSION);
    }

    public Particle bukkit() {
        if (this.particle != null) return particle;
        try {
            return particle = Particle.valueOf(this.name());
        } catch (Exception e) {
            return null;
        }
    }

    public enum Property {
        DIRECTIONAL,
        CAN_BE_COLORED,
        REQUIRES_BLOCK,
        REQUIRES_ITEM,
        REQUIRES_WATER,
        RESIZEABLE,
        DUST
    }
}