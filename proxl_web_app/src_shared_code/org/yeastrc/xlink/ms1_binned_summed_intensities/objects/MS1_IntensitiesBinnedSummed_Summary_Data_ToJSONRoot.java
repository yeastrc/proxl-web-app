package org.yeastrc.xlink.ms1_binned_summed_intensities.objects;

/**
 * 
 *
 */
public class MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot {

	private String jsonContents = " 'BinMax' props are max bin values.  bin values are 'floor' of actual values. "
			+ " Have 'MaxPossibleValue' props since MaxPossibleValue is BinMax + 1 " ;
	
	private long binnedSummedIntensityCount;
	private long rtBinMin;
	private long rtBinMax;
	private long rtMaxPossibleValue;
	
	private long mzBinMin;
	private long mzBinMax;
	private long mzMaxPossibleValue;

	private double intensityBinnedMin;
	private double intensityBinnedMax;

	public double getIntensityBinnedMin() {
		return intensityBinnedMin;
	}

	public void setIntensityBinnedMin(double intensityBinnedMin) {
		this.intensityBinnedMin = intensityBinnedMin;
	}

	public double getIntensityBinnedMax() {
		return intensityBinnedMax;
	}

	public void setIntensityBinnedMax(double intensityBinnedMax) {
		this.intensityBinnedMax = intensityBinnedMax;
	}

	public long getBinnedSummedIntensityCount() {
		return binnedSummedIntensityCount;
	}

	public void setBinnedSummedIntensityCount(long binnedSummedIntensityCount) {
		this.binnedSummedIntensityCount = binnedSummedIntensityCount;
	}

	public long getRtBinMin() {
		return rtBinMin;
	}

	public void setRtBinMin(long rtBinMin) {
		this.rtBinMin = rtBinMin;
	}

	public long getRtBinMax() {
		return rtBinMax;
	}

	public void setRtBinMax(long rtBinMax) {
		this.rtBinMax = rtBinMax;
	}

	public long getRtMaxPossibleValue() {
		return rtMaxPossibleValue;
	}

	public void setRtMaxPossibleValue(long rtMaxPossibleValue) {
		this.rtMaxPossibleValue = rtMaxPossibleValue;
	}

	public long getMzBinMin() {
		return mzBinMin;
	}

	public void setMzBinMin(long mzBinMin) {
		this.mzBinMin = mzBinMin;
	}

	public long getMzBinMax() {
		return mzBinMax;
	}

	public void setMzBinMax(long mzBinMax) {
		this.mzBinMax = mzBinMax;
	}

	public long getMzMaxPossibleValue() {
		return mzMaxPossibleValue;
	}

	public void setMzMaxPossibleValue(long mzMaxPossibleValue) {
		this.mzMaxPossibleValue = mzMaxPossibleValue;
	}

	public String getJsonContents() {
		return jsonContents;
	}

	public void setJsonContents(String jsonContents) {
		this.jsonContents = jsonContents;
	}

}
