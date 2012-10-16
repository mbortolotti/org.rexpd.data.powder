/*
 * Created on Jan 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rexpd.data.powder;

import org.rexpd.core.base.AbstractBase;


public class DataPoint extends AbstractBase implements Comparable<DataPoint>, Cloneable {
	
	public double omega;
	public double phi;
	public double psi;
	public double eta;
	
	private double xValue; 			/** generic x variable (2-theta, channel, TOF, energy...)	**/
	private double yValue; 			/** experimental intensity									**/
	private double yMod;			/** modified experimental intensity (filters etc)			**/
	private double yCalc; 			/** predicted intensity										**/
	private double bgValue;			/** optional background value 								**/
	
	public DataPoint(double x, double y, double predicted) {
		xValue = x;
		yValue = y;
		yCalc = predicted;
	}
	
	public DataPoint(double x, double y) {
		this(x, y, 0.0);
	}
	
	public double getXValue() {
		return xValue;
	}
	
	public void setXValue(double x) {
		xValue = x;
	}

	public double getYValue() {
		return yValue;
	}
	
	public void setYValue(double y) {
		yValue = y;
	}
	
	public double getYMod() {
		return yMod;
	}
	
	public void setYMod(double y) {
		yMod = y;
	}
	
	public double getYCalc() {
		return yCalc;
	}
	
	public void setYCalc(double y) {
		yCalc = y;
	}
	
	public double getBackground() {
		return bgValue;
	}
	
	public void setBackground(double bg) {
		bgValue = bg;
	}

	public int compareTo(DataPoint other) {
		return Double.compare(this.getXValue(), other.getXValue());
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		DataPoint cloned = new DataPoint(getXValue(), getYValue(), getYCalc());
		cloned.setEnabled(isEnabled());
		return cloned;
	}
		
	
}
