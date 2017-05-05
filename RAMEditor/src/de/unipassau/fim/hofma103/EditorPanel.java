package de.unipassau.fim.hofma103;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.irsn.javax.swing.CodeEditorPane;
import org.irsn.javax.swing.DefaultSyntaxColorizer.RegExpHashMap;

public class EditorPanel {
	@SuppressWarnings("unchecked")
	public HashMap<String, Color> syntax = new RegExpHashMap();
	private CodeEditorPane editor = new CodeEditorPane();
	private HashMap<String, String> help = new HashMap<String, String>();
	private String currentFile = "Unbenannt";
	private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));

	private boolean changed = false;

	private JFrame frame = new JFrame();

	private EditorPanel instance = this;
	private Debugger debug = null;

	public void showEditorPanel() throws BadLocationException {

		buildSyntaxHighlighting();
		buildHelpMenu();

		editor.setKeywordColor(syntax);
		editor.setKeywordHelp(help);
		editor.setBracesToComplete(new char[][] { { '(', ')' }, { '>', '<' }, { '-', '-' } });
		editor.setText("");

		updateWindowTitle();
		frame.getContentPane().add(editor.getContainerWithLines());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 500);

		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		JMenu fileMenu = new JMenu("Datei");
		menubar.add(fileMenu);
		fileMenu.add(New);
		fileMenu.addSeparator();
		fileMenu.add(Save);
		fileMenu.add(SaveAs);
		fileMenu.addSeparator();
		fileMenu.add(Open);
		fileMenu.addSeparator();
		fileMenu.add(Quit);

		menubar.add(new JMenuItem(Debug));

		editor.addKeyListener(k1);

		frame.setVisible(true);
	}

	private void buildSyntaxHighlighting() {
		syntax.put("add", Color.BLUE);
		syntax.put("add@", Color.BLUE);
		syntax.put("addAbs", Color.BLUE);

		syntax.put("sub", Color.BLUE);
		syntax.put("sub@", Color.BLUE);
		syntax.put("subAbs", Color.BLUE);

		syntax.put("mult", Color.BLUE);
		syntax.put("mult@", Color.BLUE);
		syntax.put("multAbs", Color.BLUE);

		syntax.put("div", Color.BLUE);
		syntax.put("div@", Color.BLUE);
		syntax.put("divAbs", Color.BLUE);

		syntax.put("load", Color.BLUE);
		syntax.put("load@", Color.BLUE);
		syntax.put("loadAbs", Color.BLUE);

		syntax.put("store", Color.BLUE);
		syntax.put("store@", Color.BLUE);

		syntax.put("jumpGtz", Color.BLUE);
		syntax.put("jumpGtz@", Color.BLUE);
		syntax.put("jumpGtzAbs", Color.BLUE);

		syntax.put("jumpZ", Color.BLUE);
		syntax.put("jumpZ@", Color.BLUE);
		syntax.put("jumpZAbs", Color.BLUE);

		syntax.put("read", Color.BLUE);
		syntax.put("print", Color.BLUE);
	}

	private void buildHelpMenu() {
		help.put("add", "acc = acc + mem[k]");
		help.put("add@", "acc = acc + mem[mem[k]]");
		help.put("addAbs", "acc = acc + k");

		help.put("sub", "acc = acc - mem[k]");
		help.put("sub@", "acc = acc - mem[mem[k]]");
		help.put("subAbs", "acc = acc - k");

		help.put("mult", "acc = acc * mem[k]");
		help.put("mult@", "acc = acc * mem[mem[k]]");
		help.put("multAbs", "acc = acc * k");

		help.put("div", "acc = acc / mem[k]");
		help.put("div@", "acc = acc / mem[mem[k]]");
		help.put("divAbs", "acc = acc / k");

		help.put("load", "acc = mem[k]");
		help.put("load@", "acc = mem[mem[k]]");
		help.put("loadAbs", "acc = k");

		help.put("store", "mem[k] = acc");
		help.put("store@", "mem[mem[k]] = acc");

		help.put("jumpGtz", "if (acc > 0) { pc = mem[k]; }");
		help.put("jumpGtz@", "if (acc > 0) { pc = mem[mem[k]]; }");
		help.put("jumpGtzAbs", "if (acc > 0) { pc = k; }");

		help.put("jumpZ", "if (acc == 0) { pc = mem[k]; }");
		help.put("jumpZ@", "if (acc == 0) { pc = mem[mem[k]]; }");
		help.put("jumpZAbs", "if (acc == 0) { pc = k; }");

		help.put("read", "acc = \"die naechste Eingabezahl\"");
		help.put("print", "Schreibe acc in eine Datei (=File) bzw auf die Konsole");
	}

	private KeyListener k1 = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			changed = true;
			Save.setEnabled(true);
			editor.updateLineNumberView();
		}
	};

	@SuppressWarnings("serial")
	Action New = new AbstractAction("Neu") {
		@Override
		public void actionPerformed(ActionEvent e) {
			saveOld();
			editor.setText("");
			currentFile = "Unbenannt";
			updateWindowTitle();
		}
	};

	@SuppressWarnings("serial")
	Action Open = new AbstractAction("Öffnen") {

		@Override
		public void actionPerformed(ActionEvent e) {
			saveOld();
			if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				readInFile(dialog.getSelectedFile().getAbsolutePath());
			}
		}
	};

	@SuppressWarnings("serial")
	Action Save = new AbstractAction("Speichern") {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!currentFile.equals("Unbenannt"))
				saveFile(currentFile);
			else
				saveFileAs();
		}
	};

	@SuppressWarnings("serial")
	Action SaveAs = new AbstractAction("Speichern als ...") {

		@Override
		public void actionPerformed(ActionEvent e) {
			saveFileAs();
		}
	};

	@SuppressWarnings("serial")
	Action Quit = new AbstractAction("Beenden") {
		public void actionPerformed(ActionEvent e) {
			saveOld();
			System.exit(0);
		}
	};

	@SuppressWarnings("serial")
	Action Debug = new AbstractAction("Debug") {
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					saveOld();
					if (debug == null)
						debug = new Debugger(instance);
					debug.setVisible();
				}
			});
		}
	};

	public CodeEditorPane getEditor() {
		return editor;
	}

	private void saveFileAs() {
		if (dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			saveFile(dialog.getSelectedFile().getAbsolutePath());
	}

	private void saveOld() {
		if (changed) {
			if (JOptionPane.showConfirmDialog(frame,
					String.format("Wollen Sie die Datei \"%s\" speichern?", currentFile), "Speichern",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				saveFile(currentFile);
		}
	}

	private void saveFile(String name) {
		try {
			FileWriter w = new FileWriter(name);
			editor.write(w);
			w.close();
			currentFile = name;
			changed = false;
			Save.setEnabled(false);
			updateWindowTitle();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readInFile(String fileName) {
		try {
			Path file = Paths.get(fileName);
			editor.setText(new String(Files.readAllBytes(file)));
			currentFile = fileName;
			changed = false;
			Save.setEnabled(false);
			updateWindowTitle();
		} catch (IOException e) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(frame,
					String.format("Die Datei \"%s\" konnte nicht gefunden werden", fileName));
		}
	}

	private void updateWindowTitle() {
		frame.setTitle(String.format("Code Editor - %s", currentFile));
	}
}
