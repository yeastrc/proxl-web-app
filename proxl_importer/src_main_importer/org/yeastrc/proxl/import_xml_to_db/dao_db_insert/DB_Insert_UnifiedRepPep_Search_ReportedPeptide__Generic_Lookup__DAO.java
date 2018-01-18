package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.dto.UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * 
 * 
 * table unified_rp__search__rep_pept__generic_lookup
 *
 */
public class DB_Insert_UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO {


	private static final Logger log = Logger.getLogger(DB_Insert_UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO.class);

	private DB_Insert_UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO() { }
	public static DB_Insert_UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO getInstance() { return new DB_Insert_UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO(); }
	

	/**
	 * @param unifiedRP_ReportedPeptide_Search__DTO
	 * @throws Exception
	 */
	public void saveToDatabase( UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO item ) throws Exception {
		
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

	private static final String MONOLINK_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_MONOLINK ) ;
	
	private static final String SAVE_SQL =
			"INSERT INTO unified_rp__search__rep_pept__generic_lookup "
			+ 	"( unified_reported_peptide_id, reported_peptide_id, search_id, link_type, "
			+  		" has_dynamic_modifictions, has_monolinks, has_isotope_labels,"
			+ 		" psm_num_at_default_cutoff, "
			+ 		" peptide_meets_default_cutoffs, related_peptides_unique_for_search ) "
			+ 	" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

	/**
	 * @param item
	 * @param conn
	 * @throws Exception
	 */
	public void saveToDatabase( UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO item, Connection conn ) throws Exception {
		
		PreparedStatement pstmt = null;
		final String sql = SAVE_SQL;
		try {
			int linkType = item.getLinkType();
			String linkTypeString = XLinkUtils.getTypeString( linkType );
			
			if ( linkType == XLinkUtils.TYPE_MONOLINK ) {
				String msg = "Invalid to insert unified_rp__search__rep_pept__generic_lookup with type Monolink, UnifiedReportedPeptideId: " + item.getUnifiedReportedPeptideId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if (MONOLINK_TYPE_STRING.equals(linkTypeString) ) {
				String msg = "Invalid to insert unified_rp__search__rep_pept__generic_lookup with type Monolink, UnifiedReportedPeptideId: " + item.getUnifiedReportedPeptideId();
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
			if ( item.isHasIsotopeLabels() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			
			counter++;
			pstmt.setInt( counter, item.getPsmNumAtDefaultCutoff() );
			
			counter++;
			pstmt.setString( counter, item.getPeptideMeetsDefaultCutoffs().value() );

			counter++;
			if ( item.isAllRelatedPeptidesUniqueForSearch() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql
					+ " :::   Item to insert: " + item, e );
			throw e;
		} finally {
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
		}
	}

}
