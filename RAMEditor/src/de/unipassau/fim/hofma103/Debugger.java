package de.unipassau.fim.hofma103;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Debugger {
	private JFrame frame;
	private JTextArea consoleOutput;
	private JTextField inputArea;

	private Debugger instance = this;

	private String input = null;

	public Debugger() {
		frame = new JFrame("Debugger");
		consoleOutput = new JTextArea();
		inputArea = new JTextField();

		consoleOutput.setEditable(false);
//		consoleOutput.setEnabled(false);

		frame.add(new JScrollPane(consoleOutput), BorderLayout.CENTER);
		frame.add(inputArea, BorderLayout.SOUTH);

		frame.setSize(800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);

		inputArea.addKeyListener(key);
	}

	public void startDebugging(String editorCode, int numLines) {
		Thread thr = new Thread(new Runnable() {

			@Override
			public void run() {
				RAMMachine machine = new RAMMachine(numLines, instance);
				ArrayList<String> code = new ArrayList<>(Arrays.asList(editorCode.split("\\r?\\n")));
				machine.inputCode(code);
				machine.processCode();
			}
		});
		thr.start();
	}

	KeyListener key = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_ENTER) {
				input = inputArea.getText();
				inputArea.setText("");
			}
		};
	};

	public String getInput() {
		while (input == null) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String tmp = input;
		input = null;
		return tmp;
	}

	public void printOutput(String out) {
		consoleOutput.append(out + "\n\r");
	}
}
