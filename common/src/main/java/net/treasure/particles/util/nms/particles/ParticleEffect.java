package net.treasure.particles.util.nms.particles;

import lombok.Getter;
import net.treasure.particles.util.nms.ReflectionUtils;
import org.bukkit.Particle;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.treasure.particles.util.nms.particles.ParticleEffect.Property.CAN_BE_COLORED;
import static net.treasure.particles.util.nms.particles.ParticleEffect.Property.DIRECTIONAL;
import static net.treasure.particles.util.nms.particles.ParticleEffect.Property.DUST;
import static net.treasure.particles.util.nms.particles.ParticleEffect.Property.OFFSET_COLOR;
import static net.treasure.particles.util.nms.particles.ParticleEffect.Property.REQUIRES_BLOCK;
import static net.treasure.particles.util.nms.particles.ParticleEffect.Property.REQUIRES_ITEM;
import static net.treasure.particles.util.nms.particles.ParticleEffect.Property.REQUIRES_TARGET;
import static net.treasure.particles.util.nms.particles.ParticleEffect.Property.REQUIRES_WATER;
import static net.treasure.particles.util.nms.particles.ParticleEffect.Property.RESIZEABLE;

@Getter
public enum ParticleEffect {
    /**
     * <p>
     * Floats throughout the atmosphere in the soul sand valley biome.
     * <i>
     * The movement of this particle is handled completely clientside
     * and can therefore not be influenced.
     * </i>
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Gray/White square</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: This particle gets a random velocity while falling down</li>
     * </ul>
     */
    ASH(version -> "ash"),
    /**
     * <b>REPLACED BY {@link #BLOCK_MARKER} SINCE 1.18</b>
     * <p>Appears when a player holds a barrier item in the main or offhand.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Red box with a slash through it</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    BARRIER(version -> version < 8 || version > 17 ? "NONE" : (version < 13 ? "BARRIER" : "barrier")),
    /**
     * <p>
     * Produced when blocks are broken, flakes off blocks being brushed, produced when iron golem walk,
     * produced when entities fall a long distance, produced when players sprint,
     * displayed when armor stands are broken, appears when sheep eat grass.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Little piece of a texture</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: This particle needs a block texture in order to work</li>
     * </ul>
     */
    BLOCK_CRACK(version -> "block", REQUIRES_BLOCK),
    /**
     * <p>Unknown.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: ???</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: This particle needs a block texture in order to work</li>
     * </ul>
     */
    BLOCK_CRUMBLE(version -> version < 21.2 ? "NONE" : "block_crumble", REQUIRES_BLOCK),
    /**
     * <p>
     * Marks the position of barriers and light blocks
     * when they are held in the main hand.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Texture of the provided block</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: This particle needs a block texture in order to work</li>
     * </ul>
     */
    BLOCK_MARKER(version -> version < 18 ? "NONE" : "block_marker", REQUIRES_BLOCK),
    /**
     * <p>
     * Appears around entities splashing in water, emitted by guardian lasers,
     * produced by guardians moving, appears by the fishing bobber and along the path of a fish,
     * trails behind projectiles and eyes of ender underwater.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Bubble with blue outline</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    BUBBLE(version -> "bubble", DIRECTIONAL, REQUIRES_WATER),
    /**
     * <p>Represents upwards bubble columns.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Little piece of a texture</li>
     *     <li>Extra:
     *     <ul>
     *         <li>The velocity of this particle can be set, the amount has to be 0</li>
     *         <li>This particle needs a block texture in order to work</li>
     *     </ul>
     *     </li>
     * </ul>
     */
    BUBBLE_COLUMN_UP(version -> "bubble_column_up", DIRECTIONAL),
    /**
     * <p>Unused.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue circle</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    BUBBLE_POP(version -> "bubble_pop", DIRECTIONAL),
    /**
     * <p>Floats off the top of campfires and soul campfires.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Smoke cloud</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    CAMPFIRE_COSY_SMOKE(version -> "campfire_cosy_smoke", DIRECTIONAL),
    /**
     * <p>
     * Floats off the top of campfires and
     * soul campfires above hay bales.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Smoke cloud</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    CAMPFIRE_SIGNAL_SMOKE(version -> "campfire_signal_smoke", DIRECTIONAL),
    /**
     * <p>Falls off the bottom of cherry leaves.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Pink leaves</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    CHERRY_LEAVES(version -> version < 20 ? "NONE" : "cherry_leaves"),
    /**
     * <p>
     * Appears when placing wet sponges in the Nether,
     * shown when entering a village with the Bad Omen effect.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Large white cloud</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    CLOUD(version -> "cloud", DIRECTIONAL),
    /**
     * <p>Produced when placing items in a composter.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Green star</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    COMPOSTER(version -> "composter"),
    /**
     * <p>Floats throughout the atmosphere in the crimson forest biome.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Pink square</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: This particle gets a random velocity up</li>
     * </ul>
     */
    CRIMSON_SPORE(version -> "crimson_spore"),
    /**
     * <p>
     * Trails behind crossbow shots and fully charged bow shots,
     * produced by evoker fangs, appears when landing a critical hit on an entity.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Light brown cross</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     * </ul>
     */
    CRIT(version -> "crit", DIRECTIONAL),
    /**
     * <p>
     * Appears when hitting entities with a sword or an axe
     * enchanted with Sharpness, Bane of Arthropods, or Smite.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Cyan star</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     * </ul>
     */
    CRIT_MAGIC(version -> "enchanted_hit", DIRECTIONAL),
    /**
     * <p>Represents downwards bubble columns.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Cyan star</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    CURRENT_DOWN(version -> "current_down"),
    /**
     * <p>Appears when a melee attack damages an entity.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Dark red heart</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    DAMAGE_INDICATOR(version -> "damage_indicator", DIRECTIONAL),
    /**
     * <p>Trails behind dolphins.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue square</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    DOLPHIN(version -> "dolphin"),
    /**
     * <p>
     * Spit out by the ender dragon, trails behind dragon fireballs,
     * emitted by clouds of dragon's breath, produced when dragon fireballs explode.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Purple cloud</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    DRAGON_BREATH(version -> "dragon_breath", DIRECTIONAL),
    /**
     * <p>
     * Represents lava drips collected on pointed dripstone
     * with lava above that have not yet dripped down.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Orange drop</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    DRIPPING_DRIPSTONE_LAVA(version -> version < 17 ? "NONE" : "dripping_dripstone_lava"),
    /**
     * <p>
     * Represents water drips collected on pointed dripstone
     * with water or nothing above that have not yet dripped down.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue drop</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    DRIPPING_DRIPSTONE_WATER(version -> version < 17 ? "NONE" : "dripping_dripstone_water"),
    /**
     * <p>
     * Represents honey drips collected on the bottom of full
     * bee nests or beehives that have not yet dripped down.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Rectangular honey drop</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: Spawns a {@link #LANDING_HONEY} particle after landing on a block</li>
     * </ul>
     */
    DRIPPING_HONEY(version -> "dripping_honey"),
    /**
     * <p>
     * Represents tears collected on the sides or bottom of
     * crying obsidian that have not yet dripped down.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Rectangular obsidian tear</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: Spawns a {@link #LANDING_OBSIDIAN_TEAR} particle after landing on a block</li>
     * </ul>
     */
    DRIPPING_OBSIDIAN_TEAR(version -> "dripping_obsidian_tear"),
    /**
     * <p>
     * Represents lava drips collected on the bottom of blocks
     * with lava above that have not yet dripped down.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Orange drop</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    DRIP_LAVA(version -> "dripping_lava"),
    /**
     * <p>
     * Represents water drips collected on the bottom of leaves in rain and blocks
     * with water above or the bottom and sides of wet sponges that have not yet dripped down.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue drop</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    DRIP_WATER(version -> "dripping_water"),
    /**
     * <p>Emitted by activated sculk sensors.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Tiny colored cloud that changes color</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: This particle supports custom size with 2 colors, it will display a fade animation between the two colors</li>
     * </ul>
     */
    DUST_COLOR_TRANSITION(version -> version < 17 ? "NONE" : "dust_color_transition", CAN_BE_COLORED, DUST),
    /**
     * <p>Produced by mace smash attacks.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Little piece of a texture</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: This particle needs a block texture in order to work</li>
     * </ul>
     */
    DUST_PILLAR(version -> version < 20.4 ? "NONE" : "dust_pillar", REQUIRES_BLOCK),
    /**
     * <p>Shown when adding items to decorated pots.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Gray dust</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     * </ul>
     */
    DUST_PLUME(version -> version < 20 ? "NONE" : "dust_plume"),
    /**
     * <p>
     * Appears when sniffer eggs are placed on moss blocks,
     * appears when sniffer eggs crack.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Green star</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    EGG_CRACK(version -> version < 20 ? "NONE" : "egg_crack"),
    /**
     * <p>Emitted by lightning rods during thunderstorms, produced when lightning hits copper.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Small spark</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set</li>
     * </ul>
     */
    ELECTRIC_SPARK(version -> version < 17 ? "NONE" : "electric_spark", DIRECTIONAL),
    /**
     * <p>Floats from bookshelves to enchanting tables.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Random letter from the galactic alphabet</li>
     *     <li>Speed Value: Influences the spread of this particle effect</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    ENCHANTMENT_TABLE(version -> "enchant", DIRECTIONAL),
    /**
     * <p>Emitted by end rods, trails behind shulker bullets.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: White circle</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    END_ROD(version -> "end_rod", DIRECTIONAL),
    /**
     * <p>Produced by explosions.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Gray ball which fades away after a few seconds</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    EXPLOSION_HUGE(version -> "explosion_emitter"),
    /**
     * <p>
     * Produced by explosion_emitter particles, shown when shearing mushrooms,
     * appears when shulker bullets hit the ground, emitted by the ender dragon as it dies,
     * shown when the ender dragon breaks blocks.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Gray ball which fades away after a few seconds</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    EXPLOSION_LARGE(version -> "explosion"),
    /**
     * <p>
     * Appears when mobs die, shown when ravagers roar after being stunned,
     * produced when silverfish enter stone, appear around mobs spawned by spawners,
     * shown when zombies trample turtle eggs, created when fireworks crafted without stars expire.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: White smoke</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     * </ul>
     */
    EXPLOSION_NORMAL(version -> "poof", DIRECTIONAL),
    /**
     * <p>Drips off pointed dripstone with lava above.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Orange drop</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    FALLING_DRIPSTONE_LAVA(version -> version < 17 ? "NONE" : "falling_dripstone_lava"),
    /**
     * <p>Drips off pointed dripstone with nothing or water above.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue drop</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    FALLING_DRIPSTONE_WATER(version -> version < 17 ? "NONE" : "falling_dripstone_water"),
    /**
     * <p>Falls off the bottom of floating blocks affected by gravity.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: a circle part of a texture</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: This particle needs a block texture in order to work</li>
     * </ul>
     */
    FALLING_DUST(version -> "falling_dust", REQUIRES_BLOCK),
    /**
     * <p>Drips off beehives and bee nests that are full of honey.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Rectangular honey drop</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    FALLING_HONEY(version -> "falling_honey"),
    /**
     * <p>Drips off the bottom of blocks with lava above.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Lava drop</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    FALLING_LAVA(version -> "falling_lava"),
    /**
     * <p>Falls off bees that have collected pollen.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: White square</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    FALLING_NECTAR(version -> "falling_nectar"),
    /**
     * <p>Drips off crying obsidian.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Purple square</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    FALLING_OBSIDIAN_TEAR(version -> "falling_obsidian_tear"),
    /**
     * <p>Drips off of spore blossoms.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Green square</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    FALLING_SPORE_BLOSSOM(version -> version < 17 ? "NONE" : "falling_spore_blossom"),
    /**
     * <p>
     * Drips off of the bottom of blocks with water above,
     * drips off the bottom of leaves during rain, drips off of wet sponges.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue drop</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    FALLING_WATER(version -> version < 17 ? "NONE" : "falling_water"),
    /**
     * <p>
     * Trails behind fireworks, produced when fireworks
     * crafted with firework stars explode.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Sparkling white star</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     * </ul>
     */
    FIREWORKS_SPARK(version -> "firework", DIRECTIONAL),
    /**
     * <p>
     * Appears inside of monster spawners, produced by magma cubes,
     * represents the flame of torches, emitted by furnaces.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Tiny flame</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    FLAME(version -> "flame", DIRECTIONAL),
    /**
     * <p>Shown when fireworks with crafted with firework stars explode.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: White glow</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: The color of this flash can't be set since it's only set clientside</li>
     * </ul>
     */
    FLASH(version -> "flash"),
    /**
     * <p>Emitted by glow squid.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Cyan star</li>
     *     <li>Speed Value: Doesn't seem to influence the particle</li>
     *     <li>Extra:
     *     <ul>
     *         <li>The velocity of this particle can be set, the amount has to be 0</li>
     *         <li>Please note that this particle is barely movable</li>
     *     </ul>
     *     </li>
     * </ul>
     */
    GLOW(version -> version < 17 ? "NONE" : "glow", DIRECTIONAL),
    /**
     * <p>Produced by glow squid when hit.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Cyan ink</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    GLOW_SQUID_INK(version -> version < 17 ? "NONE" : "glow_squid_ink", DIRECTIONAL),
    /**
     * <p>Created when a wind charge hits a block.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: ???</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    GUST(version -> version < 20 ? "NONE" : "gust"),
    /**
     * Unknown
     */
    GUST_DUST(version -> version < 20 ? "NONE" : "gust_dust"),
    /**
     * <p>Created when a wind charge hits a block.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Number of gust particles</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    GUST_EMITTER(version -> version < 20 ? "NONE" : "gust_emitter"),
    /**
     * <p>
     * Appears when taming mobs, emitted by breeding mobs,
     * feeding mobs, appears when allays duplicate.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Red heart</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    HEART(version -> "heart"),
    /**
     * <p>Produced by entities with the Infested effect.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Tiny silverfish</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    INFESTED(version -> version < 20.5 ? "NONE" : "infested"),
    /**
     * <p>Produced by entities with the Weaving effect.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Tiny cobweb texture</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    ITEM_COBWEB(version -> version < 20.5 ? "NONE" : "item_cobweb"),
    /**
     * <p>
     * Produced when tools break, produced when eating food,
     * produced when splash potions or lingering potions break,
     * shown when eyes of ender break.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Little piece of a texture</li>
     *     <li>Extra:
     *     <ul>
     *         <li>The velocity of this particle can be set, the amount has to be 0</li>
     *         <li>This particle needs an item texture in order to work</li>
     *     </ul>
     *     </li>
     * </ul>
     */
    ITEM_CRACK(version -> "item", DIRECTIONAL, REQUIRES_ITEM),
    /**
     * <p>Created when {@link #FALLING_HONEY} particles hit the ground.</p>
     * <p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Honey colored lines</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: This particle stays on the ground and doesn't instantly de-spawn</li>
     * </ul>
     */
    LANDING_HONEY(version -> "landing_honey"),
    /**
     * <p>Created when {@link #FALLING_LAVA} or {@link #FALLING_DRIPSTONE_LAVA} particles hit the ground.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Lava colored lines</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: This particle stays on the ground and doesn't instantly de-spawn</li>
     * </ul>
     */
    LANDING_LAVA(version -> "landing_lava"),
    /**
     * <p>Created when {@link #FALLING_OBSIDIAN_TEAR} particles hit the ground.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Purple colored lines</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: This particle stays on the ground and doesn't instantly de-spawn</li>
     * </ul>
     */
    LANDING_OBSIDIAN_TEAR(version -> "landing_obsidian_tear"),
    /**
     * <p>Produced by campfires, produced by lava.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Orange lava ball</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    LAVA(version -> "lava"),
    /**
     * <b>REPLACED BY {@link #BLOCK_MARKER} SINCE 1.18</b>
     * <p>In vanilla, this particle is displayed by the light block.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance:
     *     <ul>
     *         <li>1.17: Four yellow stars</li>
     *         <li>Since 1.18: Light-bulb</li>
     *     </ul>
     *     </li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    LIGHT(version -> version != 17 ? "NONE" : "light"),
    /**
     * <p>Displayed when elder guardians inflict Mining Fatigue.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Elder guardian texture</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    MOB_APPEARANCE(version -> "elder_guardian"),
    /**
     * <p>Appears and floats toward conduits, appears and floats towards mobs being attacked by a conduit.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue circle with a brown core</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    NAUTILUS(version -> "nautilus", DIRECTIONAL),
    /**
     * <p>Produced by jukeboxes and note blocks.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Colored note</li>
     *     <li>Speed Value: Causes the particle to be green when set to 0</li>
     *     <li>Extra: The offsetX parameter represents which note should be displayed, the amount has to be 0 or the color won't work</li>
     * </ul>
     */
    NOTE(version -> "note", OFFSET_COLOR, CAN_BE_COLORED),
    /**
     * <p>
     * Appears when an ominous item spawner
     * spawns an item during an ominous event.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue square</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    OMINOUS_SPAWNING(version -> version < 20.5 ? "NONE" : "ominous_spawning"),
    /**
     * <p>
     * Trails behind eyes of ender, shown when eyes of ender break,
     * floats toward where ender pearls break, points toward where dragon eggs teleport,
     * floats toward where players teleport with chorus fruit, appears and floats toward nether portals,
     * appears and floats toward end gateway portals, appears and floats toward ender chests,
     * emitted by endermen, appears and floats toward endermites.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Purple cloud</li>
     *     <li>Speed Value: Influences the spread of this particle effect</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    PORTAL(version -> "portal", DIRECTIONAL),
    /**
     * <p>Produced by players and mobs with the Raid Omen effect.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Red skull</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    RAID_OMEN(version -> version < 20.5 ? "NONE" : "raid_omen"),
    /**
     * <p>
     * Emitted by powered redstone torches, powered levers,
     * redstone ore, powered redstone dust, and powered redstone repeaters.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Tiny colored cloud</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: offsetX, offsetY and offsetZ represent the rgb values of the particle, the amount has to be 0 or the color won't work</li>
     * </ul>
     */
    REDSTONE(version -> "dust", CAN_BE_COLORED, DUST),
    /**
     * <p>Floats off the top of respawn anchors.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Purple cloud</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    REVERSE_PORTAL(version -> "reverse_portal", DIRECTIONAL),
    /**
     * <p>Shown when scraping oxidization off copper.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Cyan star</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    SCRAPE(version -> version < 17 ? "NONE" : "scrape", DIRECTIONAL),
    /**
     * <p>Marks the path of a sculk charge.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue dust turning into a circle</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra:
     *     <ul>
     *         <li>You can change the roll of this particle</li>
     *         <li>The velocity of this particle can be set, the amount has to be 0</li>
     *     </ul>
     *     </li>
     * </ul>
     */
    SCULK_CHARGE(version -> version < 19 ? "NONE" : "sculk_charge", DIRECTIONAL),
    /**
     * <p>Appears when a sculk charge ends.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue circle popping</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    SCULK_CHARGE_POP(version -> version < 19 ? "NONE" : "sculk_charge_pop", DIRECTIONAL),
    /**
     * <p>Appears above sculk catalysts when activated.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue soul</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    SCULK_SOUL(version -> version < 19 ? "NONE" : "sculk_soul", DIRECTIONAL),
    /**
     * <p>Emitted by activated sculk shriekers.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue circle flying up</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: You can set the delay before the particle appears</li>
     * </ul>
     */
    SHRIEK(version -> version < 19 ? "NONE" : "shriek"),
    /**
     * <p>Shown when slimes jump.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Tiny part of the slimeball icon</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    SLIME(version -> "item_slime"),
    /**
     * <p>Represents the flame of candles.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Small flame</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    SMALL_FLAME(version -> version < 17 ? "NONE" : "small_flame", DIRECTIONAL),
    /**
     * <p>Produced by mobs with the Wind Charged effect.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Small gust texture</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    SMALL_GUST(version -> version < 20.5 ? "NONE" : "small_gust"),
    /**
     * <p>
     * Floats off the top of fire, produced by blazes,
     * appears when trying to place water in the Nether,
     * appears when obsidian, stone, or cobblestone is created by lava and water.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Large gray cloud</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     * </ul>
     */
    SMOKE_LARGE(version -> "large_smoke", DIRECTIONAL),
    /**
     * <p>
     * Floats off the top of monster spawners, represents the smoke from candles,
     * appears when tnt is primed, floats off the top of wither roses, floats off the top of brewing stands,
     * represents the smoke of torches and soul torches, trails behind ghast fireballs, emitted by withers,
     * trails behind wither skulls, produced when dispensers or droppers fire, trails behind blaze fireballs,
     * emitted by lava and campfires during rain, emitted by furnaces, emitted by blast furnaces, emitted by smokers,
     * produced when placing eyes of ender in an end portal frame, emitted by end portals, produced when redstone torches burn out,
     * floats off the top of food placed on a campfire, shown when campfires and soul campfires are extinguished,
     * shown when failing to tame a mob, trails behind lava particles.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Little gray cloud</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     * </ul>
     */
    SMOKE_NORMAL(version -> "smoke", DIRECTIONAL),
    /**
     * <p>Sneezed out by pandas.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Green cloud</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    SNEEZE(version -> "sneeze", DIRECTIONAL),
    /**
     * <p>Produced when thrown snowballs break.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Little peace of the snowball texture</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    SNOWBALL(version -> "item_snowball"),
    /**
     * <p>Created by entities in powder snow.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Small white snowflake</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    SNOWFLAKE(version -> version < 17 ? "NONE" : "snowflake", DIRECTIONAL),
    /**
     * <p>Produced by the warden during its sonic boom attack.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue explosion</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    SONIC_BOOM(version -> version < 19 ? "NONE" : "sonic_boom"),
    /**
     * <p>
     * Created by players with Soul Speed boots
     * running on soul sand or soul soil.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Brown soul</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    SOUL(version -> "soul", DIRECTIONAL),
    /**
     * <p>Represents the flame of soul torches.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue flame</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    SOUL_FIRE_FLAME(version -> "soul_fire_flame", DIRECTIONAL),
    /**
     * <p>Produced by splash potions.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: White swirl</li>
     *     <li>Speed Value: Causes the particle to only fly up when set to 0</li>
     *     <li>Extra: Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0</li>
     * </ul>
     */
    SPELL(version -> "effect"),
    /**
     * <p>
     * Produced when splash potions or lingering potions
     * of Instant Health or Instant Damage break.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: White swirl</li>
     *     <li>Speed Value: Causes the particle to only fly up when set to 0</li>
     *     <li>Extra: Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0</li>
     * </ul>
     */
    SPELL_INSTANT(version -> "instant_effect"),
    /**
     * <p>
     * Emitted by tipped arrows, produced by ravagers when stunned,
     * produced when lingering potions break open, emitted by area effect clouds,
     * produced when evokers cast spells, emitted by the wither as it charges up and when its health is below half,
     * produced by entities with effects from sources other than conduits or beacons.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Colored swirl</li>
     *     <li>Speed Value: Represents the lightness of the color</li>
     *     <li>Extra: offsetX, offsetY and offsetZ represent the RGB values of the particle, the amount has to be 0 or the color won't work</li>
     * </ul>
     */
    SPELL_MOB(version -> "entity_effect", OFFSET_COLOR, CAN_BE_COLORED),
    /**
     * <p>Emitted by entities with effects from a beacon or a conduit.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Colored swirl</li>
     *     <li>Speed Value: Represents the lightness of the color</li>
     *     <li>Extra: offsetX, offsetY and offsetZ represent the RGB values of the particle, the amount has to be 0 or the color won't work</li>
     * </ul>
     */
    SPELL_MOB_AMBIENT(version -> "ambient_entity_effect", OFFSET_COLOR, CAN_BE_COLORED),
    /**
     * <p>Emitted by witches.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Purple cross</li>
     *     <li>Speed Value: Causes the particle to only fly up when set to 0</li>
     *     <li>Extra: Only the motion on the y-axis can be controlled, the motion on the x-axis and z-axis are multiplied by 0.1 when setting the values to 0</li>
     * </ul>
     */
    SPELL_WITCH(version -> "witch"),
    /**
     * <p>Spit out by llamas.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: White cloud</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    SPIT(version -> "spit"),
    /**
     * <p>Floats in the atmosphere around spore blossoms.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Green square</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    SPORE_BLOSSOM_AIR(version -> version < 17 ? "NONE" : "spore_blossom_air"),
    /**
     * <p>Produced by squid when hit.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Black ink</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    SQUID_INK(version -> "squid_ink", DIRECTIONAL),
    /**
     * <p>Floats in the atmosphere underwater.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Tiny blue square</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    SUSPENDED(version -> "underwater", REQUIRES_WATER),
    /**
     * <p>Appears when a sweeping attack is performed.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: White curve</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: The size of this particle can be set in the offsetX parameter, the amount has to be 0 and the speed has to be 1</li>
     * </ul>
     */
    SWEEP_ATTACK(version -> "sweep_attack", RESIZEABLE),
    /**
     * <p>Produced when a totem of undying is used.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Green/yellow circle</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    TOTEM(version -> "totem_of_undying", DIRECTIONAL),
    /**
     * <p>Appears above mycelium, trails behind the wings of phantoms.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Tiny gray square</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    TOWN_AURA(version -> "mycelium", DIRECTIONAL),
    /**
     * <p>Unknown.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: ???</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    TRAIL(version -> version < 21.2 ? "NONE" : "trail", CAN_BE_COLORED, REQUIRES_TARGET),
    /**
     * <p>Produced by players and mobs with the Trial Omen effect.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue skull</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    TRIAL_OMEN(version -> version < 20.5 ? "NONE" : "trial_omen"),
    /**
     * <p>Produced when a Trial Spawner is activated.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Tiny gold line</li>
     *     <li>Speed Value: ???</li>
     * </ul>
     */
    TRIAL_SPAWNER_DETECTION(version -> version < 20 ? "NONE" : "trial_spawner_detection"),
    /**
     * <p>Produced when an Ominous Trial Spawner is activated.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue skull</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    TRIAL_SPAWNER_DETECTION_OMINOUS(version -> version < 20.5 ? "NONE" : "trial_spawner_detection_ominous"),
    /**
     * <p>Produced when a player is near a vault.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue square</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: ???</li>
     * </ul>
     */
    VAULT_CONNECTION(version -> version < 20.5 ? "NONE" : "vault_connection"),
    /**
     * <p>
     * Moves from sounds to the warden or a sculk sensor,
     * moves from note blocks to allays.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue signal</li>
     *     <li>Speed Value: ???</li>
     *     <li>Extra: This particle needs a target location and duration value in order to work</li>
     * </ul>
     */
    VIBRATION(version -> version < 19 ? "NONE" : "vibration", REQUIRES_TARGET),
    /**
     * <p>Produced when hitting villagers or when villagers fail to breed.</p>
     * <b>Information</b>
     * <ul>
     * <li>Appearance: Gray cloud with a lightning</li>
     * <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    VILLAGER_ANGRY(version -> "angry_villager"),
    /**
     * <p>
     * Shown when using bone meal on plants, appears when trading with villagers,
     * appears when feeding baby animals or dolphins, emitted by villagers upon claiming a job site block or a bed,
     * shown when bees pollinate crops, appears when turtle eggs are placed on sand, appears when turtle eggs hatch.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Green star</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    VILLAGER_HAPPY(version -> "happy_villager", DIRECTIONAL),
    /**
     * <p>Floats in the atmosphere in warped forest biomes.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue square</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: This particle gets a random velocity up</li>
     * </ul>
     */
    WARPED_SPORE(version -> "warped_spore"),
    /**
     * <p>
     * Produced by entities splashing in water, produced by villagers
     * sweating during a raid, appears above the surface of the water when fishing,
     * created when falling_water or falling_dripstone_water particles hit the ground,
     * shaken off by wolves after exiting water.
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Blue droplet</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     * </ul>
     */
    WATER_SPLASH(version -> "splash", DIRECTIONAL),
    /**
     * <p>Represents the fish trail when fishing.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Tiny blue square</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     * </ul>
     */
    WATER_WAKE(version -> "fishing", DIRECTIONAL),
    /**
     * <p>Produced when scraping wax off copper.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: White star</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    WAX_OFF(version -> version < 17 ? "NONE" : "wax_off", DIRECTIONAL),
    /**
     * <p>Produced when using honeycomb on copper.</p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: Orange star</li>
     *     <li>Speed Value: Influences the velocity at which the particle flies off</li>
     *     <li>Extra: The velocity of this particle can be set, the amount has to be 0</li>
     * </ul>
     */
    WAX_ON(version -> version < 17 ? "NONE" : "wax_on", DIRECTIONAL),
    /**
     * <p>
     * Floats in the atmosphere in basalt delta biomes.
     * <i>
     * The movement of this particle is handled completely clientside
     * and can therefore not be influenced.
     * </i>
     * </p>
     * <b>Information</b>
     * <ul>
     *     <li>Appearance: White square</li>
     *     <li>Speed Value: Doesn't influence the particle</li>
     *     <li>Extra: This particle gets a random velocity in the -x and -z direction while falling down</li>
     * </ul>
     */
    WHITE_ASH(version -> "white_ash"),
    /**
     * Unknown
     */
    WHITE_SMOKE(version -> version < 20 ? "NONE" : "white_smoke");

    public static final List<ParticleEffect> VALUES = List.of(values());
    public static final Map<String, ParticleEffect> MINECRAFT_KEYS;

    public static final Map<String, ParticleEffect> ALL_MINECRAFT_KEYS;

    static {
        //noinspection ConstantConditions
        MINECRAFT_KEYS = Collections.unmodifiableMap(
                VALUES.stream()
                        .filter(effect -> !"NONE".equals(effect.getFieldName()))
                        .collect(Collectors.toMap(ParticleEffect::getFieldName, Function.identity()))
        );

        ALL_MINECRAFT_KEYS = Collections.unmodifiableMap(
                VALUES.stream()
                        .filter(effect -> !"NONE".equals(effect.getLatestFieldName()))
                        .collect(Collectors.toMap(ParticleEffect::getLatestFieldName, Function.identity()))
        );
    }

    public static List<ParticleEffect> byProperty(Property property) {
        return VALUES.stream().filter(effect -> effect.hasProperty(property)).toList();
    }

    private final DoubleFunction<String> fieldNameMapper;
    private final List<Property> properties;
    private Particle particle;

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

    public String getLatestFieldName() {
        return fieldNameMapper.apply(ReflectionUtils.LATEST_SUPPORTED_VERSION);
    }

    public Particle bukkit() {
        if (this.particle != null) return particle;
        try {
            return particle = Particle.valueOf(ReflectionUtils.MINECRAFT_VERSION >= 20.5 ? getFieldName().toUpperCase(Locale.ENGLISH) : name());
        } catch (Exception e) {
            return null;
        }
    }

    public enum Property {
        CAN_BE_COLORED,
        DIRECTIONAL,
        DUST,
        OFFSET_COLOR,
        PARAM_COLOR,
        REQUIRES_BLOCK,
        REQUIRES_ITEM,
        REQUIRES_TARGET,
        REQUIRES_WATER,
        RESIZEABLE
    }
}