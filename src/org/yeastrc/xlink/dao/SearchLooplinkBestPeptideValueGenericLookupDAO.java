package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchLooplinkBestPeptideValueGenericLookupDTO;

/**
 * table search_looplink_best_peptide_value_generic_lookup
 *
 */
public class SearchLooplinkBestPeptideValueGenericLookupDAO {

	private static final Logger log = Logger.getLogger(SearchLooplinkBestPeptideValueGenericLookupDAO.class);
			
	private SearchLooplinkBestPeptideValueGenericLookupDAO() { }
	public static SearchLooplinkBestPeptideValueGenericLookupDAO getInstance() { return new SearchLooplinkBestPeptideValueGenericLookupDAO(); }

	/**
	 * Save the associated data to the database
	 * @param item
	 * @throws Exception
	 */
	public void save( SearchLooplinkBestPeptideValueGenericLookupDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO search_looplink_best_peptide_value_generic_lookup "

				+ " ( search_looplink_generic_lookup_id, search_id, nrseq_id, protein_position_1, protein_position_2, "
				+   " annotation_type_id, best_peptide_value_for_ann_type_id, best_peptide_value_string_for_ann_type_id )"

				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  item.getSearchLooplinkGenericLookup() );
			counter++;
			pstmt.setInt( counter,  item.getSearchId() );
			counter++;
			pstmt.setInt( counter,  item.getNrseqId() );
			counter++;
			pstmt.setInt( counter,  item.getProteinPosition1() );
			counter++;
			pstmt.setInt( counter,  item.getProteinPosition2() );
			
			counter++;
			pstmt.setInt( counter,  item.getAnnotationTypeId() );
			counter++;
			pstmt.setDouble( counter,  item.getBestPeptideValueForAnnTypeId() );
			counter++;
			pstmt.setString( counter,  item.getBestPeptideValueStringForAnnTypeId() );
			
			pstmt.executeUpdate();

			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert record..." );
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
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
	
}