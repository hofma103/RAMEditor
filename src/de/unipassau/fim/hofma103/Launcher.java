package de.unipassau.fim.hofma103;

import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.text.BadLocationException;

public class Launcher {
	public static boolean enableMemDump = false;
	public static EventQueue queue;

	public static void main(String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("enableMemDump"))
				enableMemDump = true;
		}
		
		queue = Toolkit.getDefaultToolkit().getSystemEventQueue();

		EditorPanel editor = new EditorPanel();
		try {
			editor.showEditorPanel();
		} catch (BadLocationException e) {
			// Do nothing as this is due to the handling of backspace key
		}
	}
}
