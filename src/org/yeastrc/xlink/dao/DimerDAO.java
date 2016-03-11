package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.DimerDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;

public class DimerDAO {
	
	private static final Logger log = Logger.getLogger( DimerDAO.class );

	private DimerDAO() { }
	public static DimerDAO getInstance() { return new DimerDAO(); }



	/**
	 * 	 * Gets a random DimerDTO for psmId
	 * 
	 * Do Not Use the Protein data from this 
	 * since there may be other dimer records with different protein id and protein position data  
	 * 
	 * 
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public DimerDTO getARandomDimerDTOForPsmId( int psmId ) throws Exception {
		
		
		DimerDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM dimer WHERE psm_id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				result = new DimerDTO();
				
				result.setId( rs.getInt( "id" ) );
				
				result.setPeptide1Id( rs.getInt( "peptide_1_id" ) );
//				result.setProtein1Id( rs.getInt( "nrseq_id_1" ) );

				result.setPeptide2Id( rs.getInt( "peptide_2_id" ) );
//				result.setProtein2Id( rs.getInt( "nrseq_id_2" ) );
				
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
	 * Gets all DimerDTO for psmId
	 * 
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public List<DimerDTO> getAllDimerDTOForPsmId( int psmId ) throws Exception {
		
		
		List<DimerDTO> resultList = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM dimer WHERE psm_id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {

				DimerDTO result = populateFromResultSet( rs );

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
	public DimerDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		DimerDTO result = new DimerDTO();
		
		result.setId( rs.getInt( "id" ) );
		

		int proteinId_1 = rs.getInt( "nrseq_id_1" );
		int proteinId_2 = rs.getInt( "nrseq_id_2" );
		
		NRProteinDTO nrProteinDTO_1 = new NRProteinDTO();
		NRProteinDTO nrProteinDTO_2 = new NRProteinDTO();
		
		nrProteinDTO_1.setNrseqId(proteinId_1);
		nrProteinDTO_2.setNrseqId(proteinId_2);
		
		result.setProtein1(nrProteinDTO_1);
		result.setProtein2(nrProteinDTO_2);
		
		result.setPeptide1Id( rs.getInt( "peptide_1_id" ) );

		result.setPeptide2Id( rs.getInt( "peptide_2_id" ) );
		
		return result;
	}
	
	
	
	
}
