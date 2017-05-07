package de.unipassau.fim.hofma103;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class RAMMachine {
	private Memory memory;
	private ArrayList<String> functions;
	private ArrayList<Integer> functionParameters;

	private int accumulator = 0;
	private int programCounter = 0;

	// counts really all steps done by the program
	private int stepCounter = 0;
	// counts steps spent on jump commands (does not include the first time a
	// loop is executed if jumps are used to realize that)
	private int loopCounter = 0;
	private Debugger debug;

	public RAMMachine(int programLength, Debugger panel) {
		memory = new Memory(panel);
		functions = new ArrayList<String>(programLength);
		functionParameters = new ArrayList<Integer>(programLength);

		this.debug = panel;
	}

	public boolean inputCode(ArrayList<String> code) {
		for (int i = 0; i < code.size(); i++) {
			String str = code.get(i);
			str = str.replaceAll("\\s+", "");
			int beginIndex = 0;
			int middleIndex = str.indexOf("(");
			int endIndex = str.indexOf(")");
			if (str.startsWith("->") || str.startsWith("<-"))
				beginIndex = 2;
			String method = str.substring(beginIndex, middleIndex);
			int methodParam = Integer.MIN_VALUE;
			try {
				if (endIndex - 1 > middleIndex)
					methodParam = Integer.parseInt(str.substring(middleIndex + 1, endIndex));
			} catch (NumberFormatException e) {
				debug.printError(String.format("\"%s\" ist keine Zahl", str.substring(middleIndex + 1, endIndex)),
						i + 1);
				return false;
			}
			method = method.replace("@", "At");
			functions.add(method);
			functionParameters.add(methodParam);
		}
		return true;
	}

	public void processCode() {
		while (programCounter < functions.size() && !debug.interrupt) {
			String function = functions.get(programCounter);
			int functionParameter = functionParameters.get(programCounter);

			debug.printOutput(String.format("%s" + (functionParameter != Integer.MIN_VALUE ? "(%d)" : "()"),
					function.replace("At", "@"), functionParameter));

			java.lang.reflect.Method method = null;
			try {
				method = this.getClass().getMethod(function, int.class);
			} catch (NoSuchMethodException e1) {
				debug.printError(String.format("Funktion \"%s\" nicht gefunden!", function.replace("At", "@")),
						programCounter + 1);
			} catch (SecurityException e1) {
			}
			try {
				method.invoke(this, functionParameter);
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
				debug.printError(String.format("Funktion \"%s\" wurde mit einem ungütigen Argument aufgerufen",
						function.replace("At", "@")), programCounter + 1);
			} catch (InvocationTargetException e) {
				debug.printError(
						String.format("Funktion \"%s\" hat einen Fehler verursacht!", function.replace("At", "@")),
						programCounter + 1);
			}
			programCounter++;
			stepCounter++;

			try {
				Thread.sleep(Launcher.methodExecDelay);
			} catch (InterruptedException e) {
			}
		}
		if (debug.interrupt) {
			debug.printOutput("Durch Benutzer unterbrochen");
		} else {
			debug.printOutput(String.format("Zusammenfassung: Es wurden %d Schritte ausgeführt.", stepCounter));
			debug.printOutput(
					String.format("\t\t\t\t Davon wurden %d Schritte durch Sprünge verursacht.", loopCounter));
		}
	}

	public void add(int param) {
		accumulator = accumulator + memory.get(param);
	}

	public void addAt(int param) {
		accumulator = accumulator + memory.get(memory.get(param));
	}

	public void addAbs(int param) {
		accumulator = accumulator + param;
	}

	public void sub(int param) {
		accumulator = accumulator - memory.get(param);
	}

	public void subAt(int param) {
		accumulator = accumulator - memory.get(memory.get(param));
	}

	public void subAbs(int param) {
		accumulator = accumulator - param;
	}

	public void mult(int param) {
		accumulator = accumulator * memory.get(param);
	}

	public void multAt(int param) {
		accumulator = accumulator * memory.get(memory.get(param));
	}

	public void multAbs(int param) {
		accumulator = accumulator * param;
	}

	public void div(int param) {
		accumulator = accumulator / memory.get(param);
	}

	public void divAt(int param) {
		accumulator = accumulator / memory.get(memory.get(param));
	}

	public void divAbs(int param) {
		accumulator = accumulator / param;
	}

	public void load(int param) {
		accumulator = memory.get(param);
	}

	public void loadAt(int param) {
		accumulator = memory.get(memory.get(param));
	}

	public void loadAbs(int param) {
		accumulator = param;
	}

	public void store(int param) {
		memory.set(param, accumulator);
	}

	public void storeAt(int param) {
		memory.set(memory.get(param), accumulator);
	}

	public void jumpGtz(int param) {
		if (accumulator > 0)
			updateProgramCounter(memory.get(param));
	}

	public void jumpGtzAt(int param) {
		if (accumulator > 0)
			updateProgramCounter(memory.get(memory.get(param)));
	}

	public void jumpGtzAbs(int param) {
		if (accumulator > 0)
			updateProgramCounter(param);
	}

	public void jumpZ(int param) {
		if (accumulator == 0)
			updateProgramCounter(memory.get(param));
	}

	public void jumpZAt(int param) {
		if (accumulator == 0)
			updateProgramCounter(memory.get(memory.get(param)));
	}

	public void jumpZAbs(int param) {
		if (accumulator == 0)
			updateProgramCounter(param);
	}

	private void updateProgramCounter(int newCount) {
		int oldCount = programCounter;
		programCounter = newCount - 2;
		if (oldCount > programCounter)
			loopCounter = loopCounter + (oldCount - programCounter);
	}

	public void read(int param) {
		String input = debug.getInput();
		try {
			accumulator = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			debug.printError(String.format("\"%s\" ist keine Zahl!", input), programCounter + 1);
			debug.printOutput("Bitte geben Sie eine neue Zahl ein");
			read(param);
		}
	}

	public void print(int param) {
		debug.printOutput("" + accumulator);
	}
}
