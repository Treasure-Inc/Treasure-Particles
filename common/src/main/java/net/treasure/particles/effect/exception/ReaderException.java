package net.treasure.particles.effect.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReaderException extends Exception {
    public ReaderException(String message) {
        super(message);
    }
}