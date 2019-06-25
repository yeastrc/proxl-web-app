package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.constants.MinimumPSMsConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetCutoffPageDisplayRoot;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplayRoot;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplaySearchLevel;
import org.yeastrc.xlink.www.objects.SearchDTODetailsDisplayWrapper;
import org.yeastrc.xlink.www.project_level_default_cutoffs.ProjectLevelDefaultCutoffsAndOthers_Cache;
import org.yeastrc.xlink.www.project_level_default_cutoffs.ProjectLevelDefaultCutoffsAndOthers_Cache.ProjectLevelDefaultCutoffs_Cache_Result;

/**
 * This class is for putting data in the "request" scope for the minimum PSMs Default for minimumPSM_Count_Filter.jsp
 *
 */
public class GetMinimumPSMsDefaultForProject_PutInRequestScope {
	
//	private static final Logger log = LoggerFactory.getLogger(  GetMinimumPSMsDefaultForProject_PutInRequestScope.class );
	private static final GetMinimumPSMsDefaultForProject_PutInRequestScope instance = new GetMinimumPSMsDefaultForProject_PutInRequestScope();
	private GetMinimumPSMsDefaultForProject_PutInRequestScope() { }
	public static GetMinimumPSMsDefaultForProject_PutInRequestScope getSingletonInstance() { return instance; }
	
	/**
	 * @param projectId
	 * @param request
	 * @throws Exception
	 */
	public void getMinimumPSMsDefaultForProject_PutInRequestScope( int projectId, HttpServletRequest request ) throws Exception {
		
		ProjectLevelDefaultCutoffsAndOthers_Cache.ProjectLevelDefaultCutoffs_Cache_Result cacheResult =
				ProjectLevelDefaultCutoffsAndOthers_Cache.getSingletonInstance().getDefaultCutoffsAndOtherDefaults_ForProjectId( projectId );
		
		Integer minPsmsForProjectId = cacheResult.getMinPSMsForProjectId();
		if ( minPsmsForProjectId == null ) {
			minPsmsForProjectId = MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT;
		}
		
		request.setAttribute( "minimum_psm_count_default_value", minPsmsForProjectId );
	}
}
