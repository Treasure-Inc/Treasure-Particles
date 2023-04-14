package net.treasure.core.gui.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.treasure.core.gui.type.GUI;

import java.util.Map;

@Getter
@AllArgsConstructor
public class GUIStyle {
    String id;
    String title;
    int size;
    Map<GUI, String[]> layouts;
}