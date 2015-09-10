package org.yeastrc.xlink.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.NRProteinDTO;


public class YRC_NRSEQUtils {

	private static final Logger log = Logger.getLogger(YRC_NRSEQUtils.class);

	/**
	 * Get the protein's name based on the id and databaseName
	 * @param id
	 * @param databaseName
	 * @return
	 * @throws Exception
	 */
	public static String getProteinNameFromId( int id, String databaseName ) throws Exception {
		String name = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT accessionString FROM tblProteinDatabase WHERE databaseID = ? AND proteinID = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.YRC_NRSEQ );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, YRC_NRSEQUtils.getDatabaseIdFromName( databaseName ) );
			pstmt.setInt( 2, id );
			
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "Unable to find protein name for " + id );
			
			name = rs.getString( 1 );
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.YRC_NRSEQ + "' sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
				
		return name;
	}
	
	
	/**
	 * Get the protein's name based on the id and databaseName
	 * @param id
	 * @param databaseId
	 * @return
	 * @throws Exception
	 */
	public static String getProteinNameFromId( int id, int databaseId ) throws Exception {
		String name = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT accessionString FROM tblProteinDatabase WHERE databaseID = ? AND proteinID = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.YRC_NRSEQ );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, databaseId );
			pstmt.setInt( 2, id );
			
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "Unable to find protein name for " + id );
			
			name = rs.getString( 1 );
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.YRC_NRSEQ + "' sql: " + sql, e );
			
			throw e;
			

		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
				
		return name;
	}
	
	
	/**
	 * Get the YRC_NRSEQ protein ID for the given name for the given database (e.g., fasta file name)
	 * @param name
	 * @param databaseName
	 * @return
	 * @throws Exception
	 */
	public static int getProteinIdFromName( String name, String databaseName ) throws Exception {
		int pid = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "SELECT proteinId FROM tblProteinDatabase WHERE databaseID = ? AND accessionString LIKE ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.YRC_NRSEQ );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, YRC_NRSEQUtils.getDatabaseIdFromName( databaseName ) );
			pstmt.setString( 2, name + "%" );
			
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "Unable to find protein ID for " + name );
			
			pid = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.YRC_NRSEQ + "' sql: " + sql, e );
			
			throw e;
			

		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
				
		return pid;
	}
	
	/**
	 * Get the NRSEQ id for a protein with the given name from the given database (e.g., FASTA file name)
	 * @param name
	 * @param databaseId
	 * @return
	 * @throws Exception
	 */
	public static int getProteinIdFromName( String name, int databaseId ) throws Exception {
		int pid = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "SELECT proteinId FROM tblProteinDatabase WHERE databaseID = ? AND accessionString LIKE ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.YRC_NRSEQ );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, databaseId );
			pstmt.setString( 2, name + "%" );
			
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "Unable to find protein ID for " + name );
			
			pid = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.YRC_NRSEQ + "' sql: " + sql, e );
			
			throw e;
			

		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
				
		return pid;
	}
	
	/**
	 * Get the database ID for the given database name (e.g., FASTA file name)
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Integer> databaseIdCacheMap;
	public static int getDatabaseIdFromName( String name ) throws Exception {
		
		if( databaseIdCacheMap == null )
			databaseIdCacheMap = new HashMap<String, Integer>();
		
		if( !databaseIdCacheMap.containsKey( name ) ) {
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			final String sql = "SELECT id FROM tblDatabase WHERE name = ?";

			try {
				
				conn = DBConnectionFactory.getConnection( DBConnectionFactory.YRC_NRSEQ );
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setString( 1, name );
				
				rs = pstmt.executeQuery();
				if( !rs.next() )
					throw new Exception( "Unable to find database ID for " + name );
				
				databaseIdCacheMap.put( name, rs.getInt( 1 ) );
				
			} catch ( Exception e ) {
				
				log.error( "ERROR: database connection: '" + DBConnectionFactory.YRC_NRSEQ + "' sql: " + sql, e );
				
				throw e;
				

				} finally {
					
					// be sure database handles are closed
					if( rs != null ) {
						try { rs.close(); } catch( Throwable t ) { ; }
						rs = null;
					}
					
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
		
		return databaseIdCacheMap.get( name );
	}

	/**
	 * Get a collection of proteins whose sequences contain the supplied peptide
	 * @param peptide The peptide sequence
	 * @param databaseId The YRC_NRSEQ database ID to restrict the search to
	 * @return
	 */
	public static Collection<NRProteinDTO> getProteinsContainingPeptide( String peptide, int databaseId ) throws Exception {
		Collection<NRProteinDTO> proteins = new HashSet<NRProteinDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT DISTINCT a.id FROM tblProtein AS a INNER JOIN tblProteinSequence AS b ON a.sequenceID = b.id INNER JOIN tblProteinDatabase AS c ON a.id = c.proteinID WHERE c.databaseID = ? AND b.sequence LIKE ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.YRC_NRSEQ );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, databaseId );
			pstmt.setString( 2, "%" + peptide + "%" );
			
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				proteins.add( NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) );
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.YRC_NRSEQ + "' sql: " + sql, e );
			
			throw e;
			

		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		return proteins;
	}
	
//	
//	/**
//	 * Find and return all positions that correspond to the supplied residue (e.g. K for lysine)
//	 * Counting starts at 1
//	 * @param proteinId The protein to search
//	 * @param residue The residue to search for
//	 * @return
//	 * @throws Exception
//	 */
//	public static Collection<Integer> getPositionsOfResidue( int proteinId, String residue ) throws Exception {
//		Collection<Integer> positions = new ArrayList<Integer>();
//		String sequence = getSequence( proteinId );
//		
//		int index = sequence.indexOf( residue );
//		while (index >= 0) {
//		    positions.add( index + 1 );
//		    index = sequence.indexOf( residue, index + 1);
//		}
//		
//		return positions;		
//	}
	
	
	/**
	 * Get the sequence for the supplied protein from the database
	 * @param proteinId
	 * @return
	 * @throws Exception
	 */
	public static String getSequence( int proteinId ) throws Exception {
		String seq = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT a.sequence FROM tblProteinSequence AS a INNER JOIN tblProtein AS b ON a.id = b.sequenceID WHERE b.id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.YRC_NRSEQ );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, proteinId );
			
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "Unable to find sequence for protein: " + proteinId );
			
			seq = rs.getString( 1 );
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.YRC_NRSEQ + "' sql: " + sql, e );
			
			throw e;
			

		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		return seq;
	}
	
	/**
	 * Get the taxonomy ID for the given protein ID
	 * @param proteinId
	 * @return
	 * @throws Exception
	 */
	public static int getTaxonomyId( int proteinId ) throws Exception {
		int id = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT speciesID FROM tblProtein WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.YRC_NRSEQ );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, proteinId );
			
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "Unable to find taxonomy for protein: " + proteinId );
			
			id = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.YRC_NRSEQ + "' sql: " + sql, e );
			
			throw e;
			

		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		return id;
	}
	
}
