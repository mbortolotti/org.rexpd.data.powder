package org.rexpd.data.powder.io;

import static org.rexpd.core.utils.NumberUtils.parseDouble;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.rexpd.data.powder.DataPoint;
import org.rexpd.data.powder.DataSet;
import org.rexpd.data.powder.Pattern;



public class DataSetReaderCIF extends DataSetReader {
	
	private double two_theta_start = 0.0;
	private double two_theta_step = 0.05;
	private boolean has_two_theta_start = false;
	private boolean has_two_theta_step = false;
	
	private DataSet dataset = null;

	public DataSetReaderCIF(BufferedReader r) {
		super(r);
	}

	@Override
	public DataSet read() throws IOException {
		String line = null;
		dataset = new DataSet();
		while ((line = getReader().readLine()) != null) {
			if (!line.startsWith("_") && !line.startsWith("loop_"))
				continue;
			else if (line.startsWith("_pd_meas_2theta_range_min")) {
				two_theta_start = Double.parseDouble(line.substring(line.indexOf(" ")));
				has_two_theta_start = true;
			} else if (line.startsWith("_pd_meas_2theta_range_inc")) {
				two_theta_step = Double.parseDouble(line.substring(line.indexOf(" ")));
				has_two_theta_step = true;
			}
			if (line.startsWith("loop_")) {
				processLoopBlock();
				continue;
			}
		}
		return dataset;
	}
	
	private void processLoopBlock() throws IOException {
		String line = getReader().readLine().trim();
		if (line.startsWith("_pd"))
			processPowderDataLoopBlock(line);
		else
			skipUntilEmptyOrCommentLine(line);
	}
	
	protected void processPowderDataLoopBlock(String firstLine) throws IOException {
		
		int proc_2_theta_corrected = -1;
		int meas_2theta_scan = -1;
		int meas_intensity_total = -1;
		int meas_counts_total = -1;
		int proc_intensity_total = -1; 
		int proc_intensity_net = -1;
		int calc_intensity_total = -1;                   
		int calc_intensity_net = -1;
		int headerCount = 0;
		boolean hasPositionInfo = false;
		boolean hasIntensityInfo = false;
		
		String line = firstLine.trim();
		
		for(; line != null && line.length() >0 && line.charAt(0) == '_'; line = getReader().readLine().trim()) {
			headerCount++;
			if (line.equals("_pd_proc_2theta_corrected")) {
				hasPositionInfo = true;
				proc_2_theta_corrected = headerCount;
				continue;
			}
			if (line.equals("_pd_meas_2theta_scan")) {
				hasPositionInfo = true;
				meas_2theta_scan = headerCount;
				continue;
			}
			else if (line.equals("_pd_meas_intensity_total")) {
				hasIntensityInfo = true;
				meas_intensity_total = headerCount;
				continue;
			}
			else if (line.equals("_pd_meas_counts_total")) {
				hasIntensityInfo = true;
				meas_counts_total = headerCount;
				continue;
			}
			else if (line.equals("_pd_proc_intensity_total")) {
				hasIntensityInfo = true;
				proc_intensity_total = headerCount;
				continue;
			}
			else if (line.equals("_pd_proc_intensity_net")) {
				hasIntensityInfo = true;
				proc_intensity_net = headerCount;
				continue;
			}
			else if (line.equals("_pd_calc_intensity_net")) {
				hasIntensityInfo = true;
				calc_intensity_net = headerCount;
				continue;
			}
			else if (line.equals("_pd_calc_intensity_total")) {
				hasIntensityInfo = true;
				calc_intensity_total = headerCount;
				continue;
			}
		}
		if (!hasPositionInfo || !hasIntensityInfo) {
			skipBlockLines(line);
		} else {
			if ((proc_2_theta_corrected == -1) && !(has_two_theta_start && has_two_theta_step))
				throw new IOException("Unable to read 2 theta information");
			Pattern pattern = new Pattern();
			int loop_index = 0;
			for (; line != null; line = getReader().readLine()) {
				if ((line.length() <= 0) || (line.charAt(0) == '#'))
					continue;
				StringTokenizer tokenizer = new StringTokenizer(line.trim());
				if (tokenizer.countTokens() < headerCount)
					tokenizer = new StringTokenizer(line + getReader().readLine());
				int colIndex = 0;
				double two_theta = 0.0;
				double intensity = 0.0;
				do {
					if (!tokenizer.hasMoreTokens()) {
						pattern.addPoint(new DataPoint(two_theta, intensity));
						break;
					}	
					colIndex++;
					String field = tokenizer.nextToken();
					if (proc_2_theta_corrected == -1 && meas_2theta_scan == -1)
						two_theta = two_theta_start + loop_index++ * two_theta_step;
					// super hack - we should really make a difference here
					if (colIndex == proc_2_theta_corrected)
						two_theta = parseDouble(field);
					else if (colIndex == meas_2theta_scan)
						two_theta = parseDouble(field);
					else if (colIndex == meas_intensity_total)
						intensity = parseDouble(field);
					else if (colIndex == meas_counts_total)
						intensity = parseDouble(field);
					else if (colIndex == proc_intensity_total)
						intensity = parseDouble(field);
					else if (colIndex == proc_intensity_net)
						intensity = parseDouble(field);
					else if (colIndex == calc_intensity_net)
						intensity = parseDouble(field);
					else if (colIndex == calc_intensity_total)
						intensity = parseDouble(field);
				} while (true);
			}
			dataset.addPattern(pattern);
		}
	}
	
	private void skipBlockLines(String line) throws IOException {
		for (; line != null && line.length() > 0 && line.charAt(0) == ' '; line = getReader().readLine());
	}
	
	protected void skipUntilEmptyOrCommentLine(String line) throws IOException {
		for (; line != null && line.length() > 0 && line.charAt(0) != '#'; line = getReader().readLine().trim());
	}

}
