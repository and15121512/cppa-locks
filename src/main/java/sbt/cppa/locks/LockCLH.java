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
        cur.locked = true;
        QNode prev = tail.getAndSet(cur);
        previous.set(prev);
        while (prev.locked) {
        }
    }

    public void unlock() {
        QNode cur = current.get();
        cur.locked = false;
        current.set(previous.get());
    }

    private static class QNode {
        volatile boolean locked = false;
    }
}
