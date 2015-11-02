package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.constants.DefaultQValueCutoffConstants;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;



public class MergedSearchProteinLooplinkSearcher {
	
	private static final Logger log = Logger.getLogger(MergedSearchProteinLooplinkSearcher.class);

	private MergedSearchProteinLooplinkSearcher() { }
	private static final MergedSearchProteinLooplinkSearcher _INSTANCE = new MergedSearchProteinLooplinkSearcher();
	public static MergedSearchProteinLooplinkSearcher getInstance() { return _INSTANCE; }
	
	
	private final String SEARCH_ID_GROUP_SEPARATOR = ","; //  separator as search ids are combined by the group by

	
	public List<MergedSearchProteinLooplink> search( Collection<SearchDTO> searches, double psmCutoff, double peptideCutoff ) throws Exception {
		List<MergedSearchProteinLooplink> links = new ArrayList<MergedSearchProteinLooplink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			String sql = "SELECT nrseq_id, protein_position_1, protein_position_2, min(bestPSMQValue), min(bestPeptideQValue), "
					+ " SUM( num_psm_at_pt_01_q_cutoff ) AS num_psm_at_pt_01_q_cutoff, "
					+ " GROUP_CONCAT( DISTINCT search_id SEPARATOR '" + SEARCH_ID_GROUP_SEPARATOR + "' ) AS search_ids "
					
					+ "FROM search_looplink_lookup WHERE search_id IN (#SEARCHES#) AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  "
					+ "GROUP BY nrseq_id, protein_position_1, protein_position_2 "
					+ "ORDER BY nrseq_id, protein_position_1, protein_position_2";
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
						
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, psmCutoff );
			pstmt.setDouble( 2, peptideCutoff );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				MergedSearchProteinLooplink link = new MergedSearchProteinLooplink();
				link.setPsmCutoff( psmCutoff );
				link.setPeptideCutoff( peptideCutoff );
				link.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
				link.setProteinPosition1( rs.getInt( 2 ) );
				link.setProteinPosition2( rs.getInt( 3 ) );
				link.setBestPSMQValue( rs.getDouble( 4 ) );
				
				link.setBestPeptideQValue( rs.getDouble( 5 ) );
				if ( rs.wasNull() ) {
					link.setBestPeptideQValue( null );
				}
				


				int numPsmsForpt01Cutoff = rs.getInt( "num_psm_at_pt_01_q_cutoff" );
				
				if ( DefaultQValueCutoffConstants.PSM_Q_VALUE_CUTOFF_DEFAULT == psmCutoff ) {
					
					link.setNumPsms( numPsmsForpt01Cutoff ); // code is needed in WebMergedReportedPeptide for when psmCutoff is not default
				}
				
				

				//  Build collection of SearchDTO objects for the search ids found for this unified_reported_peptide_id
				
				String searchIdsCommaDelimString = rs.getString( "search_ids" );
				List<SearchDTO> searchesFoundInCurrentRecord = getSearchDTOsForCurrentResultRecord( searches, searchIdsCommaDelimString );
				
				List<Integer> searchIdsFoundInCurrentRecord = new ArrayList<>( searchesFoundInCurrentRecord.size() );
				
				for ( SearchDTO searchDTO : searchesFoundInCurrentRecord ) {
					
					searchIdsFoundInCurrentRecord.add( searchDTO.getId() );
				}
				
				link.setSearches( searchesFoundInCurrentRecord );
				
				
				links.add( link );
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
		
		return links;
	}
	


	//////////////////////////////////////////////////////////////////////////////

	//  Build collection of SearchDTO objects for the search ids found for this unified_reported_peptide_id
	
	private List<SearchDTO> getSearchDTOsForCurrentResultRecord( Collection<SearchDTO> searches, String searchIdsCommaDelimString ) throws SQLException, Exception {
		
		
		List<SearchDTO> searchesFoundInCurrentRecord = new ArrayList<>( searches.size() );
		
		
		if ( searchIdsCommaDelimString != null  ) {
		
			String[] searchIdsCommaDelimStringSplit = searchIdsCommaDelimString.split( SEARCH_ID_GROUP_SEPARATOR );
			
			for ( String searchIdString : searchIdsCommaDelimStringSplit ) {
				
				int searchIdFoundInCurrentRecord = 0;
				
				try {
					
					searchIdFoundInCurrentRecord = Integer.parseInt( searchIdString );
				} catch ( Exception e ) {
					
					String msg = "Failed to parse search id from comma delim query result.  searchIdString: |"
							+ searchIdString + "|, searchIdsCommaDelimString from DB: |" + searchIdsCommaDelimString + "|.";
					
					log.error( msg, e );
					
					throw new Exception(msg);
				}
				
				// get SearchDTO from passed in collection.
				
				SearchDTO searchesItemForSearchIdFoundInCurrentRecord = null;
				
				for ( SearchDTO searchesItem : searches ) {
					
					if ( searchesItem.getId() == searchIdFoundInCurrentRecord ) {
						
						searchesItemForSearchIdFoundInCurrentRecord = searchesItem;
						break;
					}
				}
				
				if ( searchesItemForSearchIdFoundInCurrentRecord == null ) {
					
					String msg = "Failed to search id from comma delim query result in list of passed in SearchDTOs."
							+ "  searchId from comma delim query result: " + searchIdFoundInCurrentRecord;
					
					log.error( msg );
					
					throw new Exception(msg);
				}
				
				searchesFoundInCurrentRecord.add( searchesItemForSearchIdFoundInCurrentRecord );
			}
		}
		return searchesFoundInCurrentRecord;
	}

}
