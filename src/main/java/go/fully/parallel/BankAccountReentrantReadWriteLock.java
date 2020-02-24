package go.fully.parallel;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class BankAccountReentrantReadWriteLock {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private long balance;
    private boolean debug;

    public BankAccountReentrantReadWriteLock(long balance) {
        this(balance, false);
    }

    public BankAccountReentrantReadWriteLock(final long balance, final boolean debug) {
        this.balance = balance;
        this.debug = debug;
    }

    public void deposit(long amount) {
        lock.writeLock().lock();
        try {
            log("Depositing");
            balance += amount;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void withdraw(long amount) {
        lock.writeLock().lock();
        try {
            log("Withdrawing");
            balance -= amount;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public long getBalance() {
        lock.readLock().lock();
        try {
            logAndSleep("Get balance");
            return balance;
        } finally {
            lock.readLock().unlock();
            log.info("After unlocking get balance");
        }
    }

    private void logAndSleep(final String debugMsg) {
        log(debugMsg);
        log.info("Sleeping");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("After sleeping");
    }

    private void log(final String debugMsg) {
        if (debug) {
            log.info(debugMsg);
        }
    }
}
