package org.yeastrc.xlink.www.qc_plots.psm_score_vs_score;

import java.util.List;

import org.yeastrc.xlink.www.objects.PsmScoreVsScoreEntry;

/**
 * Results for CreatePsmScoreVsScoreQCPlotData
 *
 */
public class CreatePsmScoreVsScoreQCPlotDataResults {

	private List<PsmScoreVsScoreEntry> crosslinkChartData;
	private List<PsmScoreVsScoreEntry> looplinkChartData;
	private List<PsmScoreVsScoreEntry> unlinkedChartData;
	
	private Integer annotationTypeId_1;
	private String annotationTypeName_1;
	private String searchProgramName_1;
	
	private Integer annotationTypeId_2;
	private String annotationTypeName_2;
	private String searchProgramName_2;
	
	public List<PsmScoreVsScoreEntry> getCrosslinkChartData() {
		return crosslinkChartData;
	}
	public void setCrosslinkChartData(List<PsmScoreVsScoreEntry> crosslinkChartData) {
		this.crosslinkChartData = crosslinkChartData;
	}
	public List<PsmScoreVsScoreEntry> getLooplinkChartData() {
		return looplinkChartData;
	}
	public void setLooplinkChartData(List<PsmScoreVsScoreEntry> looplinkChartData) {
		this.looplinkChartData = looplinkChartData;
	}
	public List<PsmScoreVsScoreEntry> getUnlinkedChartData() {
		return unlinkedChartData;
	}
	public void setUnlinkedChartData(List<PsmScoreVsScoreEntry> unlinkedChartData) {
		this.unlinkedChartData = unlinkedChartData;
	}
	public Integer getAnnotationTypeId_1() {
		return annotationTypeId_1;
	}
	public void setAnnotationTypeId_1(Integer annotationTypeId_1) {
		this.annotationTypeId_1 = annotationTypeId_1;
	}
	public String getAnnotationTypeName_1() {
		return annotationTypeName_1;
	}
	public void setAnnotationTypeName_1(String annotationTypeName_1) {
		this.annotationTypeName_1 = annotationTypeName_1;
	}
	public String getSearchProgramName_1() {
		return searchProgramName_1;
	}
	public void setSearchProgramName_1(String searchProgramName_1) {
		this.searchProgramName_1 = searchProgramName_1;
	}
	public Integer getAnnotationTypeId_2() {
		return annotationTypeId_2;
	}
	public void setAnnotationTypeId_2(Integer annotationTypeId_2) {
		this.annotationTypeId_2 = annotationTypeId_2;
	}
	public String getAnnotationTypeName_2() {
		return annotationTypeName_2;
	}
	public void setAnnotationTypeName_2(String annotationTypeName_2) {
		this.annotationTypeName_2 = annotationTypeName_2;
	}
	public String getSearchProgramName_2() {
		return searchProgramName_2;
	}
	public void setSearchProgramName_2(String searchProgramName_2) {
		this.searchProgramName_2 = searchProgramName_2;
	}
}
