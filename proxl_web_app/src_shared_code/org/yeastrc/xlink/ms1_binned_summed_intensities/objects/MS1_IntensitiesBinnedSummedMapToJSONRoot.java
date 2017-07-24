package org.yeastrc.xlink.ms1_binned_summed_intensities.objects;

import java.util.Map;

/**
 * 
 *
 */
public class MS1_IntensitiesBinnedSummedMapToJSONRoot {

	String jsonContents;
	MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot summaryData;
	Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap;
	
	public String getJsonContents() {
		return jsonContents;
	}
	public void setJsonContents(String jsonContents) {
		this.jsonContents = jsonContents;
	}
	public MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot getSummaryData() {
		return summaryData;
	}
	public void setSummaryData(MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot summaryData) {
		this.summaryData = summaryData;
	}
	public Map<Long, Map<Long, Double>> getMs1_IntensitiesBinnedSummedMap() {
		return ms1_IntensitiesBinnedSummedMap;
	}
	public void setMs1_IntensitiesBinnedSummedMap(Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap) {
		this.ms1_IntensitiesBinnedSummedMap = ms1_IntensitiesBinnedSummedMap;
	}
}
