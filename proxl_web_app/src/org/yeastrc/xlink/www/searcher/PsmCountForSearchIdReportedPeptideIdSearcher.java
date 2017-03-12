package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
/**
 * 
 *
 */
public class PsmCountForSearchIdReportedPeptideIdSearcher {
	
	private PsmCountForSearchIdReportedPeptideIdSearcher() { }
	public static PsmCountForSearchIdReportedPeptideIdSearcher getInstance() { return new PsmCountForSearchIdReportedPeptideIdSearcher(); }
	private static final Logger log = Logger.getLogger(PsmCountForSearchIdReportedPeptideIdSearcher.class);
	/**
	 * @param reportedPeptideId
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	public int getPsmCountForSearchIdReportedPeptideId( int reportedPeptideId, int searchId, SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {
		int numPsms = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
		StringBuilder sqlSB = new StringBuilder( 10000 );
		sqlSB.append( "SELECT COUNT(*) AS count FROM  " ); 
		if ( psmCutoffValuesList.isEmpty() ) {
			sqlSB.append( " psm WHERE search_id = ? AND reported_peptide_id = ? " );
		} else {
			{
				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {
					if ( counter > 1 ) {
						sqlSB.append( " INNER JOIN " );
					}
					sqlSB.append( " psm_filterable_annotation__generic_lookup AS tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					if ( counter > 1 ) {
						sqlSB.append( " ON "  );
						sqlSB.append( "tbl_" );
						sqlSB.append( Integer.toString( counter - 1 ) );
						sqlSB.append( ".psm_id " );
						sqlSB.append( " = " );
						sqlSB.append( "tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".psm_id" );
					}
				}
				sqlSB.append( " WHERE ( " );
				{
					int counter = 0; 
					for ( SearcherCutoffValuesAnnotationLevel entry : psmCutoffValuesList ) {
						counter++;
						if ( counter > 1 ) {
							sqlSB.append( " ) AND ( " );
						}
						sqlSB.append( "tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".search_id = ? AND " );
						sqlSB.append( "tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".reported_peptide_id = ? AND " );
						sqlSB.append( "tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".annotation_type_id = ? AND " );
						sqlSB.append( "tbl_" );
						sqlSB.append( Integer.toString( counter ) );
						sqlSB.append( ".value_double " );
						if ( entry.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
							String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + entry.getAnnotationTypeDTO().getId();
							log.error( msg );
							throw new Exception(msg);
						}
						if ( entry.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {
							sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );
						} else {
							sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );
						}
						sqlSB.append( "? " );
					}
				}
			}
			sqlSB.append( " ) " );
		}
		String sql = sqlSB.toString();
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int pstmtCounter = 0;
			if ( psmCutoffValuesList.isEmpty() ) {
				pstmtCounter++;
				pstmt.setInt( pstmtCounter, searchId );
				pstmtCounter++;
				pstmt.setInt( pstmtCounter, reportedPeptideId );
			} else {
				for ( SearcherCutoffValuesAnnotationLevel entry : psmCutoffValuesList ) {
					pstmtCounter++;
					pstmt.setInt( pstmtCounter, searchId );
					pstmtCounter++;
					pstmt.setInt( pstmtCounter, reportedPeptideId );
					pstmtCounter++;
					pstmt.setInt( pstmtCounter, entry.getAnnotationTypeDTO().getId() );
					pstmtCounter++;
					pstmt.setDouble( pstmtCounter, entry.getAnnotationCutoffValue() );
				}
			}
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				numPsms = rs.getInt( "count" );
			}
		} catch ( Exception e ) {
			log.error( "ERROR getting psm count:  SQL: " + sql, e );
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
		return numPsms;		
	}
}
