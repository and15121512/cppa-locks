package sbt.cppa.locks;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class WorkerCLH extends Thread {
    private final CyclicBarrier barrier;
    private final LockCLH lock;
    private final Counter locksItersCnt;

    public WorkerCLH(String name, CyclicBarrier barrier, LockCLH lock, Counter locksItersCnt) {
        this.barrier = barrier;
        this.setName(name);
        this.lock = lock;
        this.locksItersCnt = locksItersCnt;
    }

    @Override public void run() {
        System.out.printf("[ %s ] created, blocked by the barrier\n", getName());
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            lock.lock();
            ++locksItersCnt.counter;
            lock.unlock();
        }
    }
}
