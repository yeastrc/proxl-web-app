package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.CutoffsAppliedOnImportDTO;
import org.yeastrc.xlink.db.DBConnectionFactory;


/**
 * table cutoffs_applied_on_import
 *
 */
public class DB_Insert_CutoffsAppliedOnImportDAO {

	private static final Logger log = Logger.getLogger(DB_Insert_CutoffsAppliedOnImportDAO.class);

	private DB_Insert_CutoffsAppliedOnImportDAO() { }
	public static DB_Insert_CutoffsAppliedOnImportDAO getInstance() { return new DB_Insert_CutoffsAppliedOnImportDAO(); }

	private final String INSERT_SQL = 
			"INSERT INTO cutoffs_applied_on_import"
			+ " (search_id, annotation_type_id, cutoff_value_string, cutoff_value_double) "
			+ "VALUES (?, ?, ?, ?)";
	
	public void save( CutoffsAppliedOnImportDTO cutoffsAppliedOnImportDTO ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = INSERT_SQL;

		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;


			counter++;
			pstmt.setInt( counter, cutoffsAppliedOnImportDTO.getSearchId());
			counter++;
			pstmt.setInt( counter, cutoffsAppliedOnImportDTO.getAnnotationTypeId() );
			counter++;
			pstmt.setString( counter, cutoffsAppliedOnImportDTO.getCutoffValueString() );
			counter++;
			pstmt.setDouble( counter, cutoffsAppliedOnImportDTO.getCutoffValueDouble() );

			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				cutoffsAppliedOnImportDTO.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert cutoffs_applied_on_import" );
			
		} catch ( Exception e ) {
			
			String msg = "ERROR inserting cutoffs_applied_on_import. database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n cutoffs_applied_on_import: " + cutoffsAppliedOnImportDTO
					+ "\nsql: " + sql;
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
