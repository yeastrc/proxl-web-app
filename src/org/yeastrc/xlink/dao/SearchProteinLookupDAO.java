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
					if ( rs.wasNull() ) {
						prpl.setBestCrosslinkPeptideQValue( null );
					}
					
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
					
					int proteinNrseqId = rs.getInt( 1 );
					
					if( prplMap.containsKey( proteinNrseqId ) ) {
						
						double psmq = rs.getDouble( 2 );
						
						Double pepq = rs.getDouble( 3 );
						if ( rs.wasNull() ) {
							pepq = null;
						}
						
						SearchProteinLookupDTO searchProteinLookupDTOForProteinNrseqId 
							= prplMap.get( proteinNrseqId );
						
						if( searchProteinLookupDTOForProteinNrseqId.getBestCrosslinkPSMQValue() == null || searchProteinLookupDTOForProteinNrseqId.getBestCrosslinkPSMQValue() > psmq ) {
							searchProteinLookupDTOForProteinNrseqId.setBestCrosslinkPSMQValue( psmq );
						}
						
						if( pepq == null ) {
						
							//  Peptide level q value of null is the "lowest" possible value
							
							searchProteinLookupDTOForProteinNrseqId.setBestCrosslinkPeptideQValue( pepq );
							
						} else if( searchProteinLookupDTOForProteinNrseqId.getBestCrosslinkPeptideQValue() != null && searchProteinLookupDTOForProteinNrseqId.getBestCrosslinkPeptideQValue() > pepq ) {
							
							searchProteinLookupDTOForProteinNrseqId.setBestCrosslinkPeptideQValue( pepq );
						}
						
					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
						
						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestCrosslinkPSMQValue( rs.getDouble( 2 ) );
						prpl.setBestCrosslinkPeptideQValue( rs.getDouble( 3 ) );
						if ( rs.wasNull() ) {
							prpl.setBestCrosslinkPeptideQValue( null );
						}
						
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
					
					int proteinNrseqId = rs.getInt( 1 );
					
					if( prplMap.containsKey( proteinNrseqId ) ) {
						
						SearchProteinLookupDTO searchProteinLookupDTOForProteinNrseqId = 
								prplMap.get( proteinNrseqId );

						searchProteinLookupDTOForProteinNrseqId.setBestLooplinkPSMQValue( rs.getDouble( 2 ) );
						searchProteinLookupDTOForProteinNrseqId.setBestLooplinkPeptideQValue( rs.getDouble( 3 ) );
						if ( rs.wasNull() ) {
							searchProteinLookupDTOForProteinNrseqId.setBestLooplinkPeptideQValue( null );
						}


					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();

						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestLooplinkPSMQValue( rs.getDouble( 2 ) );
						
						prpl.setBestLooplinkPeptideQValue( rs.getDouble( 3 ) );
						if ( rs.wasNull() ) {
							prpl.setBestLooplinkPeptideQValue( null );
						}

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
					
					int proteinNrseqId = rs.getInt( 1 );
					
					if( prplMap.containsKey( proteinNrseqId ) ) {
						
						SearchProteinLookupDTO searchProteinLookupDTOForProteinNrseqId = 
								prplMap.get( proteinNrseqId );

						searchProteinLookupDTOForProteinNrseqId.setBestMonolinkPSMQValue( rs.getDouble( 2 ) );
						searchProteinLookupDTOForProteinNrseqId.setBestMonolinkPeptideQValue( rs.getDouble( 3 ) );
						if ( rs.wasNull() ) {
							searchProteinLookupDTOForProteinNrseqId.setBestMonolinkPeptideQValue( null );
						}


					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestMonolinkPSMQValue( rs.getDouble( 2 ) );
						prpl.setBestMonolinkPeptideQValue( rs.getDouble( 3 ) );
						if ( rs.wasNull() ) {
							prpl.setBestMonolinkPeptideQValue( null );
						}
						
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

					int proteinNrseqId = rs.getInt( 1 );
					
					if( prplMap.containsKey( proteinNrseqId ) ) {
						
						SearchProteinLookupDTO searchProteinLookupDTOForProteinNrseqId = 
								prplMap.get( proteinNrseqId );

						double psmq = rs.getDouble( 2 );
						Double pepq = rs.getDouble( 3 );
						if ( rs.wasNull() ) {
							pepq = null;
						}

						searchProteinLookupDTOForProteinNrseqId.setBestDimerPSMQValue( psmq );
						searchProteinLookupDTOForProteinNrseqId.setBestDimerPeptideQValue( pepq );
						
					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestDimerPSMQValue( rs.getDouble( 2 ) );
						prpl.setBestDimerPeptideQValue( rs.getDouble( 3 ) );
						if ( rs.wasNull() ) {
							prpl.setBestDimerPeptideQValue( null ); 
						}
								
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
					
					int proteinNrseqId = rs.getInt( 1 );
					
					if( prplMap.containsKey( proteinNrseqId ) ) {
						
						SearchProteinLookupDTO searchProteinLookupDTOForProteinNrseqId = 
								prplMap.get( proteinNrseqId );

						double psmq = rs.getDouble( 2 );
						Double pepq = rs.getDouble( 3 );
						if ( rs.wasNull() ) {
							pepq = null;
						}

						if( searchProteinLookupDTOForProteinNrseqId.getBestDimerPSMQValue() == null || searchProteinLookupDTOForProteinNrseqId.getBestDimerPSMQValue() > psmq ) {
							searchProteinLookupDTOForProteinNrseqId.setBestDimerPSMQValue( psmq );
						}
						

						if( pepq == null ) {
						
							//  Peptide level q value of null is the "lowest" possible value
							
							searchProteinLookupDTOForProteinNrseqId.setBestDimerPeptideQValue( pepq );
							
						} else if( searchProteinLookupDTOForProteinNrseqId.getBestDimerPeptideQValue() != null && searchProteinLookupDTOForProteinNrseqId.getBestDimerPeptideQValue() > pepq ) {
							
							searchProteinLookupDTOForProteinNrseqId.setBestDimerPeptideQValue( pepq );
						}
						
					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestDimerPSMQValue( rs.getDouble( 2 ) );
						prpl.setBestDimerPeptideQValue( rs.getDouble( 3 ) );
						if ( rs.wasNull() ) {
							prpl.setBestDimerPeptideQValue( null );
						}

						
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

					int proteinNrseqId = rs.getInt( 1 );
					
					if( prplMap.containsKey( proteinNrseqId ) ) {

						SearchProteinLookupDTO searchProteinLookupDTOForProteinNrseqId = 
								prplMap.get( proteinNrseqId );

						double psmq = rs.getDouble( 2 );
						Double pepq = rs.getDouble( 3 );
						if ( rs.wasNull() ) {
							pepq = null;
						}

						searchProteinLookupDTOForProteinNrseqId.setBestUnlinkedPSMQValue( psmq );
						searchProteinLookupDTOForProteinNrseqId.setBestUnlinkedPeptideQValue( pepq );
						
					} else {
					
						SearchProteinLookupDTO prpl = new SearchProteinLookupDTO();
						prpl.setNrseqId( rs.getInt( 1 ) );
						prpl.setSearchId( searchId );
						prpl.setBestUnlinkedPSMQValue( rs.getDouble( 2 ) );
						prpl.setBestUnlinkedPeptideQValue( rs.getDouble( 3 ) );
						if ( rs.wasNull() ) {
							prpl.setBestUnlinkedPeptideQValue( null );
						}
						
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