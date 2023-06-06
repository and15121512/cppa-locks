package sbt.cppa.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ThreadLocalRandom;

public class LockBackoff {
    AtomicBoolean state = new AtomicBoolean(false);
    final int MIN_DELAY = 10;
    final int MAX_DELAY = 100;

    void lock() {
        int delay = MIN_DELAY;
        while (true) {
            while (state.get()) { }
            if (!state.getAndSet(true)) {
                return;
            }
            try {
                int randomNum = ThreadLocalRandom.current().nextInt(1, MIN_DELAY);
                TimeUnit.MICROSECONDS.sleep(randomNum);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (delay < MAX_DELAY) {
                delay *= 2;
            }
        }
    }

    void unlock() {
        state.set(false);
    }
}
