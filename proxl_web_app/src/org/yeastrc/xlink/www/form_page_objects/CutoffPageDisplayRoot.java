package org.yeastrc.xlink.www.form_page_objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Display of Cutoff Data Root
 *
 */
public class CutoffPageDisplayRoot {

	private List<CutoffPageDisplaySearchLevel> perSearchDataList = new ArrayList<>();

	private boolean sorted = false;

	/**
	 * @param item
	 */
	public void addCutoffPageDisplaySearchLevel( CutoffPageDisplaySearchLevel item ) {
		this.perSearchDataList.add( item );
	}
	
	
	public List<CutoffPageDisplaySearchLevel> getPerSearchDataList() {
		
		if ( ! sorted ) {

			sorted = true;
			
			//  Sort on Search Id

			Collections.sort( perSearchDataList, new Comparator<CutoffPageDisplaySearchLevel>() {

				@Override
				public int compare(
						CutoffPageDisplaySearchLevel o1,
						CutoffPageDisplaySearchLevel o2) {

					//  Sort on Sort Order

					return o1.getProjectSearchId() - o2.getProjectSearchId();
				}
			});

		}
		
		return perSearchDataList;
	}

	public void setPerSearchDataList(
			List<CutoffPageDisplaySearchLevel> perSearchDataList) {
		this.perSearchDataList = perSearchDataList;
	}
	
}
