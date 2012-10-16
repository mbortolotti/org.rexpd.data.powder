package org.rexpd.data.powder.io;

import java.io.IOException;
import java.io.PrintWriter;

import org.rexpd.data.powder.DataSet;
import org.rexpd.data.powder.io.DataSetReader.Formats;

public abstract class DataSetWriterFactory {

	public static DataSetWriter createWriter(DataSet dataset, String fileName) throws IOException, UnsupportedFormatException {
		int dotpos = fileName.lastIndexOf('.');
		if (dotpos == -1)
			throw new IOException("Unable to recognize file extension!");
		String extension = fileName.substring(dotpos + 1);
		PrintWriter writer = new PrintWriter(fileName);
		for (String ext : Formats.XY.extensions)
			if (extension.toLowerCase().equals(ext)) {
				return new DataSetWriterXY(dataset, writer);
			}
		throw new UnsupportedFormatException(extension);
	}
	
}
