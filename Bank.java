// Bank.java

/*
 Creates a bunch of accounts and uses threads
 to post transactions to the accounts concurrently.
*/

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Bank  {

	private final Transaction nullTrans = new Transaction(-1,0,0);
	public static final int NUM_ACCOUNTS = 20;	 // number of accounts
	public static final int INITIAL_BALANCE = 1000;	 // initial balance of accounts

	private HashMap<Integer, Account> accounts;
	private Buffer transactions;
	private CountDownLatch finishLatch;

	class Worker {
		public void start() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(true) {
						Transaction task = transactions.take();

						if(task.equals(nullTrans)) {
							finishLatch.countDown();
							break;
						}
						accounts.get(task.from).adjustAccount(-task.amount);
						accounts.get(task.to).adjustAccount(task.amount);
					}
				}
			}).start();
		}
	}


	public Bank(int numWorkers) {
		accounts = new HashMap();
		transactions = new Buffer();
		finishLatch = new CountDownLatch(numWorkers);
		for (int i = 0; i < NUM_ACCOUNTS; i++) {
			accounts.put(i, new Account(this, i, INITIAL_BALANCE));
		}
	}
	

	
	/*
	 Reads transaction data (from/to/amt) from a file for processing.
	 (provided code)
	 */
	public void readFile(String file) throws IOException {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			// Use stream tokenizer to get successive words from file
			StreamTokenizer tokenizer = new StreamTokenizer(reader);
			
			while (true) {
				int read = tokenizer.nextToken();
				if (read == StreamTokenizer.TT_EOF) break;  // detect EOF
				int from = (int)tokenizer.nval;
				
				tokenizer.nextToken();
				int to = (int)tokenizer.nval;

				tokenizer.nextToken();
				int amount = (int)tokenizer.nval;
				
				transactions.put(new Transaction(from, to, amount));
			}

	}


	/*
	 Processes one file of transaction data
	 -fork off workers
	 -read file into the buffer
	 -wait for the workers to finish
	*/
	public void processFile(String file, int numWorkers) throws IOException, InterruptedException {
		for (int i=0; i<numWorkers; i++) {
			Worker newThread = new Worker();
			newThread.start();
		}

		readFile(file);

		for (int i=0; i<numWorkers; i++) {
			transactions.put(nullTrans);
		}

		finishLatch.await();

		printResults();

	}

	private void printResults() {
		for (Account curr : accounts.values()) {
			System.out.println(curr.toString());
		}
	}


	/*
	 Looks at commandline args and calls Bank processing.
	*/
	public static void main(String[] args) throws IOException, InterruptedException {
		// deal with command-lines args
		if (args.length == 0) {
			System.out.println("Args: transaction-file [num-workers [limit]]");
			return;
		}
		
		String file = args[0];
		
		int numWorkers = 1;
		if (args.length >= 2) {
			numWorkers = Integer.parseInt(args[1]);
		}
		Bank bank = new Bank(numWorkers);
		bank.processFile(file, numWorkers);
	}

	public Map<Integer,Account> getBalances() {
		return accounts;
	}
}

