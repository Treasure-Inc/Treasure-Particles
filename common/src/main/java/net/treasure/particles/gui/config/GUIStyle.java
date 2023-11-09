package net.treasure.particles.gui.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.particles.gui.type.GUIType;

import java.util.Map;

@Getter
@AllArgsConstructor
public class GUIStyle {
    String id;
    Map<GUIType, GUILayout> layouts;
}