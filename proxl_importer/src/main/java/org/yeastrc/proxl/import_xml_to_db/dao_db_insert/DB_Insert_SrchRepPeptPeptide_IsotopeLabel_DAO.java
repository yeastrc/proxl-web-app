package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptide_IsotopeLabel_DTO;


/**
 * table srch_rep_pept__peptide_isotope_label
 *
 */
public class DB_Insert_SrchRepPeptPeptide_IsotopeLabel_DAO {


	private static final Logger log = Logger.getLogger(DB_Insert_SrchRepPeptPeptide_IsotopeLabel_DAO.class);

	private DB_Insert_SrchRepPeptPeptide_IsotopeLabel_DAO() { }
	public static DB_Insert_SrchRepPeptPeptide_IsotopeLabel_DAO getInstance() { return new DB_Insert_SrchRepPeptPeptide_IsotopeLabel_DAO(); }


	private static final String INSERT_SQL = "INSERT INTO srch_rep_pept__peptide_isotope_label "

			+ " ( srch_rep_pept__peptide_id, isotope_label_id )"

			+ " VALUES ( ?, ? )";
	
	/**
	 * Save the associated data to the database
	 * @param item
	 * @throws Exception
	 */
	public void save( SrchRepPeptPeptide_IsotopeLabel_DTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = INSERT_SQL;
		
		try {

//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			pstmt = conn.prepareStatement( sql );
//			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  item.getSrchRepPeptPeptideId() );
			counter++;
			pstmt.setInt( counter,  item.getIsotopeLabelId() );

			pstmt.executeUpdate();

//			rs = pstmt.getGeneratedKeys();
//			if( rs.next() ) {
//				item.setId( rs.getInt( 1 ) );
//			}
			
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
