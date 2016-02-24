package org.yeastrc.xlink.www.qc_plots.psm_count_per_q_value;



/**
 * Root of JSON returned for Psm Count per Q value Chart
 *
 */
public class PsmCountPerQValueQCPlotDataJSONRoot {

	private PsmCountPerQValueQCPlotDataJSONPerType crosslinkData;
	private PsmCountPerQValueQCPlotDataJSONPerType looplinkData;
	private PsmCountPerQValueQCPlotDataJSONPerType unlinkedData;
	
	private PsmCountPerQValueQCPlotDataJSONPerType alllinkData;
	
	private int dataArraySize;
	
	

	public int getDataArraySize() {
		return dataArraySize;
	}

	public void setDataArraySize(int dataArraySize) {
		this.dataArraySize = dataArraySize;
	}

	public PsmCountPerQValueQCPlotDataJSONPerType getCrosslinkData() {
		return crosslinkData;
	}

	public void setCrosslinkData(
			PsmCountPerQValueQCPlotDataJSONPerType crosslinkData) {
		this.crosslinkData = crosslinkData;
	}

	public PsmCountPerQValueQCPlotDataJSONPerType getLooplinkData() {
		return looplinkData;
	}

	public void setLooplinkData(PsmCountPerQValueQCPlotDataJSONPerType looplinkData) {
		this.looplinkData = looplinkData;
	}

	public PsmCountPerQValueQCPlotDataJSONPerType getUnlinkedData() {
		return unlinkedData;
	}

	public void setUnlinkedData(PsmCountPerQValueQCPlotDataJSONPerType unlinkedData) {
		this.unlinkedData = unlinkedData;
	}

	public PsmCountPerQValueQCPlotDataJSONPerType getAlllinkData() {
		return alllinkData;
	}

	public void setAlllinkData(PsmCountPerQValueQCPlotDataJSONPerType alllinkData) {
		this.alllinkData = alllinkData;
	}
	
}
