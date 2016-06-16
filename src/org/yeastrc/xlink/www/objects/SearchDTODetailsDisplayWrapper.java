package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.dto.CutoffsAppliedOnImportDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplaySearchLevel;
import org.yeastrc.xlink.www.searcher.CutoffsAppliedOnImportSearcher;
import org.yeastrc.xlink.www.searcher.SearchProgramDisplaySearcher;

/**
 * Used for the searchDetailsBlock.jsp and viewProject.jsp
 *
 */
public class SearchDTODetailsDisplayWrapper {
	
	private static final Logger log = Logger.getLogger( SearchDTODetailsDisplayWrapper.class );
	
	

	private SearchDTO searchDTO;

	private List<SearchProgramDisplay> searchProgramDisplayList; 
	
	private List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList;
	
	/**
	 * Not used on viewProject.jsp
	 */
	private CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel;
	


	/**
	 * @return
	 * @throws Exception
	 */
	public String getLinkersDisplayString() throws Exception {
		
		if ( searchDTO == null ) {
			
			throw new IllegalStateException( "searchDTO == null");
		}
		
		List<LinkerDTO> linkers = searchDTO.getLinkers();
		
		if ( linkers == null || linkers.isEmpty() ) {
			
			return "";
		}
		
		List<String> linkerAbbreviations = new ArrayList<>( linkers.size() );
		
		for ( LinkerDTO linker : linkers ) {
			
			linkerAbbreviations.add( linker.getAbbr() );
		}
		
		Collections.sort( linkerAbbreviations );
		
		String linkersString = linkerAbbreviations.get(0);
		
		if ( linkerAbbreviations.size() > 1 ) {

			//  start loop at second index
			for ( int index = 1; index < linkerAbbreviations.size(); index++ ) {

				linkersString += ", ";
				linkersString += linkerAbbreviations.get( index );
			}
		}
		
		return linkersString;
	}
	

	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<SearchProgramDisplay> getSearchPrograms() throws Exception {
		
		if ( searchProgramDisplayList != null ) {
			
			return searchProgramDisplayList;
		}
		
		int searchId = getSearchDTO().getId();
		
		searchProgramDisplayList = SearchProgramDisplaySearcher.getInstance().getSearchProgramDisplay( searchId );
		
		return searchProgramDisplayList;
	}
	
	
	

	public List<CutoffsAppliedOnImportWebDisplay> getCutoffsAppliedOnImportList() throws Exception {
		
		if ( cutoffsAppliedOnImportList != null ) {
			
			return cutoffsAppliedOnImportList;
		}
		
		if ( searchDTO == null ) {
			
			throw new IllegalStateException( "searchDTO == null");
		}
		

		cutoffsAppliedOnImportList = new ArrayList<>();
		

		Integer searchId = searchDTO.getId();
		
		////////  List of cutoffs applied on import
		
		List<CutoffsAppliedOnImportDTO> cutoffsAppliedOnImportDTOList = 
				CutoffsAppliedOnImportSearcher.getInstance().getCutoffsAppliedOnImportDTOForSearchId( searchId );
		
		if ( ! cutoffsAppliedOnImportDTOList.isEmpty() ) {
			
			List<Integer> searchIds = new ArrayList<>( 1 );
			
			searchIds.add( searchId );

			Map<Integer, Map<Integer, AnnotationTypeDTO>> all_Peptide_Filterable_ForSearchIds =
					GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIds );
			
			Map<Integer, Map<Integer, AnnotationTypeDTO>> all_Psm_Filterable_ForSearchIds =
					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIds );
			
			if ( all_Peptide_Filterable_ForSearchIds == null ) {
				
				String msg = "ERROR: all_Peptide_Filterable_ForSearchIds == null ";
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}

			if ( all_Psm_Filterable_ForSearchIds == null ) {
				
				String msg = "ERROR: all_Psm_Filterable_ForSearchIds == null ";
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			Map<Integer, AnnotationTypeDTO> all_Peptide_Filterable_PerSearchId = all_Peptide_Filterable_ForSearchIds.get( searchId );
			Map<Integer, AnnotationTypeDTO> all_Psm_Filterable_PerSearchId = all_Psm_Filterable_ForSearchIds.get( searchId );
			
			
			for ( CutoffsAppliedOnImportDTO cutoffsAppliedOnImportDTO : cutoffsAppliedOnImportDTOList ) {
				
				AnnotationTypeDTO peptideAnnotationTypeDTO = all_Peptide_Filterable_PerSearchId.get( cutoffsAppliedOnImportDTO.getAnnotationTypeId() );
				AnnotationTypeDTO psmAnnotationTypeDTO = all_Psm_Filterable_PerSearchId.get( cutoffsAppliedOnImportDTO.getAnnotationTypeId() );
				
				
				CutoffsAppliedOnImportWebDisplay cutoffsAppliedOnImportWebDisplay = new CutoffsAppliedOnImportWebDisplay();

				cutoffsAppliedOnImportWebDisplay.setCutoffValue( cutoffsAppliedOnImportDTO.getCutoffValueString() );

				if ( peptideAnnotationTypeDTO != null ) {
					
					cutoffsAppliedOnImportWebDisplay.setAnnotationName( peptideAnnotationTypeDTO.getName() );
					cutoffsAppliedOnImportWebDisplay.setPeptideCutoff( true );
					
				} else if ( psmAnnotationTypeDTO != null ) {

					cutoffsAppliedOnImportWebDisplay.setAnnotationName( psmAnnotationTypeDTO.getName() );
					cutoffsAppliedOnImportWebDisplay.setPeptideCutoff( false );
				
				} else {

					String msg = "ERROR: cutoffsAppliedOnImportDTO AnnotationTypeId not found in Peptide or PSM. "
							+ "  AnnotationTypeId: " + cutoffsAppliedOnImportDTO.getAnnotationTypeId()
							+ ", Search id: " + searchId;
					
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				
				cutoffsAppliedOnImportList.add( cutoffsAppliedOnImportWebDisplay );
			}
			
			//  Sort on Peptide then PSM and by name within each
			
			Collections.sort( cutoffsAppliedOnImportList, new Comparator<CutoffsAppliedOnImportWebDisplay>() {

				@Override
				public int compare(CutoffsAppliedOnImportWebDisplay o1, CutoffsAppliedOnImportWebDisplay o2) {

					if ( o1.isPeptideCutoff() != o2.isPeptideCutoff() ) {
						
						//  Sort peptide before PSM
						
						if ( o1.isPeptideCutoff() ) {
							
							return -1;
						} else {
							
							return 1;
						}
					}
					
					return o1.getAnnotationName().compareTo( o2.getAnnotationName() );
				}
			});
			
			
		}
		
		return cutoffsAppliedOnImportList;
	}

	
	
	//  Getters & Setters
	
	public SearchDTO getSearchDTO() {
		return searchDTO;
	}

	public void setSearchDTO(SearchDTO searchDTO) {
		this.searchDTO = searchDTO;
	}
	

	public CutoffPageDisplaySearchLevel getCutoffPageDisplaySearchLevel() {
		return cutoffPageDisplaySearchLevel;
	}



	public void setCutoffPageDisplaySearchLevel(
			CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel) {
		this.cutoffPageDisplaySearchLevel = cutoffPageDisplaySearchLevel;
	}



	
}
