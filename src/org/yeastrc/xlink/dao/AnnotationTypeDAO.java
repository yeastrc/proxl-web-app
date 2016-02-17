package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;

/**
 * Table annotation_type
 *
 */
public class AnnotationTypeDAO {
	
	private static final Logger log = Logger.getLogger(AnnotationTypeDAO.class);

	private AnnotationTypeDAO() { }
	public static AnnotationTypeDAO getInstance() { return new AnnotationTypeDAO(); }
	
	/**
	 * Get the given annotation_type from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public AnnotationTypeDTO getItem( int id ) throws Exception {
		
		AnnotationTypeDTO item = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM annotation_type WHERE id = ?";


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
	 * Populate object from result set
	 * 
	 * @param rs
	 * @return
	 * @throws Exception 
	 */
	public AnnotationTypeDTO populateFromResultSet(	ResultSet rs) throws Exception {
		
		
		AnnotationTypeDTO item;
		item = new AnnotationTypeDTO();
		
		item.setId( rs.getInt( "id" ) );
		item.setSearchId( rs.getInt( "search_id" ) );
		item.setSearchProgramsPerSearchId( rs.getInt( "search_programs_per_search_id" ) );
		
		String psmPeptideAnnotationTypeString = rs.getString( "psm_peptide_type" );
		PsmPeptideAnnotationType psmPeptideAnnotationType = PsmPeptideAnnotationType.fromValue( psmPeptideAnnotationTypeString );
		item.setPsmPeptideAnnotationType( psmPeptideAnnotationType );

		String filterableDescriptiveAnnotationTypeString = rs.getString( "filterable_descriptive_type" );
		FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType = FilterableDescriptiveAnnotationType.fromValue( filterableDescriptiveAnnotationTypeString );
		item.setFilterableDescriptiveAnnotationType( filterableDescriptiveAnnotationType );


		item.setName( rs.getString( "name" ) );

		int defaultVisibleInt = rs.getInt( "default_visible" );
		
		if ( Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE == defaultVisibleInt ) {
			item.setDefaultVisible( false );
		} else {
			item.setDefaultVisible( true );
		}

		int displayOrder = rs.getInt( "display_order" );
		if ( ! rs.wasNull() ) {
			
			item.setDisplayOrder( displayOrder );
		}
		
		

		item.setDescription( rs.getString( "description" ) );
		
		if ( item.getFilterableDescriptiveAnnotationType() == FilterableDescriptiveAnnotationType.FILTERABLE ) {
			
			AnnotationTypeFilterableDTO annotationTypeFilterableDTO = AnnotationTypeFilterableDAO.getInstance().getItem( item.getId() );
			
			if ( annotationTypeFilterableDTO == null ) {
				
				String msg = "AnnotationTypeFilterableDTO record not found for annotation type FILTERABLE. annotation type id: " + item.getId();
				log.error( msg );
				throw new Exception(msg);
			}

			item.setAnnotationTypeFilterableDTO(annotationTypeFilterableDTO);
		}
		
		
		return item;
	}


	
	/**
	 * This will INSERT the given AnnotationTypeDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( AnnotationTypeDTO item ) throws Exception {
		
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
			"INSERT INTO annotation_type "
			
			+ "( search_id, search_programs_per_search_id, "
			+ 	" psm_peptide_type, filterable_descriptive_type, "
			+ 	" name, default_visible, display_order, description ) "
			
			+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";
	
		
	/**
	 * This will INSERT the given AnnotationTypeDTO into the database
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( AnnotationTypeDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		if ( log.isDebugEnabled() ) {
			
			log.debug( "Saving AnnotationTypeDTO item: " + item );
		}
		

		if ( item.getFilterableDescriptiveAnnotationType() == FilterableDescriptiveAnnotationType.FILTERABLE ) {
			
			AnnotationTypeFilterableDTO annotationTypeFilterableDTO = item.getAnnotationTypeFilterableDTO();
			
			if ( annotationTypeFilterableDTO == null ) {

				String msg = "ERROR: annotationTypeFilterableDTO not populated for annotation type FILTERABLE. annotation name: " + item.getName();
				log.error( msg );
				throw new IllegalArgumentException(msg);
			}
			
		} else {

			AnnotationTypeFilterableDTO annotationTypeFilterableDTO = item.getAnnotationTypeFilterableDTO();
			
			if ( annotationTypeFilterableDTO != null ) {

				String msg = "ERROR: annotationTypeFilterableDTO populated for annotation type NOT FILTERABLE. annotation name: " + item.getName();
				log.error( msg );
				throw new IllegalArgumentException(msg);
			}
		}

		String sql = INSERT_SQL;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;

			if ( item.getPsmPeptideAnnotationType() == null ) {
				
				String msg = "item.getPsmPeptideAnnotationType() cannot be null";
				log.error( msg );
				throw new IllegalArgumentException(msg);
			}
			if ( item.getFilterableDescriptiveAnnotationType() == null ) {
				
				String msg = "item.getFilterableDescriptiveAnnotationType() cannot be null";
				log.error( msg );
				throw new IllegalArgumentException(msg);
			}



			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setInt( counter, item.getSearchProgramsPerSearchId() );

			counter++;
			pstmt.setString( counter, item.getPsmPeptideAnnotationType().value() );
			
			counter++;
			pstmt.setString( counter, item.getFilterableDescriptiveAnnotationType().value() );
			

			counter++;
			pstmt.setString( counter, item.getName() );
			
			counter++;
			if ( item.isDefaultVisible() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}

			counter++;
			if ( item.getDisplayOrder() != null ) {
				pstmt.setInt( counter, item.getDisplayOrder() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}

			
			counter++;
			pstmt.setString( counter, item.getDescription() );
			
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert for " + item.getDescription() );
			
			

			if ( item.getFilterableDescriptiveAnnotationType() == FilterableDescriptiveAnnotationType.FILTERABLE ) {
				
				AnnotationTypeFilterableDTO annotationTypeFilterableDTO = item.getAnnotationTypeFilterableDTO();
				
				annotationTypeFilterableDTO.setAnnotationTypeId( item.getId() );
				
				AnnotationTypeFilterableDAO.getInstance().saveToDatabase( annotationTypeFilterableDTO, conn );
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
		
		
	}
	
}
