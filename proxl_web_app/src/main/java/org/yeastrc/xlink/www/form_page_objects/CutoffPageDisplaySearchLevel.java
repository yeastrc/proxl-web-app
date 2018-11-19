package org.yeastrc.xlink.www.form_page_objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import org.apache.log4j.Logger;

/**
 * Display of cutoff data per Search
 *
 */
public class CutoffPageDisplaySearchLevel {
	
//	private static final Logger log = Logger.getLogger( CutoffPageDisplaySearchLevel.class );


	private int projectSearchId;
	
	private List<CutoffPageDisplayAnnotationLevel> psmAnnotationCutoffData = new ArrayList<>();
	private List<CutoffPageDisplayAnnotationLevel> peptideAnnotationCutoffData = new ArrayList<>();
	
	private boolean sortedPsmAnnotations;
	private boolean sortedPeptideAnnotations;
	
	
	
	/**
	 * @param item
	 */
	public void addPsmCutoffPageDisplayAnnotationLevel( CutoffPageDisplayAnnotationLevel item ) {
		this.psmAnnotationCutoffData.add( item );
	}
	
	/**
	 * @param item
	 */
	public void addPeptideCutoffPageDisplayAnnotationLevel( CutoffPageDisplayAnnotationLevel item ) {
		this.peptideAnnotationCutoffData.add( item );
	}

	
	
	
	public List<CutoffPageDisplayAnnotationLevel> getPsmAnnotationCutoffData() {
		

		if ( ! sortedPsmAnnotations ) {

			sortedPsmAnnotations = true;
			
			//  Sort on Search Id

			Collections.sort( psmAnnotationCutoffData, new Comparator<CutoffPageDisplayAnnotationLevel>() {

				@Override
				public int compare(
						CutoffPageDisplayAnnotationLevel o1,
						CutoffPageDisplayAnnotationLevel o2) {
					
					//  Sort on annotation name

					return o1.getAnnotationName().compareTo( o2.getAnnotationName() );
				}
			});

		}
		
		return psmAnnotationCutoffData;
	}
	
	
	public void setPsmAnnotationCutoffData(
			List<CutoffPageDisplayAnnotationLevel> psmAnnotationCutoffData) {
		this.psmAnnotationCutoffData = psmAnnotationCutoffData;
	}
	
	
	public List<CutoffPageDisplayAnnotationLevel> getPeptideAnnotationCutoffData() {
		

		if ( ! sortedPeptideAnnotations ) {

			sortedPeptideAnnotations = true;
			
			//  Sort on Search Id

			Collections.sort( peptideAnnotationCutoffData, new Comparator<CutoffPageDisplayAnnotationLevel>() {

				@Override
				public int compare(
						CutoffPageDisplayAnnotationLevel o1,
						CutoffPageDisplayAnnotationLevel o2) {
					
					//  Sort on annotation name

					return o1.getAnnotationName().compareTo( o2.getAnnotationName() );
				}
			});

		}
		
		return peptideAnnotationCutoffData;
	}
	public void setPeptideAnnotationCutoffData(
			List<CutoffPageDisplayAnnotationLevel> peptideAnnotationCutoffData) {
		this.peptideAnnotationCutoffData = peptideAnnotationCutoffData;
	}

	public int getProjectSearchId() {
		return projectSearchId;
	}

	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}
}
