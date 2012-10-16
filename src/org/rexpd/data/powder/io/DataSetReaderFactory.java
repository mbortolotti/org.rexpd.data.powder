package org.rexpd.data.powder.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.rexpd.data.powder.io.DataSetReader.Formats;

public abstract class DataSetReaderFactory {

	/** 
	 * Factory method used to create the DataSet reader instance
	 * 
	 * @param filepath path to read the file from
	 * @return the DataSet reader
	 * @throws IOException
	 * @throws UnsupportedFormatException 
	 */
	public static DataSetReader createReader(String filepath) throws IOException, UnsupportedFormatException {

		String extension = "";
		int lastdot = filepath.lastIndexOf(".");
		if (lastdot != -1)
			extension = filepath.substring(lastdot + 1);

		Reader reader = new FileReader(filepath);
		BufferedReader buffer = new BufferedReader(reader);
		buffer.mark(16777216);

		/* Search the file looking for a line containing an identifying keyword */

		String line = null;
		while ((line = buffer.readLine()) != null) {
			if ((line.indexOf("BANK") != -1) || (line.indexOf("TIME_MAP") != -1)) {
				System.out.println("DataSet reader factory: GSAS file");
				buffer.reset();
				return new DataSetReaderGSAS(buffer);
			}
			if ((line.toLowerCase().indexOf("<measure") != -1)) {
				buffer.reset();
				return new DataSetReaderTNX(buffer);
			}
		}

		buffer.reset();

		if (extension.toLowerCase().equals("xcd")) {
			System.out.println("DataSet reader factory: XCD file");
			throw new UnsupportedFormatException("XCD");
		}

		if (extension.toLowerCase().equals("raw")) {
			System.out.println("DataSet reader factory: RAW file");
			throw new UnsupportedFormatException("RAW");
		}

		if (extension.toLowerCase().equals("esg")) {
			System.out.println("DataSet reader factory: ESG file");
			throw new UnsupportedFormatException("ESG");
		}

		if (extension.toLowerCase().equals("rtv")) {
			System.out.println("DataSet reader factory: RTV file");
			throw new UnsupportedFormatException("RTV");
		}

		if (extension.toLowerCase().equals("sf")) {
			System.out.println("DataSet reader factory: structure factor list file");
			throw new UnsupportedFormatException("sf");
		}

		for (String ext : Formats.CIF.extensions)
			if (extension.toLowerCase().equals(ext)) {
				System.out.println("DataSet reader factory: CIF file");
				return new DataSetReaderCIF(buffer);
			}

		for (String ext : Formats.DAT.extensions)
			if (extension.toLowerCase().equals(ext)) {
				System.out.println("DataSet reader factory: DAT file");
				return new DataSetReaderDAT(buffer);
			}

		for (String ext : Formats.XY.extensions)
			if (extension.toLowerCase().equals(ext)) {
				System.out.println("DataSet reader factory: XY file");
				return new DataSetReaderXY(buffer);
			}

		for (String ext : Formats.XYZ.extensions)
			if (extension.toLowerCase().equals(ext)) {
				System.out.println("DataSet reader factory: XYE file");
				return new DataSetReaderXYE(buffer);
			}

		for (String ext : Formats.MDI.extensions)
			if (extension.toLowerCase().equals(ext)) {
				System.out.println("DataSet reader factory: MDI file");
				return new DataSetReaderMDI(buffer);
			}

		return null;
	}

}
