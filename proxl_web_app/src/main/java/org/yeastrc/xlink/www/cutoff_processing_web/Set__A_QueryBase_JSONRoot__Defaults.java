package org.yeastrc.xlink.www.cutoff_processing_web;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.constants.MinimumPSMsConstants;
import org.yeastrc.xlink.www.form_query_json_objects.A_QueryBase_JSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.project_level_default_cutoffs.ProjectLevelDefaultCutoffsAndOthers_Cache;

/**
 * Set common defaults in object with subclass A_QueryBase_JSONRoot
 *
 */
public class Set__A_QueryBase_JSONRoot__Defaults {
	
	private static final Logger log = LoggerFactory.getLogger( Set__A_QueryBase_JSONRoot__Defaults.class );
	

	//  private constructor
	private Set__A_QueryBase_JSONRoot__Defaults() { }
	/**
	 * @return newly created instance
	 */
	public static Set__A_QueryBase_JSONRoot__Defaults getInstance() { 
		return new Set__A_QueryBase_JSONRoot__Defaults(); 
	}
	
	/**
	 * @param a_QueryBase_JSONRoot
	 */
	public void set__A_QueryBase_JSONRoot__Defaults( A_QueryBase_JSONRoot a_QueryBase_JSONRoot,
			int projectId,
			Collection<Integer> projectSearchIds,
			Collection<Integer> searchIds, 
			Map<Integer,Integer> mapProjectSearchIdToSearchId 
			) throws Exception {
		
		CutoffValuesRootLevel cutoffValuesRootLevel =
				GetDefaultPsmPeptideCutoffs.getInstance()
				.getDefaultPsmPeptideCutoffs( projectId, projectSearchIds, searchIds, mapProjectSearchIdToSearchId );
		a_QueryBase_JSONRoot.setCutoffs( cutoffValuesRootLevel );

		ProjectLevelDefaultCutoffsAndOthers_Cache.ProjectLevelDefaultCutoffs_Cache_Result cacheResult =
				ProjectLevelDefaultCutoffsAndOthers_Cache.getSingletonInstance().getDefaultCutoffsAndOtherDefaults_ForProjectId( projectId );
		
		Integer minPsmsForProjectId = cacheResult.getMinPSMsForProjectId();
		if ( minPsmsForProjectId == null ) {
			minPsmsForProjectId = MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT;
		}
		
		a_QueryBase_JSONRoot.setMinPSMs( minPsmsForProjectId );
	}
	
}
