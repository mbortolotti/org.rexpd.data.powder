package org.rexpd.data.powder.io;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.rexpd.data.powder.DataPoint;
import org.rexpd.data.powder.DataSet;
import org.rexpd.data.powder.Pattern;

public class DataSetReaderGSAS extends DataSetReader {


	private static final double ANGLE_INC = 0.01;
	
	private DataSet dataset = null;

	public DataSetReaderGSAS(BufferedReader reader) {
		super(reader);
	}

	public DataSet read() throws IOException {
		dataset = new DataSet();
		String title = getReader().readLine();
		getReader().mark(16384);
		String instrument = getReader().readLine();
		if (instrument.indexOf("Instrument parameter") < 0)
			getReader().reset();
		String line = null;
		boolean bank = false;
		int bank_number = 0;
		while ((line = getReader().readLine()) != null) {
			System.out.println(line);
			if (line.indexOf("BANK") >= 0) {
				System.out.println("BANK record found");
				Pattern pattern = new Pattern();
				StringTokenizer tokenizer = new StringTokenizer(line);
				tokenizer.nextToken();
				bank_number = Integer.parseInt(tokenizer.nextToken());
				int data_points = Integer.parseInt(tokenizer.nextToken());
				int data_rows = Integer.parseInt(tokenizer.nextToken());
				String BINTYP = tokenizer.nextToken();
				if (!BINTYP.equals("CONST"))
					throw new IOException("Only constant step width data supported!");
				double two_theta_start = ANGLE_INC * Double.parseDouble(tokenizer.nextToken());
				double two_theta_step = ANGLE_INC * Double.parseDouble(tokenizer.nextToken());
				double BCOEF3 = Double.parseDouble(tokenizer.nextToken());
				double BCOEF4 = Double.parseDouble(tokenizer.nextToken());
				String TYPE = tokenizer.nextToken();
				if (!(TYPE.equals("STD") || TYPE.equals("ESD")))
					throw new IOException("Unsupported GSAS data format");
				int data_per_point = TYPE.equals("STD") ? 1 : 2;
				double[] two_theta_array = new double[data_points];
				double[] intensity_array = new double[data_points];
				int ndp = 0;
				for (int nr = 0; nr < data_rows; nr++) {
					if (ndp >= data_points)
						break;
					line = getReader().readLine();
					if (line == null)
						break;
					StringTokenizer data_tokenizer = new StringTokenizer(line);
					if (data_tokenizer.countTokens() < 1)
						break;
					int points_per_row = data_tokenizer.countTokens() / data_per_point;
					for (int nd = 0; nd < points_per_row; nd++, ndp++) {
						if (ndp >= data_points)
							break;
						two_theta_array[ndp] = two_theta_start + ndp * (two_theta_step);
						intensity_array[ndp] = Double.parseDouble(data_tokenizer.nextToken());
						if (TYPE.equals("ESD"))
							data_tokenizer.nextToken();
					}
				}
				System.out.println("ndp: " + ndp);
				for (int n = 0; n < ndp; n++) {
					double x = two_theta_array[n];
					double y = intensity_array[n];
					pattern.addPoint(new DataPoint(x, y));
				}
				pattern.setLabel(title + "BANK" + bank_number);
				dataset.addPattern(pattern);
			}
		}
		return dataset;
	}

}
