package net.treasure.particles.effect.handler;

import lombok.AllArgsConstructor;
import net.treasure.particles.effect.data.EffectData;
import net.treasure.particles.effect.mix.MixerOptions;
import net.treasure.particles.effect.script.Script;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class TickHandler {

    public final String key;
    public final String displayName;

    public final int interval;
    public final int times;

    public final MixerOptions mixerOptions;
    public final int maxExecuted;
    public final boolean resetEvent;
    public final HandlerEvent event;

    public int executed;

    public List<Script> lines;

    public TickHandler(String key, String displayName, int interval, int times, MixerOptions mixerOptions, int maxExecuted, boolean resetEvent, HandlerEvent event) {
        this.key = key;
        this.displayName = displayName;
        this.interval = interval;
        this.times = times;
        this.mixerOptions = mixerOptions;
        this.maxExecuted = maxExecuted;
        this.resetEvent = resetEvent;
        this.event = event;

        if (event != null && event.onlyStatic())
            mixerOptions.isPrivate = true;
    }

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