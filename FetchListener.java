import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class FetchListener implements ActionListener {
    private WebFrame frame;
    private int numThreads;
    private  Semaphore numThreadsLock;
    private Vector<Thread> threads;
    private boolean isSingle;

    public FetchListener(WebFrame frame, Vector<Thread> threads, boolean isSingle) {
        this.frame = frame;
        numThreads = 1;
        this.threads = threads;
        this.isSingle = isSingle;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isSingle)
            numThreads = frame.getInputValue();
        numThreadsLock = new Semaphore(numThreads);
        frame.switchToRunningState();
        Thread launcher = new LauncherThread();
        launcher.start();
    }

    private class LauncherThread extends Thread {
        @Override
        public void run() {
            threads.add(currentThread());
            frame.increaseRunning();
            try {
                DefaultTableModel model = frame.getTableModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    numThreadsLock.acquire();

                    WebWorker worker = new WebWorker((String) model.getValueAt(i, 0), numThreadsLock, i, frame);
                    threads.add(worker);
                    worker.start();

                    if (isInterrupted()) {
                        frame.decreaseRunning();
                        return;
                    }
                }
                frame.decreaseRunning();
            } catch (InterruptedException e) {
                frame.decreaseRunning();
                return;
            }
        }
    }
}
