package de.unipassau.fim.hofma103;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;

import org.irsn.javax.swing.CodeEditorPane;
import org.irsn.javax.swing.DefaultSyntaxColorizer.RegExpHashMap;

@SuppressWarnings("serial")
public class Debugger extends JFrame {
	private JTextField inputArea;

	private CodeEditorPane consoleOutput;

	private Debugger instance = this;

	private String input = null;

	private EditorPanel panel;

	private boolean debuggerIsRunning = false;
	public boolean interrupt = false;

	private String lineseparator = System.getProperty("line.separator");
	private DefaultCaret caret;

	@SuppressWarnings("unchecked")
	private HashMap<String, Color> syntax = new RegExpHashMap();

	public Debugger(EditorPanel panel) {
		this.panel = panel;

		enableEvents(WriteTextEvent.id);
		setName("Debugger");
		consoleOutput = new CodeEditorPane();

		initDebugSyntax();

		consoleOutput.setKeywordColor(syntax);
		inputArea = new JTextField();

		consoleOutput.setEditable(false);
		caret = (DefaultCaret) consoleOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		add(consoleOutput.getContainerWithLines(), BorderLayout.CENTER);
		add(inputArea, BorderLayout.SOUTH);

		JMenuBar menubar = new JMenuBar();
		menubar.add(new JMenuItem(Start));
		menubar.add(new JMenuItem(End));
		menubar.add(new JMenuItem(ClearConsole));
		End.setEnabled(false);

		setJMenuBar(menubar);

		setSize(800, 500);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		inputArea.addKeyListener(key);
	}

	private void initDebugSyntax() {
		for (String elem : panel.syntax.keySet()) {
			syntax.put(elem, panel.syntax.get(elem));
		}

		syntax.put("^Error.*", Color.RED);
		syntax.put("^Memorydump.*", Color.GREEN);
		syntax.put("^Zusammenfassung.*", Color.GREEN);
		syntax.put("^Eingabe.*", Color.GREEN);
	}

	public void setVisible() {
		ClearConsole.actionPerformed(null);
		setVisible(true);
	}

	Action Start = new AbstractAction("Debugger starten") {
		@Override
		public void actionPerformed(ActionEvent e) {
			ClearConsole.actionPerformed(null);
			interrupt = false;
			if (!debuggerIsRunning) {
				String tmp = panel.getEditor().getText();
				tmp = tmp.replaceAll("\\s+", "");
				String[] tmpArray = tmp.split(lineseparator);
				if (tmpArray.length > 0 && tmpArray[0].length() > 0) {
					if (panel.getEditor().getText().length() > 0) {
						startDebugging(panel.getEditor().getText(), panel.getEditor().getNumberOfLines());
						End.setEnabled(true);
						Start.setEnabled(false);
						inputArea.requestFocusInWindow();
					} else {
						printOutput("Kein Code gefunden!");
					}
				} else {
					printOutput("Kein Code gefunden!");
				}
			}
		}
	};
	Action End = new AbstractAction("Abbrechen") {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (debuggerIsRunning) {
				interrupt = true;
				End.setEnabled(false);
				Start.setEnabled(true);
			}
		}
	};
	Action ClearConsole = new AbstractAction("Konsole leeren") {
		@Override
		public void actionPerformed(ActionEvent e) {
			consoleOutput.setText("");
		}
	};

	public void startDebugging(String editorCode, int numLines) {
		debuggerIsRunning = true;
		Thread thr = new Thread(new Runnable() {

			@Override
			public void run() {
				RAMMachine machine = new RAMMachine(numLines, instance);
				ArrayList<String> code = new ArrayList<>(Arrays.asList(editorCode.split("\\r?\\n")));
				machine.inputCode(code);
				machine.processCode();
				debuggerIsRunning = false;
				interrupt = false;
				End.setEnabled(false);
				Start.setEnabled(true);
			}
		});
		thr.start();
	}

	@Override
	protected void processEvent(AWTEvent e) {
		if (e instanceof WriteTextEvent) {
			WriteTextEvent event = (WriteTextEvent) e;
			Document doc = consoleOutput.getDocument();
			try {
				doc.insertString(doc.getLength(), (doc.getLength() > 0 ? lineseparator : "") + event.getContent(),
						null);
				consoleOutput.setCaretPosition(doc.getLength());
				consoleOutput.updateLineNumberDivider();
				consoleOutput.updateLineNumberView();
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		} else {
			super.processEvent(e);
		}
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
		if (input != null)
			input = null;
		while (input == null && !interrupt) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// prevents NumberFormatException if interrupting the program
		if (interrupt)
			input = "0";
		String tmp = input;
		input = null;
		printOutput(String.format("Eingabe: %s", tmp));
		return tmp;
	}

	public void printOutput(String out) {
		Launcher.queue.postEvent(new WriteTextEvent(this, out));
	}
}
