package net.treasure.gui.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.gui.type.GUI;

import java.util.Map;

@Getter
@AllArgsConstructor
public class GUIStyle {
    String id;
    Map<GUI, GUILayout> layouts;
}