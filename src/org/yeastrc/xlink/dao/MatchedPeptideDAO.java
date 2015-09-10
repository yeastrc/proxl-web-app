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
import org.yeastrc.xlink.dto.MatchedPeptideDTO;

/**
 * 
 *
 */
public class MatchedPeptideDAO {

	private static final Logger log = Logger.getLogger(MatchedPeptideDAO.class);
			
	private MatchedPeptideDAO() { }
	public static MatchedPeptideDAO getInstance() { return new MatchedPeptideDAO(); }



	/**
	 * @param id
	 * @return null if not found
	 * @throws Exception
	 */
	public MatchedPeptideDTO getMatchedPeptideDTOForId( int id ) throws Exception {


		MatchedPeptideDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM matched_peptide WHERE id = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				returnItem = populateResultObject( rs );

			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select MatchedPeptideDTO, id: " + id + ", sql: " + sql;
			
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
	 * @param psm_id
	 * @return 
	 * @throws Exception
	 */
	public List<MatchedPeptideDTO> getMatchedPeptideDTOForPsmId( int psm_id ) throws Exception {


		List<MatchedPeptideDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM matched_peptide WHERE psm_id = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psm_id );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				MatchedPeptideDTO item = populateResultObject( rs );
				results.add(item);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select MatchedPeptideDTO, psm_id: " + psm_id + ", sql: " + sql;
			
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
	public List<MatchedPeptideDTO> getMatchedPeptideDTOForPeptideId( int peptide_id ) throws Exception {


		List<MatchedPeptideDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM matched_peptide WHERE peptide_id = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, peptide_id );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				MatchedPeptideDTO item = populateResultObject( rs );
				results.add(item);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select MatchedPeptideDTO, peptide_id: " + peptide_id + ", sql: " + sql;
			
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
	private MatchedPeptideDTO populateResultObject(ResultSet rs) throws SQLException {
		
		MatchedPeptideDTO returnItem = new MatchedPeptideDTO();

		returnItem.setId( rs.getInt( "id" ) );
		returnItem.setPsm_id( rs.getInt( "psm_id" ) );
		returnItem.setPeptide_id( rs.getInt( "peptide_id" ) );
		
		return returnItem;
	}
	
	
	
	/**
	 * @param entry
	 * @throws Exception
	 */
	public void save( MatchedPeptideDTO entry ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO matched_peptide (psm_id, peptide_id) VALUES (?, ?)";

		try {
			
					  
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setInt( 1,  entry.getPsm_id() );
			pstmt.setInt( 2,  entry.getPeptide_id() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				entry.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert MatchedPeptideDTO" );
			
			
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
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
	}
	
}