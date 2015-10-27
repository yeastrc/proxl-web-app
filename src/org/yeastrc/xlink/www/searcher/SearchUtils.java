package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;
import org.yeastrc.xlink.www.objects.SearchProtein;

public class SearchUtils {

	private static final Logger log = Logger.getLogger(SearchUtils.class);
			
	/**
	 * Get the name for this protein in the context of its search (based on the
	 * FASTA file used to do the search.
	 * @param prprotein
	 * @return
	 * @throws Exception
	 */
	public static String getProteinNameForSearch( SearchProtein prprotein ) throws Exception {
		
		String name = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT GROUP_CONCAT(accessionString) FROM tblProteinDatabase WHERE proteinID = ? AND databaseID = ? GROUP BY proteinID";
		
		try {
			
			int proteinNrseqId = prprotein.getNrProtein().getNrseqId();
			String fastaFilename = prprotein.getSearch().getFastaFilename();
			int fastaFilenameDatabaseId = YRC_NRSEQUtils.getDatabaseIdFromName( fastaFilename  );
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.YRC_NRSEQ );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, proteinNrseqId );
			pstmt.setInt( 2, fastaFilenameDatabaseId );
			
			rs = pstmt.executeQuery();

			if( rs.next() )
				name = rs.getString( 1 );
			
		} catch ( Exception e ) {
			
			log.error( "ERROR getting protein name for search. database connection: '" + DBConnectionFactory.YRC_NRSEQ + "' sql: " + sql, e );
			
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
		
		return name;		
	}
	
	/**
	 * Get the description for this protein in the context of its search (based on
	 * the FASTA file used to do the search.
	 * @param prprotein
	 * @return
	 * @throws Exception
	 */
	public static String getProteinDescriptionForSearch( SearchProtein prprotein ) throws Exception {
		
		String name = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.YRC_NRSEQ );
			String sql = "SELECT description FROM tblProteinDatabase WHERE proteinID = ? AND databaseID = ? AND description IS NOT NULL AND description <> ''";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, prprotein.getNrProtein().getNrseqId() );
			pstmt.setInt( 2, YRC_NRSEQUtils.getDatabaseIdFromName( prprotein.getSearch().getFastaFilename() ) );
			
			rs = pstmt.executeQuery();

			if( rs.next() )
				name = rs.getString( 1 );
			
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
		
		if( name == null || name.equals( "" ) )
			name = "No description in FASTA.";
		
		return name;		
	}
	
}
