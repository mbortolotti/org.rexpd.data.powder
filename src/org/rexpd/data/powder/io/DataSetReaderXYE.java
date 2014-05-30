package org.rexpd.data.powder.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.rexpd.data.powder.DataPoint;
import org.rexpd.data.powder.DataSet;
import org.rexpd.data.powder.Pattern;


public class DataSetReaderXYE extends DataSetReader {
	
	public DataSetReaderXYE(BufferedReader r) {
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
			if (tokenizer.countTokens() >= 3) {
				Double two_theta = Double.valueOf(tokenizer.nextToken());
				Double intensity = Double.valueOf(tokenizer.nextToken());
				// TODO add third line (error)
				pattern.addPoint(new DataPoint(new Double(two_theta), new Double(intensity)));
			}
		}
		getReader().close();
		DataSet data = new DataSet();
		data.addPattern(pattern);
		return data;
	}

}
