package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;


/**
 * table srch_rep_pept__pept__dynamic_mod
 *
 */
public class DB_Insert_SrchRepPeptPeptDynamicModDAO {


	private static final Logger log = LoggerFactory.getLogger( DB_Insert_SrchRepPeptPeptDynamicModDAO.class);

	private DB_Insert_SrchRepPeptPeptDynamicModDAO() { }
	public static DB_Insert_SrchRepPeptPeptDynamicModDAO getInstance() { return new DB_Insert_SrchRepPeptPeptDynamicModDAO(); }


	private static final String INSERT_SQL = "INSERT INTO srch_rep_pept__pept__dynamic_mod "

			+ " ( search_reported_peptide_peptide_id, "
			+   " position, mass, is_monolink, is_n_terminal, is_c_terminal )"

			+ " VALUES ( ?, ?, ?, ?, ?, ? )";

	private static final String QUERY_UNIQUE_RECORD_SQL = 
			
			"SELECT id FROM srch_rep_pept__pept__dynamic_mod "

			+ " WHERE search_reported_peptide_peptide_id = ?  "
			+   " AND position = ? AND mass = ? AND is_monolink = ? AND is_n_terminal = ? AND is_c_terminal = ? ";

	
	/**
	 * Save the associated data to the database
	 * @param item
	 * @throws Exception
	 */
	public void save( SrchRepPeptPeptDynamicModDTO item ) throws Exception {
		
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
			pstmt.setInt( counter,  item.getSearchReportedPeptidepeptideId() );
			
			counter++;
			pstmt.setInt( counter,  item.getPosition() );
			counter++;
			pstmt.setDouble( counter,  item.getMass() );

			counter++;
			if ( item.isMonolink() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}

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
				
			} else {

				//  Inserting duplicate record so no record inserted.
				//  Need to query DB to get "id" field value using 
				//    fields that identify a "unique" record
				//    looking at the index of type UNIQUE on the table
				
				rs.close();
				rs = null;
				pstmt.close();
				pstmt = null;
				
				sql = QUERY_UNIQUE_RECORD_SQL;

				pstmt = conn.prepareStatement( sql );

				counter = 0;
				
				counter++;
				pstmt.setInt( counter,  item.getSearchReportedPeptidepeptideId() );

				counter++;
				pstmt.setInt( counter,  item.getPosition() );
				counter++;
				pstmt.setDouble( counter,  item.getMass() );

				counter++;
				if ( item.isMonolink() ) {
					pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
				} else {
					pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
				}

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
				
				rs = pstmt.executeQuery();
				
				if ( ! rs.next() ) {
				
					throw new Exception( "Failed to insert record and failed to find unique record. item: " + item );
				}
				
				item.setId( rs.getInt( "id" ) );
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
