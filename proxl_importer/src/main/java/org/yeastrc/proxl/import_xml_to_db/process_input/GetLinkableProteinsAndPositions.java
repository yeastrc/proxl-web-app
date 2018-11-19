package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.proxl.import_xml_to_db.utils.PeptideProteinSequenceForProteinInference;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;

/**
 * 
 *
 */
public class GetLinkableProteinsAndPositions {

	private static final Logger log = Logger.getLogger(GetLinkableProteinsAndPositions.class);
	
	private static final GetLinkableProteinsAndPositions instance = new GetLinkableProteinsAndPositions();
	
	//  private constructor
	private GetLinkableProteinsAndPositions() { }
	public static GetLinkableProteinsAndPositions getInstance() { return instance; }
	
	/**
	 * For Crosslinks:
	 * 
	 * Get a map of proteins and associated positions in those proteins that are mapped to by the supplied peptide and position, but
	 * only include positions that are linkable positions as defined by the supplied linker. Only proteins where linkable positions
	 * are found will be include (ie, no empty collections for protein keys)
	 * 
	 * Also validate that all the monolinks are linkable at the associated protein positions
	 * 
	 * @param peptide - 
	 * @param peptideCrossLinkPosition
	 * @param linker
	 * @param peptideProteins
	 * @return
	 * @throws Exception
	 */
	public  Map<ProteinImporterContainer, Collection<Integer>> getCrosslinkLinkableProteinsAndPositions( 
			String peptideSequence, 
			int peptideCrossLinkPosition, 
			Set<Integer> peptideMonolinkPositions,
			List<ILinker> linkerList, 
			Collection<ProteinImporterContainer> proteinImporterContainerCollection,
			ReportedPeptide reportedPeptide // For error reporting only
			) throws Exception {

		// Create copy of peptide sequence for protein inference where I and L are replaced with J
		String peptideSequenceForProteinInference = 
				PeptideProteinSequenceForProteinInference.getSingletonInstance().
				convert_PeptideOrProtein_SequenceFor_I_L_Equivalence_ChangeTo_J( peptideSequence );
		
		Map<ProteinImporterContainer, Collection<Integer>> results = new HashMap<>();
		
		for( ProteinImporterContainer proteinImporterContainer : proteinImporterContainerCollection ) {
			
			String proteinSequence = proteinImporterContainer.getProteinSequenceDTO().getSequence();
			String proteinSequenceForProteinInference = proteinImporterContainer.getProteinSequenceForProteinInference();
			
			List<Integer> proteinCrosslinkedPositions = new ArrayList<>(); 
	        for (int i = -1; (i = proteinSequenceForProteinInference.indexOf( peptideSequenceForProteinInference, i + 1)) != -1; ) {
	        	proteinCrosslinkedPositions.add( i + peptideCrossLinkPosition );
	        }
			
			//  Get linkable positions for all the linkers
			Collection<Integer> proteinLinkablePositionsCollection = new HashSet<Integer>();
			for ( ILinker linker : linkerList ) {
				Collection<Integer> proteinLinkablePositionsCollectionForLinker = linker.getLinkablePositions( proteinSequence );
				proteinLinkablePositionsCollection.addAll( proteinLinkablePositionsCollectionForLinker );
			}
			List<Integer> proteinPositionList = null;
			for( Integer proteinCrosslinkedPosition : proteinCrosslinkedPositions ) {
				boolean crosslinkAndMonolinksPositionsFoundInLinkablePositions = true; 
				if( ! ( proteinLinkablePositionsCollection.contains( proteinCrosslinkedPosition ) ) ) {
					crosslinkAndMonolinksPositionsFoundInLinkablePositions = false;
					if ( log.isInfoEnabled() ) {
						String msg = " Skipping link for protein " + proteinImporterContainer.getProteinSequenceDTO().getSequence() 
								+ " for peptide " + peptideSequence 
								+ " at position " + peptideCrossLinkPosition 
								+ " because it was not a linkable residue in the protein at crosslink position "
								+ proteinCrosslinkedPosition 
								+ " for reported peptide: " + reportedPeptide.getReportedPeptideString()
								+ ".";
						log.info( msg );
					}
				}
				if ( crosslinkAndMonolinksPositionsFoundInLinkablePositions
						&& peptideMonolinkPositions != null && ( ! peptideMonolinkPositions.isEmpty() ) ) {
					for ( Integer peptideMonolinkPosition : peptideMonolinkPositions ) {
						//  Convert peptide monolink position to protein position
						int proteinMonolinkPosition = proteinCrosslinkedPosition - peptideCrossLinkPosition + peptideMonolinkPosition; 
						//  Check if monolink protein position is a linkable position
						if( ! ( proteinLinkablePositionsCollection.contains( proteinMonolinkPosition ) ) ) {
							if ( log.isInfoEnabled() ) {
								String msg = " Skipping link for protein " + proteinImporterContainer.getProteinSequenceDTO().getSequence() 
										+ " for peptide " + peptideSequence 
										+ " because the monolink at peptide position " + peptideMonolinkPosition 
										+ " was not a linkable residue in the protein at position "
										+ proteinMonolinkPosition
										+ " while processing crosslink "
										+ " at crosslink peptide position " + peptideCrossLinkPosition 
										+ " and crosslink protein position: "
										+ proteinCrosslinkedPosition 
										+ " for reported peptide: " + reportedPeptide.getReportedPeptideString()
										+ ".";
								log.info( msg );
							}
							crosslinkAndMonolinksPositionsFoundInLinkablePositions = false;
							break;
						}
					}
				}
				if ( crosslinkAndMonolinksPositionsFoundInLinkablePositions ) {
					if ( proteinPositionList == null ) {
						//  First position found, create list and put in map
						proteinPositionList = new ArrayList<>();
						Object proteinPositionListPrev =
								results.put( proteinImporterContainer, proteinPositionList );
						if ( proteinPositionListPrev != null ) {
							String msg = "proteinImporterContainer already in map. protein sequence: "
									+ proteinImporterContainer.getProteinSequenceDTO().getSequence();
							log.error( msg );
							throw new ProxlImporterInteralException(msg);
						}
					}
					proteinPositionList.add( proteinCrosslinkedPosition );
				} else {
				}
			}
		}
		return results;
	}
	
	/**
	 * For Looplinks:
	 * 
	 * Get a map of proteins and associated positions in those proteins that are mapped to by the supplied peptide and positions. Will only
	 * return proteins and positions for proteins where both positions are linkable according to the supplied linker. Only proteins
	 * with at least one valid pair of positions are included in the map.
	 * 
	 * Also validate that all the monolinks are linkable at the associated protein positions
	 * 
	 * @param peptide
	 * @param position1
	 * @param position2
	 * @param linker
	 * @param peptideProteins
	 * @return
	 * @throws Exception
	 */
	public  Map<ProteinImporterContainer, Collection<List<Integer>>> getLooplinkLinkableProteinsAndPositionsForLooplink( 
			String peptideSequence, 
			int peptideLooplinkPosition_1, 
			int peptideLooplinkPosition_2, 
			Set<Integer> peptideMonolinkPositions,
			List<ILinker> linkerList, 
			Collection<ProteinImporterContainer> proteinImporterContainerCollection,
			ReportedPeptide reportedPeptide // For error reporting only
			) throws Exception {

		// Create copy of peptide sequence for protein inference where I and L are replaced with J
		String peptideSequenceForProteinInference = 
				PeptideProteinSequenceForProteinInference.getSingletonInstance().
				convert_PeptideOrProtein_SequenceFor_I_L_Equivalence_ChangeTo_J( peptideSequence );
		
		Map<ProteinImporterContainer, Collection<List<Integer>>> results = new HashMap<>();
		
		for( ProteinImporterContainer proteinImporterContainer : proteinImporterContainerCollection ) {
			
			String proteinSequence = proteinImporterContainer.getProteinSequenceDTO().getSequence();
			String proteinSequenceForProteinInference = proteinImporterContainer.getProteinSequenceForProteinInference();
			
			List<List<Integer>> proteinPositions = new ArrayList<>();
			for ( int i = -1; ( i = proteinSequenceForProteinInference.indexOf( peptideSequenceForProteinInference, i + 1 ) ) != -1; ) {
				List<Integer> l = new ArrayList<Integer>(2);
				l.add( i + peptideLooplinkPosition_1 );
				l.add( i + peptideLooplinkPosition_2 );

				proteinPositions.add( l );
			}

			//  Get linkable positions for all the linkers
			Collection<Integer> proteinLinkablePositionsCollection = new HashSet<Integer>();
			for ( ILinker linker : linkerList ) {
				Collection<Integer> linkablePositionsCollectionForLinker = linker.getLinkablePositions( proteinSequence );
				proteinLinkablePositionsCollection.addAll( linkablePositionsCollectionForLinker );
			}
			List<List<Integer>> proteinPosition_1_2_List = null;
			for( List<Integer> linkedPositions_1_2 : proteinPositions ) {
				boolean looplinkAndMonolinksFoundInLinkablePositions = true; 
				int looplinkProteinPosition_1 = linkedPositions_1_2.get( 0 );
				int looplinkProteinPosition_2 = linkedPositions_1_2.get( 1 );
				if( ! ( proteinLinkablePositionsCollection.contains( looplinkProteinPosition_1 )
						&& proteinLinkablePositionsCollection.contains( looplinkProteinPosition_2 ) ) ) {
					looplinkAndMonolinksFoundInLinkablePositions = false;
					if ( log.isInfoEnabled() ) {
						String msg = "Skipping looplink for protein " + proteinImporterContainer.getProteinSequenceDTO().getSequence()
								+ " for peptide " + peptideSequence 
								+ " at positions " + peptideLooplinkPosition_1 + " and " + peptideLooplinkPosition_2 
								+ " because they were not linkable residues in the protein at positions "
								+ looplinkProteinPosition_1 + " and " + looplinkProteinPosition_2 + ".";
						log.info( msg );
					}
				}
				if ( looplinkAndMonolinksFoundInLinkablePositions
						&& peptideMonolinkPositions != null && ( ! peptideMonolinkPositions.isEmpty() ) ) {
					for ( Integer peptideMonolinkPosition : peptideMonolinkPositions ) {
						//  Convert peptide monolink position to protein position
						int proteinMonolinkPosition = looplinkProteinPosition_1 - peptideLooplinkPosition_1 + peptideMonolinkPosition; 
						//  Check if monolink protein position is a linkable position
						if( ! ( proteinLinkablePositionsCollection.contains( proteinMonolinkPosition ) ) ) {
							if ( log.isInfoEnabled() ) {
								String msg = " Skipping link for protein " + proteinImporterContainer.getProteinSequenceDTO().getSequence() 
										+ " for peptide " + peptideSequence 
										+ " because the monolink at peptide position " + peptideMonolinkPosition 
										+ " was not a linkable residue in the protein at position "
										+ proteinMonolinkPosition
										+ " while processing looplink "
										+ " at looplink peptide positions " 
										+ peptideLooplinkPosition_1 + " and " + peptideLooplinkPosition_2
										+ " and looplink protein positions: "
										+ looplinkProteinPosition_1 + " and " + looplinkProteinPosition_2 
										+ " for reported peptide: " + reportedPeptide.getReportedPeptideString()
										+ ".";
								log.info( msg );
							}
							looplinkAndMonolinksFoundInLinkablePositions = false;
							break;
						}
					}
				}
				if ( looplinkAndMonolinksFoundInLinkablePositions ) {
					if ( proteinPosition_1_2_List == null ) {
						//  First position found, create list and put in map
						proteinPosition_1_2_List = new ArrayList<>();
						Object proteinPositionListPrev =
								results.put(proteinImporterContainer, proteinPosition_1_2_List);
						if ( proteinPositionListPrev != null ) {
							String msg = "proteinImporterContainer already in map. protein sequence: "
									+ proteinImporterContainer.getProteinSequenceDTO().getSequence();
							log.error( msg );
							throw new ProxlImporterInteralException(msg);
						}
					}
					List<Integer> proteinPositions_1_2_list = new ArrayList<Integer>( 2 );
					proteinPositions_1_2_list.add( looplinkProteinPosition_1 );
					proteinPositions_1_2_list.add( looplinkProteinPosition_2 );
					proteinPosition_1_2_List.add( proteinPositions_1_2_list );
				}
			}
		}
		return results;
	}
	
	/**
	 * For Unlinked and Dimer
	 * 
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param peptideDTO
	 * @param proteinImporterContainer
	 * @throws Exception
	 */
	public Map<ProteinImporterContainer, Collection<Integer>> get_Unlinked_Dimer_PeptidePositionsInProteins(
			String peptideSequence, 
			Set<Integer> peptideMonolinkPositions,
			List<ILinker> linkerList, 
			Collection<ProteinImporterContainer> proteinImporterContainerCollection,
			ReportedPeptide reportedPeptide // For error reporting only
			) throws Exception {

		// Create copy of peptide sequence for protein inference where I and L are replaced with J
		String peptideSequenceForProteinInference = 
				PeptideProteinSequenceForProteinInference.getSingletonInstance().
				convert_PeptideOrProtein_SequenceFor_I_L_Equivalence_ChangeTo_J( peptideSequence );
		
		Map<ProteinImporterContainer, Collection<Integer>> results = new HashMap<>();
		for( ProteinImporterContainer proteinImporterContainer : proteinImporterContainerCollection ) {
			
			String proteinSequence = proteinImporterContainer.getProteinSequenceDTO().getSequence();
			String proteinSequenceForProteinInference = proteinImporterContainer.getProteinSequenceForProteinInference();
			
			//  Get linkable positions for all the linkers
			Collection<Integer> proteinLinkablePositionsCollection = new HashSet<Integer>();
			for ( ILinker linker : linkerList ) {
				Collection<Integer> linkablePositionsCollectionForLinker = linker.getLinkablePositions( proteinSequence );
				proteinLinkablePositionsCollection.addAll( linkablePositionsCollectionForLinker );
			}
			List<Integer> peptidePositionInProteinList = null;
			int fromIndex = 0;
			int peptideIndex = 0;
			while ( ( peptideIndex = proteinSequenceForProteinInference.indexOf( peptideSequenceForProteinInference, fromIndex ) ) >= 0 ) {
				int proteinStartPosition = peptideIndex + 1;  //  Positions are 1 based
//				int proteinEndPosition = proteinStartPosition + peptideSequence.length() - 1;
				boolean monolinksFoundInLinkablePositions = true;
				if ( peptideMonolinkPositions != null && ( ! peptideMonolinkPositions.isEmpty() ) ) {
					for ( Integer peptideMonolinkPosition : peptideMonolinkPositions ) {
						//  Convert peptide monolink position to protein position
						int proteinMonolinkPosition = peptideIndex + peptideMonolinkPosition; 
						//  Check if monolink protein position is a linkable position
						if( ! ( proteinLinkablePositionsCollection.contains( proteinMonolinkPosition ) ) ) {
							if ( log.isInfoEnabled() ) {
								String msg = " Skipping link for protein " + proteinImporterContainer.getProteinSequenceDTO().getSequence() 
										+ " for peptide " + peptideSequence 
										+ " because the monolink at peptide position " + peptideMonolinkPosition 
										+ " was not a linkable residue in the protein at position "
										+ proteinMonolinkPosition
										+ " while processing unlinked or dimer "
										+ " at peptide start position on the protein: " 
										+ proteinStartPosition
										+ ", for reported peptide: " + reportedPeptide.getReportedPeptideString()
										+ ".";
								log.info( msg );
							}
							monolinksFoundInLinkablePositions = false;
							break;
						}
					}
				}
				if ( monolinksFoundInLinkablePositions ) {
					if ( peptidePositionInProteinList == null ) {
						//  First position found, create list and put in map
						peptidePositionInProteinList = new ArrayList<>();
						Object proteinPositionListPrev =
								results.put(proteinImporterContainer, peptidePositionInProteinList);
						if ( proteinPositionListPrev != null ) {
							String msg = "proteinImporterContainer already in map. protein sequence: "
									+ proteinImporterContainer.getProteinSequenceDTO().getSequence();
							log.error( msg );
							throw new ProxlImporterInteralException(msg);
						}
					}
					peptidePositionInProteinList.add( proteinStartPosition );
				}
				fromIndex = peptideIndex + 1;
			}
		}
		return results;
	}
	
}
