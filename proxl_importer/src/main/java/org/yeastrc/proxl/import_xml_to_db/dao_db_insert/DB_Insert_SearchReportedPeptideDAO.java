package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDTO;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * 
 * 
 * table search_reported_peptide
 *
 */
public class DB_Insert_SearchReportedPeptideDAO {


	private static final Logger log = LoggerFactory.getLogger( DB_Insert_SearchReportedPeptideDAO.class);

	private DB_Insert_SearchReportedPeptideDAO() { }
	public static DB_Insert_SearchReportedPeptideDAO getInstance() { return new DB_Insert_SearchReportedPeptideDAO(); }

	private static final String MONOLINK_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_MONOLINK ) ;


	/**
	 *	insert duplicates are ignored
	 *
	 * @param psm
	 * @throws Exception
	 */
	public void saveToDatabaseIgnoreDuplicates( SearchReportedPeptideDTO item ) throws Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "INSERT IGNORE INTO search_reported_peptide ( search_id, reported_peptide_id, link_type ) VALUES (?, ?, ?)";

		try {

			String linkTypeString = XLinkUtils.getTypeString( item.getLinkType() );

			
			if ( item.getLinkType() == XLinkUtils.TYPE_MONOLINK ) {
				
				String msg = "Invalid to insert search_reported_peptide with type Monolink, reported_peptide_id: " + item.getReportedPeptideId();
				
				log.error( msg );
				
				throw new Exception(msg);
			}
			
			
			if (MONOLINK_TYPE_STRING.equals(item.getLinkType()) ) {
				
				String msg = "Invalid to insert search_reported_peptide with type Monolink, reported_peptide_id: " + item.getReportedPeptideId();
				
				log.error( msg );
				
				throw new Exception(msg);
			}
			

			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, item.getSearchId() );
			pstmt.setInt( 2, item.getReportedPeptideId() );
			pstmt.setString( 3, linkTypeString );
			

			pstmt.executeUpdate();

		} catch ( Exception e ) {

			String msg = "Failed to insert SearchReportedPeptideDTO: " + item + ".  SQL: " + sql;

			log.error( msg );

			throw e;

		} finally {

			// be sure database handles are closed
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
