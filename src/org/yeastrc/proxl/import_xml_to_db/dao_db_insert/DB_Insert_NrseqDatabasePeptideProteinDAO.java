package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.NrseqDatabasePeptideProteinDTO;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * table nrseq_database_peptide_protein
 *
 */
public class DB_Insert_NrseqDatabasePeptideProteinDAO {


	private static final Logger log = Logger.getLogger(DB_Insert_NrseqDatabasePeptideProteinDAO.class);

	private DB_Insert_NrseqDatabasePeptideProteinDAO() { }
	public static DB_Insert_NrseqDatabasePeptideProteinDAO getInstance() { return new DB_Insert_NrseqDatabasePeptideProteinDAO(); }


	/**
	 * Save list of items as single transaction
	 * 
	 * @param itemList
	 * @throws Exception
	 */
	public void saveListAsSingleTransaction( List<NrseqDatabasePeptideProteinDTO> itemList ) throws Exception {
		

		Connection conn = null;

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			conn.setAutoCommit(false);
			
			for ( NrseqDatabasePeptideProteinDTO item : itemList ) {

				save( item, conn );
			}

			try {
				
				conn.commit();
			} catch ( Exception e2 ) {
				
				log.error( "ERROR: doing commit ", e2 );
				
				throw e2;
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: ", e );
			
			try {
				
				conn.rollback();
			} catch ( Exception e2 ) {
				
				log.error( "ERROR: doing rollback ", e2 );
			}
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
		
			if( conn != null ) {

				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
	}

	public void save( NrseqDatabasePeptideProteinDTO item ) throws Exception {
		

		Connection conn = null;

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			save( item, conn );

		} catch ( Exception e ) {
			
			log.error( "ERROR: ", e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
		
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
	}
	
	public void save( NrseqDatabasePeptideProteinDTO item, Connection conn ) throws Exception {
		
		PreparedStatement pstmt = null;
		
		String sql = "INSERT IGNORE INTO nrseq_database_peptide_protein (nrseq_database_id, peptide_id, nrseq_id, is_unique) VALUES (?, ?, ?, ?)";

		try {
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, item.getNrseqDatabaseId() );
			pstmt.setInt( 2, item.getPeptideId() );
			pstmt.setInt( 3, item.getNrseqId() );
			
			if( item.isUnique() )
				pstmt.setString( 4, "Y" );
			else
				pstmt.setString( 4, "N" );
			
			pstmt.executeUpdate();
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
		}
		
	}
	

}
