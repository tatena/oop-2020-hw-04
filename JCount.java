// JCount.java

/*
 Basic GUI/Threading exercise.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JCount extends JPanel {
	private int maxCount;
	private JTextField inputField;
	private JLabel currCount;
	private JButton start;
	private JButton end;
	private Counter curr;

	public JCount() {
		// Set the JCount to use Box layout
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		inputField = new JTextField();
		this.add(inputField);

		currCount = new JLabel("0");
		this.add(currCount);

		start = new JButton("start");
		end = new JButton("end");
		curr = null;

		addListeners();

		this.add(start);
		this.add(end);
		add(Box.createRigidArea(new Dimension(0,40)));
	}

	private void addListeners() {
		start.addActionListener(e -> {
			if (curr != null) {
				curr.interrupt();
			}
			curr = new Counter();
			curr.start();

		});
		end.addActionListener(e -> {
			if (curr != null) {
				curr.interrupt();
			}
		});
	}


	static public void main(String[] args)  {
		// Creates a frame with 4 JCounts in it.
		// (provided)
		JFrame frame = new JFrame("The Count");
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setPreferredSize(new Dimension(200, 550));


		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(frame);
			}
		});

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private static void createAndShowGUI(JFrame frame) {
		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());
	}

	private class Counter extends Thread {
		@Override
		public void run() {
			maxCount = Integer.valueOf(inputField.getText());
			setLabel(0);
			for (int i=1; i<=maxCount; i++) {
				if (isInterrupted()) {
					System.out.println("I was interrupted");
					return;
				}
				if (i%10000 == 0) {
					int savedCurr = i;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						System.out.println("I was interrupted during sleep");
						return;
					}
					setLabel(savedCurr);
				}
			}
			setLabel(maxCount);
			System.out.println("I finished my work");

		}
	}

	private void setLabel(int savedCurr) {
		SwingUtilities.invokeLater(() -> currCount.setText(String.valueOf(savedCurr)));
	}
}

