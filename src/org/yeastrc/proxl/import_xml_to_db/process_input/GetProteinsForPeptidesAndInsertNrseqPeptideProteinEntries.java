package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.NrseqDatabasePeptideProteinDAO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.NrseqDatabasePeptideProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.searchers.PeptideProteinSearcher;
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

		System.out.println( "\t\tPeptide protein assocation not cached, caching..." );

		Collection<NRProteinDTO> proteinMatches = PeptideProteinSearcher.getInstance().getProteinsContainingPeptide( peptideDTO, nrseqDatabaseId );		    			

		if( proteinMatches != null && proteinMatches.size() >= 1 ) {
			System.out.print( "\t\tFound proteins using nrseq_database_peptide_protein: " );
			for( NRProteinDTO p : proteinMatches ) { System.out.println( p.getNrseqId() + "," ); }
		}

		if( proteinMatches == null || proteinMatches.size() < 1 ) {

			proteinMatches = YRC_NRSEQUtils.getProteinsContainingPeptide( peptideSequence, nrseqDatabaseId );

			if( proteinMatches == null || proteinMatches.size() < 1 ) {
			
				String msg = "Could not find any proteins for peptide searching the protein sequences.  peptide sequence: " 
						+ peptideSequence; 
				log.error( msg );
				throw new Exception( msg );
			}

			System.out.print( "\t\tFound proteins using YRC_NRSEQ: " );
			for( NRProteinDTO p : proteinMatches ) { System.out.println( p.getNrseqId() + "," ); }

			// save these associations in the database
			boolean unique = true;
			if( proteinMatches.size() > 1 )
				unique = false;

			System.out.println( "\t\tSaving to nrseq_database_peptide_protein the peptide to protein mapping..." );
			for( NRProteinDTO protein : proteinMatches ) {

				NrseqDatabasePeptideProteinDTO prpp = new NrseqDatabasePeptideProteinDTO();
				prpp.setNrseqId( protein.getNrseqId() );
				prpp.setPeptideId( peptideDTO.getId());
				prpp.setNrseqDatabaseId( nrseqDatabaseId );
				prpp.setUnique( unique );

				NrseqDatabasePeptideProteinDAO.getInstance().save( prpp );
			}
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
				System.out.println( "Ignoring hit to " + name + " for peptide sequence: " + peptideSequence );
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