package org.rexpd.data.powder;



public class PointPair {
	
	private DataPoint point1 = null;
	private DataPoint point2 = null;
	
	public PointPair(DataPoint p1, DataPoint p2) {
		point1 = p1;
		point2 = p2;
	}
	
	public DataPoint getPoint1() {
		return point1;
	}
	
	public DataPoint getPoint2() {
		return point2;
	}

}
