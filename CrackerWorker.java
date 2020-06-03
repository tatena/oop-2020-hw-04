import javax.print.DocFlavor;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class CrackerWorker implements Runnable {
    Queue<String> processingWords;
    int maxLength;
    String target;
    CountDownLatch finishLatch;

    public CrackerWorker(int max, int start, int end, String targ, CountDownLatch finishLatch) {
        maxLength = max;
        target = targ;
        this.finishLatch = finishLatch;
        processingWords = new LinkedList<>();
        for (int i = start; i <= end; i++) {
            processingWords.offer("" + Cracker.CHARS[i]);
        }
    }

    @Override
    public void run() {
        while (processingWords.peek() != null) {
            String curr = processingWords.poll();
        //    System.out.println(curr);
            if (Cracker.generateMode(curr).equals(target)) {
                System.out.println(curr);
            }

            if (curr.length() < maxLength) {
                for (int i=0; i<Cracker.CHARS.length; i++) {
                    processingWords.offer(curr + Cracker.CHARS[i]);
                }
            }
        }
        finishLatch.countDown();
    }
}
