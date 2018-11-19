package org.yeastrc.xlink.www.protein_coverage;

public class CoverageRangeBean {

	public CoverageRangeBean( int startPosition, int endPosition ) {
		this.start = startPosition;
		this.end = endPosition;
	}
	
	public CoverageRangeBean() { }
	
	private int start;
	private int end;
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
	
}
