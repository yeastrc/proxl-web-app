package org.yeastrc.xlink.www.searcher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 *
 */
public class ScoreCountFromPsmTblSearcher {

	private static final Log log = LogFactory.getLog(ScoreCountFromPsmTblSearcher.class);
	
	private ScoreCountFromPsmTblSearcher() { }
	private static final ScoreCountFromPsmTblSearcher _INSTANCE = new ScoreCountFromPsmTblSearcher();
	public static ScoreCountFromPsmTblSearcher getInstance() { return _INSTANCE; }
	
	public static enum LinkType { CROSSLINK, LOOPLINK, UNLINKED, ALL }
	
	/**
	 * @param linkType
	 * @param searchId
	 * @param annotationTypeId
	 * @return
	 * @throws Exception
	 */
	public int getScoreCount( 
			LinkType linkType, 
			int searchId,
			int annotationTypeId
			) throws Exception {
		
		int scoreCount = 0;
		
		if ( linkType == null ) {
			throw new IllegalArgumentException("linkType cannot == null");
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sqlSB = new StringBuilder( 10000 );
		
		sqlSB.append( "SELECT COUNT(*) AS count FROM psm_filterable_annotation__generic_lookup AS pfagl " );
		sqlSB.append( " WHERE pfagl.search_id = ? AND annotation_type_id = ? " );

		if ( linkType != LinkType.ALL ) {
			sqlSB.append( " AND pfagl.psm_type IN ( " ); 
			if ( linkType == LinkType.CROSSLINK ) {
				sqlSB.append( " '" );
				sqlSB.append( XLinkUtils.CROSS_TYPE_STRING );
				sqlSB.append( "' " );
			} else if ( linkType == LinkType.LOOPLINK ) {
				sqlSB.append( " '" );
				sqlSB.append( XLinkUtils.LOOP_TYPE_STRING );
				sqlSB.append( "' " );
			} else {
				sqlSB.append( " '" );
				sqlSB.append( XLinkUtils.DIMER_TYPE_STRING );
				sqlSB.append( "' " );
				sqlSB.append( " , " );
				sqlSB.append( " '" );
				sqlSB.append( XLinkUtils.UNLINKED_TYPE_STRING );
				sqlSB.append( "' " );
			}
			sqlSB.append( " ) " );
		}
		
		String sql = sqlSB.toString();
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			paramCounter++;
			pstmt.setInt( paramCounter, annotationTypeId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				scoreCount = rs.getInt( "count" );
			}
		} catch ( Exception e ) {
			String msg = "getScoreCount(...), sql: " + sql;
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
		return scoreCount;
	}
	
	/**
	 * @param linkType
	 * @param searchId
	 * @param annotationTypeId
	 * @param psmScoreCutoff - Not NULL if set
	 * @param proteinSequenceIdsToIncludeList
	 * @return
	 * @throws Exception
	 */
	public List<Double> getScoreValues( 
			LinkType linkType,  
			int searchId,
			int annotationTypeId,
			Double psmScoreCutoff,
			List<Integer> proteinSequenceIdsToIncludeList
			) throws Exception {

		List<Double> scoreValueList = new ArrayList<Double>();
		
		if ( linkType == null ) {
			throw new IllegalArgumentException("linkType cannot == null");
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		StringBuilder sqlSB = new StringBuilder( 10000 );
		
		sqlSB.append( "SELECT value_double FROM psm_filterable_annotation__generic_lookup AS pfagl " );
		
		if ( proteinSequenceIdsToIncludeList != null &&  ( ! proteinSequenceIdsToIncludeList.isEmpty() ) ) {
			
			sqlSB.append( " INNER JOIN ( " );
			
			if ( linkType == LinkType.ALL ) {
				getScoreValuesSQLSingleLinkType( LinkType.CROSSLINK, proteinSequenceIdsToIncludeList, sqlSB);
				sqlSB.append( "UNION DISTINCT " );
				getScoreValuesSQLSingleLinkType( LinkType.LOOPLINK, proteinSequenceIdsToIncludeList, sqlSB);
				sqlSB.append( "UNION DISTINCT " );
				getScoreValuesSQLSingleLinkType( LinkType.UNLINKED, proteinSequenceIdsToIncludeList, sqlSB);
			} else {
				getScoreValuesSQLSingleLinkType(linkType, proteinSequenceIdsToIncludeList, sqlSB);
			}
			
			sqlSB.append( " ) AS rep_pept_ids ON pfagl.reported_peptide_id = rep_pept_ids.reported_peptide_id " ); //  Close the subselect and specify join
		}
		
		sqlSB.append( " WHERE pfagl.search_id = ? AND annotation_type_id = ? " );

		if ( linkType != LinkType.ALL ) {
			sqlSB.append( " AND pfagl.psm_type IN ( " ); 
			if ( linkType == LinkType.CROSSLINK ) {
				sqlSB.append( " '" );
				sqlSB.append( XLinkUtils.CROSS_TYPE_STRING );
				sqlSB.append( "' " );
			} else if ( linkType == LinkType.LOOPLINK ) {
				sqlSB.append( " '" );
				sqlSB.append( XLinkUtils.LOOP_TYPE_STRING );
				sqlSB.append( "' " );
			} else {
				sqlSB.append( " '" );
				sqlSB.append( XLinkUtils.DIMER_TYPE_STRING );
				sqlSB.append( "' " );
				sqlSB.append( " , " );
				sqlSB.append( " '" );
				sqlSB.append( XLinkUtils.UNLINKED_TYPE_STRING );
				sqlSB.append( "' " );
			}
			sqlSB.append( " ) " );
		}
		
		
		if ( psmScoreCutoff != null ) {
			sqlSB.append( " AND pfagl.value_double " );
			
			//  Not use filter direction since rest of code always displays the chart starting at the left edge
//			if ( filterDirectionType == FilterDirectionType.BELOW ) {
//				sqlSB.append( " <= " );
//			} else {
//				sqlSB.append( " >= " );
//			}
			
			sqlSB.append( " <= " );
			sqlSB.append( " ? " );
		}
		String sql = sqlSB.toString();
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;

			if ( proteinSequenceIdsToIncludeList != null &&  ( ! proteinSequenceIdsToIncludeList.isEmpty() ) ) {

				paramCounter++;
				pstmt.setInt( paramCounter, searchId );
				if ( linkType == LinkType.ALL ) { //  If "ALL", then search_id = ? in 3 times
					paramCounter++;
					pstmt.setInt( paramCounter, searchId );
					paramCounter++;
					pstmt.setInt( paramCounter, searchId );
				}
			}

			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			paramCounter++;
			pstmt.setInt( paramCounter, annotationTypeId );
			if ( psmScoreCutoff != null ) {
				paramCounter++;
				pstmt.setDouble( paramCounter, psmScoreCutoff );
			}
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				scoreValueList.add( rs.getDouble( "value_double" ) );
			}
		} catch ( Exception e ) {
			String msg = "getScoreValues(), sql: " + sql;
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
		return scoreValueList;
	}

	public void getScoreValuesSQLSingleLinkType(LinkType linkType, List<Integer> proteinSequenceIdsToIncludeList, StringBuilder sqlSB) {
		sqlSB.append( "  SELECT DISTINCT reported_peptide_id FROM " );
		if ( linkType == LinkType.CROSSLINK ) {
			sqlSB.append( " srch_rep_pept__prot_seq_id_pos_crosslink " );
		} else if ( linkType == LinkType.LOOPLINK ) {
			sqlSB.append( " srch_rep_pept__prot_seq_id_pos_looplink " );
		} else {
			sqlSB.append( " srch_rep_pept__prot_seq_id_unlinked_dimer " );
		}
		sqlSB.append( " WHERE search_id = ? AND protein_sequence_id IN ( " );
		boolean firstProteinSeqId = true;
		for ( Integer proteinSequenceId : proteinSequenceIdsToIncludeList ) {
			if ( firstProteinSeqId ) {
				firstProteinSeqId = false;
			} else {
				sqlSB.append( "," );		
			}
			sqlSB.append( proteinSequenceId.toString() );
		}
		sqlSB.append( " ) " ); //  Close the IN (
	}
}
