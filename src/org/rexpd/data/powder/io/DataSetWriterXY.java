package org.rexpd.data.powder.io;

import java.io.IOException;
import java.io.PrintWriter;

import org.rexpd.data.powder.DataPoint;
import org.rexpd.data.powder.DataSet;
import org.rexpd.data.powder.Pattern;


public class DataSetWriterXY extends DataSetWriter {

	public DataSetWriterXY(DataSet dataset, PrintWriter output) {
		super(dataset, output);
	}

	@Override
	public void write() throws IOException {
		if (writeAll()) {
			for (Pattern pattern : getDataSet().patterns()) {
				// TODO enable full dataset output!
				writePattern(pattern);
			}
		}
		else {
			Pattern active = getDataSet().getActivePattern();
			if (active == null)
				throw new IOException("No active pattern in dataset!");
			writePattern(active);
		}
		getWriter().close();
	}
	
	private void writePattern(Pattern pattern) {
		for (DataPoint point : pattern.getPointList()) {
			getWriter().print(point.getXValue());
			if (getOutputMode() == OutputMode.EXP || getOutputMode() == OutputMode.BOTH)
				getWriter().print("\t" + point.getYValue());
			if (getOutputMode() == OutputMode.CALC || getOutputMode() == OutputMode.BOTH)
				getWriter().print("\t" + point.getYCalc());
			getWriter().println();
		}
	}

}
