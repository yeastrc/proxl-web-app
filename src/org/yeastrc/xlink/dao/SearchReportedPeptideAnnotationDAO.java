package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
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
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				item = populateFromResultSet( rs );
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
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, reportedPeptideId );
			pstmt.setInt( 3, peptideFilterableAnnotationTypeId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				item = populateFromResultSet( rs );
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
	 * @throws SQLException
	 */
	public SearchReportedPeptideAnnotationDTO populateFromResultSet( ResultSet rs ) throws SQLException {
		
		SearchReportedPeptideAnnotationDTO item;
		item = new SearchReportedPeptideAnnotationDTO();
		
		item.setId( rs.getInt( "id" ) );
		item.setSearchId( rs.getInt( "search_id" ) );
		item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
		
		item.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.fromValue( rs.getString( "filterable_descriptive_type" ) ) );
		
		item.setAnnotationTypeId( rs.getInt( "annotation_type_id" ) );
		item.setValueDouble( rs.getDouble( "value_double" ) );
		item.setValueString( rs.getString( "value_string" ) );
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
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			saveToDatabase( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
		

	private final static String INSERT_SQL = 
			"INSERT INTO srch__rep_pept__annotation "
			
			+ "(search_id, reported_peptide_id, "
			+ 	" filterable_descriptive_type, annotation_type_id, value_double, value_string ) "
			
			+ "VALUES (?, ?, ?, ?, ?, ?)";
	

	
	/**
	 * This will INSERT the given SearchReportedPeptideAnnotationDTO into the database
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchReportedPeptideAnnotationDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = INSERT_SQL;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
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
			pstmt.setDouble( counter, item.getValueDouble() );
			counter++;
			pstmt.setString( counter, item.getValueString() );
			
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert for " + item.getSearchId() + ", " + item.getReportedPeptideId() );
			
			
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
