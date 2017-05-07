package de.unipassau.fim.hofma103;

import java.awt.AWTEvent;

@SuppressWarnings("serial")
public class WriteTextEvent extends AWTEvent {
	public static final int id = AWTEvent.RESERVED_ID_MAX + 1;
	private String str;

	public WriteTextEvent(Object target, String str) {
		super(target, id);
		this.str = str;
	}

	public String getContent() {
		return str;
	}

	@Override
	public int getID() {
		return id;
	}
}
