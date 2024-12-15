package net.treasure.particles.effect.script.reader;

import lombok.AllArgsConstructor;
import net.treasure.particles.effect.script.Script;
import net.treasure.particles.util.unsafe.UnsafeConsumer;

@AllArgsConstructor
public class ValidArgument<C extends ReaderContext<T>, T extends Script> {
    protected String key;
    protected UnsafeConsumer<C> reader;
    protected boolean required;
}