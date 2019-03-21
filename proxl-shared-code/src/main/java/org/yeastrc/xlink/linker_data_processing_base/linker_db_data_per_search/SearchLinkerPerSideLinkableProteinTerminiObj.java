package org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search;

import org.yeastrc.xlink.enum_classes.SearchLinkerProteinTerminusType;

public class SearchLinkerPerSideLinkableProteinTerminiObj {

	private SearchLinkerProteinTerminusType proteinTerminus_c_n;
	private int distanceFromTerminus; // 0 indicates at the terminus
	
	public int getDistanceFromTerminus() {
		return distanceFromTerminus;
	}
	public void setDistanceFromTerminus(int distanceFromTerminus) {
		this.distanceFromTerminus = distanceFromTerminus;
	}
	public SearchLinkerProteinTerminusType getProteinTerminus_c_n() {
		return proteinTerminus_c_n;
	}
	public void setProteinTerminus_c_n(SearchLinkerProteinTerminusType proteinTerminus_c_n) {
		this.proteinTerminus_c_n = proteinTerminus_c_n;
	}
}
