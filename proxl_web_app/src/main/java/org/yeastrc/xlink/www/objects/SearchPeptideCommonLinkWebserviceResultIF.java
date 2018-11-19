package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * Common methods across the SearchPeptide...linkWebserviceResult classes
 *   for Crosslink, Looplink and Monolink
 */
public interface SearchPeptideCommonLinkWebserviceResultIF {
	

	public List<String> getPsmAnnotationValueList();

	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList);

	public List<String> getPeptideAnnotationValueList();

	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList);
	
}
