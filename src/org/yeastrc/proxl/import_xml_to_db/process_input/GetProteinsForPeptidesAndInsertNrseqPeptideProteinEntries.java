package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_NrseqDatabasePeptideProteinDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.NrseqDatabasePeptideProteinDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.searchers.PeptideProteinSearcher;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;

/**
 * 
 *
 */
public class GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries {


	private static final Logger log = Logger.getLogger(GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries.class);

	private GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries() { }
	public static GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries getInstance() { return new GetProteinsForPeptidesAndInsertNrseqPeptideProteinEntries(); }
	

	/**
	 * Return NRProteinDTO objects for the peptide
	 * 
	 * Insert entries in nrseq_database_peptide_protein
	 * 
	 * @param peptideDTO - 
	 * @param proteinNameDecoyPrefix
	 * @param nrseqDatabaseId
	 * @return 
	 * @throws Exception
	 */
	public Collection<NRProteinDTO>  getProteinsForPeptidesAndInsertNrseqPeptideProteinEntries(
			
			PeptideDTO peptideDTO,
			
			List<String> proteinNameDecoyPrefixList,
			int nrseqDatabaseId
			
			) throws Exception {
		

		String peptideSequence = peptideDTO.getSequence();

		if ( log.isDebugEnabled() ) {
			System.out.println( "\t\tPeptide protein assocation not cached, caching..." );
		}
		
		Collection<NRProteinDTO> proteinMatches = PeptideProteinSearcher.getInstance().getProteinsContainingPeptide( peptideDTO, nrseqDatabaseId );		    			

		if( proteinMatches != null && proteinMatches.size() >= 1 ) {
			if ( log.isDebugEnabled() ) {
				System.out.print( "\t\tFound proteins using nrseq_database_peptide_protein: " );
				for( NRProteinDTO p : proteinMatches ) { 
					System.out.println( p.getNrseqId() + "," ); 
				}
			}
		}

		if( proteinMatches == null || proteinMatches.size() < 1 ) {

			proteinMatches = YRC_NRSEQUtils.getProteinsContainingPeptide( peptideSequence, nrseqDatabaseId );

			if( proteinMatches == null || proteinMatches.size() < 1 ) {
			
				String msg = "Could not find any proteins for peptide searching the protein sequences.  peptide sequence: " 
						+ peptideSequence; 
				log.error( msg );
				throw new Exception( msg );
			}

			if ( log.isDebugEnabled() ) {

				System.out.print( "\t\tFound proteins using YRC_NRSEQ: " );
				for( NRProteinDTO p : proteinMatches ) { 
					System.out.println( p.getNrseqId() + "," ); 
				}
			}

			// save these associations in the database
			boolean unique = true;
			if( proteinMatches.size() > 1 )
				unique = false;

			if ( log.isDebugEnabled() ) {

				System.out.println( "\t\tSaving to nrseq_database_peptide_protein the peptide to protein mapping..." );
			}
			
			//  Build list of NrseqDatabasePeptideProteinDTO and save them in 1 DAO call to save as single DB transaction
			
			List<NrseqDatabasePeptideProteinDTO> nrseqDatabasePeptideProteinDTOList = new ArrayList<>( proteinMatches.size() );
			
			for( NRProteinDTO protein : proteinMatches ) {

				NrseqDatabasePeptideProteinDTO prpp = new NrseqDatabasePeptideProteinDTO();
				prpp.setNrseqId( protein.getNrseqId() );
				prpp.setPeptideId( peptideDTO.getId());
				prpp.setNrseqDatabaseId( nrseqDatabaseId );
				prpp.setUnique( unique );

				nrseqDatabasePeptideProteinDTOList.add( prpp );
			}
			
			DB_Insert_NrseqDatabasePeptideProteinDAO.getInstance().saveListAsSingleTransaction( nrseqDatabasePeptideProteinDTOList );
		}
		


		if( proteinMatches.size() < 1 ) {
			
			String msg = "Could not find any proteins for peptide.   peptide sequence: "
					+ peptideSequence;
			log.error( msg );
			
			throw new Exception( msg );
		}

		if ( proteinNameDecoyPrefixList != null && ( ! proteinNameDecoyPrefixList.isEmpty() ) ) {

			// remove all decoys from pmatches
			Collection<NRProteinDTO> decoyMatches = new HashSet<NRProteinDTO>();
			for( NRProteinDTO protein : proteinMatches ) {
				
				for ( String proteinNameDecoyPrefix : proteinNameDecoyPrefixList ) {
					
					if( YRC_NRSEQUtils.getProteinNameFromId( protein.getNrseqId(), nrseqDatabaseId).startsWith( proteinNameDecoyPrefix ) ) {
						
						decoyMatches.add( protein );
						break;
					}
				}
			}

			for( NRProteinDTO protein : decoyMatches ) {
				String name = YRC_NRSEQUtils.getProteinNameFromId( protein.getNrseqId(), nrseqDatabaseId);
				
				if ( log.isDebugEnabled() ) {
					System.out.println( "Ignoring hit to " + name + " for peptide sequence: " + peptideSequence );
				}
				
				proteinMatches.remove( protein );
			}

		}


		if( proteinMatches.size() < 1 ) {
			
			String msg = "Could not find any proteins for peptide after removing decoy matches.   peptide sequence: "
					+ peptideSequence;
			log.error( msg );

			throw new Exception( msg );
		}

		return proteinMatches;
	}


}