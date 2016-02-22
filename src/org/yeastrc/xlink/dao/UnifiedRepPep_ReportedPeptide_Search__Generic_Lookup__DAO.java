package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * 
 * 
 * table unified_rp__rep_pept__search__generic_lookup
 *
 */
public class UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO {
	
	private static final Logger log = Logger.getLogger(UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO.class);

	private UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO() { }
	public static UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO getInstance() { return new UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO(); }
	
	


	/**
	 * Query on the 2 foreign keys
	 * 
	 * @param reportedPeptideId
	 * @param searchId
	 * @return psm.id if found, null otherwise
	 * @throws Exception
	 */
	public Integer getUnifiedReportedPeptideIdForSearchIdAndReportedPeptideId( int reportedPeptideId,  int searchId ) throws Exception {
		
		Integer unifiedReportedPeptideId = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT unified_reported_peptide_id FROM unified_rp__rep_pept__search__generic_lookup WHERE reported_peptide_id = ? AND search_id = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, reportedPeptideId );
			pstmt.setInt( 2, searchId );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				unifiedReportedPeptideId = rs.getInt( "unified_reported_peptide_id" );
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
		
		
		return unifiedReportedPeptideId;
	}
	
	
	/**
	 * @param unifiedRP_ReportedPeptide_Search__DTO
	 * @throws Exception
	 */
	public void saveToDatabase( UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO item ) throws Exception {
		
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
	


//	CREATE TABLE unified_rp__rep_pept__search__generic_lookup (
//	  unified_reported_peptide_id INT(10) UNSIGNED NOT NULL,
//	  reported_peptide_id INT(10) UNSIGNED NOT NULL,
//	  search_id INT(10) UNSIGNED NOT NULL,
//	  link_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
//	  has_dynamic_modifictions TINYINT(3) UNSIGNED NOT NULL,
//	  has_monolinks TINYINT(3) UNSIGNED NOT NULL,
//	  psm_num_at_default_cutoff INT(10) UNSIGNED NOT NULL,
//	  sample_psm_id INT(10) UNSIGNED NULL DEFAULT NULL,
			  

	private static final String MONOLINK_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_MONOLINK ) ;

	
	private static final String SAVE_SQL =
			"INSERT INTO unified_rp__rep_pept__search__generic_lookup "
			+ 	"( unified_reported_peptide_id, reported_peptide_id, search_id, link_type, "
			+  		 " has_dynamic_modifictions, has_monolinks, psm_num_at_default_cutoff, sample_psm_id, peptide_meets_default_cutoffs ) "
			+ 	" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

	
	/**
	 * @param item
	 * @param conn
	 * @throws Exception
	 */
	public void saveToDatabase( UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO item, Connection conn ) throws Exception {
		
		PreparedStatement pstmt = null;
		
		final String sql = SAVE_SQL;


		
		try {

			int linkType = item.getLinkType();

			
			String linkTypeString = XLinkUtils.getTypeString( linkType );

			
			if ( linkType == XLinkUtils.TYPE_MONOLINK ) {
				
				String msg = "Invalid to insert unified_rp__rep_pept__search__generic_lookup with type Monolink, UnifiedReportedPeptideId: " + item.getUnifiedReportedPeptideId();
				
				log.error( msg );
				
				throw new Exception(msg);
			}
			
			
			if (MONOLINK_TYPE_STRING.equals(linkTypeString) ) {
				
				String msg = "Invalid to insert unified_rp__rep_pept__search__generic_lookup with type Monolink, UnifiedReportedPeptideId: " + item.getUnifiedReportedPeptideId();
				
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
			pstmt.setInt( counter, item.getPsmNumAtDefaultCutoff() );
			
			counter++;
			pstmt.setInt( counter, item.getSamplePsmId() );
			
			counter++;
			pstmt.setString( counter, item.getPeptideMeetsDefaultCutoffs().value() );
		
			
			pstmt.executeUpdate();
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql
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
