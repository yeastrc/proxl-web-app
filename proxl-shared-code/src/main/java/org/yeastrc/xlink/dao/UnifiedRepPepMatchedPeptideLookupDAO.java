package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;

/**
 * 
 *
 */
public class UnifiedRepPepMatchedPeptideLookupDAO {

	private static final Logger log = LoggerFactory.getLogger( UnifiedRepPepMatchedPeptideLookupDAO.class);
			
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
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
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
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
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
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
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
	
	
	
	
	
}