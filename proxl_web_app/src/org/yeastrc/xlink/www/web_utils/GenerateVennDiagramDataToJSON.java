package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.IMergedSearchLink;
import org.yeastrc.xlink.www.objects.VennDiagramDataToJSON;
import org.yeastrc.xlink.www.objects.VennDiagramDataToJSON.VennDiagramDataAreaEntry;
import org.yeastrc.xlink.www.objects.VennDiagramDataToJSON.VennDiagramDataSetEntry;

/**
 * 
 *
 */
public class GenerateVennDiagramDataToJSON {
	
//	private static final Logger log = Logger.getLogger(GenerateVennDiagramDataToJSON.class);
	
	

	/**
	 * Create the Venn Diagram for up to the first 3 searches
	 * 
	 * @param links
	 * @param searches
	 * @return null if not generating venn diagram data for provided parameters
	 */
	public static VennDiagramDataToJSON createVennDiagramDataToJSON( List<? extends IMergedSearchLink> links, List<SearchDTO> searches ) {
		

		if ( searches.size() > 3 ) { // Only create the VENN diagram for 3 or fewer searches

			return null;
		}
		
		//  searches are in order as displayed in the columns on the web page
		
		int countSearch1 = 0;
		int countSearch2 = 0;
		int countSearch3 = 0;

		int countSearch1Search2 = 0;
		int countSearch1Search3 = 0;
		int countSearch2Search3 = 0;

		int countSearch1Search2Search3 = 0;

		
		SearchDTO search1 = null;
		SearchDTO search2 = null;
		SearchDTO search3 = null;
				
		if ( searches.size() >= 1 ) {
			search1 = searches.get( 0 );
		}

		if ( searches.size() >= 2 ) {
			search2 = searches.get( 1 );
		}
		
		if ( searches.size() >= 3 ) {
			search3 = searches.get( 2 );
		}
		
		
		for( IMergedSearchLink link : links ) {
			
			Collection<SearchDTO> searchesForThisCrosslink = link.getSearches();

			boolean linkContainsSearch1 = false;
			boolean linkContainsSearch2 = false;
			boolean linkContainsSearch3 = false;

			//  process search 1
			if( search1 != null && searchesForThisCrosslink.contains( search1 ) ) {
				linkContainsSearch1 = true;
			}
			
			//  process search 2
			if( search2 != null && searchesForThisCrosslink.contains( search2 ) ) {
				linkContainsSearch2 = true;
			}

			//  process search 3
			if( search3 != null && searchesForThisCrosslink.contains( search3 ) ) {
				linkContainsSearch3 = true;
			}
			
			
			//  process search 1
			if( linkContainsSearch1 ) {
				countSearch1++;
			}
			
			//  process search 2
			if( linkContainsSearch2 ) {
				countSearch2++;
			}

			//  process search 3
			if( linkContainsSearch3 ) {
				countSearch3++;
			}

			//  process search 1 and search 2
			if( linkContainsSearch1 && linkContainsSearch2 ) {
				countSearch1Search2++;
			}

			//  process search 1 and search 3
			if( linkContainsSearch1 && linkContainsSearch3 ) {
				countSearch1Search3++;
			}
			
			//  process search 2 and search 3
			if( linkContainsSearch2 && linkContainsSearch3 ) {
				countSearch2Search3++;
			}
			
			
			//  process search 1 and search 2 and search 3
			if( linkContainsSearch1 && linkContainsSearch2 && linkContainsSearch3 ) {
				countSearch1Search2Search3++;
			}

		}
		
		//  Store individual counts
		
		List<VennDiagramDataSetEntry> setsList = new ArrayList<>();
		
		VennDiagramDataSetEntry setsEntry = null;
		
		// search 1;
		if ( search1 != null ) {
			setsEntry = new VennDiagramDataSetEntry();
			setsList.add(setsEntry);
//			setsEntry.setLabel( Integer.toString( search1.getId() ) );
			setsEntry.setLabel( "" );
			setsEntry.setSize( countSearch1 );
		}		
		
		// search 2;
		if ( search2 != null ) {
			setsEntry = new VennDiagramDataSetEntry();
			setsList.add(setsEntry);
//			setsEntry.setLabel( Integer.toString( search2.getId() ) );
			setsEntry.setLabel( "" );
			setsEntry.setSize( countSearch2 );
		}		
		
		// search 3;
		if ( search3 != null ) {
			setsEntry = new VennDiagramDataSetEntry();
			setsList.add(setsEntry);
//			setsEntry.setLabel( Integer.toString( search3.getId() ) );
			setsEntry.setLabel( "" );
			setsEntry.setSize( countSearch3 );
		}		
		
		/////////////
		
		//  process intersections
		
		List<VennDiagramDataAreaEntry> areasList = new ArrayList<>();
		
		VennDiagramDataAreaEntry areasEntry = null;
		
		if ( search2 != null ) {

			//  search 1 and 2
			areasEntry = new VennDiagramDataAreaEntry();
			areasList.add(areasEntry);
			
			areasEntry.setSize( countSearch1Search2 );
			List<Integer> areasEntrySetsList = new ArrayList<>(2);
			areasEntry.setSets( areasEntrySetsList );
			areasEntrySetsList.add( 0 ); // first entry in setsList
			areasEntrySetsList.add( 1 ); // second entry in setsList
		}
		
		if ( search3 != null ) {

			//  search 1 and 3
			areasEntry = new VennDiagramDataAreaEntry();
			areasList.add(areasEntry);
			
			areasEntry.setSize( countSearch1Search3 );
			List<Integer> areasEntrySetsList = new ArrayList<>(2);
			areasEntry.setSets( areasEntrySetsList );
			areasEntrySetsList.add( 0 ); // first entry in setsList
			areasEntrySetsList.add( 2 ); // third entry in setsList

			//  search 2 and 3
			areasEntry = new VennDiagramDataAreaEntry();
			areasList.add(areasEntry);
			
			areasEntry.setSize( countSearch2Search3 );
			areasEntrySetsList = new ArrayList<>(2);
			areasEntry.setSets( areasEntrySetsList );
			areasEntrySetsList.add( 1 ); // second entry in setsList
			areasEntrySetsList.add( 2 ); // third entry in setsList
			

			//  search 1 and 2 and 3
			areasEntry = new VennDiagramDataAreaEntry();
			areasList.add(areasEntry);
			
			areasEntry.setSize( countSearch1Search2Search3 );
			areasEntrySetsList = new ArrayList<>(3);
			areasEntry.setSets( areasEntrySetsList );
			areasEntrySetsList.add( 0 ); // first entry in setsList
			areasEntrySetsList.add( 1 ); // second entry in setsList
			areasEntrySetsList.add( 2 ); // third entry in setsList
		}
		
		
		
		VennDiagramDataToJSON vennDiagramDataToJSON = new VennDiagramDataToJSON();
		
		vennDiagramDataToJSON.setSets( setsList );
		vennDiagramDataToJSON.setAreas( areasList );
		
		
//		if ( log.isInfoEnabled() ) {
//
//			log.info("Computed values for createVennDiagramDataToJSON(...)");
//
//			if ( search1 != null ) {
//
//				log.info( "search1 id :" + search1.getId() );
//			}
//			
//			if ( search2 != null ) {
//
//				log.info( "search2 id :" + search2.getId() );
//			}
//			if ( search3 != null ) {
//
//				log.info( "search3 id :" + search3.getId() );
//			}
//			
//			log.info( "countSearch1 :" +  countSearch1);
//			log.info( "countSearch2 :" + countSearch2 );
//			log.info( "countSearch3 :" + countSearch3 );
//			log.info( "countSearch1Search2 :" + countSearch1Search2 );
//			log.info( "countSearch1Search3 :" +  countSearch1Search3);
//			log.info( "countSearch2Search3 :" + countSearch2Search3 );
//			log.info( "countSearch1Search2Search3 :" + countSearch1Search2Search3 );
//
//		
//		
//		
//		
//		}
		
		
		return vennDiagramDataToJSON;
	}
	
	
	
	
}
