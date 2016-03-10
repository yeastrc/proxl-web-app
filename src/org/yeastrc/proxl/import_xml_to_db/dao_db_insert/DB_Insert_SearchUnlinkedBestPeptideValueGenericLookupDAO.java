package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchUnlinkedBestPeptideValueGenericLookupDTO;


/**
 * table search_unlinked_best_peptide_value_generic_lookup
 *
 */
public class DB_Insert_SearchUnlinkedBestPeptideValueGenericLookupDAO {


	private static final Logger log = Logger.getLogger(DB_Insert_SearchUnlinkedBestPeptideValueGenericLookupDAO.class);

	private DB_Insert_SearchUnlinkedBestPeptideValueGenericLookupDAO() { }
	public static DB_Insert_SearchUnlinkedBestPeptideValueGenericLookupDAO getInstance() { return new DB_Insert_SearchUnlinkedBestPeptideValueGenericLookupDAO(); }
	

	private static final String SQL = "INSERT INTO search_unlinked_best_peptide_value_generic_lookup "

			+ " ( search_unlinked_generic_lookup_id, search_id, nrseq_id, "
			+   " annotation_type_id, best_peptide_value_for_ann_type_id, best_peptide_value_string_for_ann_type_id )"

			+ " VALUES ( ?, ?, ?, ?, ?, ? )";
	
	/**
	 * Save the associated data to the database
	 * @param item
	 * @throws Exception
	 */
	public void save( SearchUnlinkedBestPeptideValueGenericLookupDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			pstmt = conn.prepareStatement( SQL );
			
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  item.getSearchUnlinkedGenericLookup() );
			counter++;
			pstmt.setInt( counter,  item.getSearchId() );
			counter++;
			pstmt.setInt( counter,  item.getNrseqId() );
			
			counter++;
			pstmt.setInt( counter,  item.getAnnotationTypeId() );
			counter++;
			pstmt.setDouble( counter,  item.getBestPeptideValueForAnnTypeId() );
			counter++;
			pstmt.setString( counter,  item.getBestPeptideValueStringForAnnTypeId() );
			
			pstmt.executeUpdate();

			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert record..." );
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + SQL, e );
			
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
