package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.PDBFileDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.constants.PDBFileConstants;
import org.yeastrc.xlink.www.objects.WWWPDBFile;


/**
 * Get lists of PDB files applicable to certain situations
 * @author Michael Riffle
 *
 */
public class PDBFileSearcher {

	private PDBFileSearcher() { }
	public static PDBFileSearcher getInstance() { return new PDBFileSearcher(); }

	private static final Logger log = Logger.getLogger(PDBFileSearcher.class);
	
	
	
	private static final String SQL_WITH_PROJECT_ID = 
			 "SELECT id FROM pdb_file WHERE visibility = '" + PDBFileConstants.VISIBILITY_PUBLIC
			 + "' OR ( visibility = '" + PDBFileConstants.VISIBILITY_PROJECT 
			 + "' AND project_id = ? ) ORDER BY id";
	
			
			
	
	/**
	 * Get a list of PDB files (without file contents) for the supplied user for the supplied project. Only public PDB files
	 * and project PDB files for this project will be returned
	 * 
	 * @param userId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<WWWPDBFile> getPDBFilesNoContent( int userId, int projectId ) throws Exception {
		
		List<WWWPDBFile> pdbFiles = new ArrayList<WWWPDBFile>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQL_WITH_PROJECT_ID;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				WWWPDBFile wpf = new WWWPDBFile();
				
				wpf.setDto( PDBFileDAO.getInstance().getPDBFileNoContent( rs.getInt( 1 ) ) );
				
				if( userId == wpf.getDto().getUploadedBy() )
					wpf.setCanEdit( true );
				else
					wpf.setCanEdit( false );
				
				pdbFiles.add( wpf );
			}


		} catch ( Exception e ) {

			log.error( "ERROR: getPDBFilesNoContent  SQL: " + sql, e );

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
		
		return pdbFiles;		
	}
	
	/**
	 * Get a list of PDB files (without file contents) for the supplied project. Only public PDB files
	 * and project PDB files for this project will be returned
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<WWWPDBFile> getPDBFilesNoContent( int projectId ) throws Exception {
		List<WWWPDBFile> pdbFiles = new ArrayList<WWWPDBFile>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQL_WITH_PROJECT_ID;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {

				WWWPDBFile wpf = new WWWPDBFile();
				
				wpf.setDto( PDBFileDAO.getInstance().getPDBFileNoContent( rs.getInt( 1 ) ) );
				wpf.setCanEdit( false );
				
				pdbFiles.add( wpf );
			}

		} catch ( Exception e ) {

			log.error( "ERROR: getPDBFilesNoContent  SQL: " + sql, e );

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
		
		return pdbFiles;
	}
	
	

	
	private static final String SQL_ANY_PDB_FOR_PROJECT_ID = 
			 "SELECT id FROM pdb_file WHERE "
			 + " visibility = '" + PDBFileConstants.VISIBILITY_PROJECT 
			 + "' AND project_id = ? LIMIT 1";
	
			
			
	
	/**
	 * Are there any project visibility PDB files for this project id
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public boolean anyPDBFilesForProjectId( int projectId ) throws Exception {
		
		boolean result = false;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQL_ANY_PDB_FOR_PROJECT_ID;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				result = true;
			}


		} catch ( Exception e ) {

			log.error( "ERROR: anyPDBFilesForProjectId  SQL: " + sql, e );

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
		
		return result;		
	}
	
	
}
