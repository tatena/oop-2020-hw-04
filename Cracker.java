// Cracker.java
/*
 Generates SHA hashes of short strings in parallel.
*/

import java.security.*;
import java.util.concurrent.CountDownLatch;

public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();

	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		if (args.length == 1) {
			String password = args[0];
			System.out.println(generateMode(password));
			return;
		}

		if (args.length < 2) {
			System.out.println("Args: target length [workers]");
			return;
		}

		String targ = args[0];
		int len = Integer.parseInt(args[1]);
		int num = 1;
		if (args.length > 2) {
			num = Integer.parseInt(args[2]);
		}

		crackerMode(targ, len, num);
		// a! 34800e15707fae815d7c90d49de44aca97e2d759
		// xyz 66b27417d37e024c46526c2f6d358a754fc552f3

	}

	private static void crackerMode(String targ, int len, int num) {
		CountDownLatch toWait = new CountDownLatch(num);
		int letterPerWorker = CHARS.length / num;

		for (int i = 0; i < CHARS.length; i += letterPerWorker ) {
			new Thread(new CrackerWorker(len, i, i + letterPerWorker - 1, targ, toWait)).start();
		}

		try {
			toWait.await();
			System.out.println("DONE");
		} catch (InterruptedException e) { }
	}

	public static String generateMode(String password) throws NoSuchAlgorithmException {
		MessageDigest hasher = MessageDigest.getInstance("SHA");
		hasher.update(password.getBytes());
		byte [] hashBytes = hasher.digest();
		String res = hexToString(hashBytes);
		return res;
	}
}
