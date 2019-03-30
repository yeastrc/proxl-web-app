package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetCutoffPageDisplayRoot;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplayRoot;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplaySearchLevel;
import org.yeastrc.xlink.www.objects.SearchDTODetailsDisplayWrapper;
/**
 * This class is for putting data in the "request" scope for the search details
 *
 */
public class GetSearchDetailsData {
	
//	private static final Logger log = LoggerFactory.getLogger(  GetSearchDetailsData.class );
	private static final GetSearchDetailsData instance = new GetSearchDetailsData();
	private GetSearchDetailsData() { }
	public static GetSearchDetailsData getInstance() { return instance; }
	
	/**
	 * @param search
	 * @param request
	 * @throws Exception
	 */
	public void getSearchDetailsData( SearchDTO search, HttpServletRequest request ) throws Exception {
		List<SearchDTO> searches = new ArrayList<>( 1 );
		searches.add(search);
		getSearchDetailsData( searches, request );
	}
	/**
	 * @param searches
	 * @param request
	 * @throws Exception
	 */
	public void getSearchDetailsData( List<SearchDTO> searches, HttpServletRequest request ) throws Exception {
		List<SearchDTODetailsDisplayWrapper> searchDTODetailsList = new ArrayList<>( searches.size() );
		StringBuilder searchIdListCommaDelimSB = new StringBuilder( 1000 );
		GetCutoffPageDisplayRoot getCutoffPageDisplayRoot = GetCutoffPageDisplayRoot.getInstance();
		for ( SearchDTO search : searches ) {
			int projectSearchId = search.getProjectSearchId();
			Integer searchId = search.getSearchId();
			if ( searchIdListCommaDelimSB.length() != 0 ) {
				searchIdListCommaDelimSB.append(",");
			}
			searchIdListCommaDelimSB.append( Integer.toString( projectSearchId ) );
			SearchDTODetailsDisplayWrapper searchDTODetailsDisplayWrapper = new SearchDTODetailsDisplayWrapper();
			searchDTODetailsDisplayWrapper.setSearchDTO(search);
			CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel = null;
			CutoffPageDisplayRoot cutoffPageDisplayRoot =
					getCutoffPageDisplayRoot.getCutoffPageDisplayRootSingleSearchId( projectSearchId, searchId, request );
			List<CutoffPageDisplaySearchLevel> cutoffsPerSearchDataList = cutoffPageDisplayRoot.getPerSearchDataList();
			if ( cutoffsPerSearchDataList == null || cutoffsPerSearchDataList.isEmpty() ) {
			} else {
				cutoffPageDisplaySearchLevel = cutoffsPerSearchDataList.get( 0 );
			}
			searchDTODetailsDisplayWrapper.setCutoffPageDisplaySearchLevel( cutoffPageDisplaySearchLevel );
			searchDTODetailsList.add(searchDTODetailsDisplayWrapper);
		}
		String searchIdListCommaDelim = searchIdListCommaDelimSB.toString();
		request.setAttribute( WebConstants.PARAMETER_SEARCH_ID_COMMA_DELIM_LIST_REQUEST_KEY, searchIdListCommaDelim );
		request.setAttribute( WebConstants.PARAMETER_SEARCHES_DETAILS_LIST_REQUEST_KEY, searchDTODetailsList );
	}
}
