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
            lnode.locked = true;
            lqueue.next = lnode;
            while(lnode.locked) Thread.yield();
        }
    }

    public void unlock() {
        QNode lnode = node.get();
        if (lnode.next == null) {
            if (queue.compareAndSet(lnode, null))
                return;
            while(lnode.next == null) Thread.yield();
        }
        lnode.next.locked = false;
        lnode.next = null;
    }

    private static class QNode {
        boolean locked = false;
        QNode   next   = null;
    }
}
