package org.rexpd.data.powder.io;

import java.io.IOException;
import java.io.PrintWriter;

import org.rexpd.data.powder.DataSet;


public abstract class DataSetWriter {
	
	public enum Formats {

		XY(new String[] {"xy"}, "XY files");

		public String[] extensions;
		public String description;

		Formats(String[] exts, String desc) {
			extensions = exts;
			description = desc;
		}
		
		public static String[] getFilterExtensions() {
			String[] filterExt = new String[values().length];
			for (Formats format : values()) {
				filterExt[format.ordinal()] = "";
				for (int ne = 0; ne < format.extensions.length; ne++)
					filterExt[format.ordinal()] += "*." + format.extensions[ne] + ";";
				}
			return filterExt;
		}
		
		public static String[] getFilterNames() {
			String[] filterNames = new String[values().length];
			for (Formats format : values())
				filterNames[format.ordinal()] = format.description;
			return filterNames;
		}
	}
	
	public enum OutputMode {
		EXP,
		CALC,
		BOTH
	}
	
	private DataSet dataset;
	private PrintWriter output;
	private boolean writeAll;
	private OutputMode outputMode;
	
	public abstract void write() throws IOException;

	public DataSetWriter(DataSet dataset, PrintWriter output) {
		this.dataset = dataset;
		this.output = output;
		writeAll = true;
		outputMode = OutputMode.EXP;
	}

	public DataSet getDataSet() {
		return dataset;
	}
	
	public PrintWriter getWriter() {
		return output;
	}
	
	public void setWriteAll(boolean full) {
		writeAll = full;
	}
	
	public boolean writeAll() {
		return writeAll;
	}
	
	public void setOutputMode(OutputMode mode) {
		outputMode = mode;
	}
	
	public OutputMode getOutputMode() {
		return outputMode;
	}
	
	

}
