package sbt.cppa.locks;

import java.util.concurrent.atomic.AtomicReference;

public class LockMCS {

    //AtomicReference<QNodeMCS> tail;
    //ThreadLocal<QNodeMCS> myNode;
//
    //public LockMCS() {
    //    tail = new AtomicReference<>(null);
    //    myNode = new ThreadLocal<QNodeMCS>() {
    //        protected QNodeMCS initialValue() {
    //            return new QNodeMCS();
    //        }
    //    };
    //}
//
    //public void lock() {
    //    QNodeMCS qnode = myNode.get();
    //    QNodeMCS pred = tail.getAndSet(qnode);
    //    if (pred != null) {
    //        qnode.locked.set(true);
    //        pred.next.set(qnode);
    //        while (qnode.locked.get()) { }
    //    }
    //}
//
    //public void unlock() {
    //    QNodeMCS qnode = myNode.get();
    //    if (qnode.next.get() == null) {
    //        if (tail.compareAndSet(qnode, null)) {
    //            return;
    //        }
    //        while (qnode.next.get() == null) { }
    //    }
    //    qnode.next.get().locked = false;
    //    qnode.next = null;
    //}
}
