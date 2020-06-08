import com.sun.javaws.IconUtil;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import sun.plugin2.applet.StopListener;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class WebFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JButton stop;
    private JButton single;
    private JButton concurrent;
    private JProgressBar progress;
    private JLabel running;
    private int runningValue;
    private JLabel completed;
    private int completedValue;
    private JLabel elapsed;
    private JTextField input;
    private Long startTime;
    private Long endTime;

    public WebFrame(String fileName) {
        super();
        startTime = Long.valueOf(0);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.add(getTablePanel());
        fillTable(fileName);
        this.add(getControlPanel());
        switchToReadyState();
        addActionListeners();
    }

    private void addActionListeners() {
        Vector<Thread> threads = new Vector<>();
        single.addActionListener(new FetchListener(this, threads, true));
        concurrent.addActionListener(new FetchListener(this, threads, false));
        stop.addActionListener(e -> {
            for (int i=0; i<threads.size(); i++) {
                threads.get(i).interrupt();
            }
            switchToReadyState();
        });
    }

    public int getInputValue() {
        if (input.getText().equals("")) return 1;
        return  Integer.valueOf(input.getText());
    }

    public DefaultTableModel getTableModel() {
        return model;
    }

    public void switchToReadyState() {
        endTime = System.currentTimeMillis();
        SwingUtilities.invokeLater(() -> {
            stop.setEnabled(false);
            single.setEnabled(true);
            concurrent.setEnabled(true);
            if (startTime != 0)
                elapsed.setText("Elapsed: " + (endTime - startTime));
        });
    }

    public synchronized void increaseRunning() {
        runningValue++;
        SwingUtilities.invokeLater(() -> {
            running.setText("Running: " + runningValue);
        });
    }

    public synchronized void increaseCompleted() {
        completedValue++;
        progress.setValue(completedValue);
        SwingUtilities.invokeLater(() -> {
            completed.setText("Completed: " + completedValue);
        });
    }



    public synchronized void decreaseRunning() {
        runningValue--;
        SwingUtilities.invokeLater(() -> {
            running.setText("Running: " + runningValue);
        });
        if (runningValue == 0) {
            switchToReadyState();
        }
    }



    public void switchToRunningState() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt("", i, 1);
        }
        runningValue = 0;
        completedValue = 0;
        startTime = System.currentTimeMillis();

        SwingUtilities.invokeLater(() -> {
            stop.setEnabled(true);
            single.setEnabled(false);
            concurrent.setEnabled(false);
            progress.setMaximum(model.getRowCount());
            progress.setValue(0);
            running.setText("Running: 0");
            completed.setText("Completed: 0");
            elapsed.setText("Elapsed: " );
        });
    }

    private void fillTable(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            while(true) {
                String curr = reader.readLine();
                if(curr == null) break;
                model.addRow(new String[]{curr});
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private JPanel getControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        single = new JButton("Single Thread Fetch");
        concurrent = new JButton("Concurrent Fetch");
        input = new JTextField();
        input.setMaximumSize(new Dimension(100, 40));
        running = new JLabel("Running: 0");
        runningValue = 0;
        completed = new JLabel("Completed: 0");
        completedValue = 0;
        elapsed = new JLabel("Elapsed: ");
        progress = new JProgressBar();
        stop = new JButton("Stop");

        panel.add(single);
        panel.add(Box.createRigidArea(new Dimension(10, 5)));

        panel.add(concurrent);
        panel.add(Box.createRigidArea(new Dimension(10, 11)));

        panel.add(input);
        panel.add(Box.createRigidArea(new Dimension(10, 11)));

        panel.add(running);
        panel.add(completed);

        panel.add(elapsed);
        panel.add(Box.createRigidArea(new Dimension(10, 11)));

        panel.add(progress);
        panel.add(Box.createRigidArea(new Dimension(10, 11)));

        panel.add(stop);
        panel.add(Box.createRigidArea(new Dimension(10, 11)));

        return panel;

    }

    private JPanel getTablePanel() {
        JPanel panel = new JPanel();
        model = new DefaultTableModel(new String[] { "url", "status"}, 0);
        table = new JTable(model);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(600,300));
        panel.add(scrollpane);
        return panel;
    }

    static public void main(String[] args)  {
        JFrame frame = new WebFrame("links.txt");

        frame.setPreferredSize(new Dimension(700, 600));

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
