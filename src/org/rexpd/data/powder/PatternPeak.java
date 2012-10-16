package org.rexpd.data.powder;

public class PatternPeak {
	
	private double position = 0.0;
	private double intensity = 0.0;
	
	public PatternPeak(double pos, double intens) {
		position = pos;
		intensity = intens;
	}
	
	public double getPosition() {
		return position;
	}
	
	public void setPosition(double position) {
		this.position = position;
	}
	
	public double getIntensity() {
		return intensity;
	}
	
	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

}
