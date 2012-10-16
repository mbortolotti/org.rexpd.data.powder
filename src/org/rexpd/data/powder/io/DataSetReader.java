package org.rexpd.data.powder.io;

import java.io.*;

import org.rexpd.data.powder.DataSet;


public abstract class DataSetReader {
	
	public enum Formats {

		XY(new String[] {"xy"}, "XY files"),
		XYZ(new String[] {"xyd", "xye", "epf"}, "XYZ files"),
		DAT(new String[] {"dat"}, "DAT files"),
		CIF(new String[] {"cif"}, "CIF files"),
		MDI(new String[] {"mdi"}, "MDI files"),
		XML(new String[] {"xml"}, "XML files"),
		ALL(new String[] {"*"}, "All files");

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
	
	private BufferedReader reader = null;
	
	public DataSetReader(BufferedReader r) {
		reader = r;
	}
	
	public BufferedReader getReader() {
		return reader;
	}
	
	public abstract DataSet read() throws IOException;

}
