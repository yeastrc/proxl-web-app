package org.yeastrc.xlink.www.internal_services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.auth.exceptions.AuthSharedObjectRecordNotFoundException;
import org.yeastrc.xlink.www.objects.UserDisplay;
import org.yeastrc.xlink.www.searcher.UserSearcherForSearchString;

/**
 * 
 *
 */
public class GetUserDisplayListForSearchQueryString {
	
	private static final Logger log = Logger.getLogger(GetUserDisplayListForSearchQueryString.class);
	

	//  private constructor
	private GetUserDisplayListForSearchQueryString() { }
	
	/**
	 * @return newly created instance
	 */
	public static GetUserDisplayListForSearchQueryString getInstance() { 
		return new GetUserDisplayListForSearchQueryString(); 
	}
	
	
	/**
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public List<UserDisplay> getUserDisplayListForSearchQueryString( String query ) throws AuthSharedObjectRecordNotFoundException, Exception {
		
		List<Integer> userIds = UserSearcherForSearchString.getInstance().getAuthUserIdForQuery( query );

		
		List<UserDisplay> returnList = new ArrayList<UserDisplay>( userIds.size() );

		
		GetUserDisplayDTOFromAuthUserId getUserDisplayDTOFromAuthUserId = GetUserDisplayDTOFromAuthUserId.getInstance();
		
		for ( int userId : userIds ) {
			
			try {
			
				UserDisplay userDisplay = getUserDisplayDTOFromAuthUserId.getUserDisplayDTOFromAuthUserId( userId );

				returnList.add( userDisplay );
				
			} catch ( AuthSharedObjectRecordNotFoundException e ) {
				
				String msg = "Unexpected AuthSharedObjectRecordNotFoundException for user id: " + userId + ", query: " + query;
				
				log.error( msg );
			}
		}
		

		return returnList;
	}
	
	
}
