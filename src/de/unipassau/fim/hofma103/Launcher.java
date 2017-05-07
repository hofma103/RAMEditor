package de.unipassau.fim.hofma103;

import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.text.BadLocationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Launcher {
	public static boolean enableMemDump = false;
	public static int fontSize = 10;
	public static EventQueue queue;

	public static void main(String[] args) {
		if (args.length > 0)
			cmdOptions(args);

		queue = Toolkit.getDefaultToolkit().getSystemEventQueue();

		EditorPanel editor = new EditorPanel();
		try {
			editor.showEditorPanel();
		} catch (BadLocationException e) {
			// Do nothing as this is due to the handling of backspace key
		}
	}

	private static void cmdOptions(String[] args) {
		String usageHelp = "java -jar rameditor.jar -<option> <args>";
		Options commandLineOptions = new Options();

		Option optEnableMemDump = new Option("m", "enableMemDump", false,
				"Aktiviert memory-dump während des Debuggens");
		Option optSetFontSize = new Option("f", "fontSize", true, "Überschreibt die Standardschriftgröße von 10");
		Option optShowHelp = new Option("h", "help", false, "Zeigt eine Übersicht möglicher Argumente");

		optEnableMemDump.setRequired(false);
		optSetFontSize.setRequired(false);
		optShowHelp.setRequired(false);

		commandLineOptions.addOption(optEnableMemDump);
		commandLineOptions.addOption(optSetFontSize);
		commandLineOptions.addOption(optShowHelp);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(commandLineOptions, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp(usageHelp, commandLineOptions);
			System.exit(1);
			return;
		}

		enableMemDump = cmd.hasOption("enableMemDump");
		if (cmd.hasOption("fontSize")) {
			try {
				fontSize = Integer.parseInt(cmd.getOptionValue("fontSize"));
			} catch (NumberFormatException e) {
				System.out.println("Fehlerhafte Eingabe. Schriftgröße 10 wird genutzt");
			}
		}
		if (cmd.hasOption("help")) {
			formatter.printHelp(usageHelp, commandLineOptions);
			System.exit(1);
			return;
		}
	}
}
