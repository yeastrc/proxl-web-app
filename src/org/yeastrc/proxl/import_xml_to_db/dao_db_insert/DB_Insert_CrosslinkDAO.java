package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.CrosslinkDTO;


/**
 * table crosslink
 *
 */
public class DB_Insert_CrosslinkDAO {

	private static final Logger log = Logger.getLogger(DB_Insert_CrosslinkDAO.class);

	private DB_Insert_CrosslinkDAO() { }
	public static DB_Insert_CrosslinkDAO getInstance() { return new DB_Insert_CrosslinkDAO(); }
	

	private final String INSERT_SQL = 
			"INSERT INTO crosslink (psm_id, nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position, "
			+ " peptide_1_id, peptide_2_id, peptide_1_position, peptide_2_position, " 
			+ " peptide_1_matched_peptide_id, peptide_2_matched_peptide_id, linker_mass) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public void save( CrosslinkDTO crosslink ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = INSERT_SQL;

		try {

//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			conn = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, crosslink.getPsm().getId() );
			counter++;
			pstmt.setInt( counter, crosslink.getProtein1().getNrseqId() );
			counter++;
			pstmt.setInt( counter, crosslink.getProtein2().getNrseqId() );
			counter++;
			pstmt.setInt( counter, crosslink.getProtein1Position() );
			counter++;
			pstmt.setInt( counter, crosslink.getProtein2Position() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide1Id() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide2Id() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide1Position() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide2Position() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide1MatchedPeptideId() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide2MatchedPeptideId() );
			counter++;
			pstmt.setBigDecimal( counter, crosslink.getLinkerMass() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				crosslink.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert crosslink" );
			
		} catch ( Exception e ) {
			
			String msg = "ERROR inserting crosslink. database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n crosslink: " + crosslink
					+ "\nsql: " + sql;
			log.error( msg, e );
			
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
