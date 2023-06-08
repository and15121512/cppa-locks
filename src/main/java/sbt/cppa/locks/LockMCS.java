package sbt.cppa.locks;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class LockMCS {
    AtomicReference<QNode> queue;
    ThreadLocal<QNode> node;

    public LockMCS() {
        queue = new AtomicReference<>(null);
        node = new ThreadLocal<>() {
            protected QNode initialValue() {
                return new QNode();
            }
        };
    }

    public void lock() {
        QNode lnode = node.get();
        QNode lqueue = queue.getAndSet(lnode);
        if (lqueue != null) {
            lnode.locked.set(true);
            lqueue.next.set(lnode);
            while(lnode.locked.get()) Thread.yield();
        }
    }

    public void unlock() {
        QNode lnode = node.get();
        if (lnode.next.get() == null) {
            if (queue.compareAndSet(lnode, null))
                return;
            while(lnode.next.get() == null) Thread.yield();
        }
        lnode.next.get().locked.set(false);
        lnode.next.set(null);
    }

    private static class QNode {
        AtomicBoolean locked = new AtomicBoolean(false);
        AtomicReference<QNode> next = new AtomicReference<>(null);
    }
}
