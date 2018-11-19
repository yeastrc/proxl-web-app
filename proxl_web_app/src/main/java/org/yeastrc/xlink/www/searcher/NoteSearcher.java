package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.dao.NoteDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.NoteDTO;

/**
 * Return a list of all notes in the database for project id, ordered by id
 * 
 *
 */
public class NoteSearcher {
	
	private static final Log log = LogFactory.getLog(NoteSearcher.class);
	private NoteSearcher() { }
	private static final NoteSearcher _INSTANCE = new NoteSearcher();
	public static NoteSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<NoteDTO> getSearchsForProjectId( int projectId ) throws Exception {
		List<NoteDTO> notes = new ArrayList<NoteDTO>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT id FROM note WHERE project_id = ? ORDER BY id ";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			rs = pstmt.executeQuery();
			NoteDAO noteDAO = NoteDAO.getInstance();
			while( rs.next() )
				notes.add( noteDAO.getNoteDTOForNoteId( rs.getInt( 1 ) ) );
		} catch ( Exception e ) {
			String msg = "getSearchsForProjectId(), sql: " + sql;
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return notes;
	}
}
