package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.SearchProteinLookupDTO;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;

public class SearchProteinLookupDAO {

	private static final Logger log = Logger.getLogger(SearchProteinLookupDAO.class);
			
	private SearchProteinLookupDAO() { }
	public static SearchProteinLookupDAO getInstance() { return new SearchProteinLookupDAO(); }

	/**
	 * For the given search id, will populate the search_protein_lookup table
	 * @param searchId
	 * @throws Exception
	 */
	public void createEntriesForSearch( int searchId ) throws Exception {
		
		// The lookup map of the SearchProteinLookupDTOs we're building for this search
		Map<Integer, SearchProteinLookupDTO> prplMap = new HashMap<Integer, SearchProteinLookupDTO>();
		
		
		Connection conn = null;
		
		try {

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql = null;

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			// process crosslinked peptides
			try {

				if ( log.isDebugEnabled() ) {

					log.debug( "\tProcessing crosslinks..." );
				}
				
				sql = "SELECT a.nrseq_id_1, min(b.q_value), min(c.q_value) FROM crosslink AS a "
						+ "INNER JOIN psm AS b ON a.psm_id = b.id "
						+ "INNER JOIN search_reported_peptide AS c ON (b.search_id = c.search_id AND b.reported_peptide_id = c.reported_peptide_id) "
						+ "WHERE b.search_id = ? GROUP BY a.nrseq_id_1";
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setInt( 1, searchId );

				rs = pstmt.executeQuery();
				
				while( rs.next() ) {
					SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
					prpl.setNrseqId( rs.getInt( 1 ) );
					prpl.setSearchId( searchId );
					prpl.setBestCrosslinkPSMQValue( rs.getDouble( 2 ) );
					prpl.setBestCrosslinkPeptideQValue( rs.getDouble( 3 ) );
					
					prplMap.put( prpl.getNrseqId(), prpl );
				}
				
				rs.close(); rs = null;
				pstmt.close(); pstmt = null;
				
				
				sql = "SELECT a.nrseq_id_2, min(b.q_value), min(c.q_value) FROM crosslink AS a "
						+ "INNER JOIN psm AS b ON a.psm_id = b.id "
						+ "INNER JOIN search_reported_peptide AS c ON (b.search_id = c.search_id AND b.reported_peptide_id = c.reported_peptide_id) "
						+ "WHERE b.search_id = ? GROUP BY a.nrseq_id_2";
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setInt( 1, searchId );

				rs = pstmt.executeQuery();
				
				while( rs.next() ) {
					
					int pid = rs.getInt( 1 );
					if( prplMap.containsKey( pid ) ) {
						
						double psmq = rs.getDouble( 2 );
						double pepq = rs.getDouble( 3 );
						
						if( prplMap.get( pid ).getBestCrosslinkPSMQValue() == null || prplMap.get( pid ).getBestCrosslinkPSMQValue() > psmq )
							prplMap.get( pid ).setBestCrosslinkPSMQValue( psmq );
						
						if( prplMap.get( pid ).getBestCrosslinkPeptideQValue() == null || prplMap.get( pid ).getBestCrosslinkPeptideQValue() > pepq )
							prplMap.get( pid ).setBestCrosslinkPeptideQValue( pepq );
						
					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestCrosslinkPSMQValue( rs.getDouble( 2 ) );
						prpl.setBestCrosslinkPeptideQValue( rs.getDouble( 3 ) );
						
						prplMap.put( prpl.getNrseqId(), prpl );
					}
				}
				
				
			} catch ( Exception e ) {
				
				String msg = "Failed createEntriesForSearch( int searchId ), searchId: " + searchId + ", sql: " + sql;
				
				log.error( msg, e );
				
				throw e;
				

			} finally {
				if( rs != null ) {
					try { rs.close(); } catch( Throwable t ) { ; }
					rs = null;
				}
				
				if( pstmt != null ) {
					try { pstmt.close(); } catch( Throwable t ) { ; }
					pstmt = null;
				}
			}
			
			
			
			// process looplink peptides
			try {
				
				if ( log.isDebugEnabled() ) {

					log.debug( "\tProcessing looplinks..." );
				}

				sql = "SELECT a.nrseq_id, min(b.q_value), min(c.q_value) FROM looplink AS a "
						+ "INNER JOIN psm AS b ON a.psm_id = b.id "
						+ "INNER JOIN search_reported_peptide AS c ON (b.search_id = c.search_id AND b.reported_peptide_id = c.reported_peptide_id) "
						+ "WHERE b.search_id = ? GROUP BY a.nrseq_id";
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setInt( 1, searchId );

				rs = pstmt.executeQuery();
				
				while( rs.next() ) {
					
					int pid = rs.getInt( 1 );
					if( prplMap.containsKey( pid ) ) {
						
						prplMap.get( pid ).setBestLooplinkPSMQValue( rs.getDouble( 2 ) );
						prplMap.get( pid ).setBestLooplinkPeptideQValue( rs.getDouble( 3 ) );
						
					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestLooplinkPSMQValue( rs.getDouble( 2 ) );
						prpl.setBestLooplinkPeptideQValue( rs.getDouble( 3 ) );
						
						prplMap.put( prpl.getNrseqId(), prpl );
					}
				}
				
			} catch ( Exception e ) {
				
				String msg = "Failed process looplink peptides: createEntriesForSearch( int searchId ), searchId: " + searchId + ", sql: " + sql;
				
				log.error( msg, e );
				
				throw e;
				
			} finally {
				if( rs != null ) {
					try { rs.close(); } catch( Throwable t ) { ; }
					rs = null;
				}
				
				if( pstmt != null ) {
					try { pstmt.close(); } catch( Throwable t ) { ; }
					pstmt = null;
				}
			}
			
			

			
			// process monolink peptides
			try {

				if ( log.isDebugEnabled() ) {

					log.debug( "\tProcessing monolinks..." );
				}
				
				sql = "SELECT a.nrseq_id, min(b.q_value), min(c.q_value) FROM monolink AS a "
						+ "INNER JOIN psm AS b ON a.psm_id = b.id "
						+ "INNER JOIN search_reported_peptide AS c ON (b.search_id = c.search_id AND b.reported_peptide_id = c.reported_peptide_id) "
						+ "WHERE b.search_id = ? GROUP BY a.nrseq_id";
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setInt( 1, searchId );

				rs = pstmt.executeQuery();
				
				while( rs.next() ) {
					
					int pid = rs.getInt( 1 );
					if( prplMap.containsKey( pid ) ) {
						
						prplMap.get( pid ).setBestMonolinkPSMQValue( rs.getDouble( 2 ) );
						prplMap.get( pid ).setBestMonolinkPeptideQValue( rs.getDouble( 3 ) );
						
					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestMonolinkPSMQValue( rs.getDouble( 2 ) );
						prpl.setBestMonolinkPeptideQValue( rs.getDouble( 3 ) );
						
						prplMap.put( prpl.getNrseqId(), prpl );
					}
				}
				
			} catch ( Exception e ) {
				
				String msg = "Failed process monolink peptides: createEntriesForSearch( int searchId ), searchId: " + searchId + ", sql: " + sql;
				
				log.error( msg, e );
				
				throw e;
				
			} finally {
				if( rs != null ) {
					try { rs.close(); } catch( Throwable t ) { ; }
					rs = null;
				}
				
				if( pstmt != null ) {
					try { pstmt.close(); } catch( Throwable t ) { ; }
					pstmt = null;
				}
			}
			
			
			
			// process dimer peptides
			try {

				if ( log.isDebugEnabled() ) {

					log.debug( "\tProcessing dimers..." );
				}
				
				sql = "SELECT a.nrseq_id_1, min(b.q_value), min(c.q_value) FROM dimer AS a "
						+ "INNER JOIN psm AS b ON a.psm_id = b.id "
						+ "INNER JOIN search_reported_peptide AS c ON (b.search_id = c.search_id AND b.reported_peptide_id = c.reported_peptide_id) "
						+ "WHERE b.search_id = ? GROUP BY a.nrseq_id_1";
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setInt( 1, searchId );

				rs = pstmt.executeQuery();
				
				while( rs.next() ) {

					int pid = rs.getInt( 1 );
					if( prplMap.containsKey( pid ) ) {
						
						double psmq = rs.getDouble( 2 );
						double pepq = rs.getDouble( 3 );
						
						prplMap.get( pid ).setBestDimerPSMQValue( psmq );
						prplMap.get( pid ).setBestDimerPeptideQValue( pepq );
						
					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestDimerPSMQValue( rs.getDouble( 2 ) );
						prpl.setBestDimerPeptideQValue( rs.getDouble( 3 ) );
						
						prplMap.put( prpl.getNrseqId(), prpl );
					}
				}
				
				rs.close(); rs = null;
				pstmt.close(); pstmt = null;
				
				
				sql = "SELECT a.nrseq_id_2, min(b.q_value), min(c.q_value) FROM dimer AS a "
						+ "INNER JOIN psm AS b ON a.psm_id = b.id "
						+ "INNER JOIN search_reported_peptide AS c ON (b.search_id = c.search_id AND b.reported_peptide_id = c.reported_peptide_id) "
						+ "WHERE b.search_id = ? GROUP BY a.nrseq_id_2";
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setInt( 1, searchId );

				rs = pstmt.executeQuery();
				
				while( rs.next() ) {
					
					int pid = rs.getInt( 1 );
					if( prplMap.containsKey( pid ) ) {
						
						double psmq = rs.getDouble( 2 );
						double pepq = rs.getDouble( 3 );
						
						if( prplMap.get( pid ).getBestDimerPSMQValue() == null || prplMap.get( pid ).getBestDimerPSMQValue() > psmq )
							prplMap.get( pid ).setBestDimerPSMQValue( psmq );
						
						if( prplMap.get( pid ).getBestDimerPeptideQValue() == null || prplMap.get( pid ).getBestDimerPeptideQValue() > pepq )
							prplMap.get( pid ).setBestDimerPeptideQValue( pepq );
						
					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestDimerPSMQValue( rs.getDouble( 2 ) );
						prpl.setBestDimerPeptideQValue( rs.getDouble( 3 ) );
						
						prplMap.put( prpl.getNrseqId(), prpl );
					}
				}
				
			} catch ( Exception e ) {
				
				String msg = "Failed process dimer peptides: createEntriesForSearch( int searchId ), searchId: " + searchId + ", sql: " + sql;
				
				log.error( msg, e );
				
				throw e;
				
			} finally {
				if( rs != null ) {
					try { rs.close(); } catch( Throwable t ) { ; }
					rs = null;
				}
				
				if( pstmt != null ) {
					try { pstmt.close(); } catch( Throwable t ) { ; }
					pstmt = null;
				}
			}
			

			
			// process unlinked peptides
			try {
				
				if ( log.isDebugEnabled() ) {

					log.debug( "\tProcessing unlinked..." );
				}

				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				int databaseId = YRC_NRSEQUtils.getDatabaseIdFromName( search.getFastaFilename() );
				
				sql = "SELECT a.nrseq_id, min(c.q_value), min(d.q_value) FROM nrseq_database_peptide_protein AS a "
						+ "INNER JOIN psm_peptide AS b ON a.peptide_id = b.peptide_id "
						+ "INNER JOIN psm AS c ON b.psm_id = c.id "
						+ "INNER JOIN search_reported_peptide AS d ON (c.search_id = d.search_id AND c.reported_peptide_id = d.reported_peptide_id) "
						+ "WHERE c.search_id = ? AND a.nrseq_database_id = ? AND c.type = ? "
						+ "GROUP BY a.nrseq_id";
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setInt( 1, searchId );
				pstmt.setInt( 2,  databaseId );
				pstmt.setString( 3,  XLinkUtils.getTypeString( XLinkUtils.TYPE_UNLINKED ) );

				rs = pstmt.executeQuery();
				
				while( rs.next() ) {

					int pid = rs.getInt( 1 );
					if( prplMap.containsKey( pid ) ) {
						
						double psmq = rs.getDouble( 2 );
						double pepq = rs.getDouble( 3 );
						
						prplMap.get( pid ).setBestUnlinkedPSMQValue( psmq );
						prplMap.get( pid ).setBestUnlinkedPeptideQValue( pepq );
						
					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestUnlinkedPSMQValue( rs.getDouble( 2 ) );
						prpl.setBestUnlinkedPeptideQValue( rs.getDouble( 3 ) );
						
						prplMap.put( prpl.getNrseqId(), prpl );
					}
				}	
				
			} catch ( Exception e ) {
				
				String msg = "Failed process unlinked peptides: createEntriesForSearch( int searchId ), searchId: " + searchId + ", sql: " + sql;
				
				log.error( msg, e );
				
				throw e;
				
			} finally {
				if( rs != null ) {
					try { rs.close(); } catch( Throwable t ) { ; }
					rs = null;
				}
				
				if( pstmt != null ) {
					try { pstmt.close(); } catch( Throwable t ) { ; }
					pstmt = null;
				}
			}
			

			// save all the SearchProteinLookupDTO's to the database
			if ( log.isDebugEnabled() ) {

				log.debug( "Saving to database..." );
			}
			for( int pid : prplMap.keySet() ) {
				save( prplMap.get( pid ) );
			}			
			
			
		} finally {
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		
		

		
		
	}
	
	/**
	 * Save the associated data to the database
	 * @param prpl
	 * @throws Exception
	 */
	public void save( SearchProteinLookupDTO prpl ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "INSERT INTO search_protein_lookup (search_id, nrseq_id, bestCrosslinkPSMQValue, bestCrosslinkPeptideQValue, bestLooplinkPSMQValue, "
				+ "bestLooplinkPeptideQValue, bestMonolinkPSMQValue, bestMonolinkPeptideQValue, bestDimerPSMQValue, bestDimerPeptideQValue, bestUnlinkedPSMQValue, "
				+ "bestUnlinkedPeptideQValue) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1,  prpl.getSearchId() );
			pstmt.setInt( 2,  prpl.getNrseqId() );
			
			if( prpl.getBestCrosslinkPSMQValue() != null )
				pstmt.setDouble( 3,  prpl.getBestCrosslinkPSMQValue() );
			else
				pstmt.setNull( 3, java.sql.Types.DOUBLE );

			if( prpl.getBestCrosslinkPeptideQValue() != null )
				pstmt.setDouble( 4,  prpl.getBestCrosslinkPeptideQValue() );
			else
				pstmt.setNull( 4, java.sql.Types.DOUBLE );

			if( prpl.getBestLooplinkPSMQValue() != null )
				pstmt.setDouble( 5,  prpl.getBestLooplinkPSMQValue() );
			else
				pstmt.setNull( 5, java.sql.Types.DOUBLE );

			if( prpl.getBestLooplinkPeptideQValue() != null )
				pstmt.setDouble( 6,  prpl.getBestLooplinkPeptideQValue() );
			else
				pstmt.setNull( 6, java.sql.Types.DOUBLE );

			if( prpl.getBestMonolinkPSMQValue() != null )
				pstmt.setDouble( 7,  prpl.getBestMonolinkPSMQValue() );
			else
				pstmt.setNull( 7, java.sql.Types.DOUBLE );

			if( prpl.getBestMonolinkPeptideQValue() != null )
				pstmt.setDouble( 8,  prpl.getBestMonolinkPeptideQValue() );
			else
				pstmt.setNull( 8, java.sql.Types.DOUBLE );

			if( prpl.getBestDimerPSMQValue() != null )
				pstmt.setDouble( 9,  prpl.getBestDimerPSMQValue() );
			else
				pstmt.setNull( 9, java.sql.Types.DOUBLE );

			if( prpl.getBestDimerPeptideQValue() != null )
				pstmt.setDouble( 10,  prpl.getBestDimerPeptideQValue() );
			else
				pstmt.setNull( 10, java.sql.Types.DOUBLE );

			if( prpl.getBestUnlinkedPSMQValue() != null )
				pstmt.setDouble( 11,  prpl.getBestUnlinkedPSMQValue() );
			else
				pstmt.setNull( 11, java.sql.Types.DOUBLE );

			if( prpl.getBestUnlinkedPeptideQValue() != null )
				pstmt.setDouble( 12,  prpl.getBestUnlinkedPeptideQValue() );
			else
				pstmt.setNull( 12, java.sql.Types.DOUBLE );
			
			pstmt.executeUpdate();

			
		} catch ( Exception e ) {
			
			String msg = "Failed save( SearchProteinLookupDTO prpl ), prpl.getNrseqId(): " + prpl.getNrseqId() 
					+ ", prpl.getSearchId(): " + prpl.getSearchId() + ", sql: " + sql;
			
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
	}
	
}