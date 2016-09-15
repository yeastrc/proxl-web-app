package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.enum_classes.AnnotationValueLocation;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;

/**
 * Table srch__rep_pept__annotation
 *
 */
public class SearchReportedPeptideAnnotationDAO {
	
	private static final Logger log = Logger.getLogger(SearchReportedPeptideAnnotationDAO.class);

	private SearchReportedPeptideAnnotationDAO() { }
	public static SearchReportedPeptideAnnotationDAO getInstance() { return new SearchReportedPeptideAnnotationDAO(); }
	
	/**
	 * Get the given srch__rep_pept__annotation from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SearchReportedPeptideAnnotationDTO getItem( int id ) throws Exception {
		
		SearchReportedPeptideAnnotationDTO item = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM srch__rep_pept__annotation WHERE id = ?";


		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				item = populateFromResultSet( rs );
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
		
		
		return item;
	}
	
	

	/**
	 * Get the given srch__rep_pept__annotation from the database
	 * 
	 * @param searchId
	 * @param reportedPeptideId
	 * @param peptideFilterableAnnotationTypeId
	 * @return
	 * @throws Exception
	 */
	public SearchReportedPeptideAnnotationDTO getItemForSearchIdReportedPeptideIdAnnotationId( 
			
			int searchId,
			int reportedPeptideId,
			int peptideFilterableAnnotationTypeId ) throws Exception {
		
		SearchReportedPeptideAnnotationDTO item = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "SELECT * FROM srch__rep_pept__annotation WHERE search_id = ? AND reported_peptide_id = ? AND annotation_type_id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, reportedPeptideId );
			pstmt.setInt( 3, peptideFilterableAnnotationTypeId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				item = populateFromResultSet( rs );
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
		
		
		return item;
	}
	
	
	/**
	 * @param rs
	 * @return
	 * @throws Exception 
	 */
	public SearchReportedPeptideAnnotationDTO populateFromResultSet( ResultSet rs ) throws Exception {
		
		SearchReportedPeptideAnnotationDTO item;
		item = new SearchReportedPeptideAnnotationDTO();

		AnnotationValueLocation annotationValueLocation = AnnotationValueLocation.fromValue( rs.getString( "value_location" )  );
		
		item.setId( rs.getInt( "id" ) );
		item.setSearchId( rs.getInt( "search_id" ) );
		item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
		
		item.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.fromValue( rs.getString( "filterable_descriptive_type" ) ) );
		
		item.setAnnotationTypeId( rs.getInt( "annotation_type_id" ) );
		item.setAnnotationValueLocation( annotationValueLocation );

		item.setValueDouble( rs.getDouble( "value_double" ) );
		item.setValueString( rs.getString( "value_string" ) );

		if ( annotationValueLocation == AnnotationValueLocation.LARGE_VALUE_TABLE ) {
			
			//  Get valueString from large value table instead
			
			String valueString = SearchReportedPeptideAnnotationLargeValueDAO.getInstance().getValueString( item.getId() );
			item.setValueString( valueString );
		}
		
		return item;
	}


	
	/**
	 * This will INSERT the given SearchReportedPeptideAnnotationDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchReportedPeptideAnnotationDTO item ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

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
	public void saveToDatabase( SearchReportedPeptideAnnotationDTO item, Connection conn ) throws Exception {
		
		try {

			try {
				item.setAnnotationValueLocation( AnnotationValueLocation.LOCAL );

				saveToDatabaseInternal( item, conn );

			} catch ( Exception e ) {

				//  Catch exception if valueString is too large for primary table 

				if ( item.getFilterableDescriptiveAnnotationType() == FilterableDescriptiveAnnotationType.FILTERABLE ) {
					
					//  Filterable valueString must fit in the main table since it is copied from there to lookup tables.
					
					throw e;
				}
				

				//  change to store value string in "..._large_value" table instead


				item.setAnnotationValueLocation( AnnotationValueLocation.LARGE_VALUE_TABLE );

				saveToDatabaseInternal( item, conn );

				SearchReportedPeptideAnnotationLargeValueDAO.getInstance().saveToDatabase( item.getId(), item.getValueString(), conn);
			}

		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + INSERT_SQL, e );
			
			throw e;
		}
	}


	private final static String INSERT_SQL = 
			"INSERT INTO srch__rep_pept__annotation "
			
			+ "(search_id, reported_peptide_id, "
			+ 	" filterable_descriptive_type, annotation_type_id, value_location, value_double, value_string ) "
			
			+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
	

	
	/**
	 * This will INSERT the given SearchReportedPeptideAnnotationDTO into the database
	 * @param item
	 * @throws Exception
	 */
	private void saveToDatabaseInternal( SearchReportedPeptideAnnotationDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = INSERT_SQL;
		
		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setInt( counter, item.getReportedPeptideId() );
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
				throw new Exception( "Failed to insert for " + item.getSearchId() + ", " + item.getReportedPeptideId() );
			
			
			
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
