package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinCoverageDTO;


/**
 * table protein_coverage
 *
 */
public class DB_Insert_ProteinCoverageDAO {


	private static final Logger log = LoggerFactory.getLogger( DB_Insert_ProteinCoverageDAO.class);

	private DB_Insert_ProteinCoverageDAO() { }
	public static DB_Insert_ProteinCoverageDAO getInstance() { return new DB_Insert_ProteinCoverageDAO(); }


	private static final String INSERT_SQL = "INSERT INTO protein_coverage "

			+ " ( search_id, reported_peptide_id, peptide_id_info_only, "
			+   " protein_sequence_version_id, protein_start_position, protein_end_position )"

			+ " VALUES ( ?, ?, ?, ?, ?, ? )";
	
	/**
	 * Save the associated data to the database
	 * @param item
	 * @throws Exception
	 */
	public void save( ProteinCoverageDTO item ) throws Exception {
		
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
			pstmt.setInt( counter,  item.getPeptideIdInfoOnly() );
			
			counter++;
			pstmt.setInt( counter,  item.getProteinSequenceVersionId() );

			counter++;
			pstmt.setInt( counter,  item.getProteinStartPosition() );
			counter++;
			pstmt.setInt( counter,  item.getProteinEndPosition() );

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
