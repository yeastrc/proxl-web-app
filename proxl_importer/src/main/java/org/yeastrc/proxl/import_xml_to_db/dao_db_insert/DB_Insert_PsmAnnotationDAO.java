package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.base.constants.AnnotationValueStringLocalFieldLengthConstants;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.enum_classes.AnnotationValueLocation;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;

/**
 * table psm_annotation
 *
 */
public class DB_Insert_PsmAnnotationDAO {

	private static final Logger log = LoggerFactory.getLogger( DB_Insert_PsmAnnotationDAO.class);

	private DB_Insert_PsmAnnotationDAO() { }
	public static DB_Insert_PsmAnnotationDAO getInstance() { return new DB_Insert_PsmAnnotationDAO(); }


	/**
	 * This will INSERT the given PsmAnnotationDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( PsmAnnotationDTO item ) throws Exception {
		
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
		
	
	/**
	 * This will INSERT the given PsmAnnotationDTO into the database
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( PsmAnnotationDTO item, Connection conn ) throws Exception {
		
		if ( item == null ) {
			String msg = "item to save cannot be null";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		if ( item.getValueString().length() > AnnotationValueStringLocalFieldLengthConstants.ANNOTATION_VALUE_STRING_LOCAL_FIELD_LENGTH ) {

			if ( item.getFilterableDescriptiveAnnotationType() == FilterableDescriptiveAnnotationType.FILTERABLE ) {
				//  Filterable valueString must fit in the main table since it is copied from there to lookup tables.
				String msg = "For Filterable annotation: item to save ValueString cannot have length > " 
						+ AnnotationValueStringLocalFieldLengthConstants.ANNOTATION_VALUE_STRING_LOCAL_FIELD_LENGTH
						+ ", ValueString: " + item.getValueString()
						+ ", item: " + item;
				log.error( msg );
				throw new IllegalArgumentException(msg);
			}
			
			item.setAnnotationValueLocation( AnnotationValueLocation.LARGE_VALUE_TABLE );
		} else {
			item.setAnnotationValueLocation( AnnotationValueLocation.LOCAL );
		}
		try {

			saveToDatabaseInternal( item, conn );

			if ( item.getAnnotationValueLocation() == AnnotationValueLocation.LARGE_VALUE_TABLE ) {
				DB_Insert_PsmAnnotationLargeValueDAO.getInstance().saveToDatabase( item.getId(), item.getValueString(), conn );
			}
		
		} catch ( Exception e ) {
			
			log.error( "ERROR:  If length of field value_string is exceeded, update Java class AnnotationValueStringLocalFieldLengthConstants. sql: " + INSERT_SQL
					+ ".  PsmAnnotationDTO item: " + item, e );
			
			throw e;
		}
	}


	private final static String INSERT_SQL = 
			"INSERT INTO psm_annotation "
			
			+ "(psm_id, filterable_descriptive_type, annotation_type_id, value_location, value_double, value_string ) "
			
			+ "VALUES (?, ?, ?, ?, ?, ?)";
	
	

	/**
	 * This will INSERT the given PsmAnnotationDTO into the database
	 * @param item
	 * @throws Exception
	 */
	private void saveToDatabaseInternal( PsmAnnotationDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = INSERT_SQL;
		
		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getPsmId() );
			
			counter++;
			pstmt.setString( counter, item.getFilterableDescriptiveAnnotationType().value() );
			counter++;
			pstmt.setInt( counter, item.getAnnotationTypeId() );
			
			counter++;
			pstmt.setString( counter, item.getAnnotationValueLocation().value() );
			
			counter++;
			pstmt.setDouble( counter, item.getValueDouble() );
			
			counter++;
			
			if ( item.getAnnotationValueLocation() == AnnotationValueLocation.LOCAL ) {
				pstmt.setString( counter, item.getValueString() );
			} else {
				
				pstmt.setString( counter, "" ); // store empty string since value stored in .._large_value table
			}
			

			pstmt.executeUpdate();
			
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert for " + item.getPsmId() );
			
			
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
