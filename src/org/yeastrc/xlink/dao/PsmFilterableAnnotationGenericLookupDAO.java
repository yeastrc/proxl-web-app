package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmFilterableAnnotationGenericLookupDTO;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * Table psm_filterable_annotation__generic_lookup
 *
 */
public class PsmFilterableAnnotationGenericLookupDAO {
	
	private static final Logger log = Logger.getLogger(PsmFilterableAnnotationGenericLookupDAO.class);

	private PsmFilterableAnnotationGenericLookupDAO() { }
	public static PsmFilterableAnnotationGenericLookupDAO getInstance() { return new PsmFilterableAnnotationGenericLookupDAO(); }
	
	/**
	 * Get the given psm_filterable_annotation__generic_lookup from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PsmFilterableAnnotationGenericLookupDTO getItem( int id ) throws Exception {
		
		PsmFilterableAnnotationGenericLookupDTO item = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM psm_filterable_annotation__generic_lookup WHERE id = ?";


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
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private PsmFilterableAnnotationGenericLookupDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		
		PsmFilterableAnnotationGenericLookupDTO item;
		item = new PsmFilterableAnnotationGenericLookupDTO();
		
		item.setPsmAnnotationId( rs.getInt( "psm_annotation_id" ) );
		item.setPsmId( rs.getInt( "psm_id" ) );
		item.setAnnotationTypeId( rs.getInt( "annotation_type_id" ) );
		item.setValueDouble( rs.getDouble( "value_double" ) );
		item.setValueString( rs.getString( "value_string" ) );
		
		item.setSearchId( rs.getInt( "search_id" ) );
		item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );

		String typeString = rs.getString( "psm_type" );
		int typeNumber = XLinkUtils.getTypeNumber( typeString );
		
		item.setType(typeNumber);
		
		return item;
	}


	/**
	 * This will INSERT the given PsmFilterableAnnotationGenericLookupDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( PsmFilterableAnnotationGenericLookupDTO item ) throws Exception {
		
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
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
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
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql
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
