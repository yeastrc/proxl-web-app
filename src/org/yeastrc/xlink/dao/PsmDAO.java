package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PercolatorPsmDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.utils.XLinkUtils;

public class PsmDAO {

	private static final Logger log = Logger.getLogger(PsmDAO.class);
			
	private PsmDAO() { }
	public static PsmDAO getInstance() { return new PsmDAO(); }
	
	public PsmDTO getPsmDTO( int id ) throws Exception {
		
		PsmDTO psm = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM psm WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				psm = new PsmDTO();
				
				psm.setId( id );
				psm.setSearchId( rs.getInt( "search_id" ) );
				psm.setScanId( rs.getInt( "scan_id" ) );
				
				psm.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				psm.setqValue( rs.getDouble( "q_value" ) );
				
				int charge = rs.getInt( "charge" );
				if ( ! rs.wasNull() ) {
					
					psm.setCharge( charge );
				}
				
				String typeString = rs.getString( "type" );
				int typeNumber = XLinkUtils.getTypeNumber( typeString );
				
				psm.setType( typeNumber );
				
				
				PercolatorPsmDTO percolatorPsm = PercolatorPsmDAO.getInstance().getPercolatorPsmDTO( id );
				
				psm.setPercolatorPsm( percolatorPsm );
				
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
		
		
		return psm;
	}
	
	

	/**
	 * Query on the 2 foreign keys
	 * 
	 * @param reportedPeptideId
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public PsmDTO getOnePsmDTOForSearchIdAndReportedPeptideId( int reportedPeptideId,  int searchId ) throws Exception {
		
		PsmDTO psm = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM psm WHERE reported_peptide_id = ? AND search_id = ? LIMIT 1";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, reportedPeptideId );
			pstmt.setInt( 2, searchId );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				psm = new PsmDTO();
				
				psm.setId( rs.getInt( "id" ) );
				psm.setSearchId( rs.getInt( "search_id" ) );
				psm.setScanId( rs.getInt( "scan_id" ) );
				
				psm.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				psm.setqValue( rs.getDouble( "q_value" ) );
				
				int charge = rs.getInt( "charge" );
				if ( ! rs.wasNull() ) {
					
					psm.setCharge( charge );
				}
				
				String typeString = rs.getString( "type" );
				int typeNumber = XLinkUtils.getTypeNumber( typeString );
				
				psm.setType( typeNumber );
				
				
				PercolatorPsmDTO percolatorPsm = PercolatorPsmDAO.getInstance().getPercolatorPsmDTO( psm.getId() );
				
				psm.setPercolatorPsm( percolatorPsm );
				
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
		
		
		return psm;
	}
	
	
	
//	CREATE TABLE psm (
//			  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//			  search_id INT(10) UNSIGNED NOT NULL,
//			  scan_id INT UNSIGNED NOT NULL,
//			  q_value DOUBLE NULL DEFAULT NULL,
//			  type ENUM('looplink','crosslink','unlinked','monolink','dimer') NOT NULL,
//			  reported_peptide_id INT(10) UNSIGNED NOT NULL,
//    		  charge SMALLINT NULL

	
	/**
	 *
	CREATE TABLE psm (
	    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	    search_id INT UNSIGNED NOT NULL,
	    scan_id INT UNSIGNED UNSIGNED NOT NULL,
	    q_value DOUBLE,
	    type ENUM('looplink','crosslink', 'unlinked', 'monolink', 'dimer' ) NOT NULL,
	    reported_peptide_id INT UNSIGNED NOT NULL,
    	charge SMALLINT NULL
	);



	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public void saveToDatabase(PsmDTO item ) throws Exception {

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
	
	private static final String MONOLINK_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_MONOLINK ) ;

	/**
	 * @param psm
	 * @param conn
	 * @throws Exception
	 */
	public void saveToDatabase( PsmDTO psm, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO psm ( search_id, scan_id, q_value, type, reported_peptide_id, charge ) VALUES (?, ?, ?, ?, ?, ?)";
		

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			int psmType = psm.getType();

			
			String psmTypeString = XLinkUtils.getTypeString( psmType );

			
			if ( psmType == XLinkUtils.TYPE_MONOLINK ) {
				
				String msg = "Invalid to insert psm with type Monolink, scan_id: " + psm.getScanId();
				
				log.error( msg );
				
				throw new Exception(msg);
			}
			
			
			if (MONOLINK_TYPE_STRING.equals(psmTypeString) ) {
				
				String msg = "Invalid to insert psm with type Monolink, scan_id: " + psm.getScanId();
				
				log.error( msg );
				
				throw new Exception(msg);
			}
			
						
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, psm.getSearchId() );
			counter++;
			pstmt.setInt( counter, psm.getScanId() );

			counter++;
			pstmt.setDouble( counter, psm.getqValue() );
			counter++;
			pstmt.setString( counter, psmTypeString );
			counter++;
			pstmt.setInt( counter, psm.getReportedPeptideId() );
			
			counter++;
			
			if ( psm.isChargeSet() ) {
				pstmt.setInt( counter, psm.getCharge() );
			} else {
				pstmt.setInt( counter, java.sql.Types.INTEGER );
			}
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				psm.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert psm..." );
			
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
