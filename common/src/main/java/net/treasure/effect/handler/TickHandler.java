package net.treasure.effect.handler;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.treasure.effect.mix.MixerOptions;
import net.treasure.effect.data.EffectData;
import net.treasure.effect.script.Script;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
public class TickHandler {
    // Generic
    public final String key;
    public final String displayName;
    public final int interval;
    public final int times;
    // Mixer Options
    public final MixerOptions mixerOptions;
    // Event Exclusive
    public final int maxExecuted;
    public final boolean resetEvent;
    public final HandlerEvent event;
    // Non-final
    public int executed;

    // Scripts
    public List<Script> lines;

    public TickHandler clone() {
        List<Script> copy = new ArrayList<>();
        for (var line : lines)
            copy.add(line.cloneScript());
        return new TickHandler(key, displayName, interval, times, mixerOptions, maxExecuted, resetEvent, event, 0, copy);
    }

    public boolean execute(EffectData data, HandlerEvent event) {
        if (maxExecuted <= 0) return this.event == null || this.event == event;

        if (executed >= maxExecuted) {
            if (this.event != event) {
                executed = 0;
                return false;
            }
            if (resetEvent)
                data.resetEvent();
            return false;
        }

        if (this.event != null && this.event != event) return false;
        executed++;
        return true;
    }
}