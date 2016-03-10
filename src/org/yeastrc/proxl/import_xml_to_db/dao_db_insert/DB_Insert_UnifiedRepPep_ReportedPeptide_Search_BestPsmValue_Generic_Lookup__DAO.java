package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO;
import org.yeastrc.xlink.utils.XLinkUtils;


/**
 * 
 * 
 * table unified_rp__rep_pept__search__best_psm_value_generic_lookup
 *
 */
public class DB_Insert_UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DAO {


	private static final Logger log = Logger.getLogger(DB_Insert_UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DAO.class);

	private DB_Insert_UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DAO() { }
	public static DB_Insert_UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DAO getInstance() { return new DB_Insert_UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DAO(); }

	/**
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO item ) throws Exception {
		
		Connection conn = null;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
						
			saveToDatabase( item, conn );
			
		} catch ( Exception e ) {
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
		
		
	}
	


//	CREATE TABLE unified_rp__rep_pept__search__best_psm_value_generic_lookup (
//			  unified_reported_peptide_id INT(10) UNSIGNED NOT NULL,
//			  reported_peptide_id INT(10) UNSIGNED NOT NULL,
//			  search_id INT(10) UNSIGNED NOT NULL,
//			  annotation_type_id INT(10) UNSIGNED NOT NULL,
//			  link_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
//			  has_dynamic_modifictions TINYINT(3) UNSIGNED NOT NULL,
//			  has_monolinks TINYINT(3) UNSIGNED NOT NULL,
//			  psm_num_at_default_cutoff INT(11) NOT NULL,
//			  sample_psm_id INT(10) UNSIGNED NULL DEFAULT NULL,
//			  best_psm_value_for_ann_type_id DOUBLE NOT NULL,
//			  best_psm_value_string_for_ann_type_id VARCHAR(200) NOT NULL,
			  

	private static final String MONOLINK_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_MONOLINK ) ;

	
	private static final String SAVE_SQL =
			"INSERT INTO unified_rp__rep_pept__search__best_psm_value_generic_lookup "
			+ 	"( unified_reported_peptide_id, reported_peptide_id, search_id, "
			+ 		" annotation_type_id, link_type, "
			+  		" has_dynamic_modifictions, has_monolinks, psm_num_for_ann_type_id_at_default_cutoff, sample_psm_id, "
			+ 		" best_psm_value_for_ann_type_id, best_psm_value_string_for_ann_type_id ) "
			+ 	" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?  )";

	
	/**
	 * @param item
	 * @param conn
	 * @throws Exception
	 */
	public void saveToDatabase( UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO item, Connection conn ) throws Exception {
		
		PreparedStatement pstmt = null;
		
		final String sql = SAVE_SQL;


		
		try {

			int linkType = item.getLinkType();

			
			String linkTypeString = XLinkUtils.getTypeString( linkType );

			
			if ( linkType == XLinkUtils.TYPE_MONOLINK ) {
				
				String msg = "Invalid to insert unified_rp__rep_pept__search__best_psm_value_generic_lookup with type Monolink, UnifiedReportedPeptideId: " + item.getUnifiedReportedPeptideId();
				
				log.error( msg );
				
				throw new Exception(msg);
			}
			
			
			if (MONOLINK_TYPE_STRING.equals(linkTypeString) ) {
				
				String msg = "Invalid to insert unified_rp__rep_pept__search__best_psm_value_generic_lookup with type Monolink, UnifiedReportedPeptideId: " + item.getUnifiedReportedPeptideId();
				
				log.error( msg );
				
				throw new Exception(msg);
			}
			
					
			
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getUnifiedReportedPeptideId() );
			counter++;
			pstmt.setInt( counter, item.getReportedPeptideId() );
			counter++;
			pstmt.setInt( counter, item.getSearchId() );

			counter++;
			pstmt.setInt( counter, item.getAnnotationTypeId() );

			counter++;
			pstmt.setString( counter, linkTypeString );


			counter++;
			if ( item.isHasDynamicModifications() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}

			counter++;
			if ( item.isHasMonolinks() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			
			counter++;
			if ( item.getPsmNumForAnnTypeIdAtDefaultCutoff() != null ) {
				pstmt.setInt( counter, item.getPsmNumForAnnTypeIdAtDefaultCutoff() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}
			
			counter++;
			pstmt.setInt( counter, item.getSamplePsmId() );
			

			counter++;
			pstmt.setDouble( counter, item.getBestPsmValueForAnnTypeId() );
			counter++;
			pstmt.setString( counter, item.getBestPsmValueStringForAnnTypeId() );
			
			pstmt.executeUpdate();
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql
					+ " :::  item: " + item, e );
			
			throw e;
			
		} finally {
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
		}
		
		
	}
}
