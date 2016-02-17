package org.yeastrc.xlink.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.AnnotationTypeDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;


/**
 * Get AnnotationTypeDTO records for Search Id
 *
 */
public class AnnotationTypesForSearchIdPSMPeptideTypeSearcher {


	private static final Logger log = Logger.getLogger(AnnotationTypesForSearchIdPSMPeptideTypeSearcher.class);

	private AnnotationTypesForSearchIdPSMPeptideTypeSearcher() { }
	public static AnnotationTypesForSearchIdPSMPeptideTypeSearcher getInstance() { return new AnnotationTypesForSearchIdPSMPeptideTypeSearcher(); }


	/**
	 * Get the annotation_type records for the search id from the database
	 * 
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public List<AnnotationTypeDTO> get_PSM_Filterable_ForSearchId( int searchId ) throws Exception {
		
		return getForSearchIdPsmPeptideTypeFilterableDescType( searchId, PsmPeptideAnnotationType.PSM, FilterableDescriptiveAnnotationType.FILTERABLE );
		
	}

	/**
	 * Get the annotation_type records for the search id from the database
	 * 
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public List<AnnotationTypeDTO> get_Peptide_Filterable_ForSearchId( int searchId ) throws Exception {
		
		return getForSearchIdPsmPeptideTypeFilterableDescType( searchId, PsmPeptideAnnotationType.PEPTIDE, FilterableDescriptiveAnnotationType.FILTERABLE );
		
	}
		

	private static final String SEARCH_SQL = 
			"SELECT annotation_type.* "
			+ "FROM annotation_type "
			+ " WHERE annotation_type.search_id = ? AND annotation_type.psm_peptide_type = ? AND filterable_descriptive_type = ?";

//	CREATE TABLE IF NOT EXISTS `proxl_generic_fields`.`annotation_type` (
//			  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  `search_id` INT UNSIGNED NOT NULL,
//			  `search_programs_per_search_id` INT(10) UNSIGNED NOT NULL,
//			  `psm_peptide_type` ENUM('psm','peptide') NOT NULL,
//			  `filterable_descriptive_type` ENUM('filterable','descriptive') NOT NULL,
//			  `name` VARCHAR(255) NOT NULL,
//			  `default_visible` INT(1) NOT NULL,
//			  `display_order` INT NULL,
//			  `description` VARCHAR(4000) NULL,
			  
	/**
	 * Get the annotation_type records for the search id from the database
	 * 
	 * @param searchId
	 * @param psmPeptideAnnotationType
	 * @param filterableDescriptiveAnnotationType
	 * @return
	 * @throws Exception
	 */
	public List<AnnotationTypeDTO> getForSearchIdPsmPeptideTypeFilterableDescType( 
			int searchId,

			PsmPeptideAnnotationType psmPeptideAnnotationType,
			FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType
			
			) throws Exception {
		
		List<AnnotationTypeDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = SEARCH_SQL;

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			paramCounter++;
			pstmt.setString( paramCounter, psmPeptideAnnotationType.value() );
			paramCounter++;
			pstmt.setString( paramCounter, filterableDescriptiveAnnotationType.value() );

			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				//  Can use this since only fields in result set are from table srch_pgm__filterable_psm_annotation_type
				
				AnnotationTypeDTO item = 
						AnnotationTypeDAO.getInstance().populateFromResultSet( rs );
				
				results.add( item );
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
		
		
		return results;
	}


}
