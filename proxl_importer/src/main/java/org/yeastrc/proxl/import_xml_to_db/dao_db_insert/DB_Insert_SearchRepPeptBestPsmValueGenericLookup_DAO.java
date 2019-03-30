package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchRepPeptBestPsmValueGenericLookupDTO;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.utils.XLinkUtils;


/**
 * 
 * 
 * table search__rep_pept__best_psm_value_generic_lookup
 *
 */
public class DB_Insert_SearchRepPeptBestPsmValueGenericLookup_DAO {


	private static final Logger log = LoggerFactory.getLogger( DB_Insert_SearchRepPeptBestPsmValueGenericLookup_DAO.class);

	private DB_Insert_SearchRepPeptBestPsmValueGenericLookup_DAO() { }
	public static DB_Insert_SearchRepPeptBestPsmValueGenericLookup_DAO getInstance() { return new DB_Insert_SearchRepPeptBestPsmValueGenericLookup_DAO(); }

	/**
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchRepPeptBestPsmValueGenericLookupDTO item ) throws Exception {
		
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
			"INSERT INTO search__rep_pept__best_psm_value_generic_lookup "
			+ 	"( search_id, reported_peptide_id,  "
			+ 		" annotation_type_id, link_type, "
			+  		" has_dynamic_modifictions, has_monolinks, "
			+ 		" best_psm_value_for_ann_type_id ) "
			+ 	" VALUES ( ?, ?, ?, ?, ?, ?, ?  )";

	
	/**
	 * @param item
	 * @param conn
	 * @throws Exception
	 */
	public void saveToDatabase( SearchRepPeptBestPsmValueGenericLookupDTO item, Connection conn ) throws Exception {
		
		PreparedStatement pstmt = null;
		
		final String sql = SAVE_SQL;


		
		try {

			int linkType = item.getLinkType();

			
			String linkTypeString = XLinkUtils.getTypeString( linkType );

			
			if ( linkType == XLinkUtils.TYPE_MONOLINK ) {
				
				String msg = "Invalid to insert search__rep_pept__best_psm_value_generic_lookup with type Monolink, ReportedPeptideId: " + item.getReportedPeptideId();
				
				log.error( msg );
				
				throw new Exception(msg);
			}
			
			
			if (MONOLINK_TYPE_STRING.equals(linkTypeString) ) {
				
				String msg = "Invalid to insert search__rep_pept__best_psm_value_generic_lookup with type Monolink, ReportedPeptideId: " + item.getReportedPeptideId();
				
				log.error( msg );
				
				throw new Exception(msg);
			}
			
					
			
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setInt( counter, item.getReportedPeptideId() );

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
			pstmt.setDouble( counter, item.getBestPsmValueForAnnTypeId() );
			
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
