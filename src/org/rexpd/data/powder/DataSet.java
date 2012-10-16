package org.rexpd.data.powder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rexpd.core.base.AbstractBase;
import org.rexpd.core.utils.DoublePair;



public class DataSet extends AbstractBase {

	public static final String DATASET_TAG = "dataset";

	public enum Events {
		DATA_CHANGED
	}

	public static enum Measurement {

		TWO_THETA {
			@Override
			public String getLabel() { return "2\u03B8"; }
		},

		TOF {
			@Override
			public String getLabel() { return "T.O.F."; }
		},

		CHANNEL {
			@Override
			public String getLabel() { return "Channel"; }
		},

		ENERGY {
			@Override
			public String getLabel() { return "Energy"; }
		};

		public abstract String getLabel();

	}

	private List<Pattern> patterns = null;
	private Pattern activePattern = null;

	private Measurement measurement = Measurement.TWO_THETA;

	public DataSet() {
		setType(DATASET_TAG);
		setLabel(DATASET_TAG);
		patterns = new ArrayList<Pattern>();
	}

	public void setActiveDomain(DoublePair domain) {
		for (Pattern pattern : patterns)
			pattern.setActiveDomain(domain);
	}

	public DoublePair getActiveDomain() {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (Pattern pattern : patterns) {
			DoublePair domain = pattern.getActiveDomain();
			if (domain.getMin() < min) min = domain.getMin();
			if (domain.getMax() > max) max = domain.getMax();
		}
		return new DoublePair(min, max);
	}

	@Override
	public void setEnabled(boolean enabled) {
		for (Pattern pattern : patterns)
			pattern.setEnabled(enabled);
	}

	@Override
	public boolean isEnabled() {
		for (Pattern pattern : patterns) {
			if (!(pattern.isEnabled()))
				return false;
		}
		return true;
	}

	public Measurement getMeasurement() {
		return measurement;
	}

	public Iterable<Pattern> patterns() {
		return Collections.unmodifiableCollection(patterns);
	}

	public void addPattern(Pattern pattern) {
		patterns.add(pattern);
		activePattern = pattern;
	}

	public void removePattern(Pattern pattern) {
		if (pattern == null)
			return;
		patterns.remove(pattern);
		if (patterns.size() > 0)
			activePattern = patterns.get(0);
	}

	public void removePattern(int pattern) {
		patterns.remove(pattern);
		if (patterns.size() > 0)
			activePattern = patterns.get(0);
	}

	public int getPatternNumber() {
		return patterns.size();
	}

	public Pattern getPattern(int position) {
		return patterns.get(position);
	}

	public Pattern getActivePattern() {
		return activePattern;
	}

	public int getActivePatternIndex() {
		return patterns.indexOf(activePattern);
	}

	public void setActivePattern(int active) {
		if ((active >= 0) && (active < patterns.size()))
			activePattern = patterns.get(active);
	}

	public void setActivePattern(Pattern active) {
		if (!(patterns.contains(active))) {
			patterns.add(active);
		}
		activePattern = active;
	}

	public void clear() {
		patterns.clear();	
	}

}
