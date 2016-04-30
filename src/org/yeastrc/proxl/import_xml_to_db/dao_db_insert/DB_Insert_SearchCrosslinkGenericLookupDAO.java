package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchCrosslinkGenericLookupDTO;

/**
 * table search_crosslink_generic_lookup
 *
 */
public class DB_Insert_SearchCrosslinkGenericLookupDAO {


	private static final Logger log = Logger.getLogger(DB_Insert_SearchCrosslinkGenericLookupDAO.class);

	private DB_Insert_SearchCrosslinkGenericLookupDAO() { }
	public static DB_Insert_SearchCrosslinkGenericLookupDAO getInstance() { return new DB_Insert_SearchCrosslinkGenericLookupDAO(); }
	
	

	/**
	 * Save the associated data to the database
	 * @param item
	 * @throws Exception
	 */
	public void save( SearchCrosslinkGenericLookupDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO search_crosslink_generic_lookup "

				+ " ( search_id, nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position, "
				+   " num_psm_at_default_cutoff, num_linked_peptides_at_default_cutoff, num_unique_peptides_linked_at_default_cutoff )"

				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";

		try {

//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  item.getSearchId() );
			counter++;
			pstmt.setInt( counter,  item.getNrseqId1() );
			counter++;
			pstmt.setInt( counter,  item.getNrseqId2() );
			counter++;
			pstmt.setInt( counter,  item.getProtein1Position() );
			counter++;
			pstmt.setInt( counter,  item.getProtein2Position() );
			
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
