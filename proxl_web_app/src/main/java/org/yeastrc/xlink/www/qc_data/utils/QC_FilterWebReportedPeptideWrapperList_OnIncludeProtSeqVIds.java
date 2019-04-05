package org.yeastrc.xlink.www.qc_data.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.objects.WebProteinPosition;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;

public class QC_FilterWebReportedPeptideWrapperList_OnIncludeProtSeqVIds {

	private static final Logger log = LoggerFactory.getLogger( QC_FilterWebReportedPeptideWrapperList_OnIncludeProtSeqVIds.class);

	/**
	 * private constructor
	 */
	private QC_FilterWebReportedPeptideWrapperList_OnIncludeProtSeqVIds(){}
	public static QC_FilterWebReportedPeptideWrapperList_OnIncludeProtSeqVIds getInstance( ) {
		QC_FilterWebReportedPeptideWrapperList_OnIncludeProtSeqVIds instance = new QC_FilterWebReportedPeptideWrapperList_OnIncludeProtSeqVIds();
		return instance;
	}
	
	/**
	 * @param wrappedLinksInputList
	 * @param includeProteinSeqVIdsDecodedArray
	 * @return - wrappedLinksInput filtered using includeProteinSeqVIdsDecodedArray if populated
	 * @throws Exception 
	 */
	public List<WebReportedPeptideWrapper> filter_WebReportedPeptideWrapper_OnIncludeProtSeqVIds( 
			List<WebReportedPeptideWrapper> wrappedLinksInputList,
			List<Integer> includeProteinSeqVIdsDecodedArray ) throws Exception {
		
		if ( wrappedLinksInputList == null ) {
			throw new IllegalArgumentException( "wrappedLinksInput == null" );
		}
		if ( wrappedLinksInputList.isEmpty() ) {
			//  No Data in list to filter
			return wrappedLinksInputList; // EARLY RETURN
		}
		if ( includeProteinSeqVIdsDecodedArray == null || includeProteinSeqVIdsDecodedArray.isEmpty() ) {
			//  Nothing to filter on
			return wrappedLinksInputList; // EARLY RETURN
		}

		List<WebReportedPeptideWrapper> wrappedLinksOutput = new ArrayList<>( wrappedLinksInputList.size() );
				
		Set<Integer> includeProteinSeqVIdsDecodedSet = new HashSet<>( includeProteinSeqVIdsDecodedArray );
		
		//  Get proteinSequenceVersionIds for all Reported Peptides

		for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedLinksInputList ) {
			WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
			
			boolean foundReportedPeptideEntry_ProtSeqId_In_IncludeProtSeqId_Set = false;
			
			List<WebProteinPosition> peptide_1_ProteinPositionsList = webReportedPeptide.getPeptide1ProteinPositions();
			if ( peptide_1_ProteinPositionsList != null ) {
				for ( WebProteinPosition webProteinPosition : peptide_1_ProteinPositionsList ) {
					int proteinSequenceVersionId = webProteinPosition.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
					if ( includeProteinSeqVIdsDecodedSet.contains( proteinSequenceVersionId ) ) {
						// Found proteinSequenceVersionId in includeProteinSeqVIdsDecodedSet
						foundReportedPeptideEntry_ProtSeqId_In_IncludeProtSeqId_Set = true;
						break;
					}
				}
			}
			if ( ! foundReportedPeptideEntry_ProtSeqId_In_IncludeProtSeqId_Set ) {
				// Only process if not found in first list
				List<WebProteinPosition> peptide_2_ProteinPositionsList = webReportedPeptide.getPeptide2ProteinPositions();
				if ( peptide_2_ProteinPositionsList != null && ( ! peptide_2_ProteinPositionsList.isEmpty() ) ) {
					for ( WebProteinPosition webProteinPosition : peptide_2_ProteinPositionsList ) {
						int proteinSequenceVersionId = webProteinPosition.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						if ( includeProteinSeqVIdsDecodedSet.contains( proteinSequenceVersionId ) ) {
							// Found proteinSequenceVersionId in includeProteinSeqVIdsDecodedSet
							foundReportedPeptideEntry_ProtSeqId_In_IncludeProtSeqId_Set = true;
							break;
						}
					}
				}
			}
			if ( foundReportedPeptideEntry_ProtSeqId_In_IncludeProtSeqId_Set ) {
				wrappedLinksOutput.add( webReportedPeptideWrapper );
			}
		}
			
		return wrappedLinksOutput;
	}
}
