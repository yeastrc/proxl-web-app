package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dao.PDBAlignmentDAO;
import org.yeastrc.xlink.www.dto.PDBAlignmentDTO;

public class PDBAlignmentSearcher {

	private PDBAlignmentSearcher() { }
	private static final PDBAlignmentSearcher _INSTANCE = new PDBAlignmentSearcher();
	public static PDBAlignmentSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * Get the alignments associated with a given pdb file. Returns a map where the key is the
	 * chain id in the pdb, and the value is a list of alignment dtos corresponding to it.
	 * @param pdbFileId
	 * @return
	 * @throws Exception
	 */
	public Map<String, List<PDBAlignmentDTO>> getAlignmentsForPDBFile( int pdbFileId ) throws Exception {
		
		Map<String, List<PDBAlignmentDTO>> alignments = new HashMap<String, List<PDBAlignmentDTO>>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			String sql = "SELECT id FROM pdb_alignment WHERE pdb_file_id = ? ORDER BY id";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, pdbFileId );
			
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				
				PDBAlignmentDTO dto = PDBAlignmentDAO.getInstance().getPDBAlignment( rs.getInt( 1 ) );
				
				if( !alignments.containsKey( dto.getChainId() ) )
					alignments.put( dto.getChainId(), new ArrayList<PDBAlignmentDTO>() );
				
				alignments.get( dto.getChainId() ).add( dto );				
			}
			
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
		
		return alignments;
	}
	
	
}
