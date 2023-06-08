package sbt.cppa.locks;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class LockCLH {
    private final AtomicReference<QNode> tail;
    private final ThreadLocal<QNode> current;
    private final ThreadLocal<QNode> previous;

    public LockCLH() {
        tail = new AtomicReference<>(new QNode());
        current = ThreadLocal.withInitial(QNode::new);
        previous = ThreadLocal.withInitial(() -> null);
    }

    public void lock() {
        QNode cur = current.get();
        cur.locked.set(true);
        QNode prev = tail.getAndSet(cur);
        previous.set(prev);

        while (prev.locked.get()) {
        }
    }

    public void unlock() {
        QNode cur = current.get();
        cur.locked.set(false);
        current.set(previous.get());
    }

    private static class QNode {
        AtomicBoolean locked = new AtomicBoolean(false);
    }
}
