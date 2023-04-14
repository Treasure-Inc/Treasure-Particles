package net.treasure.effect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.treasure.effect.script.Script;

import java.util.List;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class TickHandler {
    final String key;
    final int times;
    List<Script> lines;
}