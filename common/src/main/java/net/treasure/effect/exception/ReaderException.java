package net.treasure.effect.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReaderException extends Exception {
    public ReaderException(String message) {
        super(message);
    }
}