package net.treasure.effect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.treasure.effect.script.Script;

import java.util.List;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class TickHandler {
    String name;
    int times;
    List<Script> lines;
}