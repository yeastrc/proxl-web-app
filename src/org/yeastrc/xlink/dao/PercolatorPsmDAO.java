package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PercolatorPsmDTO;


public class PercolatorPsmDAO {

	private static final Logger log = Logger.getLogger(PercolatorPsmDAO.class);
			
	private PercolatorPsmDAO() { }
	public static PercolatorPsmDAO getInstance() { return new PercolatorPsmDAO(); }
	
	
	
	
	public PercolatorPsmDTO getPercolatorPsmDTO( int psmId ) throws Exception {
		
		
		PercolatorPsmDTO percolatorPsmDTO = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		//String sql = "INSERT INTO percolator_psm (psm_id, q_value, svm_score, calc_mass, pep, perc_percolatorPsmDTO_id) ";

		String sql = "SELECT * FROM percolator_psm WHERE psm_id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				percolatorPsmDTO = new PercolatorPsmDTO();
				
				percolatorPsmDTO.setPsmDBId( rs.getInt( "psm_id" ) );
				percolatorPsmDTO.setPercolatorFileId( rs.getInt( "percolator_file_id" ) );
				
				percolatorPsmDTO.setqValue( rs.getDouble( "q_value" ) );
				percolatorPsmDTO.setCalcMass( rs.getDouble( "calc_mass" ) );
				percolatorPsmDTO.setPep( rs.getDouble( "pep" ) );
				percolatorPsmDTO.setSvmScore( rs.getDouble( "svm_score" ) );
				percolatorPsmDTO.setPsmId( rs.getString( "perc_psm_id" ) );
				
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
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
		
		
		return percolatorPsmDTO;
	}
	

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public void saveToDatabase( PercolatorPsmDTO item ) throws Exception {

		Connection connection = null;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			saveToDatabase( item, connection );
			
		} finally {

			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}
	}
	

	public void saveToDatabase( PercolatorPsmDTO percolatorPsmDTO, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO percolator_psm (psm_id, percolator_file_id, q_value, svm_score, calc_mass, pep, perc_psm_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, percolatorPsmDTO.getPsmDBId() );
			
			counter++;
			pstmt.setInt( counter, percolatorPsmDTO.getPercolatorFileId() );
			

			counter++;
			pstmt.setDouble( counter, percolatorPsmDTO.getqValue() );

			counter++;
			pstmt.setDouble( counter, percolatorPsmDTO.getSvmScore() );

			counter++;
			pstmt.setDouble( counter, percolatorPsmDTO.getCalcMass() );
			counter++;
			pstmt.setDouble( counter, percolatorPsmDTO.getPep() );

			counter++;
			pstmt.setString( counter, percolatorPsmDTO.getPsmId() );
			
			pstmt.executeUpdate();
			
//			rs = pstmt.getGeneratedKeys();
//			if( rs.next() ) {
//				percolatorPsmDTO.setId( rs.getInt( 1 ) );
//			} else
//				throw new Exception( "Failed to insert percolatorPsmDTO..." );
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
	}
	
}
