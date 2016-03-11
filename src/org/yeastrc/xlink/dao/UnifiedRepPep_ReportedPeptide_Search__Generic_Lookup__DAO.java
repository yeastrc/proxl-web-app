package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
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

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );


			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, reportedPeptideId );
			pstmt.setInt( 2, searchId );

			rs = pstmt.executeQuery();

			if ( rs.next() ) {
				unifiedReportedPeptideId = rs.getInt( "unified_reported_peptide_id" );
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


		return unifiedReportedPeptideId;
	}


	/**
	 * Populate object from result set
	 * 
	 * @param rs
	 * @return
	 * @throws Exception 
	 */
	public UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO populateFromResultSet(	ResultSet rs) throws Exception {

		UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO item = new UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO();


		item.setSearchId( rs.getInt( "search_id" ) );
		item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );

		String typeString = rs.getString( "link_type" );
		int typeNumber = XLinkUtils.getTypeNumber( typeString );
		item.setLinkType(typeNumber);

		int hasDynamicModificationsInt = rs.getInt( "has_dynamic_modifictions" );
		int hashasMonolinksInt = rs.getInt( "has_monolinks" );

		if ( hasDynamicModificationsInt ==	Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE ) {

			item.setHasDynamicModifications( true );
		}

		if ( hashasMonolinksInt ==	Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE ) {

			item.setHasMonolinks( true );
		}


		item.setPsmNumAtDefaultCutoff( rs.getInt( "psm_num_at_default_cutoff" ) );
		item.setSamplePsmId( rs.getInt( "sample_psm_id" ) );


		String peptideMeetsDefaultCutoffsString = rs.getString( "peptide_meets_default_cutoffs" );
		Yes_No__NOT_APPLICABLE_Enum peptideMeetsDefaultCutoffs = Yes_No__NOT_APPLICABLE_Enum.fromValue( peptideMeetsDefaultCutoffsString );
		item.setPeptideMeetsDefaultCutoffs( peptideMeetsDefaultCutoffs );


		int allRelatedPeptidesUniqueForSearchInt = rs.getInt( "related_peptides_unique_for_search" );

		if ( allRelatedPeptidesUniqueForSearchInt ==	Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE ) {

			item.setAllRelatedPeptidesUniqueForSearch( true );
		}


		return item;

	}


}
