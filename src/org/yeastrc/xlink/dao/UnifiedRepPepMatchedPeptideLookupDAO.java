package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;

/**
 * 
 *
 */
public class UnifiedRepPepMatchedPeptideLookupDAO {

	private static final Logger log = Logger.getLogger(UnifiedRepPepMatchedPeptideLookupDAO.class);
			
	private UnifiedRepPepMatchedPeptideLookupDAO() { }
	public static UnifiedRepPepMatchedPeptideLookupDAO getInstance() { return new UnifiedRepPepMatchedPeptideLookupDAO(); }



	/**
	 * @param id
	 * @return null if not found
	 * @throws Exception
	 */
	public UnifiedRepPepMatchedPeptideLookupDTO getUnifiedRpMatchedPeptideDTOForId( int id ) throws Exception {


		UnifiedRepPepMatchedPeptideLookupDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM unified_rep_pep_matched_peptide_lookup WHERE id = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				returnItem = populateResultObject( rs );

			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select UnifiedRpMatchedPeptideDTO, id: " + id + ", sql: " + sql;
			
			log.error( msg, e );
			
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
		
		return returnItem;
	}
	
	

	/**
	 * @param unified_reported_peptide_id
	 * @return 
	 * @throws Exception
	 */
	public List<UnifiedRepPepMatchedPeptideLookupDTO> getUnifiedRpMatchedPeptideDTOForUnifiedReportedPeptideId( int unified_reported_peptide_id ) throws Exception {


		List<UnifiedRepPepMatchedPeptideLookupDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM unified_rep_pep_matched_peptide_lookup WHERE unified_reported_peptide_id = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, unified_reported_peptide_id );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				UnifiedRepPepMatchedPeptideLookupDTO item = populateResultObject( rs );
				results.add(item);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select UnifiedRpMatchedPeptideDTO, unified_reported_peptide_id: " + unified_reported_peptide_id + ", sql: " + sql;
			
			log.error( msg, e );
			
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
		
		return results;
	}
	
	

	/**
	 * @param peptide_id
	 * @return 
	 * @throws Exception
	 */
	public List<UnifiedRepPepMatchedPeptideLookupDTO> getUnifiedRpMatchedPeptideDTOForPeptideId( int peptide_id ) throws Exception {


		List<UnifiedRepPepMatchedPeptideLookupDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM unified_rep_pep_matched_peptide_lookup WHERE peptide_id = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, peptide_id );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				UnifiedRepPepMatchedPeptideLookupDTO item = populateResultObject( rs );
				results.add(item);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select UnifiedRpMatchedPeptideDTO, peptide_id: " + peptide_id + ", sql: " + sql;
			
			log.error( msg, e );
			
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
		
		return results;
	}
	

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private UnifiedRepPepMatchedPeptideLookupDTO populateResultObject(ResultSet rs) throws SQLException {
		
		UnifiedRepPepMatchedPeptideLookupDTO returnItem = new UnifiedRepPepMatchedPeptideLookupDTO();

		returnItem.setId( rs.getInt( "id" ) );
		returnItem.setUnifiedReportedPeptideId( rs.getInt( "unified_reported_peptide_id" ) );
		returnItem.setPeptideId( rs.getInt( "peptide_id" ) );
		
		returnItem.setPeptideOrder( rs.getInt( "peptide_order" ) );
		
		Integer linkPosition = null; 
		int linkPositionInt = rs.getInt( "link_position_1" );

		if ( ! rs.wasNull() ) {
			
			linkPosition = linkPositionInt;
		}
		
		returnItem.setLinkPosition1( linkPosition );
		
		
		linkPosition = null; 
		linkPositionInt = rs.getInt( "link_position_2" );

		if ( ! rs.wasNull() ) {
			
			linkPosition = linkPositionInt;
		}
		
		returnItem.setLinkPosition2( linkPosition );
				
		return returnItem;
	}
	
	
	
	/**
	 * @param entry
	 * @throws Exception
	 */
	public void save( UnifiedRepPepMatchedPeptideLookupDTO entry ) throws Exception {
		
		Connection conn = null;

		try {
			
					  
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			save( entry, conn );
			
		} catch ( Exception e ) {
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed

			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
	}
	
	

	
	/**
	 * @param entry
	 * @throws Exception
	 */
	public void save( UnifiedRepPepMatchedPeptideLookupDTO entry, Connection conn ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO unified_rep_pep_matched_peptide_lookup (unified_reported_peptide_id, peptide_id, peptide_order, link_position_1, link_position_2) VALUES ( ?, ?, ?, ?, ? )";
		  
		try {
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  entry.getUnifiedReportedPeptideId() );
			counter++;
			pstmt.setInt( counter,  entry.getPeptideId() );
			counter++;
			pstmt.setInt( counter,  entry.getPeptideOrder() );
			
			counter++;
			
			if ( entry.getLinkPosition1() != null ) {
				pstmt.setInt( counter, entry.getLinkPosition1() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}

			counter++;
			
			if ( entry.getLinkPosition2() != null ) {
				pstmt.setInt( counter, entry.getLinkPosition2() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				entry.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert UnifiedRpMatchedPeptideDTO" );
			
			
		} catch ( Exception e ) {
			
			String msg = "Failed to save, sql: " + sql;
			
			log.error( msg, e );
			
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
			
			
		}
		
	}
	
	
}