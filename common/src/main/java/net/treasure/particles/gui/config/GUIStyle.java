package net.treasure.particles.gui.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.treasure.particles.gui.type.GUIType;

import java.util.Map;

@Getter
@AllArgsConstructor
public class GUIStyle {
    private String id;
    private Map<GUIType, GUILayout> layouts;
}