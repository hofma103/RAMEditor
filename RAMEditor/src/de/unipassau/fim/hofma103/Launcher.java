package de.unipassau.fim.hofma103;

import javax.swing.text.BadLocationException;

public class Launcher {
	public static void main(String[] args) {
		EditorPanel editor = new EditorPanel();
		try {
			editor.showEditorPanel();
		} catch (BadLocationException e) {
			// Do nothing as this is due to the handling of backspace key
		}
	}
}
