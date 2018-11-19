package org.yeastrc.xlink.www.objects;

import java.util.Map;

public class AnnotationTypesMinMaxValuesForAnnTypeIdsSearchIdServiceResult {

	Map<Integer, AnnotationTypesMinMaxValuesEntry> minMaxValuesPerAnnType;
	
	
	public Map<Integer, AnnotationTypesMinMaxValuesEntry> getMinMaxValuesPerAnnType() {
		return minMaxValuesPerAnnType;
	}


	public void setMinMaxValuesPerAnnType(
			Map<Integer, AnnotationTypesMinMaxValuesEntry> minMaxValuesPerAnnType) {
		this.minMaxValuesPerAnnType = minMaxValuesPerAnnType;
	}


	public static class AnnotationTypesMinMaxValuesEntry {
		
		private double minValue;
		private double maxValue;
		
		
		public double getMinValue() {
			return minValue;
		}
		public void setMinValue(double minValue) {
			this.minValue = minValue;
		}
		public double getMaxValue() {
			return maxValue;
		}
		public void setMaxValue(double maxValue) {
			this.maxValue = maxValue;
		}
		
	}
}
