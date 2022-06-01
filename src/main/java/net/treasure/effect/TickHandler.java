package net.treasure.effect;

import lombok.AllArgsConstructor;
import net.treasure.effect.script.Script;

import java.util.List;

@AllArgsConstructor
public class TickHandler {

    int times;
    List<Script> lines;
}
