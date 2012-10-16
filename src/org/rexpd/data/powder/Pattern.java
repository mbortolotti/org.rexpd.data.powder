package org.rexpd.data.powder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.rexpd.core.base.AbstractBase;
import org.rexpd.core.utils.DoublePair;
import org.rexpd.structure.structure.StructureFactorList;

public class Pattern extends AbstractBase {

	public static final String PATTERN_TAG = "pattern";

	private List<DataPoint> dataPoints = null;

	// TODO remove dependency from org.rexpd.structure package
	private List<StructureFactorList> structureFactorLists = null;

	public Pattern() {
		setType(PATTERN_TAG);
		dataPoints = new ArrayList<DataPoint>();
		structureFactorLists = new ArrayList<StructureFactorList>();
	}

	public Pattern(Pattern original) {
		this();
		setLabel(original.getLabel());
		try {
			for (DataPoint point : original.getPointList())
				addPoint((DataPoint) point.clone());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setActiveDomain(original.getActiveDomain());
	}

	public Pattern getModifiedPattern() {
		Pattern modified = new Pattern();
		modified.setLabel(getLabel());
		for (DataPoint point : getPointList()) {
			DataPoint p = new DataPoint(point.getXValue(), point.getYMod());
			modified.addPoint(p);
		}
		modified.setActiveDomain(getActiveDomain());
		return modified;
	}

	public void applyModifications() {
		for (DataPoint point : getActivePointList()) {
			point.setYValue(point.getYMod());
			point.setYMod(0.0);
		}
	}

	public void addPoint(DataPoint point) {
		dataPoints.add(point);
	}

	public List<DataPoint> getPointList() {
		return dataPoints;
	}

	public List<DataPoint> getActivePointList() {
		List<DataPoint> activePoints = new ArrayList<DataPoint>();
		for (DataPoint point : getPointList()) {
			if (point.isEnabled())
				activePoints.add(point);
		}
		return activePoints;
	}

	public int getClosestIndex(DataPoint point) {
		int position = Collections.binarySearch(dataPoints, point);
		position = position >= 0 ? position : Math.abs(position) - 1;
		position = position < dataPoints.size() ? position : dataPoints.size() - 1;
		return position;
	}

	public DataPoint getClosestPoint(DataPoint point) {
		return dataPoints.get(getClosestIndex(point));
	}

	public DataPoint getClosestPeakPoint(DataPoint point, int xRange) {
		int index = getClosestIndex(point);
		int x_min = (index - xRange) >= 0 ? (index - xRange) : 0;
		int x_max = (index + xRange) < dataPoints.size() ? (index + xRange) : dataPoints.size() - 1;
		double IMax = dataPoints.get(x_min).getYValue();
		int peakPos = x_min;
		for (int np = x_min; np < x_max; np++) {
			if (dataPoints.get(np).getYValue() > IMax) {
				IMax = dataPoints.get(np).getYValue();
				peakPos = np;
			}
		}
		return dataPoints.get(peakPos);
	}

	public void sortDomain() {
		Collections.sort(dataPoints);
	}

	public double getStepSize() {
		int npoints = dataPoints.size();
		if (npoints == 0) 
			return 0.0;
		double min_x = dataPoints.get(0).getXValue();
		double max_x = dataPoints.get(dataPoints.size() - 1).getXValue();
		return Math.abs(max_x - min_x) / npoints;
	}

	/**
	 * Returns the full domain of the pattern.
	 * Assumes the pattern is already sorted with respect to x.
	 * 
	 * @return DoubleRange range containing the two x values corresponding to 
	 * the first and the last points in pattern
	 */
	public DoublePair getDomain() {
		PointPair domain = getPointDomain();
		return new DoublePair(domain.getPoint1().getXValue(), domain.getPoint2().getXValue());
	}

	/**
	 * Returns the first and the last point in the pattern with respect to x.
	 * Assumes the pattern is already sorted with respect to the x variable.
	 * 
	 * @return PointRange range containing the two first and the last point in the pattern
	 */
	public PointPair getPointDomain() {
		DataPoint min = dataPoints.get(0);
		DataPoint max = dataPoints.get(dataPoints.size() - 1);
		return new PointPair(min, max);
	}

	/**
	 * Returns the active domain of the pattern.
	 * Assumes the pattern is already sorted with respect to x.
	 * 
	 * @return DoubleRange range containing the two x values corresponding to 
	 * the first and the last active points in pattern
	 */
	public DoublePair getActiveDomain() {
		PointPair domain = getActivePointDomain();
		return new DoublePair(domain.getPoint1().getXValue(), domain.getPoint2().getXValue());
	}

	/**
	 * Set the active domain for the pattern.
	 * Assumes the pattern is already sorted with respect to x.
	 * 
	 * @param domain the minimum and maximum x values of the domain
	 */
	public void setActiveDomain(DoublePair domain) {
		double x_min = domain.getMin();
		double x_max = domain.getMax();
		if ((x_min < 0) || (x_max <= 0) || (x_min >= x_max))
			return;
		for (DataPoint point : getPointList())
			point.setEnabled(point.getXValue() >= x_min && point.getXValue() <= x_max);
	}

	/**
	 * Returns the first and the last active points in the pattern with respect to x.
	 * Assumes the pattern is already sorted with respect to the x variable.
	 * 
	 * @return PointRange range containing the two data points corresponding to
	 * the minimum and maximum active x values
	 */
	public PointPair getActivePointDomain() {
		ListIterator<DataPoint> it_min = firstActivePoint();
		ListIterator<DataPoint> it_max = lastActivePoint();
		if (!it_min.hasNext() || !it_max.hasPrevious())
			return null;
		return new PointPair(it_min.next(), it_max.previous());
	}

	/**
	 * Finds the point pair corresponding to the minimum and maximum y experimental 
	 * values in the data pattern
	 * 
	 * @return PointRange range containing the two active data points corresponding to
	 * the minimum and maximum experimental y values
	 */
	public PointPair getPointRangeYExp() {
		ListIterator<DataPoint> it_min = firstActivePoint();
		ListIterator<DataPoint> it_max = lastActivePoint();
		if (!it_min.hasNext() || !it_max.hasPrevious())
			return null;
		DataPoint min = it_min.next();
		DataPoint max = it_max.previous();
		while (it_min.nextIndex() <= it_max.previousIndex()) {
			DataPoint point = it_min.next();
			if (point.getYValue() < min.getYValue())
				min = point;
			if (point.getYValue() > max.getYValue())
				max = point;
		}
		return new PointPair(min, max);
	}

	/**
	 * Returns the active experimental range of the pattern.
	 * 
	 * @return DoubleRange range containing the two minimum 
	 * and maximum y experimental values in the pattern
	 */
	public DoublePair getActiveRangeExp() {
		PointPair rangeExp = getPointRangeYExp();
		return new DoublePair(rangeExp.getPoint1().getYValue(), rangeExp.getPoint2().getYValue());
	}

	/**
	 * Finds the point pair corresponding to the minimum and maximum y calculated 
	 * values in the data pattern
	 * 
	 * @return PointRange range containing the two active data points corresponding to
	 * the minimum and maximum calculated y values
	 */
	public PointPair getPointRangeYCalc() {
		ListIterator<DataPoint> it_min = firstActivePoint();
		ListIterator<DataPoint> it_max = lastActivePoint();
		if (!it_min.hasNext() || !it_max.hasPrevious())
			return null;
		DataPoint min = it_min.next();
		DataPoint max = it_max.previous();
		while (it_min.nextIndex() <= it_max.previousIndex()) {
			DataPoint point = it_min.next();
			if (point.getYCalc() < min.getYCalc())
				min = point;
			if (point.getYCalc() > max.getYCalc())
				max = point;
		}
		return new PointPair(min, max);
	}

	/**
	 * Returns the active calculated range of the pattern.
	 * 
	 * @return DoubleRange range containing the two minimum 
	 * and maximum y calculated values in the pattern
	 */
	public DoublePair getActiveRangeCalc() {
		PointPair rangeCalc = getPointRangeYCalc();
		return new DoublePair(rangeCalc.getPoint1().getYCalc(), rangeCalc.getPoint2().getYCalc());
	}

	private ListIterator<DataPoint> firstActivePoint() {
		ListIterator<DataPoint> it_first = dataPoints.listIterator();
		if (!it_first.hasNext())
			return null;
		while (it_first.hasNext() && !it_first.next().isEnabled());
		it_first.previous();
		return it_first;
	}

	private ListIterator<DataPoint> lastActivePoint() {
		ListIterator<DataPoint> it_last = dataPoints.listIterator(dataPoints.size());
		while (it_last.hasPrevious() && !it_last.previous().isEnabled());
		it_last.next();
		return it_last;
	}

	public double getYExpMax() {
		return (Collections.max(dataPoints, new PointComparatorYExp())).getYValue();
	}

	public double getYExpMin() {
		return (Collections.min(dataPoints, new PointComparatorYExp())).getYValue();
	}

	public double getYCalcMax() {
		return (Collections.max(dataPoints, new PointComparatorYCalc())).getYCalc();
	}

	public double getYCalcMin() {
		return (Collections.min(dataPoints, new PointComparatorYCalc())).getYCalc();
	}

	public double getYExpIntegrated(boolean subtractMin) {
		double yMin = subtractMin ? getYExpMin() : 0.0;
		double yInt = 0.0;
		for (DataPoint point : getPointList()) {
			yInt += point.getYValue() - yMin;
		}
		return yInt;
	}

	public void addStructureFactorList(StructureFactorList list) {
		structureFactorLists.add(list);
	}

	public List<StructureFactorList> getStructureFactorsList() {
		return structureFactorLists;
	}

	public void resetCalcData() {
		for (DataPoint point : dataPoints) {
			point.setYCalc(0.0);
		}
		structureFactorLists.clear();
	}

}

class PointComparatorRSVar implements Comparator<DataPoint> {

	public int compare(DataPoint o1, DataPoint o2) {
		double x1 = o1.getXValue();
		double x2 = o2.getXValue();
		return Double.compare(x1, x2);
	}

}

class PointComparatorYExp implements Comparator<DataPoint> {

	public int compare(DataPoint o1, DataPoint o2) {
		double y1 = o1.getYValue();
		double y2 = o2.getYValue();
		return Double.compare(y1, y2);
	}

}

class PointComparatorYCalc implements Comparator<DataPoint> {

	public int compare(DataPoint o1, DataPoint o2) {
		double y1 = o1.getYCalc();
		double y2 = o2.getYCalc();
		return Double.compare(y1, y2);
	}

}
