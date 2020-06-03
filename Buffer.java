// Buffer.java

import java.util.concurrent.*;

/*
 Holds the transactions for the worker
 threads.
*/
public class Buffer {
	public static final int SIZE = 64;

	private BlockingQueue<Transaction> transactions;
	
	public Buffer() {
		transactions = new ArrayBlockingQueue<>(SIZE);
	}

	public Transaction take() {
		Transaction res = null;
		try {
			res = transactions.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return res;
	}

	public void put(Transaction transaction) {
		try {
			transactions.put(transaction);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// YOUR CODE HERE
}
