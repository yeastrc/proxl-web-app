package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmFilterableAnnotationGenericLookupDTO;
import org.yeastrc.xlink.utils.XLinkUtils;


/**
 * Table psm_filterable_annotation__generic_lookup
 *
 */
public class DB_Insert_PsmFilterableAnnotationGenericLookupDAO {

	private static final Logger log = Logger.getLogger(DB_Insert_PsmFilterableAnnotationGenericLookupDAO.class);

	private DB_Insert_PsmFilterableAnnotationGenericLookupDAO() { }
	public static DB_Insert_PsmFilterableAnnotationGenericLookupDAO getInstance() { return new DB_Insert_PsmFilterableAnnotationGenericLookupDAO(); }
	

	/**
	 * This will INSERT the given PsmFilterableAnnotationGenericLookupDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( PsmFilterableAnnotationGenericLookupDTO item ) throws Exception {
		
		Connection dbConnection = null;

		try {

//			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			dbConnection = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			saveToDatabase( item, dbConnection );

		} finally {
			
//			if( dbConnection != null ) {
//				try { dbConnection.close(); } catch( Throwable t ) { ; }
//				dbConnection = null;
//			}
			
		}
		
	}
		

	private final static String INSERT_SQL = 
			"INSERT INTO psm_filterable_annotation__generic_lookup "
			
			+ "( psm_annotation_id, psm_id, annotation_type_id, value_double, "
			+ " search_id, reported_peptide_id, psm_type ) "
			
			+ "VALUES ( ?, ?, ?, ?, ?, ?, ? )";

		
	/**
	 * This will INSERT the given PsmFilterableAnnotationGenericLookupDTO into the database
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( PsmFilterableAnnotationGenericLookupDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = INSERT_SQL;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql ); // , Statement.RETURN_GENERATED_KEYS
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getPsmAnnotationId() );
			counter++;
			pstmt.setInt( counter, item.getPsmId() );
			counter++;
			pstmt.setInt( counter, item.getAnnotationTypeId() );
			counter++;
			pstmt.setDouble( counter, item.getValueDouble() );
			
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setInt( counter, item.getReportedPeptideId() );


			int psmType = item.getType();

			
			String psmTypeString = XLinkUtils.getTypeString( psmType );

			counter++;
			pstmt.setString( counter, psmTypeString );
			
			
			pstmt.executeUpdate();
			
//			rs = pstmt.getGeneratedKeys();
//			if( rs.next() ) {
//				item.setId( rs.getInt( 1 ) );
//			} else
//				throw new Exception( "Failed to insert for " + item.getPsmId() );
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql
					+ ".  PsmFilterableAnnotationGenericLookupDTO item: " + item, e );
			
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
