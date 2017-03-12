package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.PDBFileDTO;
/**
 * table pdb_file
 *
 */
public class PDBFileDAO {
	
	private static final Logger log = Logger.getLogger(PDBFileDAO.class);
	private PDBFileDAO() { }
	private static final PDBFileDAO _INSTANCE = new PDBFileDAO();
	public static PDBFileDAO getInstance() { return _INSTANCE; }
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PDBFileDTO getPDBFile( int id ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM pdb_file WHERE id = ?";
		PDBFileDTO pdbFile = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "could not find pdb file with id: " + id );
			pdbFile = new PDBFileDTO();
			pdbFile.setId( id );
			pdbFile.setContent( rs.getString( "content" ) );
			pdbFile.setDescription( rs.getString( "description" ) );
			pdbFile.setName( rs.getString( "name" ) );
			pdbFile.setProjectId( rs.getInt( "project_id" ) );
			pdbFile.setUploadDate( new DateTime( rs.getDate( "upload_date" ) ) );
			pdbFile.setUploadedBy( rs.getInt( "uploaded_by" ) );
			pdbFile.setVisibility( rs.getString( "visibility" ) );
		} catch ( Exception e ) {
			log.error( "ERROR: getPDBFile ", e );
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return pdbFile;
	}
	
	/**
	 * Get an PDBFile object but without the content of the file
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PDBFileDTO getPDBFileNoContent( int id ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT id, name, description, upload_date, uploaded_by, project_id, visibility FROM pdb_file WHERE id = ?";
		PDBFileDTO pdbFile = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "could not find pdb file with id: " + id );
			pdbFile = new PDBFileDTO();
			pdbFile.setId( id );
			pdbFile.setDescription( rs.getString( "description" ) );
			pdbFile.setName( rs.getString( "name" ) );
			pdbFile.setProjectId( rs.getInt( "project_id" ) );
			pdbFile.setUploadDate( new DateTime( rs.getDate( "upload_date" ) ) );
			pdbFile.setUploadedBy( rs.getInt( "uploaded_by" ) );
			pdbFile.setVisibility( rs.getString( "visibility" ) );
		} catch ( Exception e ) {
			log.error( "ERROR: getPDBFileNoContent ", e );
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return pdbFile;
	}
	
	/**
	 * Get the content of a PDB file from the database
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getPDBFileContent( int id ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT content FROM pdb_file WHERe id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "could not find pdb file with id: " + id );
			return rs.getString( 1 );
		} catch ( Exception e ) {
			log.error( "ERROR: getPDBFileContent ", e );
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
	}
	
	/**
	 * Delete the PDB file with the supplied pdbFileId from the database
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public void deletePDBFile( int id ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM pdb_file WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			log.error( "ERROR: getPDBFileContent ", e );
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
	}
}
