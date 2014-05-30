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
		DataSetWriter datasetWriter = null;
		for (String ext : Formats.XY.extensions)
			if (extension.toLowerCase().equals(ext)) {
				datasetWriter = new DataSetWriterXY(dataset, writer);
			}
		if (datasetWriter != null)
			return datasetWriter;
		throw new UnsupportedFormatException(extension);
	}

}
