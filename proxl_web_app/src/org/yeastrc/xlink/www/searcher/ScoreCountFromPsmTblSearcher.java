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
		
		sqlSB.append( "SELECT COUNT(*) AS count FROM psm_filterable_annotation__generic_lookup AS pfagl \n" );
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
		
//		if ( linkType != LinkType.CROSSLINK && linkType != LinkType.LOOPLINK ) {  //  TODO  TEMP
//			
//			return scoreValueList;
//		}
		
		String searchIdString = Integer.toString( searchId );
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		StringBuilder sqlSB = new StringBuilder( 10000 );
		
		sqlSB.append( "SELECT value_double FROM psm_filterable_annotation__generic_lookup AS pfagl \n" );
		
		if ( ( proteinSequenceIdsToIncludeList != null && ( ! proteinSequenceIdsToIncludeList.isEmpty() ) )
				|| ( proteinSequenceIdsToExcludeList != null && ( ! proteinSequenceIdsToExcludeList.isEmpty() ) ) ) {
			
			sqlSB.append( " INNER JOIN ( \n" );
			
			if ( linkType == LinkType.ALL ) {
				getScoreValuesSQLSingleLinkType( 
						LinkType.CROSSLINK, searchIdString, proteinSequenceIdsToIncludeList, proteinSequenceIdsToExcludeList, sqlSB);
				sqlSB.append( "UNION DISTINCT \n" );
				getScoreValuesSQLSingleLinkType( 
						LinkType.LOOPLINK, searchIdString, proteinSequenceIdsToIncludeList, proteinSequenceIdsToExcludeList, sqlSB);
				sqlSB.append( "UNION DISTINCT \n" );
				getScoreValuesSQLSingleLinkType( 
						LinkType.UNLINKED, searchIdString, proteinSequenceIdsToIncludeList, proteinSequenceIdsToExcludeList, sqlSB);
			} else {
				getScoreValuesSQLSingleLinkType(
						linkType, searchIdString, proteinSequenceIdsToIncludeList, proteinSequenceIdsToExcludeList, sqlSB);
			}
			
			sqlSB.append( " ) AS rep_pept_ids ON pfagl.reported_peptide_id = rep_pept_ids.reported_peptide_id " ); //  Close the subselect and specify join
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

	public void getScoreValuesSQLSingleLinkType(
			LinkType linkType, 
			String searchIdString,
			List<Integer> proteinSequenceIdsToIncludeList, 
			List<Integer> proteinSequenceIdsToExcludeList, 
			StringBuilder sqlSB) {

		//  Include
		
		boolean haveIncludeProteins = false;
		boolean haveExcludeProteins = false;
		
		if ( ( proteinSequenceIdsToIncludeList != null && ( ! proteinSequenceIdsToIncludeList.isEmpty() ) ) ) {
			haveIncludeProteins = true;
		}
		if ( ( proteinSequenceIdsToExcludeList != null && ( ! proteinSequenceIdsToExcludeList.isEmpty() ) ) ) {
			haveExcludeProteins = true;
		}


		//  If have Both Include and Exclude, Add surrounding SELECT and INNER JOIN Between Them 
		if ( haveIncludeProteins && haveExcludeProteins ) {
			sqlSB.append( "  SELECT DISTINCT included_rep_pept.reported_peptide_id FROM ( \n" );
		}
		
		if ( haveIncludeProteins ) {
				
			sqlSB.append( "  SELECT DISTINCT reported_peptide_id FROM " );
			if ( linkType == LinkType.CROSSLINK ) {
				sqlSB.append( " srch_rep_pept__prot_seq_id_pos_crosslink " );
			} else if ( linkType == LinkType.LOOPLINK ) {
				sqlSB.append( " srch_rep_pept__prot_seq_id_pos_looplink " );
			} else {
				sqlSB.append( " srch_rep_pept__prot_seq_id_unlinked_dimer " );
			}
			
			sqlSB.append( "\n WHERE search_id = " );
			sqlSB.append( searchIdString );
			sqlSB.append( "\n AND protein_sequence_id IN ( " );
			boolean firstProteinSeqId = true;
			for ( Integer proteinSequenceId : proteinSequenceIdsToIncludeList ) {
				if ( firstProteinSeqId ) {
					firstProteinSeqId = false;
				} else {
					sqlSB.append( "," );		
				}
				sqlSB.append( proteinSequenceId.toString() );
			}
			sqlSB.append( " ) \n" ); //  Close the IN (
		
		
		}
		
		//  If have Both Include and Exclude, Add surrounding SELECT and INNER JOIN Between Them 
		if ( haveIncludeProteins && haveExcludeProteins ) {
			sqlSB.append( " ) AS included_rep_pept \n" ); 
			sqlSB.append( " INNER JOIN \n" ); 
			sqlSB.append( " ( \n" );
		}
			
		//  Exclude proteins
		
		if ( haveExcludeProteins ) {
		
			//  Specific SQL for each link type
			
			if ( linkType == LinkType.CROSSLINK ) {

				sqlSB.append( " SELECT DISTINCT link1.reported_peptide_id  \n" );
				sqlSB.append(  " FROM  srch_rep_pept__prot_seq_id_pos_crosslink AS link1  \n" );
				sqlSB.append(  " INNER JOIN  srch_rep_pept__prot_seq_id_pos_crosslink AS link2 \n" );
				sqlSB.append(   " ON link1.search_id = link2.search_id\n" );
				sqlSB.append(   " AND link1.reported_peptide_id = link2.reported_peptide_id \n" );
				sqlSB.append(   " WHERE link1.search_reported_peptide_peptide_id \n" );
				sqlSB.append(   " != link2.search_reported_peptide_peptide_id \n" );
						sqlSB.append( " AND link1.search_id = " );
				sqlSB.append( searchIdString );
				sqlSB.append(   "\n AND link1.protein_sequence_id NOT IN ( " ); 
				{
					boolean firstProteinSeqId = true;
					for ( Integer proteinSequenceId : proteinSequenceIdsToExcludeList ) {
						if ( firstProteinSeqId ) {
							firstProteinSeqId = false;
						} else {
							sqlSB.append( "," );		
						}
						sqlSB.append( proteinSequenceId.toString() );
					}
					sqlSB.append( " ) \n" ); //  Close the NOT IN (
				}
				sqlSB.append(   " AND link2.protein_sequence_id NOT IN ( " );
				{
					boolean firstProteinSeqId = true;
					for ( Integer proteinSequenceId : proteinSequenceIdsToExcludeList ) {
						if ( firstProteinSeqId ) {
							firstProteinSeqId = false;
						} else {
							sqlSB.append( "," );		
						}
						sqlSB.append( proteinSequenceId.toString() );
					}
					sqlSB.append( " ) \n" ); //  Close the NOT IN (
				}
				
			} else if ( linkType == LinkType.LOOPLINK ) {
				
//				sqlSB.append( " srch_rep_pept__prot_seq_id_pos_looplink " );
				

				sqlSB.append( " SELECT DISTINCT reported_peptide_id  \n" );
				sqlSB.append(  " FROM  srch_rep_pept__prot_seq_id_pos_looplink \n" );
				sqlSB.append(   " WHERE search_id = " );
				sqlSB.append( searchIdString );
				sqlSB.append(   "\n AND protein_sequence_id NOT IN ( " ); 
				boolean firstProteinSeqId = true;
				for ( Integer proteinSequenceId : proteinSequenceIdsToExcludeList ) {
					if ( firstProteinSeqId ) {
						firstProteinSeqId = false;
					} else {
						sqlSB.append( "," );		
					}
					sqlSB.append( proteinSequenceId.toString() );
				}
				sqlSB.append( " ) \n" ); //  Close the NOT IN (
				
			} else {
				
				//  "Unlinked"    Unlinked and Dimer
				
				//   For both, inner join to search_reported_peptide to get link type
				
//				sqlSB.append( " srch_rep_pept__prot_seq_id_unlinked_dimer " );
				
				sqlSB.append( " SELECT DISTINCT combined.reported_peptide_id FROM  \n" );

				sqlSB.append( " ( \n" );

				//  Dimer - 
				
				sqlSB.append( " SELECT DISTINCT pept_prot_1.reported_peptide_id  \n" );
				sqlSB.append(  " FROM \n" );
				sqlSB.append( " ( \n" );

				sqlSB.append(   " SELECT pept_prot.* \n" );
				sqlSB.append(   " FROM srch_rep_pept__prot_seq_id_unlinked_dimer AS pept_prot \n" );
				sqlSB.append(   " INNER JOIN search_reported_peptide as srp \n" );
				sqlSB.append(   "  ON pept_prot.search_id = srp.search_id \n" );
				sqlSB.append(   	"  AND pept_prot.reported_peptide_id = srp.reported_peptide_id \n" );
				sqlSB.append(   "  WHERE srp.link_type = '" );
				sqlSB.append(   XLinkUtils.DIMER_TYPE_STRING );
				sqlSB.append(   "' " );
				sqlSB.append(   " AND pept_prot.search_id = " );
				sqlSB.append( searchIdString );
				sqlSB.append( ") AS pept_prot_1  \n" );
				
				sqlSB.append(   " INNER JOIN \n " );
				
				sqlSB.append( " ( \n" );
				sqlSB.append(   " SELECT pept_prot.* \n" );
				sqlSB.append(   " FROM srch_rep_pept__prot_seq_id_unlinked_dimer AS pept_prot \n" );
				sqlSB.append(   " INNER JOIN search_reported_peptide as srp \n" );
				sqlSB.append(   "  ON pept_prot.search_id = srp.search_id \n" );
				sqlSB.append(   	"  AND pept_prot.reported_peptide_id = srp.reported_peptide_id \n" );
				sqlSB.append(   "  WHERE srp.link_type = '" );
				sqlSB.append(   XLinkUtils.DIMER_TYPE_STRING );
				sqlSB.append(   "' " );
				sqlSB.append(   " AND pept_prot.search_id = " );
				sqlSB.append( searchIdString );
				sqlSB.append( ") AS pept_prot_2  \n" );						
				sqlSB.append(   " ON pept_prot_1.search_id = pept_prot_2.search_id\n" );
				sqlSB.append(   " AND pept_prot_1.reported_peptide_id = pept_prot_2.reported_peptide_id \n" );
				sqlSB.append(   " WHERE pept_prot_1.search_reported_peptide_peptide_id \n" );
				sqlSB.append(   " != pept_prot_2.search_reported_peptide_peptide_id \n" );
						sqlSB.append( " AND pept_prot_1.search_id = " );
				sqlSB.append( searchIdString );
				sqlSB.append(   "\n AND pept_prot_1.protein_sequence_id NOT IN ( " ); 
				{
					boolean firstProteinSeqId = true;
					for ( Integer proteinSequenceId : proteinSequenceIdsToExcludeList ) {
						if ( firstProteinSeqId ) {
							firstProteinSeqId = false;
						} else {
							sqlSB.append( "," );		
						}
						sqlSB.append( proteinSequenceId.toString() );
					}
					sqlSB.append( " ) \n" ); //  Close the NOT IN (
				}
				sqlSB.append(   " AND pept_prot_2.protein_sequence_id NOT IN ( " );
				{
					boolean firstProteinSeqId = true;
					for ( Integer proteinSequenceId : proteinSequenceIdsToExcludeList ) {
						if ( firstProteinSeqId ) {
							firstProteinSeqId = false;
						} else {
							sqlSB.append( "," );		
						}
						sqlSB.append( proteinSequenceId.toString() );
					}
					sqlSB.append( " ) \n" ); //  Close the NOT IN (
				}
				
//				sqlSB.append( " ) AS dimer_rept_pept_ids \n" ); //  Close select dimer data
				
				sqlSB.append( " UNION DISTINCT \n" ); //  UNION dimer data and unlinked data
				
				//  Unlinked
				
				sqlSB.append(   " SELECT pept_prot.reported_peptide_id \n" );
				sqlSB.append(   " FROM srch_rep_pept__prot_seq_id_unlinked_dimer AS pept_prot \n" );
				sqlSB.append(   " INNER JOIN search_reported_peptide as srp \n" );
				sqlSB.append(   "  ON pept_prot.search_id = srp.search_id \n" );
				sqlSB.append(   	"  AND pept_prot.reported_peptide_id = srp.reported_peptide_id \n" );
				sqlSB.append(   "  WHERE srp.link_type = '" );
				sqlSB.append(   XLinkUtils.UNLINKED_TYPE_STRING );
				sqlSB.append(   "' " );
				sqlSB.append(   " AND pept_prot.search_id = " );
				sqlSB.append( searchIdString );
				sqlSB.append(   "\n AND pept_prot.protein_sequence_id NOT IN ( " ); 
				boolean firstProteinSeqId = true;
				for ( Integer proteinSequenceId : proteinSequenceIdsToExcludeList ) {
					if ( firstProteinSeqId ) {
						firstProteinSeqId = false;
					} else {
						sqlSB.append( "," );		
					}
					sqlSB.append( proteinSequenceId.toString() );
				}
				sqlSB.append( " ) \n" ); //  Close the NOT IN (			}

//				sqlSB.append( " ) AS unlinked_rept_pept_ids \n" ); //  Close select unlinked data
				

				sqlSB.append( " ) AS combined \n" ); //  Close select combined data
				
			}
		}

		//  If have Both Include and Exclude, Add surrounding SELECT and INNER JOIN Between Them 
		if ( haveIncludeProteins && haveExcludeProteins ) {
			sqlSB.append( " ) AS not_excluded_rep_pept " );                 
			sqlSB.append( " ON included_rep_pept.reported_peptide_id = not_excluded_rep_pept.reported_peptide_id " );
		}
			
	}
}
