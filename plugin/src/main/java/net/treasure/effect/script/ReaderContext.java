package net.treasure.effect.script;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.treasure.effect.Effect;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor
public class ReaderContext<S extends Script> {
    private final Effect effect;
    private final String type, line;
    protected final S script;
    private String key;
    private String value;
    private int start;
    private int end;
}