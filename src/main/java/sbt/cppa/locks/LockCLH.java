package sbt.cppa.locks;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class LockCLH {
    private final ThreadLocal<Node> pred;
    private final ThreadLocal<Node> node;
    private final AtomicReference<Node> tail = new AtomicReference<Node>(new Node());

    public LockCLH() {
        this.node = new ThreadLocal<Node>() {
            protected Node initialValue() {
                return new Node();
            }
        };

        this.pred = new ThreadLocal<Node>() {
            protected Node initialValue() {
                return null;
            }
        };
    }

    public void lock() {
        final Node node = this.node.get();
        node.locked = true;
        Node pred = this.tail.getAndSet(node);
        this.pred.set(pred);
        while (pred.locked) {}
    }

    public void unlock() {
        final Node node = this.node.get();
        node.locked = false;
        this.node.set(this.pred.get());
    }

    private static class Node {
        private volatile boolean locked;
    }
}
