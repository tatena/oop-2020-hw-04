import javax.print.DocFlavor;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class CrackerWorker implements Runnable {
  //  Queue<String> processingWords;4
    int startIndex;
    int endIndex;
    int maxLength;
    String target;
    CountDownLatch finishLatch;

    public CrackerWorker(int max, int start, int end, String targ, CountDownLatch finishLatch) {
        maxLength = max;
        target = targ;
        this.finishLatch = finishLatch;
        startIndex = start;
        endIndex = end;
    }

    @Override
    public void run() {
        for (int i = startIndex; i <= endIndex; i++) {
            recSearch(Cracker.CHARS[i] + "");
        }
        finishLatch.countDown();
    }

    private void recSearch(String soFar) {
        if (soFar.length() > maxLength)
            return;
        try {
            if (Cracker.generateMode(soFar).equals(target)) {
                System.out.println(soFar);
            }
        } catch (NoSuchAlgorithmException e) { }

        for (int i = 0; i < Cracker.CHARS.length; i++) {
            recSearch(soFar + Cracker.CHARS[i]);
        }

    }
}
