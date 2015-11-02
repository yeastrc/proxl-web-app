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
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;

public class MergedSearchProteinCrosslinkSearcher {
	
	private static final Logger log = Logger.getLogger(MergedSearchProteinCrosslinkSearcher.class);

	private MergedSearchProteinCrosslinkSearcher() { }
	private static final MergedSearchProteinCrosslinkSearcher _INSTANCE = new MergedSearchProteinCrosslinkSearcher();
	public static MergedSearchProteinCrosslinkSearcher getInstance() { return _INSTANCE; }
	
	private final String SEARCH_ID_GROUP_SEPARATOR = ","; //  separator as search ids are combined by the group by


	public List<MergedSearchProteinCrosslink> search( Collection<SearchDTO> searches, double psmCutoff, double peptideCutoff ) throws Exception {
		List<MergedSearchProteinCrosslink> links = new ArrayList<MergedSearchProteinCrosslink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			String sql = "SELECT nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position, min(bestPSMQValue), min(bestPeptideQValue), "
					+ " SUM( num_psm_at_pt_01_q_cutoff ) AS num_psm_at_pt_01_q_cutoff, "
					+ " GROUP_CONCAT( DISTINCT search_id SEPARATOR '" + SEARCH_ID_GROUP_SEPARATOR + "' ) AS search_ids "
			
					+ "FROM search_crosslink_lookup WHERE search_id IN (#SEARCHES#) AND bestPSMQValue <= ? AND ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL ) "
					+ "GROUP BY nrseq_id_1, protein_1_position, nrseq_id_2, protein_2_position "
					+ "ORDER BY nrseq_id_1, protein_1_position, nrseq_id_2, protein_2_position";
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
						
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, psmCutoff );
			pstmt.setDouble( 2, peptideCutoff );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				MergedSearchProteinCrosslink link = new MergedSearchProteinCrosslink();
				link.setPsmCutoff( psmCutoff );
				link.setPeptideCutoff( peptideCutoff );
				link.setProtein1( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				link.setProtein2( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 2 ) ) ) );
				
				link.setProtein1Position( rs.getInt( 3 ) );
				link.setProtein2Position( rs.getInt( 4 ) );
				link.setBestPSMQValue( rs.getDouble( 5 ) );

				link.setBestPeptideQValue( rs.getDouble( 6 ) );
				if ( rs.wasNull() ) {
					link.setBestPeptideQValue( null );
				}
				
				


				int numPsmsForpt01Cutoff = rs.getInt( "num_psm_at_pt_01_q_cutoff" );
				
				if ( DefaultQValueCutoffConstants.PSM_Q_VALUE_CUTOFF_DEFAULT == psmCutoff ) {
					
					link.setNumPsms( numPsmsForpt01Cutoff ); // code is needed in WebMergedReportedPeptide for when psmCutoff is not default
				}
				
				
				if( link.getProtein1() == null || link.getProtein2() == null )
					throw new Exception( "Got null for one of the proteins in the crosslink..." );
				
				
				
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
