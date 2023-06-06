package sbt.cppa.locks;

import java.util.concurrent.atomic.AtomicBoolean;

public class LockTAS {
    AtomicBoolean state = new AtomicBoolean(false);

    void lock() {
        while (state.getAndSet(true)) { }
    }

    void unlock() {
        state.set(false);
    }
}
