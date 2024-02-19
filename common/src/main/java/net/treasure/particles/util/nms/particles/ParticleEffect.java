package net.treasure.particles.util.nms.particles;

import lombok.Getter;
import net.treasure.particles.util.ReflectionUtils;
import org.bukkit.Particle;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.treasure.particles.util.nms.particles.ParticleEffect.Property.*;

@Getter
public enum ParticleEffect {
    /**
     * Emitted by entities with effects from a beacon or a conduit.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: colored swirl.</li>
     * <li>Speed value: Represents the lightness of the color.</li>
     * <li>Extra: offsetX, offsetY and offsetZ represent the rgb values of the particle. The amount has to be 0 or the color won't work.</li>
     * </ul>
     */
    AMBIENT_ENTITY_EFFECT(version -> "ambient_entity_effect", OFFSET_COLOR, CAN_BE_COLORED),
    /**
     * Produced when hitting villagers or when villagers fail to breed.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Gray cloud with a lightning.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    ANGRY_VILLAGER(version -> "angry_villager"),
    /**
     * Floats throughout the atmosphere in the soul sand valley biome.
     * <p>
     * The movement of this particle is handled completely clientside
     * and can therefore not be influenced.
     * <p>
     * <b>Information</b>
     * <ul>
     * <li>Appearance: Gray/White square</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle gets a random velocity while falling down.</li>
     * </ul>
     */
    ASH(version -> "ash"),
    /**
     * <b>REPLACED BY {@link #BLOCK_MARKER} SINCE 1.18</b>
     * <p>
     * Appears when a player holds a barrier item in the main or off hand.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Red box with a slash through it.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    BARRIER(version -> version < 8 || version > 17 ? "NONE" : (version < 13 ? "BARRIER" : "barrier")),
    /**
     * Produced when blocks are broken, flakes off blocks being brushed, produced when iron golems walk,
     * produced when entities fall a long distance, produced when players sprint,
     * displayed when armor stands are broken, appears when sheep eat grass.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Little piece of a texture.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle needs a block texture in order to work.</li>
     * </ul>
     */
    BLOCK(version -> "block", REQUIRES_BLOCK),
    /**
     * Marks the position of barriers and light blocks
     * when they are held in the main hand.
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
     * Appears around entities splashing in water, emitted by guardian lasers,
     * produced by guardians moving, appears by the fishing bobber and along the path of a fish,
     * trails behind projectiles and eyes of ender underwater.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Bubble with blue outline.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    BUBBLE(version -> "bubble", DIRECTIONAL, REQUIRES_WATER),
    /**
     * Represents upwards bubble columns.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Little piece of a texture.</li>
     * <li>Extra:<ul>
     * <li>  The velocity of this particle can be set. The amount has to be 0</li>
     * <li> This particle needs a block texture in order to work.</li></ul></li>
     * </ul>
     */
    BUBBLE_COLUMN_UP(version -> "bubble_column_up", DIRECTIONAL),
    /**
     * Unused
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue circle.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    BUBBLE_POP(version -> "bubble_pop", DIRECTIONAL),
    /**
     * Floats off the top of campfires and soul campfires.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Smoke cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    CAMPFIRE_COSY_SMOKE(version -> "campfire_cosy_smoke", DIRECTIONAL),
    /**
     * Floats off the top of campfires and soul campfires above hay bales.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Smoke cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    CAMPFIRE_SIGNAL_SMOKE(version -> "campfire_signal_smoke", DIRECTIONAL),
    /**
     * Falls off the bottom of cherry leaves.
     */
    CHERRY_LEAVES(version -> version < 20 ? "NONE" : "cherry_leaves"),
    /**
     * Appears when placing wet sponges in the Nether,
     * shown when entering a village with the Bad Omen effect.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Large white cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    CLOUD(version -> "cloud", DIRECTIONAL),
    /**
     * Produced when placing items in a composter.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Green start</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    COMPOSTER(version -> "composter"),
    /**
     * Floats throughout the atmosphere in the crimson forest biome.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Pink square.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: This particle gets a random velocity up.</li>
     * </ul>
     */
    CRIMSON_SPORE(version -> "crimson_spore"),
    /**
     * Trails behind crossbow shots and fully charged bow shots,
     * produced by evoker fangs, appears when landing a critical hit on an entity.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Light brown cross.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    CRIT(version -> "crit", DIRECTIONAL),
    /**
     * Represents downwards bubble columns.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Cyan star.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    CURRENT_DOWN(version -> "current_down"),
    /**
     * Appears when a melee attack damages an entity.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A dark red heart.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    DAMAGE_INDICATOR(version -> "damage_indicator", DIRECTIONAL),
    /**
     * Trails behind dolphins.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A blue square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    DOLPHIN(version -> "dolphin"),
    /**
     * Spit out by the ender dragon, trails behind dragon fireballs,
     * emitted by clouds of dragon's breath, produced when dragon fireballs explode.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A purple cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    DRAGON_BREATH(version -> "dragon_breath", DIRECTIONAL),
    /**
     * Represents lava drips collected on pointed dripstone with lava above that have not yet dripped down.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Orange drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    DRIPPING_DRIPSTONE_LAVA(version -> version < 17 ? "NONE" : "dripping_dripstone_lava"),
    /**
     * Represents water drips collected on pointed dripstone with water or nothing above that have not yet dripped down.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    DRIPPING_DRIPSTONE_WATER(version -> version < 17 ? "NONE" : "dripping_dripstone_water"),
    /**
     * Represents honey drips collected on the bottom of full bee nests or beehives that have not yet dripped down.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A rectangular honey drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: Spawns a {@link #LANDING_HONEY} particle after landing on a block.</li>
     * </ul>
     */
    DRIPPING_HONEY(version -> "dripping_honey"),
    /**
     * Represents lava drips collected on the bottom of blocks with lava above that have not yet dripped down.
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Orange drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    DRIPPING_LAVA(version -> "dripping_lava"),
    /**
     * Represents tears collected on the sides or bottom of crying obsidian that have not yet dripped down.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A rectangular obsidian tear.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: Spawns a {@link #LANDING_OBSIDIAN_TEAR} particle after landing on a block.</li>
     * </ul>
     */
    DRIPPING_OBSIDIAN_TEAR(version -> "dripping_obsidian_tear"),
    /**
     * Represents water drips collected on the bottom of leaves in rain and blocks with water above or the bottom and sides of wet sponges that have not yet dripped down.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    DRIPPING_WATER(version -> "dripping_water"),
    /**
     * Emitted by powered redstone torches, powered levers,
     * redstone ore, powered redstone dust, and powered redstone repeaters.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny colored cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: offsetX, offsetY and offsetZ represent the rgb values of the particle. The amount has to be 0 or the color won't work.</li>
     * </ul>
     */
    DUST(version -> "dust", CAN_BE_COLORED, Property.DUST),
    /**
     * Emitted by activated sculk sensors.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny colored cloud that changes color.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle supports 2 colors. It will display a fade animation between the two colors. It also
     * supports a custom size.
     * </ul>
     */
    DUST_COLOR_TRANSITION(version -> version < 17 ? "NONE" : "dust_color_transition", CAN_BE_COLORED, Property.DUST),
    /**
     * Shown when adding items to decorated pots.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A gray dust.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    DUST_PLUME(version -> version < 20 ? "NONE" : "dust_plume"),
    /**
     * Produced by splash potions.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White swirl.</li>
     * <li>Speed value: Causes the particle to only fly up when set to 0.</li>
     * <li>Extra: Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0</li>
     * </ul>
     */
    EFFECT(version -> "effect"),
    /**
     * Appears when sniffer eggs are placed on moss blocks,
     * appears when sniffer eggs crack.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A green star.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    EGG_CRACK(version -> version < 20 ? "NONE" : "egg_crack"),
    /**
     * Displayed when elder guardians inflict Mining Fatigue.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A elder guardian.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    ELDER_GUARDIAN(version -> "elder_guardian"),
    /**
     * Emitted by lightning rods during thunderstorms, produced when lightning hits copper.
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
     * Floats from bookshelves to enchanting tables.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A random letter from the galactic alphabet.</li>
     * <li>Speed value: Influences the spread of this particle effect.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    ENCHANT(version -> "enchant", DIRECTIONAL),
    /**
     * Appears when hitting entities with a sword or an axe
     * enchanted with Sharpness, Bane of Arthropods, or Smite.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Cyan star.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    ENCHANTED_HIT(version -> "enchanted_hit", DIRECTIONAL),
    /**
     * Emitted by end rods, trails behind shulker bullets.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White circle.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    END_ROD(version -> "end_rod", DIRECTIONAL),
    /**
     * Emitted by tipped arrows, produced by ravagers when stunned,
     * produced when lingering potions break open, emitted by area effect clouds,
     * produced when evokers cast spells, emitted by the wither as it charges up and when its health is below half,
     * produced by entities with effects from sources other than conduits or beacons.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: colored swirl.</li>
     * <li>Speed value: Represents the lightness of the color.</li>
     * <li>Extra: offsetX, offsetY and offsetZ represent the rgb values of the particle. The amount has to be 0 or the color won't work.</li>
     * </ul>
     */
    ENTITY_EFFECT(version -> "entity_effect", OFFSET_COLOR, CAN_BE_COLORED),
    /**
     * Produced by explosion_emitter particles, shown when shearing mushrooms,
     * appears when shulker bullets hit the ground, emitted by the ender dragon as it dies,
     * shown when the ender dragon breaks blocks.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Gray ball which fades away after a few seconds.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    EXPLOSION(version -> "explosion"),
    /**
     * Produced by explosions.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Gray ball which fades away after a few seconds.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    EXPLOSION_EMITTER(version -> "explosion_emitter"),
    /**
     * Drips off pointed dripstone with lava above.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Orange drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_DRIPSTONE_LAVA(version -> version < 17 ? "NONE" : "falling_dripstone_lava"),
    /**
     * Drips off pointed dripstone with nothing or water above.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_DRIPSTONE_WATER(version -> version < 17 ? "NONE" : "falling_dripstone_water"),
    /**
     * Falls off the bottom of floating blocks affected by gravity.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: a circle part of a texture.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle needs a block texture in order to work.</li>
     * </ul>
     */
    FALLING_DUST(version -> "falling_dust", REQUIRES_BLOCK),
    /**
     * Drips off beehives and bee nests that are full of honey.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A rectangular honey drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_HONEY(version -> "falling_honey"),
    /**
     * Drips off the bottom of blocks with lava above.
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A lava drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     */
    FALLING_LAVA(version -> "falling_lava"),
    /**
     * Falls off bees that have collected pollen.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_NECTAR(version -> "falling_nectar"),
    /**
     * Drips off crying obsidian.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Purple square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_OBSIDIAN_TEAR(version -> "falling_obsidian_tear"),
    /**
     * Drips off of spore blossoms.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Green square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_SPORE_BLOSSOM(version -> version < 17 ? "NONE" : "falling_spore_blossom"),
    /**
     * Drips off of the bottom of blocks with water above,
     * drips off the bottom of leaves during rain, drips off of wet sponges.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue drop.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FALLING_WATER(version -> version < 17 ? "NONE" : "falling_water"),
    /**
     * Trails behind fireworks, produced when fireworks crafted with firework stars explode.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Sparkling white star.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    FIREWORK(version -> "firework", DIRECTIONAL),
    /**
     * Represents the fish trail when fishing.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny blue square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    FISHING(version -> "fishing", DIRECTIONAL),
    /**
     * Appears inside of monster spawners, produced by magma cubes,
     * represents the flame of torches, emitted by furnaces.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny flame.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    FLAME(version -> "flame", DIRECTIONAL),
    /**
     * Shown when fireworks with crafted with firework stars explode.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A white glow.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: The color of this flash can't be set since it's only set clientside.</li>
     * </ul>
     */
    FLASH(version -> "flash"),
    /**
     * Emitted by glow squid.
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
     * Produced by glow squid when hit.
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
     * Created when a wind charge hits a block.
     */
    GUST(version -> version < 20 ? "NONE" : "gust"),
    /**
     * Unknown
     */
    GUST_DUST(version -> version < 20 ? "NONE" : "gust_dust"),
    /**
     * Created when a wind charge hits a block. Spawns a number of gust particles.
     */
    GUST_EMITTER(version -> version < 20 ? "NONE" : "gust_emitter"),
    /**
     * Shown when using bone meal on plants, appears when trading with villagers,
     * appears when feeding baby animals or dolphins, emitted by villagers upon claiming a job site block or a bed,
     * shown when bees pollinate crops, appears when turtle eggs are placed on sand, appears when turtle eggs hatch.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Green star.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    HAPPY_VILLAGER(version -> "happy_villager", DIRECTIONAL),
    /**
     * Appears when taming mobs, emitted by breeding mobs,
     * feeding mobs, appears when allays duplicate.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Red heart.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    HEART(version -> "heart"),
    /**
     * Produced when splash potions or lingering potions of Instant Health or Instant Damage break.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White swirl.</li>
     * <li>Speed value: Causes the particle to only fly up when set to 0.</li>
     * <li>Extra: Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0</li>
     * </ul>
     */
    INSTANT_EFFECT(version -> "instant_effect"),
    /**
     * Produced when tools break, produced when eating food,
     * produced when splash potions or lingering potions break, shown when eyes of ender break.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Little piece of a texture.</li>
     * <li>Extra:<ul>
     * <li> The velocity of this particle can be set. The amount has to be 0.</li>
     * <li> This particle needs a item texture in order to work.</li></ul></li>
     * </ul>
     */
    ITEM(version -> "item", DIRECTIONAL, REQUIRES_ITEM),
    /**
     * Shown when slimes jump
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny part of the slimeball icon.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    ITEM_SLIME(version -> "item_slime"),
    /**
     * Produced when thrown snowballs break.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Little peace of the snowball texture.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    ITEM_SNOWBALL(version -> "item_snowball"),
    /**
     * Created when falling_honey particles hit the ground.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Honey colored lines.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle stays on the ground and doesn't instantly de-spawn.</li>
     * </ul>
     */
    LANDING_HONEY(version -> "landing_honey"),
    /**
     * Created when falling_lava or falling_dripstone_lava particles hit the ground.
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Lava colored lines.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle stays on the ground and doesn't instantly de-spawn.</li>
     * </ul>
     */
    LANDING_LAVA(version -> "landing_lava"),
    /**
     * Created when falling_obsidian_tear particles hit the ground.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Purple colored lines.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle stays on the ground and doesn't instantly de-spawn.</li>
     * </ul>
     */
    LANDING_OBSIDIAN_TEAR(version -> "landing_obsidian_tear"),
    /**
     * Floats off the top of fire, produced by blazes,
     * appears when trying to place water in the Nether,
     * appears when obsidian, stone, or cobblestone is created by lava and water.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Large gray cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    LARGE_SMOKE(version -> "large_smoke", DIRECTIONAL),
    /**
     * Produced by campfires, produced by lava.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Orange lava ball.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    LAVA(version -> "lava"),
    /**
     * <b>REPLACED BY {@link #BLOCK_MARKER} SINCE 1.18</b>
     * <p>
     * In vanilla, this particle is displayed by the light block.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: 1.17: four yellow stars. Since 1.18: A light-bulb</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    LIGHT(version -> version != 17 ? "NONE" : "light"),
    /**
     * Appears above mycelium, trails behind the wings of phantoms.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny gray square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    MYCELIUM(version -> "mycelium", DIRECTIONAL),
    /**
     * Appears and floats toward conduits, appears and floats towards mobs being attacked by a conduit.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue circle with a brown core.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    NAUTILUS(version -> "nautilus", DIRECTIONAL),
    /**
     * Produced by jukeboxes, produced by note blocks.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Colored Note.</li>
     * <li>Speed value: Causes the particle to be green when set to 0.</li>
     * <li>Extra: the offsetX parameter represents which note should be displayed. The amount has to be 0 or the color won't work.</li>
     * </ul>
     */
    NOTE(version -> "note", OFFSET_COLOR, CAN_BE_COLORED),
    /**
     * Appears when mobs die, shown when ravagers roar after being stunned,
     * produced when silverfish enter stone, appear around mobs spawned by spawners,
     * shown when zombies trample turtle eggs, created when fireworks crafted without stars expire.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White smoke.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    POOF(version -> "poof", DIRECTIONAL),
    /**
     * Trails behind eyes of ender, shown when eyes of ender break,
     * floats toward where ender pearls break, points toward where dragon eggs teleport,
     * floats toward where players teleport with chorus fruit, appears and floats toward nether portals,
     * appears and floats toward end gateway portals, appears and floats toward ender chests, emitted by endermen, appears and floats toward endermites.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Purple cloud.</li>
     * <li>Speed value: Influences the spread of this particle effect.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    PORTAL(version -> "portal", DIRECTIONAL),
    /**
     * Floats off the top of respawn anchors.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Purple Cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    REVERSE_PORTAL(version -> "reverse_portal", DIRECTIONAL),
    /**
     * Shown when scraping oxidization off copper.
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
     * Marks the path of a sculk charge.
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
     * Appears when a sculk charge ends.
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
     * Appears above sculk catalysts when activated.
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
     * Emitted by activated sculk shriekers.
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
     * Represents the flame of candles.
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
     * Floats off the top of monster spawners, represents the smoke from candles,
     * appears when tnt is primed, floats off the top of wither roses, floats off the top of brewing stands,
     * represents the smoke of torches and soul torches, trails behind ghast fireballs, emitted by withers,
     * trails behind wither skulls, produced when dispensers or droppers fire, trails behind blaze fireballs,
     * emitted by lava and campfires during rain, emitted by furnaces, emitted by blast furnaces, emitted by smokers,
     * produced when placing eyes of ender in an end portal frame, emitted by end portals, produced when redstone torches burn out,
     * floats off the top of food placed on a campfire, shown when campfires and soul campfires are extinguished,
     * shown when failing to tame a mob, trails behind lava particles.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Little gray cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    SMOKE(version -> "smoke", DIRECTIONAL),
    /**
     * Sneezed out by pandas.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Green cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SNEEZE(version -> "sneeze", DIRECTIONAL),
    /**
     * Created by entities in powder snow.
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
     * Produced by the warden during its sonic boom attack.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A blue explosion.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    SONIC_BOOM(version -> version < 19 ? "NONE" : "sonic_boom"),
    /**
     * Created by players with Soul Speed boots running on soul sand or soul soil.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A soul.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SOUL(version -> "soul", DIRECTIONAL),
    /**
     * Represents the flame of soul torches.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue flame.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SOUL_FIRE_FLAME(version -> "soul_fire_flame", DIRECTIONAL),
    /**
     * Spit out by llamas.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: White cloud.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SPIT(version -> "spit"),
    /**
     * Produced by entities splashing in water, produced by villagers sweating during a raid,
     * appears above the surface of the water when fishing, created when falling_water or falling_dripstone_water particles hit the ground,
     * shaken off by wolves after exiting water.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue droplet.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    SPLASH(version -> "splash", DIRECTIONAL),
    /**
     * Floats in the atmosphere around spore blossoms.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Green square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    SPORE_BLOSSOM_AIR(version -> version < 17 ? "NONE" : "spore_blossom_air"),
    /**
     * Produced by squid when hit.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Black ink.</li>
     * <li>Speed value:Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    SQUID_INK(version -> "squid_ink", DIRECTIONAL),
    /**
     * Appears when a sweeping attack is performed.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A white curve.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: The size of this particle can be set in the offsetX parameter. The amount has to be 0 and the speed has to be 1.</li>
     * </ul>
     */
    SWEEP_ATTACK(version -> "sweep_attack", RESIZEABLE),
    /**
     * Produced when a totem of undying is used.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: A green/yellow circle.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: The velocity of this particle can be set. The amount has to be 0.</li>
     * </ul>
     */
    TOTEM_OF_UNDYING(version -> "totem_of_undying", DIRECTIONAL),
    /**
     * Produced when a Trial Spawner is activated.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny gold line.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * </ul>
     */
    TRIAL_SPAWNER_DETECTION(version -> version < 20 ? "NONE" : "trial_spawner_detection"),
    /**
     * Floats in the atmosphere underwater.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Tiny blue square.</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * </ul>
     */
    UNDERWATER(version -> "underwater", REQUIRES_WATER),
    VIBRATION(version -> version < 19 ? "NONE" : "vibration"),
    /**
     * Floats in the atmosphere in warped forest biomes.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Blue square.</li>
     * <li>Speed value: Influences the velocity at which the particle flies off.</li>
     * <li>Extra: This particle gets a random velocity up.</li>
     * </ul>
     */
    WARPED_SPORE(version -> "warped_spore"),
    /**
     * Produced when scraping wax off copper.
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
     * Produced when using honeycomb on copper.
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
     * Floats in the atmosphere in basalt delta biomes.
     * <p>
     * The movement of this particle is handled completely clientside
     * and can therefore not be influenced.
     * <p>
     * <b>Information</b>
     * <ul>
     * <li>Appearance: White square</li>
     * <li>Speed value: Doesn't influence the particle.</li>
     * <li>Extra: This particle gets a random velocity in the -x and -z direction while falling down.</li>
     * </ul>
     */
    WHITE_ASH(version -> "white_ash"),
    /**
     * Unknown
     */
    WHITE_SMOKE(version -> version < 20 ? "NONE" : "white_smoke"),
    /**
     * Emitted by witches.
     * <p>
     * <b>Information</b>:
     * <ul>
     * <li>Appearance: Purple cross.</li>
     * <li>Speed value: Causes the particle to only fly up when set to 0.</li>
     * <li>Extra: Only the motion on the y-axis can be controlled, the motion on the x- and z-axis are multiplied by 0.1 when setting the values to 0</li>
     * </ul>
     */
    WITCH(version -> "witch");

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
        OFFSET_COLOR,
        REQUIRES_BLOCK,
        REQUIRES_ITEM,
        REQUIRES_WATER,
        RESIZEABLE,
        DUST
    }
}