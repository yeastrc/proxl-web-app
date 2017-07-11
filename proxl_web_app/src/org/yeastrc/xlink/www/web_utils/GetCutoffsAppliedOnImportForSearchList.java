package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.dto.CutoffsAppliedOnImportDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.CutoffsAppliedOnImportWebDisplay;
import org.yeastrc.xlink.www.searcher.CutoffsAppliedOnImportSearcher;
/**
 * 
 *
 */
public class GetCutoffsAppliedOnImportForSearchList {
	
	private static final Logger log = Logger.getLogger( GetCutoffsAppliedOnImportForSearchList.class );
	private static final GetCutoffsAppliedOnImportForSearchList instance = new GetCutoffsAppliedOnImportForSearchList();
	private GetCutoffsAppliedOnImportForSearchList() { }
	public static GetCutoffsAppliedOnImportForSearchList getInstance() { return instance; }
	
	/**
	 * This is cached in class 
	 * 
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public List<CutoffsAppliedOnImportWebDisplay> getCutoffsAppliedOnImportList( int searchId ) throws Exception {
		List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList = new ArrayList<>();
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
				AnnotationTypeDTO peptideAnnotationTypeDTO = null;
				AnnotationTypeDTO psmAnnotationTypeDTO = null;
				if ( all_Peptide_Filterable_PerSearchId != null ) {
					peptideAnnotationTypeDTO = all_Peptide_Filterable_PerSearchId.get( cutoffsAppliedOnImportDTO.getAnnotationTypeId() );
				}
				if ( all_Psm_Filterable_PerSearchId != null ) {
					psmAnnotationTypeDTO = all_Psm_Filterable_PerSearchId.get( cutoffsAppliedOnImportDTO.getAnnotationTypeId() );
				}
				CutoffsAppliedOnImportWebDisplay cutoffsAppliedOnImportWebDisplay = new CutoffsAppliedOnImportWebDisplay();
				cutoffsAppliedOnImportWebDisplay.setAnnotationTypeId( cutoffsAppliedOnImportDTO.getAnnotationTypeId() );
				cutoffsAppliedOnImportWebDisplay.setCutoffValue( cutoffsAppliedOnImportDTO.getCutoffValueString() );
				cutoffsAppliedOnImportWebDisplay.setCutoffValueDouble( cutoffsAppliedOnImportDTO.getCutoffValueDouble() );
				AnnotationTypeDTO annotationTypeDTO = null;
				if ( peptideAnnotationTypeDTO != null ) {
					annotationTypeDTO = peptideAnnotationTypeDTO;
					cutoffsAppliedOnImportWebDisplay.setPeptideCutoff( true );
				} else if ( psmAnnotationTypeDTO != null ) {
					annotationTypeDTO = psmAnnotationTypeDTO;
					cutoffsAppliedOnImportWebDisplay.setPeptideCutoff( false );
				} else {
					String msg = "ERROR: cutoffsAppliedOnImportDTO AnnotationTypeId not found in Peptide or PSM. "
							+ "  AnnotationTypeId: " + cutoffsAppliedOnImportDTO.getAnnotationTypeId()
							+ ", Search id: " + searchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				cutoffsAppliedOnImportWebDisplay.setAnnotationName( annotationTypeDTO.getName() );
				//  Get filter direction
				AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
				if ( annotationTypeFilterableDTO == null ) {
					String msg = "No annotationTypeFilterableDTO for AnnotationTypeId. "
							+ "  AnnotationTypeId: " + cutoffsAppliedOnImportDTO.getAnnotationTypeId()
							+ ", Search id: " + searchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				FilterDirectionType filterDirectionType = annotationTypeFilterableDTO.getFilterDirectionType();
				if ( filterDirectionType == null ) {
					String msg = "No filterDirectionType for AnnotationTypeId. "
							+ "  AnnotationTypeId: " + cutoffsAppliedOnImportDTO.getAnnotationTypeId()
							+ ", Search id: " + searchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				cutoffsAppliedOnImportWebDisplay.setAnnotationFilterDirection( filterDirectionType.value() );
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
}
