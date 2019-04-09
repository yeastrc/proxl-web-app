package org.yeastrc.xlink.www.download_data_utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.actions.ProteinsMergedCommonPageDownload.ProteinsMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * For downloading proteins/links/UDR from Image Page
 * 
 * The Javascript on the page will create a JSON of the selected/highlighted links.
 * 
 * This code will deserialize that JSON and filter the 
 *
 */
public class FilterProteinsOnSelectedLinks {
	
	private static final Logger log = LoggerFactory.getLogger( FilterProteinsOnSelectedLinks.class );
	
	private FilterProteinsOnSelectedLinks() { }
	public static FilterProteinsOnSelectedLinks getInstance() { return new FilterProteinsOnSelectedLinks(); }
	
	/**
	 * @param proteinsMergedCommonPageDownloadResult
	 * @param selectedCrosslinksLooplinksMonolinksJSON
	 * @return
	 * @throws Exception
	 */
	public ProteinsMergedCommonPageDownloadResult filterProteinsOnSelectedLinks( 
			ProteinsMergedCommonPageDownloadResult proteinsMergedCommonPageDownloadResult,
			String selectedCrosslinksLooplinksMonolinksJSON ) throws Exception {
		
		if ( StringUtils.isEmpty( selectedCrosslinksLooplinksMonolinksJSON ) ) {
			return proteinsMergedCommonPageDownloadResult; // EARLY RETURN
		}

		SelectedCrosslinksLooplinksMonolinksRoot selectedCrosslinksLooplinksMonolinksRoot = null;
		try {
			selectedCrosslinksLooplinksMonolinksRoot =
					get_selectedCrosslinksLooplinksMonolinks_From_JSON( selectedCrosslinksLooplinksMonolinksJSON );
		} catch ( Exception e ) {
			String msg = "parse request failed";
			log.warn( msg );
			throw e;
		}
		
		List<MergedSearchProteinCrosslink> crosslinksExistingList = proteinsMergedCommonPageDownloadResult.getCrosslinks();
	
		if ( crosslinksExistingList != null && ( ! crosslinksExistingList.isEmpty() ) ) {

			List<MergedSearchProteinCrosslink> crosslinks_New_List = new ArrayList<>( crosslinksExistingList.size() );
			proteinsMergedCommonPageDownloadResult.setCrosslinks( crosslinks_New_List );  // Update main object with new list

			// Map<From Protein Id, Map<To Protein Id, Map<From Position, Set<To Position>>>>
			Map<Integer,Map<Integer,Map<Integer,Set<Integer>>>> selectedCrosslinksInMaps = getSelectedCrosslinksInMaps( selectedCrosslinksLooplinksMonolinksRoot );

			if ( selectedCrosslinksInMaps != null && ( ! selectedCrosslinksInMaps.isEmpty() ) ) {

				for ( MergedSearchProteinCrosslink crosslink : crosslinksExistingList ) {

					Integer fromProtId = crosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
					Integer fromPos = crosslink.getProtein1Position();
					Integer toProtId = crosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
					Integer toPos = crosslink.getProtein2Position();
					
					if ( fromProtId > toProtId ) {
						//  Order so fromProtId <= toProtId
						fromProtId = crosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						fromPos = crosslink.getProtein2Position();
						toProtId = crosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						toPos = crosslink.getProtein1Position();
					} else if ( fromProtId.intValue() == toProtId.intValue() && fromPos >  toPos ) {
						//  Order so fromProtId == toProtId && fromPos <=  toPos) {
						fromPos = crosslink.getProtein2Position();
						toPos = crosslink.getProtein1Position();
					}

					// Map<From Protein Id, Map<To Protein Id, Map<From Position, Set<To Position>>>>
					Map<Integer,Map<Integer,Set<Integer>>> mapKeyToProteinId = selectedCrosslinksInMaps.get( fromProtId );
					if ( mapKeyToProteinId != null ) {
						Map<Integer,Set<Integer>> mapKeyFromPosition = mapKeyToProteinId.get( toProtId );
						if ( mapKeyFromPosition != null ) {
							Set<Integer> toPosition_Entries = mapKeyFromPosition.get( fromPos );
							if ( toPosition_Entries != null ) {
								if ( toPosition_Entries.contains( toPos ) ) {
									//  Found entry in the Selection Proteins so add to New/Output List
									crosslinks_New_List.add( crosslink );
								}
							}
						}
					}
				}
			}
		}
		
		List<MergedSearchProteinLooplink> looplinksExistingList = proteinsMergedCommonPageDownloadResult.getLooplinks();

		if ( looplinksExistingList != null && ( ! looplinksExistingList.isEmpty() ) ) {

			List<MergedSearchProteinLooplink> looplinks_New_List = new ArrayList<>( looplinksExistingList.size() );
			proteinsMergedCommonPageDownloadResult.setLooplinks( looplinks_New_List );  // Update main object with new list

			// Map<Protein Id, Map<Position 1, Set<Set<Position 2>>>>>
			Map<Integer,Map<Integer,Set<Integer>>> selectedLooplinksInMaps = getSelectedLooplinksInMaps( selectedCrosslinksLooplinksMonolinksRoot );

			if ( selectedLooplinksInMaps != null && ( ! selectedLooplinksInMaps.isEmpty() ) ) {

				for ( MergedSearchProteinLooplink looplink : looplinksExistingList ) {

					Integer protId = looplink.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();;
					Integer pos1 = looplink.getProteinPosition1();
					Integer pos2 = looplink.getProteinPosition2();
					if ( pos1 >  pos2 ) {
						//  Order so pos1 <=  pos2 
						pos1 = looplink.getProteinPosition2();
						pos2 = looplink.getProteinPosition1();
					}

					// Map<Protein Id, Map<Position 1, Set<Position 2>>>>
					Map<Integer,Set<Integer>> mapKey_Position_1 = selectedLooplinksInMaps.get( protId );
					if ( mapKey_Position_1 != null ) {
						Set<Integer> position_2_Entries = mapKey_Position_1.get( pos1 );
						if ( position_2_Entries != null ) {
							if ( position_2_Entries.contains( pos2 ) ) {
								//  Found entry in the Selection Proteins so add to New/Output List
								looplinks_New_List.add( looplink );
							}
						}
					}
				}
			}
		}
		
		return proteinsMergedCommonPageDownloadResult;
	}
	
	/**
	 * @param selectedCrosslinksLooplinksMonolinksRoot
	 * @return Map<From Protein Id, Map<To Protein Id, Map<From Position, Set<To Position>>>>
	 * @throws ProxlWebappDataException 
	 */
	private Map<Integer,Map<Integer,Map<Integer,Set<Integer>>>> getSelectedCrosslinksInMaps( SelectedCrosslinksLooplinksMonolinksRoot selectedCrosslinksLooplinksMonolinksRoot ) throws ProxlWebappDataException {
		
		List<SelectedCrosslinks> selectedCrosslinks = selectedCrosslinksLooplinksMonolinksRoot.selectedCrosslinks;
		
		if ( selectedCrosslinks == null || selectedCrosslinks.isEmpty() ) {
			return null;
		}

		// Map<From Protein Id, Map<To Protein Id, Map<From Position, Set<To Position>>>>
		Map<Integer,Map<Integer,Map<Integer,Set<Integer>>>>  selectedCrosslinksInMaps = new HashMap<>();
		
		for ( SelectedCrosslinks selectedCrosslink : selectedCrosslinks ) {
			
			if ( selectedCrosslink.fromProtId == null || selectedCrosslink.fromPos == null || selectedCrosslink.toProtId == null || selectedCrosslink.toPos == null ) {
				String msg = "Entry in selectedCrosslinks is invalid. at least one of fields is null.";
				log.error(msg);
				throw new ProxlWebappDataException(msg);
			}
			
			Integer fromProtId = selectedCrosslink.fromProtId;
			Integer fromPos = selectedCrosslink.fromPos;
			Integer toProtId = selectedCrosslink.toProtId;
			Integer toPos = selectedCrosslink.toPos;
			if ( fromProtId > toProtId ) {
				//  Order so fromProtId <= toProtId
				fromProtId = selectedCrosslink.toProtId;
				fromPos = selectedCrosslink.toPos;
				toProtId = selectedCrosslink.fromProtId;
				toPos = selectedCrosslink.fromPos;
			} else if ( fromProtId.intValue() == toProtId.intValue() && fromPos >  toPos ) {
				//  Order so fromProtId == toProtId && fromPos <=  toPos) {
				fromPos = selectedCrosslink.toPos;
				toPos = selectedCrosslink.fromPos;
			}
			
			// Map<From Protein Id, Map<To Protein Id, Map<From Position, Set<To Position>>>>
			
			// Map<To Protein Id, Map<From Position, Set<To Position>>>
			Map<Integer,Map<Integer,Set<Integer>>> mapKeyToProteinId = selectedCrosslinksInMaps.get( fromProtId );
			if ( mapKeyToProteinId == null ) {
				mapKeyToProteinId = new HashMap<>();
				selectedCrosslinksInMaps.put( fromProtId, mapKeyToProteinId );
			}

			// Map<From Position, Set<To Position>>>
			Map<Integer,Set<Integer>> mapKeyFromPosition = mapKeyToProteinId.get( toProtId );
			if ( mapKeyFromPosition == null ) {
				mapKeyFromPosition = new HashMap<>();
				mapKeyToProteinId.put( toProtId, mapKeyFromPosition );
			}

			//  Set<To Position>
			Set<Integer> toPosition_entries = mapKeyFromPosition.get( fromPos );
			if ( toPosition_entries == null ) {
				toPosition_entries = new HashSet<>();
				mapKeyFromPosition.put( fromPos, toPosition_entries );
			}
			
			//  Add To Position
			toPosition_entries.add( toPos );
		}
		
		return  selectedCrosslinksInMaps;
	}

	/**
	 * @param selectedCrosslinksLooplinksMonolinksRoot
	 * @return  Map<Protein Id, Map<Position 1, Set<Position 2>>>>
	 * @throws ProxlWebappDataException 
	 */
	private Map<Integer,Map<Integer,Set<Integer>>> getSelectedLooplinksInMaps( SelectedCrosslinksLooplinksMonolinksRoot selectedCrosslinksLooplinksMonolinksRoot ) throws ProxlWebappDataException {
		
		List<SelectedLooplinks> selectedLooplinks = selectedCrosslinksLooplinksMonolinksRoot.selectedLooplinks;
		
		if ( selectedLooplinks == null || selectedLooplinks.isEmpty() ) {
			return null;
		}

		// Map<Protein Id, Map<Position 1, Set<Position 2>>>
		Map<Integer,Map<Integer,Set<Integer>>>  selectedLooplinksInMaps = new HashMap<>();
		
		for ( SelectedLooplinks selectedLooplink : selectedLooplinks ) {
			
			if ( selectedLooplink.protId == null || selectedLooplink.pos1 == null || selectedLooplink.pos2 == null ) {
				String msg = "Entry in selectedLooplinks is invalid. at least one of fields is null.";
				log.error(msg);
				throw new ProxlWebappDataException(msg);
			}
			
			Integer protId = selectedLooplink.protId;
			Integer pos1 = selectedLooplink.pos1;
			Integer pos2 = selectedLooplink.pos2;
			if ( pos1 >  pos2 ) {
				//  Order so pos1 <=  pos2 
				pos1 = selectedLooplink.pos2;
				pos2 = selectedLooplink.pos1;
			}

			// Map<Protein Id, Map<Position 1, Set<Position 2>>>
			
			// Map<Position 1, Set<Position 2>>
			Map<Integer,Set<Integer>> mapKey_Pos_1 = selectedLooplinksInMaps.get( protId );
			if ( mapKey_Pos_1 == null ) {
				mapKey_Pos_1 = new HashMap<>();
				selectedLooplinksInMaps.put( protId, mapKey_Pos_1 );
			}
			
			//  Set<Position 2>
			Set<Integer> position_2_entries = mapKey_Pos_1.get( pos1 );
			if ( position_2_entries == null ) {
				position_2_entries = new HashSet<>();
				mapKey_Pos_1.put( pos1, position_2_entries );
			}
			
			//  Add Position 2
			position_2_entries.add( pos2 );
		}
		
		return  selectedLooplinksInMaps;
	}

	/**
	 * @param selectedCrosslinksLooplinksMonolinksJSON
	 * @return
	 * @throws Exception
	 */
	private SelectedCrosslinksLooplinksMonolinksRoot get_selectedCrosslinksLooplinksMonolinks_From_JSON( String selectedCrosslinksLooplinksMonolinksJSON ) throws Exception {

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		//   deserialize 
		SelectedCrosslinksLooplinksMonolinksRoot selectedCrosslinksLooplinksMonolinks = null;
		try {
			selectedCrosslinksLooplinksMonolinks = jacksonJSON_Mapper.readValue( selectedCrosslinksLooplinksMonolinksJSON, SelectedCrosslinksLooplinksMonolinksRoot.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'postBody', JsonParseException. selectedCrosslinksLooplinksMonolinksJSON: " + selectedCrosslinksLooplinksMonolinksJSON; 
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'postBody', JsonMappingException. selectedCrosslinksLooplinksMonolinksJSON: " + selectedCrosslinksLooplinksMonolinksJSON;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to parse 'postBody', IOException. selectedCrosslinksLooplinksMonolinksJSON: " + selectedCrosslinksLooplinksMonolinksJSON;
			log.error( msg, e );
			throw e;
		}
		
		return selectedCrosslinksLooplinksMonolinks;
	}
	
	private static class SelectedCrosslinksLooplinksMonolinksRoot  {
		
		private List<SelectedCrosslinks> selectedCrosslinks;
		private List<SelectedLooplinks> selectedLooplinks;
		
		@SuppressWarnings("unused")
		public void setSelectedCrosslinks(List<SelectedCrosslinks> selectedCrosslinks) {
			this.selectedCrosslinks = selectedCrosslinks;
		}
		@SuppressWarnings("unused")
		public void setSelectedLooplinks(List<SelectedLooplinks> selectedLooplinks) {
			this.selectedLooplinks = selectedLooplinks;
		}
		
	}

	private static class SelectedCrosslinks  {
		private Integer fromProtId;
		private Integer toProtId;
		private Integer fromPos;
		private Integer toPos;
		
		@SuppressWarnings("unused")
		public void setFromProtId(Integer fromProtId) {
			this.fromProtId = fromProtId;
		}
		@SuppressWarnings("unused")
		public void setToProtId(Integer toProtId) {
			this.toProtId = toProtId;
		}
		@SuppressWarnings("unused")
		public void setFromPos(Integer fromPos) {
			this.fromPos = fromPos;
		}
		@SuppressWarnings("unused")
		public void setToPos(Integer toPos) {
			this.toPos = toPos;
		}
		
	}

	private static class SelectedLooplinks  {
		private Integer protId;
		private Integer pos1;
		private Integer pos2;
		
		@SuppressWarnings("unused")
		public void setProtId(Integer protId) {
			this.protId = protId;
		}
		@SuppressWarnings("unused")
		public void setPos1(Integer pos1) {
			this.pos1 = pos1;
		}
		@SuppressWarnings("unused")
		public void setPos2(Integer pos2) {
			this.pos2 = pos2;
		}
	}
}
