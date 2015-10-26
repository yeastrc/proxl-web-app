package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.SearchDTODetailsDisplayWrapper;

/**
 * This class is for putting data in the "request" scope for the search details
 *
 */
public class GetSearchDetailsData {

	private static final GetSearchDetailsData instance = new GetSearchDetailsData();
	
	private static final String SEARCHES_DETAILS_LIST_REQUEST_KEY = "searches_details_list";

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
		
		for ( SearchDTO search : searches ) {
		
			SearchDTODetailsDisplayWrapper searchDTODetailsDisplayWrapper = new SearchDTODetailsDisplayWrapper();
			
			searchDTODetailsDisplayWrapper.setSearchDTO(search);
			searchDTODetailsList.add(searchDTODetailsDisplayWrapper);
		}

		
		request.setAttribute( SEARCHES_DETAILS_LIST_REQUEST_KEY, searchDTODetailsList );

	}
}
