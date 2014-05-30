package org.rexpd.data.powder.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.rexpd.data.powder.DataPoint;
import org.rexpd.data.powder.DataSet;
import org.rexpd.data.powder.Pattern;


public class DataSetReaderDAT extends DataSetReader {

	public DataSetReaderDAT(BufferedReader r) {
		super(r);
	}

	@Override
	public DataSet read() throws IOException {
		boolean headerFound = false;
		double x_start = 0;
		double x_step = 0.01;
		int point_index = 0;
		String line;
		Pattern pattern = new Pattern();
		while ((line = getReader().readLine()) != null) {
			/** skip comment block **/
			if ((line.startsWith("_")) || (line.startsWith(";")) || (line.startsWith("!")) || (line.startsWith("#"))) {
				System.out.println("commented line: " + line);
				continue;
			}
			/** check for free comments **/
			if (Character.isLetter(line.charAt(0))) {
				System.out.println("uncommented line: " + line);
				continue;
			}
			StringTokenizer tokenizer = new StringTokenizer(line);
			if (tokenizer.countTokens() >= 3 && !headerFound) {
				x_start = Double.parseDouble(tokenizer.nextToken());
				x_step = Double.parseDouble(tokenizer.nextToken());
				System.out.println("x_start: " + x_start);
				System.out.println("x_step: " + x_step);
				headerFound = true;
			}
			else if (tokenizer.countTokens() > 0) {
				if (!headerFound)
					throw new IOException("DAT reader: no header found!");
				while (tokenizer.hasMoreTokens()) {
					double x = x_start + x_step * point_index++;
					double value = Double.parseDouble(tokenizer.nextToken());
					pattern.addPoint(new DataPoint(x, value));
				}
			}
		}
		getReader().close();
		if (pattern.getPointList().size() == 0)
			throw new IOException("DAT reader: no data found!");
		DataSet data = new DataSet();
		data.addPattern(pattern);
		return data;
	}

}
