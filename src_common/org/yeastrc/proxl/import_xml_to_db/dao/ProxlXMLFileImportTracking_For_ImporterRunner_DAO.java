package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.proxl_xml_file_import.dao.ProxlXMLFileImportTrackingHistoryDAO;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.base.proxl_xml_file_import.populate_dto_from_result.ProxlXMLFileImportTracking_PopulateDTO;

/**
 * 
 * table proxl_xml_file_import_tracking
 */
public class ProxlXMLFileImportTracking_For_ImporterRunner_DAO {


	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTracking_For_ImporterRunner_DAO.class);
	

	//  private constructor
	private ProxlXMLFileImportTracking_For_ImporterRunner_DAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTracking_For_ImporterRunner_DAO getInstance() { 
		return new ProxlXMLFileImportTracking_For_ImporterRunner_DAO(); 
	}
	

	/**
	 * Get next import tracking item that is queued or re-queued
	 * @return
	 * @throws Exception
	 */
	public ProxlXMLFileImportTrackingDTO getNextQueued( ) throws Exception {


		ProxlXMLFileImportTrackingDTO result = null;
		
		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM proxl_xml_file_import_tracking "
				+ " WHERE status_id IN ( " 
				+ 		ProxlXMLFileImportStatus.QUEUED.value()
				+	  "," + ProxlXMLFileImportStatus.RE_QUEUED.value()
				+ 	") AND marked_for_deletion != " + Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE
				+ " ORDER BY ID LIMIT 1 FOR UPDATE";
		
		
		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			dbConnection.setAutoCommit(false);
			
			pstmt = dbConnection.prepareStatement( sql );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				
				result = ProxlXMLFileImportTracking_PopulateDTO.getInstance().populateResultObject( rs );
			}
			
			if ( result != null ) {
			
				if ( ProxlXMLFileImportStatus.QUEUED == result.getStatus()
						|| ProxlXMLFileImportStatus.RE_QUEUED == result.getStatus() ) {
					
					result.setStatus( ProxlXMLFileImportStatus.STARTED );

					updateStatusStarted( result.getId(), dbConnection );
					
				} else {
					
					result = null;
				}

			}

			dbConnection.commit();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select ProxlXMLFileImportTrackingDTO, sql: " + sql;
			
			log.error( msg, e );
			
			
			if ( dbConnection != null ) {
				
				try {
					dbConnection.rollback();
				} catch (Exception ex) {
					String msg2 = "Failed dbConnection.rollback() in getNextQueued(...)";

					log.error( msg2, ex );
				}
			}
			
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
			

			if( dbConnection != null ) {


				try {
					dbConnection.setAutoCommit(true);  /// reset for next user of connection
				} catch (Exception ex) {
					String msg = "Failed dbConnection.setAutoCommit(true) in getNextQueued(...)";

					log.error( msg, ex );
				}

				if( dbConnection != null ) {
					try { dbConnection.close(); } catch( Throwable t ) { ; }
					dbConnection = null;
				}

			}
			
		}
		
		return result;
	}
	

	/**
	 * @param id
	 * @throws Exception
	 */
	public void updateStatusStarted( int id, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE proxl_xml_file_import_tracking SET status_id = "
				+ ProxlXMLFileImportStatus.STARTED.value() 
				+ ", import_start_date_time = NOW(), last_updated_date_time = NOW() WHERE id = ?";

		
		try {
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update status, sql: " + sql;
			
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
		
		ProxlXMLFileImportTrackingHistoryDAO.getInstance().save( ProxlXMLFileImportStatus.STARTED, id /* ProxlXMLFileImportTrackingId */, dbConnection );		
	}
	
	


	/**
	 * @param status
	 * @param id
	 * @throws Exception
	 */
	public void updateStatusAtImportEnd( ProxlXMLFileImportStatus status, int id ) throws Exception {
		
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			updateStatusAtImportEnd( status, id, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
	
	
	/**
	 * @param status
	 * @param id
	 * @throws Exception
	 */
	public void updateStatusAtImportEnd( ProxlXMLFileImportStatus status, int id, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE proxl_xml_file_import_tracking SET status_id = ?"
				+ ", import_end_date_time = NOW(), last_updated_date_time = NOW() WHERE id = ?";

		
		try {
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;

			counter++;
			pstmt.setInt( counter, status.value() );
			
			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update status, sql: " + sql;
			
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
		
		ProxlXMLFileImportTrackingHistoryDAO.getInstance().save( ProxlXMLFileImportStatus.STARTED, id /* ProxlXMLFileImportTrackingId */, dbConnection );		
	}
	


}
