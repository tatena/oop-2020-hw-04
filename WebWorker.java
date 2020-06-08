import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.Semaphore;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class WebWorker extends Thread {
    private String urlString;
    private WebFrame frame;
    private Semaphore numThreadsLock;
    private int rowIndex;
    private DefaultTableModel model;
    private Long timeElapsed;


    public WebWorker(String url, Semaphore numThreadsLock, int rowIndex, WebFrame frame) {
        urlString = url;
        this.frame = frame;
        this.numThreadsLock = numThreadsLock;
        this.rowIndex = rowIndex;
        model = frame.getTableModel();
    }

    @Override
    public void run() {
        frame.increaseRunning();
        download();
        frame.decreaseRunning();
        frame.increaseCompleted();
        numThreadsLock.release();
    }

    public void download() {
		InputStream input = null;
		StringBuilder contents = null;

        try {
            Long start = System.currentTimeMillis();
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
		
			// Set connect() to throw an IOException
			// if connection does not succeed in this many msecs.
			connection.setConnectTimeout(5000);
			
			connection.connect();
			input = connection.getInputStream();

			BufferedReader reader  = new BufferedReader(new InputStreamReader(input));
		
			char[] array = new char[1000];
			int len;
			contents = new StringBuilder(1000);
			while ((len = reader.read(array, 0, array.length)) > 0) {
			    if (isInterrupted()) {
                    try{
                        if (input != null) input.close();
                    }
                    catch(IOException ignored) {}
                    model.setValueAt( "interrupted", rowIndex, 1);
                    return;
                }
				contents.append(array, 0, len);
				Thread.sleep(100);
			}
			// OK
            String currTime = LocalTime.now().toString();
			String length = String.valueOf(contents.length());
            Long end = System.currentTimeMillis();
            timeElapsed = end - start;
            String message = currTime + "   " + timeElapsed + " ms   " + length + " bytes";
            model.setValueAt(message, rowIndex, 1);

		}
		// Otherwise control jumps to a catch...
		catch(MalformedURLException ignored) {
            model.setValueAt( "err", rowIndex, 1);
        }
		catch(InterruptedException exception) {
			// deal with interruption
            model.setValueAt( "interrupted  ex", rowIndex, 1);
        }
		catch(IOException ignored) {
            model.setValueAt( "err", rowIndex, 1);
        }
		// "finally" clause, to close the input stream
		// in any case
		finally {
			try{
				if (input != null) input.close();
			}
			catch(IOException ignored) {}
		}

    }
}
