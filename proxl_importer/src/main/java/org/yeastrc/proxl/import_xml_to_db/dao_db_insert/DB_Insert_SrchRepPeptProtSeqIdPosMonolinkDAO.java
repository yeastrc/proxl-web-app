package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosMonolinkDTO;
import org.yeastrc.proxl.import_xml_to_db.exception.ProxlImporterDuplicateDataException;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;


/**
 * Monolink  
 *   
 * table srch_rep_pept__prot_seq_id_pos_monolink
 *
 */
public class DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO {


	private static final Logger log = LoggerFactory.getLogger( DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO.class);
	
	private static final DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO instance = new DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO();

	private DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO() { }
	public static DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO getSingletonInstance() { return instance; }
	
	
	/**
	 * To not insert duplicates
	 */
	private Map<SrchRepPeptProtSeqIdPosMonolinkDTO, SrchRepPeptProtSeqIdPosMonolinkDTO> duplicateTracking = new HashMap<>();


	private static final String INSERT_SQL = "INSERT INTO srch_rep_pept__prot_seq_id_pos_monolink "

			+ " ( search_id, reported_peptide_id, search_reported_peptide_peptide_id, "
			+   " peptide_position, protein_sequence_version_id, protein_sequence_position, is_n_terminal, is_c_terminal )"

			+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";

	/**
	 * Save the associated data to the database
	 * @param item
	 * @throws Exception
	 * @throws ProxlImporterDuplicateDataException
	 */
	public void save( SrchRepPeptProtSeqIdPosMonolinkDTO item ) throws Exception, ProxlImporterDuplicateDataException {
		
		SrchRepPeptProtSeqIdPosMonolinkDTO prevItemForThisKey = duplicateTracking.put( item, item );
		if ( prevItemForThisKey != null ) {
			//  Already inserted a record with this key (based on equals() of SrchRepPeptProtSeqIdPosMonolinkDTO)
			String msg = "Aleady inserted record with value: " + item;
			log.error( msg );
			throw new ProxlImporterDuplicateDataException(msg);
		}
		
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
			pstmt.setInt( counter,  item.getPeptidePosition() );
			
			counter++;
			pstmt.setInt( counter,  item.getProteinSequenceVersionId() );
			
			counter++;
			pstmt.setInt( counter,  item.getProteinSequencePosition() );

			counter++;
			if ( item.isIs_N_Terminal() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			counter++;
			if ( item.isIs_C_Terminal() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			
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
