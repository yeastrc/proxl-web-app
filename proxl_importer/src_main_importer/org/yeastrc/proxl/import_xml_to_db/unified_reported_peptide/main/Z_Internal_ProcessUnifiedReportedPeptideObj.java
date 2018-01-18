package org.yeastrc.proxl.import_xml_to_db.unified_reported_peptide.main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.UnifiedReportedPeptideConstants;
import org.yeastrc.xlink.dao.IsotopeLabelDAO;
import org.yeastrc.xlink.dto.IsotopeLabelDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepIsotopeLabelLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptide_IsotopeLabel_DTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.unified_reported_peptide.objects.UnifiedRpSinglePeptideDynamicMod;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * 
 *  Internal to unified reported peptide processing
 *  
 */
class Z_Internal_ProcessUnifiedReportedPeptideObj {

	private static final Logger log = Logger.getLogger(Z_Internal_ProcessUnifiedReportedPeptideObj.class);


	//  add the formatted peptides with the separator
	
	private static final String CROSSLINK_SEPARATOR = "--";
	private static final String DIMER_SEPARATOR = "+";
	
	//  Placed before the isotope label in the generated string
	private static final String ISOTOPE_LABEL_PREFIX_SEPARATOR = "-";
	
	
	// private constructor
	private Z_Internal_ProcessUnifiedReportedPeptideObj() { }


	public static Z_Internal_ProcessUnifiedReportedPeptideObj getInstance() { 
		return new Z_Internal_ProcessUnifiedReportedPeptideObj(); 
	}


	/**
	 * Format the provided link type, peptide(s) and dynamic mods into a unified/standardized format.
	 * This format is initially the Kojak reported peptide format.
	 * @param unifiedReportedPeptideObj
	 * @return
	 * @throws Exception 
	 */
	
	//  Package private method
	Z_Internal_UnifiedReportedPeptide_Holder processUnifiedReportedPeptideObj( int linkType, List<PerPeptideData> perPeptideDataList ) throws Exception {

		if ( perPeptideDataList == null || perPeptideDataList.isEmpty() ) {

			String msg = "ERROR: perPeptideDataList is null or empty";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}


		Z_Internal_UnifiedReportedPeptide_Holder unifiedReportedPeptide_Holder = null; 
		
		if ( linkType == XLinkUtils.TYPE_CROSSLINK ) {

			if ( perPeptideDataList.size() != 2 ) {

				String msg = "ERROR: perPeptideDataList.size must be 2 for crosslink.  perPeptideDataList.size: " + perPeptideDataList.size();
				log.error( msg );
				throw new Exception(msg);
			}
			
			unifiedReportedPeptide_Holder = createCrosslinkUnifiedReportedPeptideString( perPeptideDataList );

		} else if ( linkType == XLinkUtils.TYPE_LOOPLINK) {

			if ( perPeptideDataList.size() != 1 ) {

				String msg = "ERROR: perPeptideDataList.size must be 1 for looplink.  perPeptideDataList.size: " + perPeptideDataList.size();
				log.error( msg );
				throw new Exception(msg);
			}
			
			unifiedReportedPeptide_Holder = createLooplinkUnifiedReportedPeptideString( perPeptideDataList );
			
		} else if ( linkType == XLinkUtils.TYPE_DIMER ) {

			if ( perPeptideDataList.size() != 2 ) {

				String msg = "ERROR: perPeptideDataList.size must be 2 for dimer.  perPeptideDataList.size: " + perPeptideDataList.size();
				log.error( msg );
				throw new Exception(msg);
			}
			
			unifiedReportedPeptide_Holder = createDimerUnifiedReportedPeptideString( perPeptideDataList );
			
		} else if ( linkType == XLinkUtils.TYPE_UNLINKED ) {

			if ( perPeptideDataList.size() != 1 ) {

				String msg = "ERROR: perPeptideDataList.size must be 1 for unlinked.  perPeptideDataList.size: " + perPeptideDataList.size();
				log.error( msg );
				throw new Exception(msg);
			}
			
			unifiedReportedPeptide_Holder = createUnlinkUnifiedReportedPeptideString( perPeptideDataList );
			
		} else {

			String msg = "ERROR: unknown link type: " + linkType;
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}

		
		if ( unifiedReportedPeptide_Holder == null ) {
			
			String msg = "ERROR: Internal error, unifiedReportedPeptide == null";
			log.error( msg );
			throw new Exception(msg);
		}
		
		UnifiedReportedPeptideLookupDTO unifiedReportedPeptideDTO = unifiedReportedPeptide_Holder.getUnifiedReportedPeptideDTO();

		{
			boolean hasMods = false;
			for ( Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder : unifiedReportedPeptide_Holder.getZ_Internal_UnifiedRpMatchedPeptide_HolderList() ) {
				if ( unifiedRpMatchedPeptide_Holder.getZ_Internal_UnifiedRpDynamicMod_Holder_List() != null 
						&& ( ! unifiedRpMatchedPeptide_Holder.getZ_Internal_UnifiedRpDynamicMod_Holder_List().isEmpty() ) ) {
					hasMods = true;
					break;
				}
			}
			unifiedReportedPeptideDTO.setHasMods( hasMods );
		}

		{
			boolean hasIsotopeLabels = false;
			for ( Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder : unifiedReportedPeptide_Holder.getZ_Internal_UnifiedRpMatchedPeptide_HolderList() ) {
				if ( unifiedRpMatchedPeptide_Holder.getZ_Internal_UnifiedRpIsotopeLabel_Holder_List() != null 
						&& ( ! unifiedRpMatchedPeptide_Holder.getZ_Internal_UnifiedRpIsotopeLabel_Holder_List().isEmpty() ) ) {
					hasIsotopeLabels = true;
					break;
				}
			}
			unifiedReportedPeptideDTO.setHasIsotopeLabels( hasIsotopeLabels );
		}
		
		unifiedReportedPeptideDTO.setLinkTypeNumber(linkType);
		

		return unifiedReportedPeptide_Holder;
	}
	

	/**
	 * @param perPeptideDataList
	 * @return
	 * @throws Exception 
	 */
	private Z_Internal_UnifiedReportedPeptide_Holder createCrosslinkUnifiedReportedPeptideString( List<PerPeptideData> perPeptideDataList ) throws Exception {

		if ( perPeptideDataList.size() != 2 ) {

			String msg = "ERROR: perPeptideDataList.size must be 2 for crosslinks";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}

		
		PerPeptideData singlePeptide1 = perPeptideDataList.get(0);
		PerPeptideData singlePeptide2 = perPeptideDataList.get(1);
		
		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder_1
			= getPeptideWithModsAndIsotopeLabelsForCrosslinks( singlePeptide1 );

		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder_2
			= getPeptideWithModsAndIsotopeLabelsForCrosslinks( singlePeptide2 );
		
		{
			if ( unifiedRpMatchedPeptide_Holder_1.getFormattedPeptideString()
					.compareTo( unifiedRpMatchedPeptide_Holder_2.getFormattedPeptideString() ) > 0 ) {

				//  flip the peptides make them in sorted order

				Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder_Temp 
					= unifiedRpMatchedPeptide_Holder_1;
				
				unifiedRpMatchedPeptide_Holder_1 = unifiedRpMatchedPeptide_Holder_2;
				
				unifiedRpMatchedPeptide_Holder_2 = unifiedRpMatchedPeptide_Holder_Temp;
			}
		}
		
		
		// Set the order

		unifiedRpMatchedPeptide_Holder_1.getUnifiedRpMatchedPeptideDTO().setPeptideOrder( 1 );
		unifiedRpMatchedPeptide_Holder_2.getUnifiedRpMatchedPeptideDTO().setPeptideOrder( 2 );

		//  add the formatted peptides with the separator
		
		String formattedPeptide1 = unifiedRpMatchedPeptide_Holder_1.getFormattedPeptideString();
		String formattedPeptide2 = unifiedRpMatchedPeptide_Holder_2.getFormattedPeptideString();


		String unifiedReportedPeptideString = formattedPeptide1 + CROSSLINK_SEPARATOR + formattedPeptide2;

	
		UnifiedReportedPeptideLookupDTO unifiedReportedPeptideDTO = new UnifiedReportedPeptideLookupDTO();
	
		unifiedReportedPeptideDTO.setUnifiedSequence( unifiedReportedPeptideString );

		
		Z_Internal_UnifiedReportedPeptide_Holder unifiedReportedPeptide_Holder 
			= new Z_Internal_UnifiedReportedPeptide_Holder();
	
		unifiedReportedPeptide_Holder.setUnifiedReportedPeptideDTO( unifiedReportedPeptideDTO );
		
		List<Z_Internal_UnifiedRpMatchedPeptide_Holder> unifiedRpMatchedPeptide_HolderList = new ArrayList<>( 2 );
		unifiedReportedPeptide_Holder.setZ_Internal_UnifiedRpMatchedPeptide_HolderList(unifiedRpMatchedPeptide_HolderList);
		
		unifiedRpMatchedPeptide_HolderList.add( unifiedRpMatchedPeptide_Holder_1 );
		unifiedRpMatchedPeptide_HolderList.add( unifiedRpMatchedPeptide_Holder_2 );
		
		return unifiedReportedPeptide_Holder;
	}
	
	/**
	 * @param perPeptideData
	 * @return
	 * @throws Exception
	 */
	private Z_Internal_UnifiedRpMatchedPeptide_Holder getPeptideWithModsAndIsotopeLabelsForCrosslinks( PerPeptideData perPeptideData ) throws Exception {

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();
		
		if ( srchRepPeptPeptideDTO.getPeptidePosition_1() < 0 ) {
			
			String msg = "ERROR: srchRepPeptPeptideDTO.getPeptidePosition_1() must be populated for crosslinks";
			log.error( msg );
			throw new Exception(msg);
		}

		if ( srchRepPeptPeptideDTO.getPeptidePosition_2() >= 0 ) {
			
			String msg = "ERROR: srchRepPeptPeptideDTO.getPeptidePosition_2() must NOT be populated for crosslinks";
			log.error( msg );
			throw new Exception(msg);
		}
				

		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder 
			= addModsAndIsotopeLabelsToPeptideString( perPeptideData );
		
		String peptideStringWithModsAndIsotopeLabels = unifiedRpMatchedPeptide_Holder.getPeptideStringWithModsAndIsotopeLabels();
		
		String formattedPeptideString = peptideStringWithModsAndIsotopeLabels + "(" + srchRepPeptPeptideDTO.getPeptidePosition_1() + ")"; 

		unifiedRpMatchedPeptide_Holder.setFormattedPeptideString( formattedPeptideString );


		UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptideDTO = 
				unifiedRpMatchedPeptide_Holder.getUnifiedRpMatchedPeptideDTO();
		
		unifiedRpMatchedPeptideDTO.setLinkPosition1(srchRepPeptPeptideDTO.getPeptidePosition_1());
		
		
		
		return unifiedRpMatchedPeptide_Holder;
	}
	
	

	/**
	 * @param perPeptideDataList
	 * @return
	 * @throws Exception 
	 */
	private Z_Internal_UnifiedReportedPeptide_Holder createLooplinkUnifiedReportedPeptideString( List<PerPeptideData> perPeptideDataList ) throws Exception {


		if ( perPeptideDataList.size() != 1 ) {

			String msg = "ERROR: perPeptideDataList.size must be 1 for looplinks";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		PerPeptideData perPeptideData = perPeptideDataList.get(0);

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();
		
		if ( srchRepPeptPeptideDTO.getPeptidePosition_1() < 0 ) {
			
			String msg = "ERROR: srchRepPeptPeptideDTO.getPeptidePosition_1() must be populated for looplinks";
			log.error( msg );
			throw new Exception(msg);
		}

		if ( srchRepPeptPeptideDTO.getPeptidePosition_2() < 0 ) {
			
			String msg = "ERROR: srchRepPeptPeptideDTO.getPeptidePosition_2() must be populated for looplinks";
			log.error( msg );
			throw new Exception(msg);
		}

		
		
		int[] linkPositions = { srchRepPeptPeptideDTO.getPeptidePosition_1() , srchRepPeptPeptideDTO.getPeptidePosition_2() };

		Arrays.sort( linkPositions );
		


		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder 
			= addModsAndIsotopeLabelsToPeptideString( perPeptideData );
		
		// Set the order

		unifiedRpMatchedPeptide_Holder.getUnifiedRpMatchedPeptideDTO().setPeptideOrder( 1 );

		
		String peptideStringWithMods = unifiedRpMatchedPeptide_Holder.getPeptideStringWithModsAndIsotopeLabels();
		
		String formattedPeptideString = peptideStringWithMods + "(" + linkPositions[ 0 ] + "," + linkPositions[ 1 ] + ")"; 

		unifiedRpMatchedPeptide_Holder.setFormattedPeptideString( formattedPeptideString );
		

		UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptideDTO = 
				unifiedRpMatchedPeptide_Holder.getUnifiedRpMatchedPeptideDTO();

		unifiedRpMatchedPeptideDTO.setLinkPosition1(linkPositions[ 0 ]);

		unifiedRpMatchedPeptideDTO.setLinkPosition2(linkPositions[ 1 ]);
		
		
		

		UnifiedReportedPeptideLookupDTO unifiedReportedPeptideDTO = new UnifiedReportedPeptideLookupDTO();
	
		unifiedReportedPeptideDTO.setUnifiedSequence( formattedPeptideString );

		
		Z_Internal_UnifiedReportedPeptide_Holder unifiedReportedPeptide_Holder 
			= new Z_Internal_UnifiedReportedPeptide_Holder();
	
		unifiedReportedPeptide_Holder.setUnifiedReportedPeptideDTO( unifiedReportedPeptideDTO );

		List<Z_Internal_UnifiedRpMatchedPeptide_Holder> unifiedRpMatchedPeptide_HolderList = new ArrayList<>( 1 );
		unifiedReportedPeptide_Holder.setZ_Internal_UnifiedRpMatchedPeptide_HolderList(unifiedRpMatchedPeptide_HolderList);
		
		unifiedRpMatchedPeptide_HolderList.add( unifiedRpMatchedPeptide_Holder );
		
		return unifiedReportedPeptide_Holder;
	}
	

	/**
	 * @param perPeptideDataList
	 * @return
	 * @throws Exception 
	 */
	private Z_Internal_UnifiedReportedPeptide_Holder createDimerUnifiedReportedPeptideString( List<PerPeptideData> perPeptideDataList ) throws Exception {


		if ( perPeptideDataList.size() != 2 ) {

			String msg = "ERROR: perPeptideDataList.size must be 2 for dimer";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		

		PerPeptideData singlePeptide1 = perPeptideDataList.get(0);
		PerPeptideData singlePeptide2 = perPeptideDataList.get(1);

		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder_1
			= getPeptideWithModsForDimers( singlePeptide1 );

		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder_2
			= getPeptideWithModsForDimers( singlePeptide2 );
		
		{
			if ( unifiedRpMatchedPeptide_Holder_1.getFormattedPeptideString()
					.compareTo( unifiedRpMatchedPeptide_Holder_2.getFormattedPeptideString() ) > 0 ) {

				//  flip the peptides make them in sorted order

				Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder_Temp 
					= unifiedRpMatchedPeptide_Holder_1;
				
				unifiedRpMatchedPeptide_Holder_1 = unifiedRpMatchedPeptide_Holder_2;
				
				unifiedRpMatchedPeptide_Holder_2 = unifiedRpMatchedPeptide_Holder_Temp;
			}
		}
		
		
		// Set the order

		unifiedRpMatchedPeptide_Holder_1.getUnifiedRpMatchedPeptideDTO().setPeptideOrder( 1 );
		unifiedRpMatchedPeptide_Holder_2.getUnifiedRpMatchedPeptideDTO().setPeptideOrder( 2 );

		//  add the formatted peptides with the separator
		
		String formattedPeptide1 = unifiedRpMatchedPeptide_Holder_1.getFormattedPeptideString();
		String formattedPeptide2 = unifiedRpMatchedPeptide_Holder_2.getFormattedPeptideString();

		String unifiedReportedPeptideString = formattedPeptide1 + DIMER_SEPARATOR + formattedPeptide2;


		UnifiedReportedPeptideLookupDTO unifiedReportedPeptideDTO = new UnifiedReportedPeptideLookupDTO();
	
		unifiedReportedPeptideDTO.setUnifiedSequence( unifiedReportedPeptideString );

		
		Z_Internal_UnifiedReportedPeptide_Holder unifiedReportedPeptide_Holder 
			= new Z_Internal_UnifiedReportedPeptide_Holder();
	
		unifiedReportedPeptide_Holder.setUnifiedReportedPeptideDTO( unifiedReportedPeptideDTO );
		

		List<Z_Internal_UnifiedRpMatchedPeptide_Holder> unifiedRpMatchedPeptide_HolderList = new ArrayList<>( 2 );
		unifiedReportedPeptide_Holder.setZ_Internal_UnifiedRpMatchedPeptide_HolderList(unifiedRpMatchedPeptide_HolderList);
		
		unifiedRpMatchedPeptide_HolderList.add( unifiedRpMatchedPeptide_Holder_1 );
		unifiedRpMatchedPeptide_HolderList.add( unifiedRpMatchedPeptide_Holder_2 );
		
		return unifiedReportedPeptide_Holder;
		
	}
	
	

	/**
	 * @param perPeptideData
	 * @return
	 * @throws Exception
	 */
	private Z_Internal_UnifiedRpMatchedPeptide_Holder getPeptideWithModsForDimers( PerPeptideData perPeptideData ) throws Exception {


		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();
		
		if ( srchRepPeptPeptideDTO.getPeptidePosition_1() >= 0 ) {
			
			String msg = "ERROR: srchRepPeptPeptideDTO.getPeptidePosition_1() must NOT be populated for dimer";
			log.error( msg );
			throw new Exception(msg);
		}

		if ( srchRepPeptPeptideDTO.getPeptidePosition_2() >= 0 ) {
			
			String msg = "ERROR: srchRepPeptPeptideDTO.getPeptidePosition_2() must NOT be populated for dimer";
			log.error( msg );
			throw new Exception(msg);
		}

		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder 
			= addModsAndIsotopeLabelsToPeptideString( perPeptideData );
		
		String peptideStringWithMods = unifiedRpMatchedPeptide_Holder.getPeptideStringWithModsAndIsotopeLabels();
		
		String formattedPeptideString = peptideStringWithMods; 

		unifiedRpMatchedPeptide_Holder.setFormattedPeptideString( formattedPeptideString );
		

//		UnifiedRpMatchedPeptideDTO unifiedRpMatchedPeptideDTO = 
//				unifiedRpMatchedPeptide_Holder.getUnifiedRpMatchedPeptideDTO();
		
		
		return unifiedRpMatchedPeptide_Holder;
	}
	
	
	
	

	/**
	 * @param perPeptideDataList
	 * @return
	 * @throws Exception 
	 */
	private Z_Internal_UnifiedReportedPeptide_Holder createUnlinkUnifiedReportedPeptideString( List<PerPeptideData> perPeptideDataList ) throws Exception {


		if ( perPeptideDataList.size() != 1 ) {

			String msg = "ERROR: perPeptideDataList.size must be 1 for unlinked";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		PerPeptideData perPeptideData = perPeptideDataList.get(0);

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();
		
		if ( srchRepPeptPeptideDTO.getPeptidePosition_1() >= 0 ) {
			
			String msg = "ERROR: srchRepPeptPeptideDTO.getPeptidePosition_1() must NOT be populated for unlinked";
			log.error( msg );
			throw new Exception(msg);
		}

		if ( srchRepPeptPeptideDTO.getPeptidePosition_2() >= 0 ) {
			
			String msg = "ERROR: srchRepPeptPeptideDTO.getPeptidePosition_2() must NOT be populated for unlinked";
			log.error( msg );
			throw new Exception(msg);
		}
		

		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder 
			= addModsAndIsotopeLabelsToPeptideString( perPeptideData );
		
		// Set the order

		unifiedRpMatchedPeptide_Holder.getUnifiedRpMatchedPeptideDTO().setPeptideOrder( 1 );

		
		String peptideStringWithMods = unifiedRpMatchedPeptide_Holder.getPeptideStringWithModsAndIsotopeLabels();
		
		String formattedPeptideString = peptideStringWithMods; 

		unifiedRpMatchedPeptide_Holder.setFormattedPeptideString( formattedPeptideString );
		

//		UnifiedRpMatchedPeptideDTO unifiedRpMatchedPeptideDTO = 
//				unifiedRpMatchedPeptide_Holder.getUnifiedRpMatchedPeptideDTO();


		UnifiedReportedPeptideLookupDTO unifiedReportedPeptideDTO = new UnifiedReportedPeptideLookupDTO();
	
		unifiedReportedPeptideDTO.setUnifiedSequence( formattedPeptideString );

		
		Z_Internal_UnifiedReportedPeptide_Holder unifiedReportedPeptide_Holder 
			= new Z_Internal_UnifiedReportedPeptide_Holder();
	
		unifiedReportedPeptide_Holder.setUnifiedReportedPeptideDTO( unifiedReportedPeptideDTO );
		

		List<Z_Internal_UnifiedRpMatchedPeptide_Holder> unifiedRpMatchedPeptide_HolderList = new ArrayList<>( 1 );
		unifiedReportedPeptide_Holder.setZ_Internal_UnifiedRpMatchedPeptide_HolderList(unifiedRpMatchedPeptide_HolderList);
		
		unifiedRpMatchedPeptide_HolderList.add( unifiedRpMatchedPeptide_Holder );
		
		return unifiedReportedPeptide_Holder;
	}
	
	

	/**
	 * @param perPeptideData
	 * @return
	 * @throws Exception 
	 */
	private Z_Internal_UnifiedRpMatchedPeptide_Holder addModsAndIsotopeLabelsToPeptideString( PerPeptideData perPeptideData ) throws Exception {

		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder 
			= new Z_Internal_UnifiedRpMatchedPeptide_Holder();
		
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		
		
		UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptideDTO = new UnifiedRepPepMatchedPeptideLookupDTO();
		unifiedRpMatchedPeptide_Holder.setUnifiedRpMatchedPeptideDTO( unifiedRpMatchedPeptideDTO );

		unifiedRpMatchedPeptideDTO.setPeptideId( peptideDTO.getId() );


		String peptideSequence = peptideDTO.getSequence();
		
		List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList = perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide();

		List<SrchRepPeptPeptide_IsotopeLabel_DTO> srchRepPeptPeptide_IsotopeLabel_DTO_List = perPeptideData.getSrchRepPeptPeptide_IsotopeLabel_DTOList_Peptide();
		
		//  
		
		if ( ( srchRepPeptPeptDynamicModDTOList == null || srchRepPeptPeptDynamicModDTOList.isEmpty() )
				&& ( srchRepPeptPeptide_IsotopeLabel_DTO_List == null || srchRepPeptPeptide_IsotopeLabel_DTO_List.isEmpty() ) ) {
			
			//  Optimization/Early Exit

			unifiedRpMatchedPeptide_Holder.setPeptideStringWithModsAndIsotopeLabels( peptideSequence );
			
			return unifiedRpMatchedPeptide_Holder; // EARLY RETURN
		}

		int srchRepPeptPeptDynamicModDTOList_Size = 0;
		int srchRepPeptPeptide_IsotopeLabel_DTO_List_Size = 0;
		
		if ( srchRepPeptPeptDynamicModDTOList != null ) {
			srchRepPeptPeptDynamicModDTOList_Size = srchRepPeptPeptDynamicModDTOList.size();
		}
		if ( srchRepPeptPeptide_IsotopeLabel_DTO_List != null ) {
			srchRepPeptPeptide_IsotopeLabel_DTO_List_Size = srchRepPeptPeptide_IsotopeLabel_DTO_List.size();
		}
		
		StringBuilder peptideSequenceOutSB = 
				new StringBuilder( peptideSequence.length() 
						+ ( srchRepPeptPeptDynamicModDTOList_Size * 13 )
						+ ( srchRepPeptPeptide_IsotopeLabel_DTO_List_Size * 13 ) );


		
		if ( srchRepPeptPeptDynamicModDTOList == null || srchRepPeptPeptDynamicModDTOList.isEmpty() ) {
			
			peptideSequenceOutSB.append( peptideSequence );
			
		} else {
			// Process Dynamic Mods
			
			//  List to store in parent object
			List<Z_Internal_UnifiedRpDynamicMod_Holder> unifiedRpDynamicMod_Holder_List = new ArrayList<>( srchRepPeptPeptDynamicModDTOList.size() );
			unifiedRpMatchedPeptide_Holder.setZ_Internal_UnifiedRpDynamicMod_Holder_List( unifiedRpDynamicMod_Holder_List );

			//  Build list of local holder dynamic mods 

			List<UnifiedRpSinglePeptideDynamicMod> unifiedRpSinglePeptideDynamicModList = new ArrayList<>( srchRepPeptPeptDynamicModDTOList.size() );

			for ( SrchRepPeptPeptDynamicModDTO srchRepPeptPeptDynamicModDTO : srchRepPeptPeptDynamicModDTOList ) {

				UnifiedRpSinglePeptideDynamicMod unifiedRpSinglePeptideDynamicMod = new UnifiedRpSinglePeptideDynamicMod();
				unifiedRpSinglePeptideDynamicModList.add(unifiedRpSinglePeptideDynamicMod);

				unifiedRpSinglePeptideDynamicMod.setPosition( srchRepPeptPeptDynamicModDTO.getPosition() );
				unifiedRpSinglePeptideDynamicMod.setMass( srchRepPeptPeptDynamicModDTO.getMass() );
			}


			Collections.sort( unifiedRpSinglePeptideDynamicModList );

			Iterator<UnifiedRpSinglePeptideDynamicMod> dynamicModIterator = unifiedRpSinglePeptideDynamicModList.iterator();

			UnifiedRpSinglePeptideDynamicMod mod = dynamicModIterator.next();

			char[] peptideSequenceArray = peptideSequence.toCharArray();

			int modOrder = 0;
			
			for ( int seqIndex = 0; seqIndex < peptideSequenceArray.length; seqIndex++ ) {

				int modPosition = seqIndex + 1; // since mod position is 1 based

				char seqChar = peptideSequenceArray[ seqIndex ];

				peptideSequenceOutSB.append( seqChar );

				while ( mod != null && mod.getPosition() == modPosition ) {

					modOrder++;

					BigDecimal modMassRounded = mod.getMassRounded();

					peptideSequenceOutSB.append( "[" );
					peptideSequenceOutSB.append( modMassRounded );
					peptideSequenceOutSB.append( "]" );

					Z_Internal_UnifiedRpDynamicMod_Holder unifiedRpDynamicMod_Holder = new Z_Internal_UnifiedRpDynamicMod_Holder();
					unifiedRpDynamicMod_Holder_List.add( unifiedRpDynamicMod_Holder );

					UnifiedRepPepDynamicModLookupDTO unifiedRpDynamicModDTO = new UnifiedRepPepDynamicModLookupDTO();
					unifiedRpDynamicMod_Holder.setUnifiedRpDynamicModDTO( unifiedRpDynamicModDTO );

					unifiedRpDynamicModDTO.setPosition( mod.getPosition() );

					unifiedRpDynamicModDTO.setMass( mod.getMass() );
					unifiedRpDynamicModDTO.setMassRounded( mod.getMassRounded().doubleValue() );
					unifiedRpDynamicModDTO.setMassRoundedString( mod.getMassRounded().toString() );
					unifiedRpDynamicModDTO.setMassRoundingPlaces( UnifiedReportedPeptideConstants.DECIMAL_POSITIONS_ROUNDED_TO );
					unifiedRpDynamicModDTO.setModOrder( modOrder );

					if ( dynamicModIterator.hasNext() ) {
						mod = dynamicModIterator.next();
					} else {
						mod = null;
					}
				}

			}
		} 
		
		// Add Isotope Labels to end of string
		
		if ( srchRepPeptPeptide_IsotopeLabel_DTO_List != null && ( ! srchRepPeptPeptide_IsotopeLabel_DTO_List.isEmpty() ) ) {
			
			//  List to store in parent object
			List<Z_Internal_UnifiedRpIsotopeLabel_Holder> unifiedRpIsotopeLabel_Holder_List = new ArrayList<>( srchRepPeptPeptide_IsotopeLabel_DTO_List.size() );
			unifiedRpMatchedPeptide_Holder.setZ_Internal_UnifiedRpIsotopeLabel_Holder_List( unifiedRpIsotopeLabel_Holder_List );

			// process isotope labels
			List<String> isotopeLabels = new ArrayList<>( srchRepPeptPeptide_IsotopeLabel_DTO_List.size() );
			for ( SrchRepPeptPeptide_IsotopeLabel_DTO srchRepPeptPeptide_IsotopeLabel_DTO : srchRepPeptPeptide_IsotopeLabel_DTO_List ) {
				IsotopeLabelDTO isotopeLabelDTO = 
						IsotopeLabelDAO.getInstance().getIsotopeLabelDTOForId( srchRepPeptPeptide_IsotopeLabel_DTO.getIsotopeLabelId() );
				if ( isotopeLabelDTO == null ) {
					String msg = "No Isotope label found for id: " + srchRepPeptPeptide_IsotopeLabel_DTO.getIsotopeLabelId();
					log.error( msg );
					throw new ProxlImporterInteralException( msg );
				}
				isotopeLabels.add( isotopeLabelDTO.getName() );
				
				Z_Internal_UnifiedRpIsotopeLabel_Holder z_Internal_UnifiedRpIsotopeLabel_Holder = new Z_Internal_UnifiedRpIsotopeLabel_Holder();
				unifiedRpIsotopeLabel_Holder_List.add( z_Internal_UnifiedRpIsotopeLabel_Holder );
				
				UnifiedRepPepIsotopeLabelLookupDTO unifiedRepPepIsotopeLabelLookupDTO = new UnifiedRepPepIsotopeLabelLookupDTO();
				z_Internal_UnifiedRpIsotopeLabel_Holder.setUnifiedRepPepIsotopeLabelLookupDTO( unifiedRepPepIsotopeLabelLookupDTO );

				unifiedRepPepIsotopeLabelLookupDTO.setIsotopeLabelId( srchRepPeptPeptide_IsotopeLabel_DTO.getIsotopeLabelId() );
			}
			// Sort on isotope label string		
			Collections.sort( isotopeLabels );
			// Add isotope labels to peptideSequenceOutSB
			for ( String isotopeLabel : isotopeLabels ) {
				peptideSequenceOutSB.append( ISOTOPE_LABEL_PREFIX_SEPARATOR );
				peptideSequenceOutSB.append( isotopeLabel );
			}

		}
		
		String peptideSequenceOut = peptideSequenceOutSB.toString();
		
		unifiedRpMatchedPeptide_Holder.setPeptideStringWithModsAndIsotopeLabels( peptideSequenceOut );
		
		return unifiedRpMatchedPeptide_Holder;
	}
	
}
