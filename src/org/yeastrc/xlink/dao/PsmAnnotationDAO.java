package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.enum_classes.AnnotationValueLocation;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;

/**
 * Table psm_annotation
 *
 */
public class PsmAnnotationDAO {
	
	private static final Logger log = Logger.getLogger(PsmAnnotationDAO.class);

	private PsmAnnotationDAO() { }
	public static PsmAnnotationDAO getInstance() { return new PsmAnnotationDAO(); }
	
	/**
	 * Get the given psm_annotation from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PsmAnnotationDTO getItem( int id ) throws Exception {
		
		PsmAnnotationDTO item = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM psm_annotation WHERE id = ?";
		
		

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				item = populateFromResultSet(rs);
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
		
		
		return item;
	}
	
	
	
	/**
	 * @param rs
	 * @return
	 * @throws Exception 
	 */
	public PsmAnnotationDTO populateFromResultSet(ResultSet rs)
			throws Exception {
	
		
		PsmAnnotationDTO item;
		item = new PsmAnnotationDTO();
		
		AnnotationValueLocation annotationValueLocation = AnnotationValueLocation.fromValue( rs.getString( "value_location" )  );
		
		item.setId( rs.getInt( "id" ) );
		item.setPsmId( rs.getInt( "psm_id" ) );
		item.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.fromValue( rs.getString( "filterable_descriptive_type" )  ) );
		item.setAnnotationTypeId( rs.getInt( "annotation_type_id" ) );
		item.setAnnotationValueLocation( annotationValueLocation );
		item.setValueDouble( rs.getDouble( "value_double" ) );
		item.setValueString( rs.getString( "value_string" ) );
		
		if ( annotationValueLocation == AnnotationValueLocation.LARGE_VALUE_TABLE ) {
			
			//  Get valueString from large value table instead
			
			String valueString = PsmAnnotationLargeValueDAO.getInstance().getValueString( item.getId() );
			item.setValueString( valueString );
		}
		
		return item;
	}

	
	/**
	 * This will INSERT the given PsmAnnotationDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( PsmAnnotationDTO item ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			saveToDatabase( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
		
	
	/**
	 * This will INSERT the given PsmAnnotationDTO into the database
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( PsmAnnotationDTO item, Connection conn ) throws Exception {
		
		try {

			try {
				item.setAnnotationValueLocation( AnnotationValueLocation.LOCAL );

				saveToDatabaseInternal( item, conn );

			} catch ( Exception e ) {

				//  Catch exception if valueString is too large for primary table 

				//  change to store value string in "..._large_value" table instead


				item.setAnnotationValueLocation( AnnotationValueLocation.LARGE_VALUE_TABLE );

				saveToDatabaseInternal( item, conn );

				PsmAnnotationLargeValueDAO.getInstance().saveToDatabase( item.getId(), item.getValueString(), conn);
			}


		
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + INSERT_SQL
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
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
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
