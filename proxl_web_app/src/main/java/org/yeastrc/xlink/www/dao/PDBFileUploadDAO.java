package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
/**
 * table pdb_file
 *
 */
public class PDBFileUploadDAO {
	
	private static final Logger log = LoggerFactory.getLogger( PDBFileUploadDAO.class);
	private PDBFileUploadDAO() { }
	public static PDBFileUploadDAO getInstance() { return new PDBFileUploadDAO(); }
	
	/**
	 * 
	 * @param file
	 * @param description
	 * @throws Exception
	 */
	public void savePDBFile( byte[] pdbFileContents, String pdbFilename, String description, int userId, int projectId, String visibility ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		final String sql = "INSERT INTO pdb_file (name, description, content, uploaded_by, project_id, visibility) VALUES (?, ?, ?, ?, ?, ?)";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, pdbFilename );
			if( description != null && !description.equals( "" ) )
				pstmt.setString( 2, description);
			else
				pstmt.setNull( 2, java.sql.Types.VARCHAR);
			pstmt.setBytes( 3, pdbFileContents );
			pstmt.setInt( 4, userId );
			pstmt.setInt( 5,  projectId );
			pstmt.setString( 6, visibility );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to insert PDB file: " + pdbFilename + ", sql: " + sql;
			log.error( msg, e );
			throw e;
		} finally {
			// be sure database handles are closed
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return;
	}
}
