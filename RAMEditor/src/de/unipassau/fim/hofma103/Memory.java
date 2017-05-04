package de.unipassau.fim.hofma103;

import java.util.Arrays;
import java.util.Random;

/**
 * The implementation is based on java.util.ArrayList I implemented it on my
 * own, as the only needed access to the memory is via set(index, value) and
 * get(index) and also as I wanted to make a more realistic memory which just
 * contains random numbers in uninitialized fields
 *
 */
public class Memory {
	private int[] memory;
	private Random random;
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	public Memory() {
		memory = new int[5];
		random = new Random();

		for (int i = 0; i < memory.length; i++) {
			memory[i] = random.nextInt();
		}
	}

	/**
	 * Basic function to read an element from the memory. If the index is
	 * outside of the memories range, it returns a random number
	 * 
	 * @param index
	 * @return returns the element from memory at the given position
	 */
	public int get(int index) {
		if (index >= memory.length)
			return random.nextInt();
		return memory[index];
	}

	/**
	 * Sets the memory at the given index to newVal if memory is to small, its
	 * size is increased based on how ArrayLists handle this. In addition, the
	 * newly added memory gets filled with random numbers to simulate a real
	 * memory.
	 * 
	 * @param index
	 *            which should be set to newVal
	 * @param newVal
	 *            value to store
	 * @return the old value at the given index
	 */
	public int set(int index, int newVal) {
		dumpMemory();
		if (index >= memory.length)
			increaseMemory(index + 1);
		int oldValue = memory[index];
		memory[index] = newVal;
		return oldValue;
	}

	private void increaseMemory(int minCapazity) {
		int oldCapazity = memory.length;
		int newCapazity = oldCapazity + (oldCapazity >> 1);
		if (newCapazity - minCapazity < 0)
			newCapazity = minCapazity;
		if (newCapazity - MAX_ARRAY_SIZE > 0)
			newCapazity = hugeCapazity(minCapazity);
		memory = Arrays.copyOf(memory, newCapazity);

		for (int i = oldCapazity; i < newCapazity; i++) {
			memory[i] = random.nextInt();
		}
	}

	private int hugeCapazity(int minCapazity) {
		if (minCapazity < 0)
			throw new OutOfMemoryError();
		return minCapazity > MAX_ARRAY_SIZE ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
	}

	/**
	 * prints the current memory to the console. This is mainly for debug
	 * purposes, but can also be used to see how the memory changed exactly
	 * after storing a value to it
	 */
	private void dumpMemory() {
		System.out.format("%s\n", Arrays.toString(memory));
	}
}