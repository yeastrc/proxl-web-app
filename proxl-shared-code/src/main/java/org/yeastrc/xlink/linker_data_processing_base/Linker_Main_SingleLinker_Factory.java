package org.yeastrc.xlink.linker_data_processing_base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchMonolinkMassDTO;
import org.yeastrc.xlink.dto.SearchLinkerDTO;
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;
import org.yeastrc.xlink.exceptions.ProxlBaseInternalErrorException;
import org.yeastrc.xlink.linker_data_processing_base.Z_SingleLinkerDefinition_Internal.LinkableProteinTerminus;
import org.yeastrc.xlink.linker_data_processing_base.Z_SingleLinkerDefinition_Internal.LinkerPerSide;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.LinkersDBDataSingleSearchSingleLinker;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.SearchLinkerPerSideDefinitionObj;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.SearchLinkerPerSideLinkableProteinTerminiObj;
import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.Get_BuiltIn_Linker_From_Abbreviation_Factory;
import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin.ILinker_Builtin_Linker;

/**
 * Creates object of class Linker_Main
 *
 */
public class Linker_Main_SingleLinker_Factory {

	private static final Logger log = LoggerFactory.getLogger(  Linker_Main_SingleLinker_Factory.class );
	
	/**
	 * @param linkersDBDataSingleSearchSingleLinker
	 * @return
	 * @throws ProxlBaseDataException 
	 */
	public static ILinker_Main getILinker_Main( LinkersDBDataSingleSearchSingleLinker linkersDBDataSingleSearchSingleLinker ) throws ProxlBaseDataException {
		
		return getLinker_Main( linkersDBDataSingleSearchSingleLinker );
	}

	/**
	 * Package Private 
	 * 
	 * @param linkersDBDataSingleSearchSingleLinker
	 * @return
	 * @throws ProxlBaseDataException 
	 */
	static Linker_Main getLinker_Main( LinkersDBDataSingleSearchSingleLinker linkersDBDataSingleSearchSingleLinker ) throws ProxlBaseDataException {
		
		SearchLinkerDTO searchLinkerDTO = linkersDBDataSingleSearchSingleLinker.getSearchLinkerDTO();
		String linkerAbbreviation = searchLinkerDTO.getLinkerAbbr();
		
		ILinker_Builtin_Linker linker_Builtin_Linker = Get_BuiltIn_Linker_From_Abbreviation_Factory.getLinkerForAbbr( linkerAbbreviation );
		
		Z_SingleLinkerDefinition_Internal z_SingleLinkerDefinition_Internal = getZ_SingleLinkerDefinition_Internal( linkersDBDataSingleSearchSingleLinker );
		
		List<LinkerPerSearchCrosslinkMassDTO> linkerPerSearchCrosslinkMassDTOList = null;
		List<LinkerPerSearchMonolinkMassDTO> linkerPerSearchMonolinkMassDTOList = null;
		List<LinkerPerSearchCleavedCrosslinkMassDTO> linkerPerSearchCleavedCrosslinkMassDTOList = null;
		
		if ( linkersDBDataSingleSearchSingleLinker.getLinkerPerSearchCrosslinkMassDTOList() != null ) {
			linkerPerSearchCrosslinkMassDTOList = Collections.unmodifiableList( linkersDBDataSingleSearchSingleLinker.getLinkerPerSearchCrosslinkMassDTOList() );
		}
		if ( linkersDBDataSingleSearchSingleLinker.getLinkerPerSearchMonolinkMassDTOList() != null ) {
			linkerPerSearchMonolinkMassDTOList = Collections.unmodifiableList( linkersDBDataSingleSearchSingleLinker.getLinkerPerSearchMonolinkMassDTOList() );
		}
		if ( linkersDBDataSingleSearchSingleLinker.getLinkerPerSearchCleavedCrosslinkMassDTOList() != null ) {
			linkerPerSearchCleavedCrosslinkMassDTOList = Collections.unmodifiableList( linkersDBDataSingleSearchSingleLinker.getLinkerPerSearchCleavedCrosslinkMassDTOList() );
		}
		
		Linker_Main linker_Main = new Linker_Main( linkerAbbreviation, linker_Builtin_Linker, z_SingleLinkerDefinition_Internal,
				linkerPerSearchCrosslinkMassDTOList,
				linkerPerSearchMonolinkMassDTOList,
				linkerPerSearchCleavedCrosslinkMassDTOList );
		
		linker_Main.setSearchId( searchLinkerDTO.getSearchId() );
		
		linker_Main.setSpacerArmLength( searchLinkerDTO.getSpacerArmLength() );
		linker_Main.setSpacerArmLengthString( searchLinkerDTO.getSpacerArmLengthString() );
		
		return linker_Main;
	}
	
	/**
	 * @param linkersDBDataSingleSearchSingleLinker
	 * @return
	 */
	private static Z_SingleLinkerDefinition_Internal getZ_SingleLinkerDefinition_Internal( LinkersDBDataSingleSearchSingleLinker linkersDBDataSingleSearchSingleLinker) {
		
		List<SearchLinkerPerSideDefinitionObj> searchLinkerPerSideDefinitionObjList = linkersDBDataSingleSearchSingleLinker.getSearchLinkerPerSideDefinitionObjList();
		
		if ( searchLinkerPerSideDefinitionObjList == null || searchLinkerPerSideDefinitionObjList.isEmpty() ) {
			return null;  // EARLY EXIT
		}
		
		if ( searchLinkerPerSideDefinitionObjList.size() != 2 ) {
			String msg = "searchLinkerPerSideDefinitionObjList must be size 2, is size: " + searchLinkerPerSideDefinitionObjList.size();
			log.error(msg);
			throw new ProxlBaseInternalErrorException(msg);
		}
		
		Z_SingleLinkerDefinition_Internal z_SingleLinkerDefinition_Internal = new Z_SingleLinkerDefinition_Internal();
		
		Iterator<SearchLinkerPerSideDefinitionObj> searchLinkerPerSideDefinitionObjListIter = searchLinkerPerSideDefinitionObjList.iterator();
		
		z_SingleLinkerDefinition_Internal.linkerPerSide_1 = createLinkerPerSide( searchLinkerPerSideDefinitionObjListIter.next() );
		z_SingleLinkerDefinition_Internal.linkerPerSide_2 = createLinkerPerSide( searchLinkerPerSideDefinitionObjListIter.next() );
		
		return z_SingleLinkerDefinition_Internal;
	}
	
	/**
	 * @param searchLinkerPerSideDefinitionObj
	 * @return
	 */
	private static LinkerPerSide createLinkerPerSide( SearchLinkerPerSideDefinitionObj searchLinkerPerSideDefinitionObj ) {

		LinkerPerSide linkerPerSide = new LinkerPerSide();
		
		linkerPerSide.linkableResidueList = searchLinkerPerSideDefinitionObj.getResidues();
		
		if ( searchLinkerPerSideDefinitionObj.getProteinTerminiList() != null ) {
			linkerPerSide.linkableProteinTerminusList = new ArrayList<>( searchLinkerPerSideDefinitionObj.getProteinTerminiList().size() );
			for ( SearchLinkerPerSideLinkableProteinTerminiObj dbObj : searchLinkerPerSideDefinitionObj.getProteinTerminiList() ) {
				LinkableProteinTerminus linkableProteinTerminus = new LinkableProteinTerminus();
				linkableProteinTerminus.proteinTerminus_c_n = dbObj.getProteinTerminus_c_n();
				linkableProteinTerminus.distanceFromTerminus = dbObj.getDistanceFromTerminus();
				linkerPerSide.linkableProteinTerminusList.add( linkableProteinTerminus );
			}
		}
		
		return linkerPerSide;
	}
}
