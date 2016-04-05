package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.MonolinkDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;



public class MonolinkDAO {

	private static final Logger log = Logger.getLogger(MonolinkDAO.class);
			
	private MonolinkDAO() { }
	public static MonolinkDAO getInstance() { return new MonolinkDAO(); }

	


	/**
	 * 	 * Gets a random MonolinkDTO for psmId
	 * 
	 * Do Not Use the Protein data from this 
	 * since there may be other crosslink records with different protein id and protein position data  
	 * 
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public MonolinkDTO getARandomMonolinkDTOForPsmId( int psmId ) throws Exception {
		
		
		MonolinkDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM monolink WHERE psm_id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				result = new MonolinkDTO();
				
				result.setId( rs.getInt( "id" ) );
				
				result.setPeptideId( rs.getInt( "peptide_id" ) );
				result.setPeptidePosition( rs.getInt( "peptide_position" ) );
//				result.setProteinPosition( rs.getInt( "protein_position" ) );
			}
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
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
		
		
		return result;
	}


	/**
	 * Gets all MonolinkDTO for psmId
	 * 
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public List<MonolinkDTO> getAllMonolinkDTOForPsmId( int psmId ) throws Exception {
		
		
		List<MonolinkDTO> resultList = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM monolink WHERE psm_id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {

				MonolinkDTO result = populateFromResultSet( rs );

				resultList.add( result );
			}
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
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
		
		
		return resultList;
	}




	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private MonolinkDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		MonolinkDTO result = new MonolinkDTO();
		
		result.setId( rs.getInt( "id" ) );

		int proteinId = rs.getInt( "nrseq_id" );
		
		NRProteinDTO nrProteinDTO = new NRProteinDTO();
		
		nrProteinDTO.setNrseqId(proteinId);
		
		result.setProtein(nrProteinDTO);
		
		result.setPeptideId( rs.getInt( "peptide_id" ) );
		result.setPeptidePosition( rs.getInt( "peptide_position" ) );
		result.setProteinPosition( rs.getInt( "protein_position" ) );
		
		return result;
	}
	
	
}
