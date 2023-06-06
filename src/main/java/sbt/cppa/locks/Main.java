package sbt.cppa.locks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void warmUp(int createIters) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < createIters; ++i) {
            Counter cntr = new Counter();
            ++cntr.counter;

            LockTAS tas = new LockTAS();
            tas.lock(); tas.unlock();

            LockTTAS ttas = new LockTTAS();
            ttas.lock(); ttas.unlock();

            LockBackoff backoff = new LockBackoff();
            backoff.lock(); backoff.unlock();
        }
        long finish = System.currentTimeMillis();
        float timeElapsed = (float)(finish - start) / 1000;
        System.out.printf("WarmUp time: %f seconds\n", timeElapsed);
    }

    public static void main(String[] args) {
        final int warmUpIters = 100_000;
        //final int threadsCnt = 32;
        //final int testDurationSeconds = 10;

        Scanner in = new Scanner(System.in);
        System.out.println("Please, enter number of threads:");
        final int threadsCnt = in.nextInt();

        System.out.println("Please, enter test duration (seconds):");
        final int testDurationSeconds = in.nextInt();

        System.out.println("Please, enter lock type (TAS, TTAS, Backoff):");
        final String testLockType = in.next();

        System.out.printf(
                "Run test: threadsCnt=%d, testDurationSeconds=%d, testLockType=\"%s\"\n",
                threadsCnt,
                testDurationSeconds,
                testLockType);
        //warmUp(warmUpIters);
        if (testLockType.equals("TAS")) {
            testTAS(threadsCnt, testDurationSeconds);
        }
        else if (testLockType.equals("TTAS")) {
            testTTAS(threadsCnt, testDurationSeconds);
        }
        else if (testLockType.equals("Backoff")) {
            testBackoff(threadsCnt, testDurationSeconds);
        }
        else {
            System.out.printf("[ERROR] Unknown lock type: %s%n", testLockType);
            throw new RuntimeException(String.format("[ERROR] Unknown lock type: %s", testLockType));
        }
    }

    public static void testTAS(int threadsCnt, int testDurationSeconds) {
        CyclicBarrier barrier = new CyclicBarrier(threadsCnt+1);
        LockTAS lock = new LockTAS();
        ArrayList<WorkerTAS> workers = new ArrayList<>();

        Counter totalLocksCnt = new Counter();

        for (int i = 0; i < threadsCnt; ++i) {
            WorkerTAS worker = new WorkerTAS(String.valueOf(i), barrier, lock, totalLocksCnt);
            workers.add(worker);
            worker.start();
        }

        long start = System.currentTimeMillis();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        try {
            TimeUnit.SECONDS.sleep(testDurationSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        lock.lock();
        int resultLocksCnt = totalLocksCnt.counter;
        lock.unlock();

        long finish = System.currentTimeMillis();
        float timeElapsed = (float)(finish - start) / 1000;

        System.out.printf("%d Threads --> Duration: %f Locks called: %d Throughput: %f",
                threadsCnt,
                timeElapsed,
                resultLocksCnt,
                (float)resultLocksCnt / (float)testDurationSeconds
        );

        for (WorkerTAS worker : workers) {
            worker.stop();
        }
    }

    public static void testTTAS(int threadsCnt, int testDurationSeconds) {
        CyclicBarrier barrier = new CyclicBarrier(threadsCnt+1);
        LockTTAS lock = new LockTTAS();
        ArrayList<WorkerTTAS> workers = new ArrayList<>();

        Counter totalLocksCnt = new Counter();

        for (int i = 0; i < threadsCnt; ++i) {
            WorkerTTAS worker = new WorkerTTAS(String.valueOf(i), barrier, lock, totalLocksCnt);
            workers.add(worker);
            worker.start();
        }

        long start = System.currentTimeMillis();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        try {
            TimeUnit.SECONDS.sleep(testDurationSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        lock.lock();
        int resultLocksCnt = totalLocksCnt.counter;
        lock.unlock();

        long finish = System.currentTimeMillis();
        float timeElapsed = (float)(finish - start) / 1000;

        System.out.printf("%d Threads --> Duration: %f Locks called: %d Throughput: %f",
                threadsCnt,
                timeElapsed,
                resultLocksCnt,
                (float)resultLocksCnt / (float)testDurationSeconds
        );

        for (WorkerTTAS worker : workers) {
            worker.stop();
        }
    }

    public static void testBackoff(int threadsCnt, int testDurationSeconds) {
        CyclicBarrier barrier = new CyclicBarrier(threadsCnt+1);
        LockBackoff lock = new LockBackoff();
        ArrayList<WorkerBackoff> workers = new ArrayList<>();

        Counter totalLocksCnt = new Counter();

        for (int i = 0; i < threadsCnt; ++i) {
            WorkerBackoff worker = new WorkerBackoff(String.valueOf(i), barrier, lock, totalLocksCnt);
            workers.add(worker);
            worker.start();
        }

        long start = System.currentTimeMillis();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        try {
            TimeUnit.SECONDS.sleep(testDurationSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        lock.lock();
        int resultLocksCnt = totalLocksCnt.counter;
        lock.unlock();

        long finish = System.currentTimeMillis();
        float timeElapsed = (float)(finish - start) / 1000;

        System.out.printf("%d Threads --> Duration: %f Locks called: %d Throughput: %f",
                threadsCnt,
                timeElapsed,
                resultLocksCnt,
                (float)resultLocksCnt / (float)testDurationSeconds
        );

        for (WorkerBackoff worker : workers) {
            worker.stop();
        }
    }

    public static void testCLH(int threadsCnt, int testDurationSeconds) {
        CyclicBarrier barrier = new CyclicBarrier(threadsCnt+1);
        LockCLH lock = new LockCLH();
        ArrayList<WorkerCLH> workers = new ArrayList<>();

        Counter totalLocksCnt = new Counter();

        for (int i = 0; i < threadsCnt; ++i) {
            WorkerCLH worker = new WorkerCLH(String.valueOf(i), barrier, lock, totalLocksCnt);
            workers.add(worker);
            worker.start();
        }

        long start = System.currentTimeMillis();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        try {
            TimeUnit.SECONDS.sleep(testDurationSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        lock.lock();
        int resultLocksCnt = totalLocksCnt.counter;
        lock.unlock();

        long finish = System.currentTimeMillis();
        float timeElapsed = (float)(finish - start) / 1000;

        System.out.printf("%d Threads --> Duration: %f Locks called: %d Throughput: %f",
                threadsCnt,
                timeElapsed,
                resultLocksCnt,
                (float)resultLocksCnt / (float)testDurationSeconds
        );

        for (WorkerCLH worker : workers) {
            worker.stop();
        }
    }
}