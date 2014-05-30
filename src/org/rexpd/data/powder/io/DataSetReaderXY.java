package org.rexpd.data.powder.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.rexpd.data.powder.DataPoint;
import org.rexpd.data.powder.DataSet;
import org.rexpd.data.powder.Pattern;


public class DataSetReaderXY extends DataSetReader {

	public DataSetReaderXY(BufferedReader r) {
		super(r);
	}

	@Override
	public DataSet read() throws IOException {
		String line;
		Pattern pattern = new Pattern();
		while ((line = getReader().readLine()) != null) {
			String trimmed = line.trim();
			if (trimmed.length() == 0 || Character.isLetter(trimmed.charAt(0)))
				continue;
			StringTokenizer tokenizer = new StringTokenizer(line);
			if (tokenizer.countTokens() >= 2) {
				double two_theta = Double.valueOf(tokenizer.nextToken());
				double intensity = Double.valueOf(tokenizer.nextToken());
				pattern.addPoint(new DataPoint(two_theta, intensity));
			}
		}
		getReader().close();
		DataSet data = new DataSet();
		data.addPattern(pattern);
		return data;
	}

}
