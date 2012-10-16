package org.rexpd.data.powder.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.rexpd.data.powder.DataPoint;
import org.rexpd.data.powder.DataSet;
import org.rexpd.data.powder.Pattern;


public class DataSetReaderTNX extends DataSetReader {

	public DataSetReaderTNX(BufferedReader r) {
		super(r);
	}

	@Override
	public DataSet read() throws IOException {
		String line;
		DataSet dataset = new DataSet();
		Pattern pattern = null;
		double theta_offset = 0.0;
		boolean thinfilm = false;
		while ((line = getReader().readLine()) != null) {
			if (line.toLowerCase().indexOf("<measure") != -1) {
				pattern = new Pattern();
				thinfilm = (line.toLowerCase().indexOf("scan (thin film)") != -1);
			}
			else if (line.toLowerCase().indexOf("</measure>") != -1) {
				if (pattern != null)
					dataset.addPattern(pattern);
				pattern = null;
				theta_offset = 0.0;
			}
			else if (thinfilm && line.toLowerCase().indexOf("theta1=") != -1) {
				StringTokenizer tokenizer = new StringTokenizer(line.substring(line.toLowerCase().indexOf("theta1")), "\"");
				tokenizer.nextToken();
				theta_offset = Double.valueOf(tokenizer.nextToken());
			}
			
			else if (line.replaceAll(" *\\= *", "\\=").indexOf("x=\"") != -1) {
				StringTokenizer tokenizer = new StringTokenizer(line, "\"");
				if (tokenizer.countTokens() == 5 && pattern != null) {
					tokenizer.nextToken();
					double two_theta = theta_offset + Double.valueOf(tokenizer.nextToken());
					tokenizer.nextToken();
					double intensity = Double.valueOf(tokenizer.nextToken());
					pattern.addPoint(new DataPoint(two_theta, intensity));
				}
			}
		}
		getReader().close();
		return dataset;
	}

}
