package org.yeastrc.xlink.www.web_utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;
import org.yeastrc.xlink.dto.SearchLinkerDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_Linker_CrossLinkMass_ForSearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SearchLinkerDTO_ForSearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.Linker_CrosslinkMass_ForSearchId_Response;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SearchLinkerDTO_ForSearchId_Response;

/**
 * Get SearchLinkerDTO or Linker Abbreviation (Part of SearchLinkerDTO) for a Linker Mass
 *
 */
public class SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util {
	
	private static final Logger log = LoggerFactory.getLogger(  SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util.class );
	
	private static final int DECIMAL_PLACE_ROUNDING_FOR_COMPARISON = 3;

	private SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util() { }
	
	List<SingleLinkerData> singleLinkerDataList;
	
	/**
	 * 
	 *
	 */
	public static class SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_Response {
		
		private SearchLinkerDTO searchLinkerDTO;
		private LinkerPerSearchCrosslinkMassDTO linkerPerSearchCrosslinkMassDTO;
		
		public SearchLinkerDTO getSearchLinkerDTO() {
			return searchLinkerDTO;
		}
		public LinkerPerSearchCrosslinkMassDTO getLinkerPerSearchCrosslinkMassDTO() {
			return linkerPerSearchCrosslinkMassDTO;
		}
	}
	
	/**
	 * @param searchId
	 * @return new instance for searchId
	 * @throws Exception 
	 */
	public static SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util getInstanceForSearchId( int searchId ) throws Exception { 
		
		SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util instance = new SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util();
		
		instance.init( searchId );
		
		return instance;
	}
	
	/**
	 * @param searchId
	 * @return
	 * @throws Exception 
	 */
	private void init( int searchId ) throws Exception {
		
		SearchLinkerDTO_ForSearchId_Response searchLinkerDTO_ForSearchId_Response = Cached_SearchLinkerDTO_ForSearchId.getInstance().getSearchLinkers_ForSearchId_Response( searchId );
		List<SearchLinkerDTO> searchLinkerDTOList = searchLinkerDTO_ForSearchId_Response.getSearchLinkerDTOList();
		if ( searchLinkerDTOList.isEmpty() ) {
			String msg = "init(...) No search_linker for search id: " + searchId;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}

		Linker_CrosslinkMass_ForSearchId_Response linker_CrosslinkMass_ForSearchId_Response =
				Cached_Linker_CrossLinkMass_ForSearchId.getInstance().getLinker_CrosslinkMass_ForSearchId_Response( searchId );
		List<LinkerPerSearchCrosslinkMassDTO> linkerPerSearchCrosslinkMassDTOList = linker_CrosslinkMass_ForSearchId_Response.getLinkerPerSearchCrosslinkMassDTOList();
		
		//  Build singleLinkerDataList
		
		singleLinkerDataList = new ArrayList<>( searchLinkerDTOList.size() );
		
		for ( SearchLinkerDTO searchLinkerDTO : searchLinkerDTOList ) {
			
			SingleLinkerData singleLinkerData = new SingleLinkerData();
			singleLinkerDataList.add( singleLinkerData );
			
			singleLinkerData.searchLinkerDTO = searchLinkerDTO;
			
			singleLinkerData.linkerMassDataList = new ArrayList<>( linkerPerSearchCrosslinkMassDTOList.size() );
			
			//  Put linkerPerSearchCrosslinkMassDTO in list for searchLinkerDTO.id
			
			for ( LinkerPerSearchCrosslinkMassDTO linkerPerSearchCrosslinkMassDTO : linkerPerSearchCrosslinkMassDTOList ) {
			
				if ( linkerPerSearchCrosslinkMassDTO.getSearchLinkerId() == searchLinkerDTO.getId() ) {
					//  Entry is for searchLinkerDTO.id being processed so save it
				
					BigDecimal linkerMassBD = new BigDecimal( linkerPerSearchCrosslinkMassDTO.getCrosslinkMassDouble() );
					BigDecimal LinkerMassRoundedBD = linkerMassBD.setScale( DECIMAL_PLACE_ROUNDING_FOR_COMPARISON, RoundingMode.HALF_UP );
					String linkerMassString = LinkerMassRoundedBD.toString();
					
					SingleCrosslinkerMassData singleCrosslinkerMassData = new SingleCrosslinkerMassData();
					singleCrosslinkerMassData.linkerPerSearchCrosslinkMassDTO = linkerPerSearchCrosslinkMassDTO;
					singleCrosslinkerMassData.linkerMassRounded = linkerMassString;
					
					singleLinkerData.linkerMassDataList.add( singleCrosslinkerMassData );
				}
			}
		}
		
	}
	
	/**
	 * @param linkerMass
	 * @return - Linker Abbreviation
	 */
	public String getLinkerAbbreviationForLinkerMass( BigDecimal linkerMass ) {
		
		SearchLinkerDTO searchLinkerDTO = getSearchLinkerDTOForLinkerMass( linkerMass );
		
		if ( searchLinkerDTO != null ) {
			
			return searchLinkerDTO.getLinkerAbbr();
		}
		
		return null;
	}
	
	/**
	 * @param linkerMass
	 * @return - SearchLinkerDTO object
	 */
	public SearchLinkerDTO getSearchLinkerDTOForLinkerMass( BigDecimal linkerMass ) {
		
		SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_Response response = get_SearchLinkerDTO_LinkerPerSearchCrosslinkMassDTO_ForLinkerMass( linkerMass );
		if ( response == null ) {
			return null;
		}
		return response.searchLinkerDTO;
	}


	/**
	 * @param linkerMass
	 * @return - SearchLinkerDTO object and LinkerPerSearchCrosslinkMassDTO object
	 */
	public SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_Response get_SearchLinkerDTO_LinkerPerSearchCrosslinkMassDTO_ForLinkerMass( BigDecimal linkerMass ) {
		
		BigDecimal LinkerMassRounded = linkerMass.setScale( DECIMAL_PLACE_ROUNDING_FOR_COMPARISON, RoundingMode.HALF_UP );
		String LinkerMassRoundedString = LinkerMassRounded.toString();
		
		for ( SingleLinkerData singleLinkerData : singleLinkerDataList ) {
			
			for ( SingleCrosslinkerMassData singleCrosslinkerMassData : singleLinkerData.linkerMassDataList ) {
				
				if ( LinkerMassRoundedString.equals( singleCrosslinkerMassData.linkerMassRounded ) ) {
					//  Linker mass provided matches the linker mass for the linker when both are rounded so return this linker abbreviation
					
					SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_Response response = new SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_Response();
					response.searchLinkerDTO = singleLinkerData.searchLinkerDTO;
					response.linkerPerSearchCrosslinkMassDTO = singleCrosslinkerMassData.linkerPerSearchCrosslinkMassDTO;
					
					return response; //  EARLY RETURN
				}
			}
		}
		
		return null; //  No Linker match found
	}
	
	/**
	 * Internal container class
	 *
	 */
	private static class SingleLinkerData {
		
		SearchLinkerDTO searchLinkerDTO;
		List<SingleCrosslinkerMassData> linkerMassDataList;
	}

	/**
	 * Internal container class
	 *
	 */
	private static class SingleCrosslinkerMassData {
		
		LinkerPerSearchCrosslinkMassDTO linkerPerSearchCrosslinkMassDTO;
		String linkerMassRounded;
	}
}
