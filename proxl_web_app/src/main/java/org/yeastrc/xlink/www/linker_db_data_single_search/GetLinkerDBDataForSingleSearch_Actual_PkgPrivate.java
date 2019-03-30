package org.yeastrc.xlink.www.linker_db_data_single_search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchMonolinkMassDTO;
import org.yeastrc.xlink.dto.SearchLinkerDTO;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.LinkersDBDataSingleSearchSingleLinker;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.LinkersDBDataSingleSearchRoot;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.SearchLinkerPerSideDefinitionObj;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.SearchLinkerPerSideLinkableProteinTerminiObj;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.searcher.LinkerPerSearchCleavedCrosslinkMass_Searcher;
import org.yeastrc.xlink.www.searcher.LinkerPerSearchMonolinkMass_Searcher;
import org.yeastrc.xlink.www.searcher.SearchLinkerPerSideLinkableProteinTerminiSearcher;
import org.yeastrc.xlink.www.searcher.SearchLinkerPerSideLinkableProteinTerminiSearcher.SearchLinkerPerSideLinkableProteinTerminiSearcher_ResponseItem;
import org.yeastrc.xlink.www.searcher.SearchLinkerPerSideLinkableResiduesSearcher;
import org.yeastrc.xlink.www.searcher.SearchLinkerPerSideLinkableResiduesSearcher.SearchLinkerPerSideLinkableResiduesSearcher_ResponseItem;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_Linker_CrossLinkMass_ForSearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SearchLinkerDTO_ForSearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.Linker_CrosslinkMass_ForSearchId_Response;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SearchLinkerDTO_ForSearchId_Response;

/**
 * Package Private Code
 * 
 * Get and assemble the Linker DB Data
 *
 */
class GetLinkerDBDataForSingleSearch_Actual_PkgPrivate {

	private static final Logger log = LoggerFactory.getLogger( GetLinkerDBDataForSingleSearch_Actual_PkgPrivate.class);

	/**
	 * Static singleton instance
	 */
	private static GetLinkerDBDataForSingleSearch_Actual_PkgPrivate _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	static synchronized GetLinkerDBDataForSingleSearch_Actual_PkgPrivate getInstance() throws Exception {
		if ( _instance == null ) {
			_instance = new GetLinkerDBDataForSingleSearch_Actual_PkgPrivate();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private GetLinkerDBDataForSingleSearch_Actual_PkgPrivate() {}
	
	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	LinkersDBDataSingleSearchRoot getLinkersDBDataSingleSearchRoot_ForSearchId( int searchId ) throws Exception {
		
		SearchLinkerDTO_ForSearchId_Response SearchLinkerDTO_ForSearchId_Response =
				Cached_SearchLinkerDTO_ForSearchId.getInstance().getSearchLinkers_ForSearchId_Response( searchId );
		List<SearchLinkerDTO> searchLinkerDTOList = SearchLinkerDTO_ForSearchId_Response.getSearchLinkerDTOList();
		if ( searchLinkerDTOList == null ) {
			throw new ProxlWebappInternalErrorException( "No Linker data (searchLinkerDTOList) for searchId: " + searchId );
		}
		
		Linker_CrosslinkMass_ForSearchId_Response linker_CrosslinkMass_ForSearchId_Response =
				Cached_Linker_CrossLinkMass_ForSearchId.getInstance().getLinker_CrosslinkMass_ForSearchId_Response( searchId );
		List<LinkerPerSearchCrosslinkMassDTO> linkerPerSearchCrosslinkMassDTOList = linker_CrosslinkMass_ForSearchId_Response.getLinkerPerSearchCrosslinkMassDTOList();
		
		List<LinkerPerSearchCleavedCrosslinkMassDTO> linkerPerSearchCleavedCrosslinkMassDTOList = LinkerPerSearchCleavedCrosslinkMass_Searcher.getInstance().getForSearchId( searchId );
		List<LinkerPerSearchMonolinkMassDTO> linkerPerSearchMonolinkMassDTOList = LinkerPerSearchMonolinkMass_Searcher.getInstance().getForSearchId( searchId );
		
		//   Not using LinkerPerSearchMonolinkMassDTO so currently do NOT load them
		
		//  Data Per Linker Side
		
		List<SearchLinkerPerSideLinkableResiduesSearcher_ResponseItem> perSideLinkableResidesList =
				SearchLinkerPerSideLinkableResiduesSearcher.getInstance().getPerSideLinkableResidesForSearch( searchId );
		
		List<SearchLinkerPerSideLinkableProteinTerminiSearcher_ResponseItem> perSideLinkableProteinTerminiList =
				SearchLinkerPerSideLinkableProteinTerminiSearcher.getInstance().getPerSideLinkableProteinTerminiForSearch( searchId );
		
		/////////////
		
		//  Build response
		
		LinkersDBDataSingleSearchRoot linkersDBDataSingleSearchRoot = new LinkersDBDataSingleSearchRoot();
		
		List<LinkersDBDataSingleSearchSingleLinker> linkersDBDataSingleSearchPerLinkerList = new ArrayList<>( searchLinkerDTOList.size() );
		linkersDBDataSingleSearchRoot.setLinkersDBDataSingleSearchPerLinkerList( linkersDBDataSingleSearchPerLinkerList );
		
		for ( SearchLinkerDTO searchLinkerDTO : searchLinkerDTOList ) {
			
			LinkersDBDataSingleSearchSingleLinker linkersDBDataSingleSearchPerLinker  = new LinkersDBDataSingleSearchSingleLinker();
			linkersDBDataSingleSearchPerLinkerList.add( linkersDBDataSingleSearchPerLinker );
			
			linkersDBDataSingleSearchPerLinker.setSearchLinkerDTO( searchLinkerDTO );

			//  Extract data for the other lists for this linker
			
			if ( searchLinkerDTOList.size() == 1 ) {
				//  Optimization for 1 linker since will usually be this.
				linkersDBDataSingleSearchPerLinker.setLinkerPerSearchCrosslinkMassDTOList( linkerPerSearchCrosslinkMassDTOList );
				linkersDBDataSingleSearchPerLinker.setLinkerPerSearchCleavedCrosslinkMassDTOList( linkerPerSearchCleavedCrosslinkMassDTOList );
				linkersDBDataSingleSearchPerLinker.setLinkerPerSearchMonolinkMassDTOList( linkerPerSearchMonolinkMassDTOList );
			} else {
				List<LinkerPerSearchCrosslinkMassDTO> this_linker_linkerPerSearchCrosslinkMassDTOList = new ArrayList<>( linkerPerSearchCrosslinkMassDTOList.size() );
				for ( LinkerPerSearchCrosslinkMassDTO item : linkerPerSearchCrosslinkMassDTOList ) {
					if ( item.getSearchLinkerId() == searchLinkerDTO.getId() ) {
						this_linker_linkerPerSearchCrosslinkMassDTOList.add( item );
					}
				}
				List<LinkerPerSearchCleavedCrosslinkMassDTO> this_linker_linkerPerSearchCleavedCrosslinkMassDTOList = new ArrayList<>( linkerPerSearchCleavedCrosslinkMassDTOList.size() );
				for ( LinkerPerSearchCleavedCrosslinkMassDTO item : linkerPerSearchCleavedCrosslinkMassDTOList ) {
					if ( item.getSearchLinkerId() == searchLinkerDTO.getId() ) {
						this_linker_linkerPerSearchCleavedCrosslinkMassDTOList.add( item );
					}
				}
				List<LinkerPerSearchMonolinkMassDTO> this_linker_linkerPerSearchMonolinkMassDTOList = new ArrayList<>( linkerPerSearchMonolinkMassDTOList.size() );
				for ( LinkerPerSearchMonolinkMassDTO item : linkerPerSearchMonolinkMassDTOList ) {
					if ( item.getSearchLinkerId() == searchLinkerDTO.getId() ) {
						this_linker_linkerPerSearchMonolinkMassDTOList.add( item );
					}
				}
				linkersDBDataSingleSearchPerLinker.setLinkerPerSearchCrosslinkMassDTOList( this_linker_linkerPerSearchCrosslinkMassDTOList );
				linkersDBDataSingleSearchPerLinker.setLinkerPerSearchCleavedCrosslinkMassDTOList( this_linker_linkerPerSearchCleavedCrosslinkMassDTOList );
				linkersDBDataSingleSearchPerLinker.setLinkerPerSearchMonolinkMassDTOList( this_linker_linkerPerSearchMonolinkMassDTOList );


			}
			
			//  Linker per side Linkable residues and protein termini
			
			List<SearchLinkerPerSideDefinitionObj> searchLinkerPerSideDefinitionObjList = new ArrayList<>( 2 );
			
			{
				Map<Integer, SearchLinkerPerSideDefinitionObj> searchLinkerPerSideDefinitionObjMap = new HashMap<>();

				if ( perSideLinkableResidesList != null ) {
					for ( SearchLinkerPerSideLinkableResiduesSearcher_ResponseItem item : perSideLinkableResidesList ) {
						if ( item.searchLinkerId == searchLinkerDTO.getId() ) {
							SearchLinkerPerSideDefinitionObj sideItem = searchLinkerPerSideDefinitionObjMap.get( item.sideId );
							if ( sideItem == null ) {
								sideItem = new SearchLinkerPerSideDefinitionObj();
								searchLinkerPerSideDefinitionObjMap.put( item.sideId, sideItem );
							}
							List<String> residues = sideItem.getResidues();
							if ( residues == null ) {
								residues = new ArrayList<>();
								sideItem.setResidues( residues );
							}
							residues.add( item.residue );
						}
					}
				}

				if ( perSideLinkableProteinTerminiList != null ) {
					for ( SearchLinkerPerSideLinkableProteinTerminiSearcher_ResponseItem item : perSideLinkableProteinTerminiList ) {
						if ( item.searchLinkerId == searchLinkerDTO.getId() ) {
							SearchLinkerPerSideDefinitionObj sideItem = searchLinkerPerSideDefinitionObjMap.get( item.sideId );
							if ( sideItem == null ) {
								sideItem = new SearchLinkerPerSideDefinitionObj();
								searchLinkerPerSideDefinitionObjMap.put( item.sideId, sideItem );
							}
							List<SearchLinkerPerSideLinkableProteinTerminiObj> proteinTerminiList = sideItem.getProteinTerminiList();
							if ( proteinTerminiList == null ) {
								proteinTerminiList = new ArrayList<>();
								sideItem.setProteinTerminiList( proteinTerminiList );
							}
							SearchLinkerPerSideLinkableProteinTerminiObj searchLinkerPerSideLinkableProteinTerminiObj = new SearchLinkerPerSideLinkableProteinTerminiObj();
							searchLinkerPerSideLinkableProteinTerminiObj.setProteinTerminus_c_n( item.n_terminus_c_terminus );
							searchLinkerPerSideLinkableProteinTerminiObj.setDistanceFromTerminus( item.distanceFromTerminus );
							proteinTerminiList.add( searchLinkerPerSideLinkableProteinTerminiObj );
						}
					}
				}

				//  Copy map to list
				
				for ( Map.Entry<Integer, SearchLinkerPerSideDefinitionObj> entry : searchLinkerPerSideDefinitionObjMap.entrySet() ) {
					searchLinkerPerSideDefinitionObjList.add( entry.getValue() );
				}
			}
			
			linkersDBDataSingleSearchPerLinker.setSearchLinkerPerSideDefinitionObjList( searchLinkerPerSideDefinitionObjList );
		}
		
		return linkersDBDataSingleSearchRoot;
	}
}
