package org.yeastrc.proxl.import_xml_to_db.import_post_processing.unified_reported_peptide.main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.UnifiedReportedPeptideConstants;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.unified_reported_peptide.objects.UnifiedReportedPeptideObj;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.unified_reported_peptide.objects.UnifiedRpSinglePeptideDynamicMod;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.unified_reported_peptide.objects.UnifiedRpSinglePeptideObj;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * 
 *  Internal to unified reported peptide processing
 *  
 */
class Z_Internal_ProcessUnifiedReportedPeptideObj {

	private static final Logger log = Logger.getLogger(Z_Internal_ProcessUnifiedReportedPeptideObj.class);


	//  add the formatted peptides with the separator
	
	private String CROSSLINK_SEPARATOR = "--";
	private String DIMER_SEPARATOR = "+";
	
	
	
	
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
	Z_Internal_UnifiedReportedPeptide_Holder processUnifiedReportedPeptideObj( UnifiedReportedPeptideObj unifiedReportedPeptideObj ) throws Exception {


		int linkType = unifiedReportedPeptideObj.getLinkType();

		List<UnifiedRpSinglePeptideObj> singlePeptides = unifiedReportedPeptideObj.getSinglePeptides();

		if ( singlePeptides == null || singlePeptides.isEmpty() ) {

			String msg = "ERROR: singlePeptides is null or empty";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}


		Z_Internal_UnifiedReportedPeptide_Holder unifiedReportedPeptide_Holder = null; 
		
		if ( linkType == XLinkUtils.TYPE_CROSSLINK ) {

			if ( singlePeptides.size() != 2 ) {

				String msg = "ERROR: singlePeptides.size must be 2 for crosslink.  singlePeptides.size: " + singlePeptides.size();
				log.error( msg );
				throw new Exception(msg);
			}
			
			unifiedReportedPeptide_Holder = createCrosslinkUnifiedReportedPeptideString( singlePeptides );

		} else if ( linkType == XLinkUtils.TYPE_LOOPLINK) {

			if ( singlePeptides.size() != 1 ) {

				String msg = "ERROR: singlePeptides.size must be 1 for looplink.  singlePeptides.size: " + singlePeptides.size();
				log.error( msg );
				throw new Exception(msg);
			}
			
			unifiedReportedPeptide_Holder = createLooplinkUnifiedReportedPeptideString( singlePeptides );
			
		} else if ( linkType == XLinkUtils.TYPE_DIMER ) {

			if ( singlePeptides.size() != 2 ) {

				String msg = "ERROR: singlePeptides.size must be 2 for dimer.  singlePeptides.size: " + singlePeptides.size();
				log.error( msg );
				throw new Exception(msg);
			}
			
			unifiedReportedPeptide_Holder = createDimerUnifiedReportedPeptideString( singlePeptides );
			
		} else if ( linkType == XLinkUtils.TYPE_UNLINKED ) {

			if ( singlePeptides.size() != 1 ) {

				String msg = "ERROR: singlePeptides.size must be 1 for unlinked.  singlePeptides.size: " + singlePeptides.size();
				log.error( msg );
				throw new Exception(msg);
			}
			
			unifiedReportedPeptide_Holder = createUnlinkUnifiedReportedPeptideString( singlePeptides );
			
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
		
		
		boolean hasMods = false;
		
		for ( Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder : unifiedReportedPeptide_Holder.getZ_Internal_UnifiedRpMatchedPeptide_HolderList() ) {
			
			if ( unifiedRpMatchedPeptide_Holder.getZ_Internal_UnifiedRpDynamicMod_Holder_List() != null 
					&& ( ! unifiedRpMatchedPeptide_Holder.getZ_Internal_UnifiedRpDynamicMod_Holder_List().isEmpty() ) ) {
				
				hasMods = true;
				break;
			}
		}
		
		unifiedReportedPeptide_Holder.getUnifiedReportedPeptideDTO().setHasMods( hasMods );

		unifiedReportedPeptide_Holder.getUnifiedReportedPeptideDTO().setLinkTypeNumber(linkType);
		

		return unifiedReportedPeptide_Holder;
	}
	

	/**
	 * @param singlePeptides
	 * @return
	 * @throws Exception 
	 */
	private Z_Internal_UnifiedReportedPeptide_Holder createCrosslinkUnifiedReportedPeptideString( List<UnifiedRpSinglePeptideObj> singlePeptides ) throws Exception {

		if ( singlePeptides.size() != 2 ) {

			String msg = "ERROR: singlePeptides.size must be 2 for crosslinks";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}

		
		UnifiedRpSinglePeptideObj singlePeptide1 = singlePeptides.get(0);
		UnifiedRpSinglePeptideObj singlePeptide2 = singlePeptides.get(1);
		
		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder_1
			= getPeptideWithModsForCrosslinks( singlePeptide1 );

		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder_2
			= getPeptideWithModsForCrosslinks( singlePeptide2 );
		
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
	 * @param singlePeptide
	 * @return
	 * @throws Exception
	 */
	private Z_Internal_UnifiedRpMatchedPeptide_Holder getPeptideWithModsForCrosslinks( UnifiedRpSinglePeptideObj singlePeptide ) throws Exception {

		int[] linkPositions = singlePeptide.getLinkPositions();
		
		if ( linkPositions == null ) {
			
			String msg = "ERROR: singlePeptides entry must have exactly 1 link position for crosslinks";
			log.error( msg );
			throw new Exception(msg);
		}
		if ( linkPositions.length != 1 ) {
			
			String msg = "ERROR: singlePeptides entry must have exactly 1 link position for crosslinks";
			log.error( msg );
			throw new Exception(msg);
		}
		

		
		

		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder 
			= addModsToPeptideString( singlePeptide );
		
		String peptideStringWithMods = unifiedRpMatchedPeptide_Holder.getPeptideStringWithMods();
		
		String formattedPeptideString = peptideStringWithMods + "(" + linkPositions[ 0 ] + ")"; 

		unifiedRpMatchedPeptide_Holder.setFormattedPeptideString( formattedPeptideString );


		UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptideDTO = 
				unifiedRpMatchedPeptide_Holder.getUnifiedRpMatchedPeptideDTO();
		
		unifiedRpMatchedPeptideDTO.setLinkPosition1(linkPositions[ 0 ]);
		
		
		
		return unifiedRpMatchedPeptide_Holder;
	}
	
	

	/**
	 * @param singlePeptides
	 * @return
	 * @throws Exception 
	 */
	private Z_Internal_UnifiedReportedPeptide_Holder createLooplinkUnifiedReportedPeptideString( List<UnifiedRpSinglePeptideObj> singlePeptides ) throws Exception {


		if ( singlePeptides.size() != 1 ) {

			String msg = "ERROR: singlePeptides.size must be 1 for looplinks";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		UnifiedRpSinglePeptideObj singlePeptide = singlePeptides.get(0);

		int[] linkPositions = singlePeptide.getLinkPositions();

		if ( linkPositions == null ) {

			String msg = "ERROR: singlePeptides entry must have exactly 2 link positions for looplinks";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		if ( linkPositions.length != 2 ) {

			String msg = "ERROR: singlePeptides entry must have exactly 2 link positions for looplinks";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		Arrays.sort( linkPositions );
		


		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder 
			= addModsToPeptideString( singlePeptide );
		
		// Set the order

		unifiedRpMatchedPeptide_Holder.getUnifiedRpMatchedPeptideDTO().setPeptideOrder( 1 );

		
		String peptideStringWithMods = unifiedRpMatchedPeptide_Holder.getPeptideStringWithMods();
		
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
	 * @param singlePeptides
	 * @return
	 * @throws Exception 
	 */
	private Z_Internal_UnifiedReportedPeptide_Holder createDimerUnifiedReportedPeptideString( List<UnifiedRpSinglePeptideObj> singlePeptides ) throws Exception {


		if ( singlePeptides.size() != 2 ) {

			String msg = "ERROR: singlePeptides.size must be 2 for dimer";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		

		UnifiedRpSinglePeptideObj singlePeptide1 = singlePeptides.get(0);
		UnifiedRpSinglePeptideObj singlePeptide2 = singlePeptides.get(1);

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
	 * @param singlePeptide
	 * @return
	 * @throws Exception
	 */
	private Z_Internal_UnifiedRpMatchedPeptide_Holder getPeptideWithModsForDimers( UnifiedRpSinglePeptideObj singlePeptide ) throws Exception {


		int[] linkPositions = singlePeptide.getLinkPositions();
		
		if ( linkPositions != null && ( linkPositions.length != 0 ) ) {
			
			String msg = "ERROR: singlePeptides entry must have no link positions for dimer";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		


		
		

		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder 
			= addModsToPeptideString( singlePeptide );
		
		String peptideStringWithMods = unifiedRpMatchedPeptide_Holder.getPeptideStringWithMods();
		
		String formattedPeptideString = peptideStringWithMods; 

		unifiedRpMatchedPeptide_Holder.setFormattedPeptideString( formattedPeptideString );
		

//		UnifiedRpMatchedPeptideDTO unifiedRpMatchedPeptideDTO = 
//				unifiedRpMatchedPeptide_Holder.getUnifiedRpMatchedPeptideDTO();
		
		
		return unifiedRpMatchedPeptide_Holder;
	}
	
	
	
	

	/**
	 * @param singlePeptides
	 * @return
	 */
	private Z_Internal_UnifiedReportedPeptide_Holder createUnlinkUnifiedReportedPeptideString( List<UnifiedRpSinglePeptideObj> singlePeptides ) {


		if ( singlePeptides.size() != 1 ) {

			String msg = "ERROR: singlePeptides.size must be 1 for unlinked";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		UnifiedRpSinglePeptideObj singlePeptide = singlePeptides.get(0);

		int[] linkPositions = singlePeptide.getLinkPositions();

		if ( linkPositions != null && ( linkPositions.length != 0 ) ) {
			
			String msg = "ERROR: singlePeptides entry must have no link positions for unlinked";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}


		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder 
			= addModsToPeptideString( singlePeptide );
		
		// Set the order

		unifiedRpMatchedPeptide_Holder.getUnifiedRpMatchedPeptideDTO().setPeptideOrder( 1 );

		
		String peptideStringWithMods = unifiedRpMatchedPeptide_Holder.getPeptideStringWithMods();
		
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
	 * @param unifiedRpSinglePeptideObj
	 * @return
	 */
	private Z_Internal_UnifiedRpMatchedPeptide_Holder addModsToPeptideString( UnifiedRpSinglePeptideObj unifiedRpSinglePeptideObj ) {

		Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder 
			= new Z_Internal_UnifiedRpMatchedPeptide_Holder();
		
		PeptideDTO peptideDTO = unifiedRpSinglePeptideObj.getPeptideDTO();
		
		
		UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptideDTO = new UnifiedRepPepMatchedPeptideLookupDTO();
		unifiedRpMatchedPeptide_Holder.setUnifiedRpMatchedPeptideDTO( unifiedRpMatchedPeptideDTO );

		unifiedRpMatchedPeptideDTO.setPeptideId( peptideDTO.getId() );


		String peptideSequence = peptideDTO.getSequence();
		
		List<UnifiedRpSinglePeptideDynamicMod> dynamicModList = unifiedRpSinglePeptideObj.getDynamicModList();

		if ( dynamicModList == null || dynamicModList.isEmpty() ) {

			unifiedRpMatchedPeptide_Holder.setPeptideStringWithMods( peptideSequence );
			
			return unifiedRpMatchedPeptide_Holder;
		}
		
		
		List<Z_Internal_UnifiedRpDynamicMod_Holder> unifiedRpDynamicMod_Holder_List = new ArrayList<>();
			
		unifiedRpMatchedPeptide_Holder.setZ_Internal_UnifiedRpDynamicMod_Holder_List( unifiedRpDynamicMod_Holder_List );

		
		
		StringBuilder peptideSequenceOutSB = new StringBuilder( peptideSequence.length() + ( dynamicModList.size() * 13 ) );

		Collections.sort( dynamicModList );

		Iterator<UnifiedRpSinglePeptideDynamicMod> dynamicModIterator = dynamicModList.iterator();
		
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
		
		String peptideSequenceOut = peptideSequenceOutSB.toString();
		
		unifiedRpMatchedPeptide_Holder.setPeptideStringWithMods( peptideSequenceOut );
		
		return unifiedRpMatchedPeptide_Holder;
	}
	
}
