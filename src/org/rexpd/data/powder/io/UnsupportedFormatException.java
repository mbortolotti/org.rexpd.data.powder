package org.rexpd.data.powder.io;

public class UnsupportedFormatException extends Exception {
	
	public UnsupportedFormatException() {
		super("Unsupported file format");
	}
	
	public UnsupportedFormatException(String format) {
		super("Unsupported file format: " + format);
	}
	
}
