package org.yeastrc.xlink.www.objects;

import java.util.List;
import java.util.Map;


/**
 * 
 *
 */
public class AnnotationDisplayUserSelectionDetailsData {

	private List<AnnotationDisplayUserSelectionDetailsPerSearch> annotationDisplayUserSelectionDetailsPerSearchList;
	
	private DefaultAnnTypeIdDisplay defaultAnnTypeIdDisplay;
	private String defaultAnnTypeIdDisplayJSONString;
	

	/**
	 * 
	 *
	 */
	public static class DefaultAnnTypeIdDisplay {

		private Map<Integer, DefaultAnnTypesPerSearch> searches;

		public Map<Integer, DefaultAnnTypesPerSearch> getSearches() {
			return searches;
		}

		public void setSearches(Map<Integer, DefaultAnnTypesPerSearch> searches) {
			this.searches = searches;
		}
	}
	
	/**
	 * 
	 *
	 */
	public static class DefaultAnnTypesPerSearch {

		List<Integer> psmDefaultAnnotationTypeIdList;
		List<Integer> peptideDefaultAnnotationTypeIdList;
		
		
		public List<Integer> getPsm() {
			return psmDefaultAnnotationTypeIdList;
		}
		public void setPsmDefaultAnnotationTypeIdList(
				List<Integer> psmDefaultAnnotationTypeIdList) {
			this.psmDefaultAnnotationTypeIdList = psmDefaultAnnotationTypeIdList;
		}
		public List<Integer> getPeptide() {
			return peptideDefaultAnnotationTypeIdList;
		}
		public void setPeptideDefaultAnnotationTypeIdList(
				List<Integer> peptideDefaultAnnotationTypeIdList) {
			this.peptideDefaultAnnotationTypeIdList = peptideDefaultAnnotationTypeIdList;
		}
		
	}


	public List<AnnotationDisplayUserSelectionDetailsPerSearch> getAnnotationDisplayUserSelectionDetailsPerSearchList() {
		return annotationDisplayUserSelectionDetailsPerSearchList;
	}

	public void setAnnotationDisplayUserSelectionDetailsPerSearchList(
			List<AnnotationDisplayUserSelectionDetailsPerSearch> annotationDisplayUserSelectionDetailsPerSearchList) {
		this.annotationDisplayUserSelectionDetailsPerSearchList = annotationDisplayUserSelectionDetailsPerSearchList;
	}

	public DefaultAnnTypeIdDisplay getDefaultAnnTypeIdDisplay() {
		return defaultAnnTypeIdDisplay;
	}

	public void setDefaultAnnTypeIdDisplay(
			DefaultAnnTypeIdDisplay defaultAnnTypeIdDisplay) {
		this.defaultAnnTypeIdDisplay = defaultAnnTypeIdDisplay;
	}

	public String getDefaultAnnTypeIdDisplayJSONString() {
		return defaultAnnTypeIdDisplayJSONString;
	}

	public void setDefaultAnnTypeIdDisplayJSONString(
			String defaultAnnTypeIdDisplayJSONString) {
		this.defaultAnnTypeIdDisplayJSONString = defaultAnnTypeIdDisplayJSONString;
	}


}
