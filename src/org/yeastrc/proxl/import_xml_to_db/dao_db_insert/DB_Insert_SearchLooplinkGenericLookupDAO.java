package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchLooplinkGenericLookupDTO;


/**
 * table search_looplink_generic_lookup
 *
 */
public class DB_Insert_SearchLooplinkGenericLookupDAO {


	private static final Logger log = Logger.getLogger(DB_Insert_SearchLooplinkGenericLookupDAO.class);

	private DB_Insert_SearchLooplinkGenericLookupDAO() { }
	public static DB_Insert_SearchLooplinkGenericLookupDAO getInstance() { return new DB_Insert_SearchLooplinkGenericLookupDAO(); }

	/**
	 * Save the associated data to the database
	 * @param item
	 * @throws Exception
	 */
	public void save( SearchLooplinkGenericLookupDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO search_looplink_generic_lookup "

				+ " ( search_id, nrseq_id, protein_position_1, protein_position_2, "
				+   " num_psm_at_default_cutoff, num_linked_peptides_at_default_cutoff, num_unique_peptides_linked_at_default_cutoff )"

				+ " VALUES ( ?, ?, ?, ?, ?, ?, ? )";

		try {

//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			pstmt = conn.prepareStatement( sql );
			
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  item.getSearchId() );
			counter++;
			pstmt.setInt( counter,  item.getNrseqId() );
			counter++;
			pstmt.setInt( counter,  item.getProteinPosition1() );
			counter++;
			pstmt.setInt( counter,  item.getProteinPosition2() );
			
			counter++;
			pstmt.setInt( counter,  item.getNumPsmAtDefaultCutoff() );
			counter++;
			pstmt.setInt( counter,  item.getNumLinkedPeptidesAtDefaultCutoff() );
			counter++;
			pstmt.setInt( counter,  item.getNumUniqueLinkedPeptidesAtDefaultCutoff() );
			
			pstmt.executeUpdate();

			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert record..." );
			
			
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
		
	}
}
