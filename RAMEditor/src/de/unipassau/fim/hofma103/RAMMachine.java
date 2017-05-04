package de.unipassau.fim.hofma103;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class RAMMachine {
	private Memory memory;
	private ArrayList<String> functions;
	private ArrayList<Integer> functionParameters;

	private int accumulator = 0;
	private int programCounter = 0;
	private Debugger debug;

	public RAMMachine(int programLength, Debugger panel) {
		memory = new Memory(panel);
		functions = new ArrayList<String>(programLength);
		functionParameters = new ArrayList<Integer>(programLength);

		this.debug = panel;
	}

	public void inputCode(ArrayList<String> code) {
		for (int i = 0; i < code.size(); i++) {
			String str = code.get(i);
			int beginIndex = 0;
			int middleIndex = str.indexOf("(");
			int endIndex = str.indexOf(")");
			if (str.startsWith("->") || str.startsWith("<-") || str.startsWith("  "))
				beginIndex = 2;
			String method = str.substring(beginIndex, middleIndex);
			int methodParam = 0;
			if (endIndex - 1 > middleIndex)
				methodParam = Integer.parseInt(str.substring(middleIndex + 1, endIndex));
			method = method.replace("@", "At");
			functions.add(method);
			functionParameters.add(methodParam);
		}
	}

	public void processCode() {
		while (programCounter < functions.size()) {
			String function = functions.get(programCounter);
			int functionParameter = functionParameters.get(programCounter);
			if (function.equals("read") || function.equals("print"))
				debug.printOutput(String.format("%s()", function));
			else
				debug.printOutput(String.format("%s(%d)", function.replace("At", "@"), functionParameter));
			java.lang.reflect.Method method = null;
			try {
				method = this.getClass().getMethod(function, int.class);
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			}
			try {
				method.invoke(this, functionParameter);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			programCounter++;
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
			programCounter = memory.get(param) - 2;
	}

	public void jumpGtzAt(int param) {
		if (accumulator > 0)
			programCounter = memory.get(memory.get(param)) - 2;
	}

	public void jumpGtzAbs(int param) {
		if (accumulator > 0)
			programCounter = param - 2;
	}

	public void jumpZ(int param) {
		if (accumulator == 0)
			programCounter = memory.get(param) - 2;
	}

	public void jumpZAt(int param) {
		if (accumulator == 0)
			programCounter = memory.get(memory.get(param)) - 2;
	}

	public void jumpZAbs(int param) {
		if (accumulator == 0)
			programCounter = param - 2;
	}

	public void read(int param) {
		try {
			accumulator = Integer.parseInt(debug.getInput());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void print(int param) {
		debug.printOutput("" + accumulator);
	}
}
