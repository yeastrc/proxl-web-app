package org.yeastrc.xlink.www.searcher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
	 * Get count of records, never applying psmScoreCutoff
	 * 
	 * @param linkType
	 * @param searchId
	 * @param annotationTypeId
	 * @param proteinSequenceIdsToIncludeList
	 * @param proteinSequenceIdsToExcludeList
	 * @return
	 * @throws Exception
	 */
	public int getScoreCount( 
			LinkType linkType, 
			int searchId,
			int annotationTypeId,
			List<Integer> proteinSequenceIdsToIncludeList,
			List<Integer> proteinSequenceIdsToExcludeList
			) throws Exception {
		
		int scoreCount = 0;
		
		if ( linkType == null ) {
			throw new IllegalArgumentException("linkType cannot == null");
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = createSelectSQL( 
				"COUNT(*) AS count", 
				linkType, 
				searchId, 
				annotationTypeId, 
				null /* psmScoreCutoff */, 
				proteinSequenceIdsToIncludeList, 
				proteinSequenceIdsToExcludeList );
				
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
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
	 * @param proteinSequenceIdsToExcludeList
	 * @return
	 * @throws Exception
	 */
	public List<Double> getScoreValues( 
			LinkType linkType,  
			int searchId,
			int annotationTypeId,
			Double psmScoreCutoff,
			List<Integer> proteinSequenceIdsToIncludeList,
			List<Integer> proteinSequenceIdsToExcludeList
			) throws Exception {
		

		List<Double> scoreValueList = new ArrayList<Double>();
		
		if ( linkType == null ) {
			throw new IllegalArgumentException("linkType cannot == null");
		}
		
		
		String sql = createSelectSQL( 
				"value_double", 
				linkType, 
				searchId, 
				annotationTypeId, 
				psmScoreCutoff, 
				proteinSequenceIdsToIncludeList, 
				proteinSequenceIdsToExcludeList );
				


		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;

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
	
	
	/**
	 * Create SQL
	 * 
	 * @param selectResult - What the select should return 
	 * @param linkType
	 * @param searchId
	 * @param annotationTypeId
	 * @param psmScoreCutoff - Not NULL if set
	 * @param proteinSequenceIdsToIncludeList
	 * @param proteinSequenceIdsToExcludeList
	 * @return
	 * @throws Exception
	 */
	private String createSelectSQL( 
			String selectResult,
			LinkType linkType,  
			int searchId,
			int annotationTypeId,
			Double psmScoreCutoff,
			List<Integer> proteinSequenceIdsToIncludeList,
			List<Integer> proteinSequenceIdsToExcludeList
			) throws Exception {

		
		String searchIdString = Integer.toString( searchId );
		
		//  Convert include and exclude protein sequence id lists to comma delim strings 
		String includeProteinSequenceIdsCommaDelim = null;
		String excludeProteinSequenceIdsCommaDelim = null;
		
		if ( proteinSequenceIdsToIncludeList != null && ( ! proteinSequenceIdsToIncludeList.isEmpty() ) ) {
			includeProteinSequenceIdsCommaDelim = StringUtils.join(proteinSequenceIdsToIncludeList, ',');
		}
		if ( proteinSequenceIdsToExcludeList != null && ( ! proteinSequenceIdsToExcludeList.isEmpty() ) ) {
			excludeProteinSequenceIdsCommaDelim = StringUtils.join(proteinSequenceIdsToExcludeList, ',');
		}
		
		StringBuilder sqlSB = new StringBuilder( 10000 );
		
		
		sqlSB.append( "SELECT " );
		sqlSB.append( selectResult );
		sqlSB.append( " FROM psm_filterable_annotation__generic_lookup AS pfagl \n" );
		
		if ( ( proteinSequenceIdsToIncludeList != null && ( ! proteinSequenceIdsToIncludeList.isEmpty() ) )
				|| ( proteinSequenceIdsToExcludeList != null && ( ! proteinSequenceIdsToExcludeList.isEmpty() ) ) ) {
			
			sqlSB.append( " INNER JOIN ( \n" );
			
			if ( linkType == LinkType.ALL ) {

				// If "ALL", Get Reported Peptide Ids for each link type and use "UNION DISTINCT" to combine them
				
				String singleLinkTypeSQL = null;
				
				singleLinkTypeSQL = 
						getScoreValuesSQLSingleLinkType( 
								LinkType.CROSSLINK, searchIdString, includeProteinSequenceIdsCommaDelim, excludeProteinSequenceIdsCommaDelim);
				sqlSB.append( singleLinkTypeSQL );

				sqlSB.append( "UNION DISTINCT \n" );

				singleLinkTypeSQL = 
						getScoreValuesSQLSingleLinkType( 
								LinkType.LOOPLINK, searchIdString, includeProteinSequenceIdsCommaDelim, excludeProteinSequenceIdsCommaDelim);
				sqlSB.append( singleLinkTypeSQL );

				sqlSB.append( "UNION DISTINCT \n" );

				singleLinkTypeSQL = 
						getScoreValuesSQLSingleLinkType( 
								LinkType.UNLINKED, searchIdString, includeProteinSequenceIdsCommaDelim, excludeProteinSequenceIdsCommaDelim);
				sqlSB.append( singleLinkTypeSQL );
				
			} else {
				String singleLinkTypeSQL = 
					getScoreValuesSQLSingleLinkType(
							linkType, searchIdString, includeProteinSequenceIdsCommaDelim, excludeProteinSequenceIdsCommaDelim);
				sqlSB.append( singleLinkTypeSQL );
			}
			
			sqlSB.append( "\n ) AS rep_pept_ids ON pfagl.reported_peptide_id = rep_pept_ids.reported_peptide_id " ); //  Close the subselect and specify join
		}
		 
		sqlSB.append( "\n WHERE pfagl.search_id = " );
		sqlSB.append( searchIdString );
		sqlSB.append( " AND annotation_type_id = " );
		sqlSB.append( Integer.toString( annotationTypeId ) );

		if ( linkType != LinkType.ALL ) {
			sqlSB.append( "\n AND pfagl.psm_type IN ( " ); 
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
			sqlSB.append( " ) \n" );
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
			sqlSB.append( " ? \n" );
		}
		String sql = sqlSB.toString();
		if ( log.isDebugEnabled() ) {
			String msg = "\nlinkType: " + linkType.toString() 
			+ "\nInclude Prot Seq Ids: " + includeProteinSequenceIdsCommaDelim
			+ "\nExclude Prot Seq Ids: " + excludeProteinSequenceIdsCommaDelim
			+ "\nSQL: " + sql;
			log.debug( msg );
		}
		
		return sql;
	}

	/**
	 * @param linkType
	 * @param searchIdString
	 * @param includeProteinSequenceIdsCommaDelim
	 * @param excludeProteinSequenceIdsCommaDelim
	 * @return
	 */
	private String getScoreValuesSQLSingleLinkType(
			LinkType linkType, 
			String searchIdString,
			String includeProteinSequenceIdsCommaDelim, 
			String excludeProteinSequenceIdsCommaDelim) {

		boolean haveIncludeProteins = false;
		boolean haveExcludeProteins = false;
		
		if ( includeProteinSequenceIdsCommaDelim != null ) {
			haveIncludeProteins = true;
		}
		if ( excludeProteinSequenceIdsCommaDelim != null ) {
			haveExcludeProteins = true;
		}
		
		StringBuilder sqlSB = new StringBuilder( 1000 );


		//  If have Both Include and Exclude, Add surrounding SELECT and INNER JOIN Between Them 
		if ( haveIncludeProteins && haveExcludeProteins ) {
			sqlSB.append( "  SELECT DISTINCT included_rep_pept.reported_peptide_id FROM ( \n" );
		}

		//  Include proteins
		if ( haveIncludeProteins ) {
			String includeSQL = getScoreValuesSQLSingleLinkTypeInclude( linkType, searchIdString, includeProteinSequenceIdsCommaDelim );
			sqlSB.append( includeSQL );
		}

		//  If have Both Include and Exclude, Add surrounding SELECT and INNER JOIN Between Them 
		if ( haveIncludeProteins && haveExcludeProteins ) {
			sqlSB.append( " ) AS included_rep_pept \n" ); 
			sqlSB.append( " INNER JOIN \n" ); 
			sqlSB.append( " ( \n" );
		}
			
		//  Exclude proteins
		if ( haveExcludeProteins ) {
			String excludeSQL = getScoreValuesSQLSingleLinkTypeExclude( linkType, searchIdString, excludeProteinSequenceIdsCommaDelim );
			sqlSB.append( excludeSQL );
		}

		//  If have Both Include and Exclude, Add surrounding SELECT and INNER JOIN Between Them 
		if ( haveIncludeProteins && haveExcludeProteins ) {
			sqlSB.append( " ) AS not_excluded_rep_pept " );                 
			sqlSB.append( " ON included_rep_pept.reported_peptide_id = not_excluded_rep_pept.reported_peptide_id " );
		}
			
		String sql = sqlSB.toString();
		
		return sql;
	}
	
	/**
	 * SQL for Included Protein Sequence Ids 
	 * @param linkType
	 * @param searchIdString
	 * @param includeProteinSequenceIdsCommaDelim
	 * @return
	 */
	private String getScoreValuesSQLSingleLinkTypeInclude(
			LinkType linkType, 
			String searchIdString,
			String includeProteinSequenceIdsCommaDelim ) {

		if ( linkType == LinkType.UNLINKED ) {
			
			String sql = 
					" SELECT reported_peptide_id FROM "
					+ "srch_rep_pept__prot_seq_id_unlinked"
					+ "\n WHERE search_id = " + searchIdString
					+ "\n AND protein_sequence_id IN ( " + includeProteinSequenceIdsCommaDelim + " ) \n"
					+ "UNION DISTINCT \n" 
					+ " SELECT reported_peptide_id FROM "
					+ "srch_rep_pept__prot_seq_id_dimer"
					+ "\n WHERE search_id = " + searchIdString
					+ "\n AND protein_sequence_id IN ( " + includeProteinSequenceIdsCommaDelim + " ) \n";

			return sql;

		} else {

			String tableName = null;
			
			if ( linkType == LinkType.CROSSLINK ) {
				tableName = "srch_rep_pept__prot_seq_id_pos_crosslink";
			} else if ( linkType == LinkType.LOOPLINK ) {
				tableName = "srch_rep_pept__prot_seq_id_pos_looplink ";
			}

			String sql = " SELECT DISTINCT reported_peptide_id FROM "
					+ tableName
					+ "\n WHERE search_id = " + searchIdString
					+ "\n AND protein_sequence_id IN ( " + includeProteinSequenceIdsCommaDelim + " ) \n";

			return sql;
		}
	}
	

	/**
	 * SQL for Excluded Protein Sequence Ids 
	 * @param linkType
	 * @param searchIdString
	 * @param excludeProteinSequenceIdsCommaDelim
	 * @return
	 */
	private String getScoreValuesSQLSingleLinkTypeExclude(
			LinkType linkType, 
			String searchIdString,
			String excludeProteinSequenceIdsCommaDelim ) {

		String sql = null;

		//  Specific SQL for each link type

		if ( linkType == LinkType.CROSSLINK ) {

			//  For Crosslink, the table srch_rep_pept__prot_seq_id_pos_crosslink is joined to itself
			//   Only reported peptide ids that are in both halves after excluding protein sequence ids 
			//   are in the result
			
			sql = " SELECT DISTINCT link1.reported_peptide_id  \n" 
					+  " FROM  srch_rep_pept__prot_seq_id_pos_crosslink AS link1  \n" 
					+  " INNER JOIN  srch_rep_pept__prot_seq_id_pos_crosslink AS link2 \n" 
					+   " ON link1.search_id = link2.search_id\n" 
					+   " AND link1.reported_peptide_id = link2.reported_peptide_id \n" 
					+   " WHERE link1.search_reported_peptide_peptide_id \n" 
					+   " != link2.search_reported_peptide_peptide_id \n" 
					+ " AND link1.search_id = " + searchIdString 
					+   "\n AND link1.protein_sequence_id NOT IN ( "  
					+                 excludeProteinSequenceIdsCommaDelim + " ) \n"
					+   " AND link2.protein_sequence_id NOT IN ( " 
					+                 excludeProteinSequenceIdsCommaDelim + " ) \n";

		} else if ( linkType == LinkType.LOOPLINK ) {

			//  For Looplink, it is a simple query excluding the protein sequence ids
			
			sql = " SELECT DISTINCT reported_peptide_id  \n" 
					+  " FROM  srch_rep_pept__prot_seq_id_pos_looplink \n" 
					+   " WHERE search_id = " + searchIdString 
					+   "\n AND protein_sequence_id NOT IN ( "  
					+                 excludeProteinSequenceIdsCommaDelim + " ) \n";

		} else {

			//  "Unlinked"    Unlinked and Dimer


			//  For Dimer, the table srch_rep_pept__prot_seq_id_dimer is joined to itself
			//   Only reported peptide ids that are in both halves after excluding protein sequence ids 
			//   are in the result
			
			sql = " SELECT DISTINCT dimer_half_1.reported_peptide_id  \n" 
					+  " FROM  srch_rep_pept__prot_seq_id_dimer AS dimer_half_1  \n" 
					+  " INNER JOIN  srch_rep_pept__prot_seq_id_dimer AS dimer_half_2 \n" 
					+   " ON dimer_half_1.search_id = dimer_half_2.search_id\n" 
					+   " AND dimer_half_1.reported_peptide_id = dimer_half_2.reported_peptide_id \n" 
					+   " WHERE dimer_half_1.search_reported_peptide_peptide_id \n" 
					+   " != dimer_half_2.search_reported_peptide_peptide_id \n" 
					+ " AND dimer_half_1.search_id = " + searchIdString 
					+   "\n AND dimer_half_1.protein_sequence_id NOT IN ( "  
					+                 excludeProteinSequenceIdsCommaDelim + " ) \n"
					+   " AND dimer_half_2.protein_sequence_id NOT IN ( " 
					+                 excludeProteinSequenceIdsCommaDelim + " ) \n"

//			//  UNION dimer reported peptide ids and unlinked reported peptide ids

			+ " UNION DISTINCT \n"  

			//  For Unlinked, it is a simple query excluding the protein sequence ids
			
			+ " SELECT DISTINCT reported_peptide_id  \n" 
					+  " FROM  srch_rep_pept__prot_seq_id_unlinked \n" 
					+   " WHERE search_id = " + searchIdString 
					+   "\n AND protein_sequence_id NOT IN ( "  
					+                 excludeProteinSequenceIdsCommaDelim + " ) \n";
			
		}
	
		return sql;
	}
}
