package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosUnlinkedDTO;


/**
 * Unlinked
 * 
 * table srch_rep_pept__prot_seq_id_unlinked
 *
 */
public class DB_Insert_SrchRepPeptProtSeqIdPosUnlinkedDAO {


	private static final Logger log = Logger.getLogger(DB_Insert_SrchRepPeptProtSeqIdPosUnlinkedDAO.class);

	private DB_Insert_SrchRepPeptProtSeqIdPosUnlinkedDAO() { }
	public static DB_Insert_SrchRepPeptProtSeqIdPosUnlinkedDAO getInstance() { return new DB_Insert_SrchRepPeptProtSeqIdPosUnlinkedDAO(); }


	private static final String INSERT_SQL = "INSERT INTO srch_rep_pept__prot_seq_id_unlinked "

			+ " ( search_id, reported_peptide_id, search_reported_peptide_peptide_id, "
			+   " protein_sequence_id )"

			+ " VALUES ( ?, ?, ?, ? )";

	/**
	 * Save the associated data to the database
	 * @param item
	 * @throws Exception
	 */
	public void save( SrchRepPeptProtSeqIdPosUnlinkedDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = INSERT_SQL;
		
		try {

//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  item.getSearchId() );
			counter++;
			pstmt.setInt( counter,  item.getReportedPeptideId());
			counter++;
			pstmt.setInt( counter,  item.getSearchReportedPeptidepeptideId() );
			counter++;
			pstmt.setInt( counter,  item.getProteinSequenceId() );
			
			pstmt.executeUpdate();

			rs = pstmt.getGeneratedKeys();
			
			if( rs.next() ) {
				
				item.setId( rs.getInt( 1 ) );
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: sql: " + sql, e );
			
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
