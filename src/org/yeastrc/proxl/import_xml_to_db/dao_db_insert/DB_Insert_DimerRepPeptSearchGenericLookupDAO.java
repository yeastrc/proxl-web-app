package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.DimerRepPeptSearchGenericLookupDTO;

/**
 * 
 * 
 * table dimer__rep_pept__search__generic_lookup
 *
 */
public class DB_Insert_DimerRepPeptSearchGenericLookupDAO {


	private static final Logger log = Logger.getLogger(DB_Insert_DimerRepPeptSearchGenericLookupDAO.class);

	private DB_Insert_DimerRepPeptSearchGenericLookupDAO() { }
	public static DB_Insert_DimerRepPeptSearchGenericLookupDAO getInstance() { return new DB_Insert_DimerRepPeptSearchGenericLookupDAO(); }
	

	/**
	 * @param unifiedRP_ReportedPeptide_Search__DTO
	 * @throws Exception
	 */
	public void saveToDatabase( DimerRepPeptSearchGenericLookupDTO item ) throws Exception {
		
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
		  
	private static final String SAVE_SQL =
			"INSERT INTO dimer__rep_pept__search__generic_lookup "
			+ 	"( search_id, reported_peptide_id, "
			+ 	"  nrseq_id_1, nrseq_id_2, "
			+ 	"  psm_num_at_default_cutoff, peptide_meets_default_cutoffs, related_peptides_unique_for_search ) "
			+ 	" VALUES ( ?, ?, ?, ?, ?, ?, ? )";

	/**
	 * @param item
	 * @param conn
	 * @throws Exception
	 */
	public void saveToDatabase( DimerRepPeptSearchGenericLookupDTO item, Connection conn ) throws Exception {
		
		PreparedStatement pstmt = null;
		
		final String sql = SAVE_SQL;


		
		try {

			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setInt( counter, item.getReportedPeptideId() );
	

			counter++;
			pstmt.setInt( counter, item.getProteinId_1() );
			counter++;
			pstmt.setInt( counter, item.getProteinId_2() );
			
			
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
