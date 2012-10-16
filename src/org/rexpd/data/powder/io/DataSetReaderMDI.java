package org.rexpd.data.powder.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.rexpd.data.powder.DataPoint;
import org.rexpd.data.powder.DataSet;
import org.rexpd.data.powder.Pattern;


public class DataSetReaderMDI extends DataSetReader {

	private DataSet data = null;

	public DataSetReaderMDI(BufferedReader r) {
		super(r);
	}

	@Override
	public DataSet read() throws IOException {

		data = new DataSet();

		String title = getReader().readLine().substring(20); // datafile title
		data.setLabel(title);

		String line;

		while ((line = getReader().readLine()) != null) {
			StringTokenizer header_tokenizer = new StringTokenizer(line);
			if (header_tokenizer.countTokens() == 7) {
				readDataBlock(line);
			}
		}
		getReader().close();
		return data;
	}

	private void readDataBlock(String line) throws IOException {
		StringTokenizer header_tokenizer = new StringTokenizer(line);
		Pattern pattern = new Pattern();
		double rs_start = Double.parseDouble(header_tokenizer.nextToken());
		double rs_step = Double.parseDouble(header_tokenizer.nextToken());
		header_tokenizer.nextToken();
		header_tokenizer.nextToken();
		header_tokenizer.nextToken();
		header_tokenizer.nextToken();
		int points_number = Integer.parseInt(header_tokenizer.nextToken());
		int current_point = 0;
		while (current_point < points_number) {
			StringTokenizer data_tokenizer = new StringTokenizer(getReader().readLine());
			while (data_tokenizer.hasMoreTokens()) {
				double rspacevar = rs_start + current_point++ * (rs_step);
				double intensity = Double.parseDouble(data_tokenizer.nextToken());
				pattern.addPoint(new DataPoint(rspacevar, intensity));
			}
		}
		data.addPattern(pattern);
	}

}
