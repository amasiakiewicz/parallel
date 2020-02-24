package go.fully.parallel;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class LockTest {
	private ExecutorService pool = Executors.newFixedThreadPool(10);

	@Test
	void writeLockWaitForReadReentrantRW() throws InterruptedException {
		final BankAccountReentrantReadWriteLock reentrantRWLock = new BankAccountReentrantReadWriteLock(5, true);
		pool.submit(reentrantRWLock::getBalance);
		
		TimeUnit.SECONDS.sleep(1);
		
		log.info("Trying to withdraw");
		reentrantRWLock.withdraw(2);

		TimeUnit.SECONDS.sleep(10);
	}

	@Test
	void compareReentrantRWAndStampedLockPerfTest() {
		final BankAccountReentrantReadWriteLock reentrantRWLock = new BankAccountReentrantReadWriteLock(5);
		measureExecution(reentrantRWLock::getBalance, "Reentrant rw lock");

		final BankAccountStampedLock stampedLock = new BankAccountStampedLock(3);
		measureExecution(stampedLock::getBalanceOptimisticRead, "Stamped lock");

	}

	private void measureExecution(final Runnable runnable, final String lockType) {
		final long start = System.currentTimeMillis();
		for (int i = 0; i < 150000000; i++) {
			runnable.run();
		}
		final long stop = System.currentTimeMillis();
		log.info("{} {} ms", lockType, (stop - start));
	}

}
