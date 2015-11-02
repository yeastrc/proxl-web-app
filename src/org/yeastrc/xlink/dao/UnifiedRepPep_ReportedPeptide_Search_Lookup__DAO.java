package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedRepPep_ReportedPeptide_Search_Lookup__DTO;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * 
 * 
 * table unified_rep_pep__reported_peptide__search_lookup
 *
 */
public class UnifiedRepPep_ReportedPeptide_Search_Lookup__DAO {
	
	private static final Logger log = Logger.getLogger(UnifiedRepPep_ReportedPeptide_Search_Lookup__DAO.class);

	private UnifiedRepPep_ReportedPeptide_Search_Lookup__DAO() { }
	public static UnifiedRepPep_ReportedPeptide_Search_Lookup__DAO getInstance() { return new UnifiedRepPep_ReportedPeptide_Search_Lookup__DAO(); }
	
//	CREATE TABLE unified_rep_pep__reported_peptide__search_lookup (
//			  unified_reported_peptide_id INT UNSIGNED NOT NULL,
//			  reported_peptide_id INT UNSIGNED NOT NULL,
//			  search_id INT UNSIGNED NOT NULL,
//	  		  link_type ENUM('looplink','crosslink','unlinked','monolink','dimer') NOT NULL,
//			  peptide_q_value_for_search DOUBLE NULL,
//			  best_psm_q_value DOUBLE NULL,
//	  		  psm_num_at_pt_01_q_cutoff INT NOT NULL,
//			  sample_psm_id INT(10) UNSIGNED NULL DEFAULT NULL
//			  PRIMARY KEY (unified_reported_peptide_id, reported_peptide_id, search_id),
			  
	
	/**
	 * @param unifiedRP_ReportedPeptide_Search__DTO
	 * @throws Exception
	 */
	public void saveToDatabase( UnifiedRepPep_ReportedPeptide_Search_Lookup__DTO item ) throws Exception {
		
		Connection conn = null;

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			saveToDatabase( item, conn );
			
		} catch ( Exception e ) {
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		
	}
	


//	CREATE TABLE unified_rep_pep__reported_peptide__search_lookup (
//			  unified_reported_peptide_id INT UNSIGNED NOT NULL,
//			  reported_peptide_id INT UNSIGNED NOT NULL,
//			  search_id INT UNSIGNED NOT NULL,
//	  		  link_type ENUM('looplink','crosslink','unlinked','monolink','dimer') NOT NULL,
//			  peptide_q_value_for_search DOUBLE NULL,
//			  best_psm_q_value DOUBLE NULL,
//	  		  has_dynamic_modifictions TINYINT UNSIGNED NOT NULL,
//	  		  has_monolinks TINYINT UNSIGNED NOT NULL,
//	  		  psm_num_at_pt_01_q_cutoff INT NOT NULL,
//	  		  sample_psm_id INT(10) UNSIGNED NULL DEFAULT NULL
//			  PRIMARY KEY (unified_reported_peptide_id, reported_peptide_id, search_id),

	

	private static final String MONOLINK_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_MONOLINK ) ;

	
	private static final String SAVE_SQL =
			"INSERT INTO unified_rep_pep__reported_peptide__search_lookup "
			+ 	"( unified_reported_peptide_id, reported_peptide_id, search_id, link_type, "
			+        " peptide_q_value_for_search, best_psm_q_value, "
			+  		 " has_dynamic_modifictions, has_monolinks, psm_num_at_pt_01_q_cutoff, sample_psm_id ) "
			+ 	" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

	
	/**
	 * @param item
	 * @param conn
	 * @throws Exception
	 */
	public void saveToDatabase( UnifiedRepPep_ReportedPeptide_Search_Lookup__DTO item, Connection conn ) throws Exception {
		
		PreparedStatement pstmt = null;
		
		final String sql = SAVE_SQL;


		
		try {

			int linkType = item.getLinkType();

			
			String linkTypeString = XLinkUtils.getTypeString( linkType );

			
			if ( linkType == XLinkUtils.TYPE_MONOLINK ) {
				
				String msg = "Invalid to insert unified_rep_pep__reported_peptide__search_lookup with type Monolink, UnifiedReportedPeptideId: " + item.getUnifiedReportedPeptideId();
				
				log.error( msg );
				
				throw new Exception(msg);
			}
			
			
			if (MONOLINK_TYPE_STRING.equals(linkTypeString) ) {
				
				String msg = "Invalid to insert unified_rep_pep__reported_peptide__search_lookup with type Monolink, UnifiedReportedPeptideId: " + item.getUnifiedReportedPeptideId();
				
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
			pstmt.setString( counter, linkTypeString );

			
			counter++;
			if ( item.getPeptideQValue() != null ) {
				pstmt.setDouble( counter, item.getPeptideQValue() );
			} else {
				
				pstmt.setNull( counter, java.sql.Types.DOUBLE );
			}

			counter++;
			if ( item.getBestPsmQValue() != null ) {
				pstmt.setDouble( counter, item.getBestPsmQValue() );
			} else {
				
				pstmt.setNull( counter, java.sql.Types.DOUBLE );
			}
			

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
			pstmt.setInt( counter, item.getPsmNumAtPt01QvalueCutoff() );
			
			counter++;
			pstmt.setInt( counter, item.getSamplePsmId() );
			
			
			pstmt.executeUpdate();
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
		}
		
		
	}
	
}
