package net.treasure.effect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.treasure.effect.script.Script;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class TickHandler {
    final String key;
    final int times;
    List<Script> lines;

    public TickHandler clone() {
        List<Script> copy = new ArrayList<>();
        for (var line : lines)
            copy.add(line.cloneScript());
        return new TickHandler(key, times, copy);
    }
}