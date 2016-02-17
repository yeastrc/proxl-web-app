package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.AnnotationTypeDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;

/**
 * 
 *
 */
public class AnnotationTypeForSearchIdsSearcher {

	private static final Logger log = Logger.getLogger(AnnotationTypeForSearchIdsSearcher.class);

	private AnnotationTypeForSearchIdsSearcher() { }
	public static AnnotationTypeForSearchIdsSearcher getInstance() {
		return new AnnotationTypeForSearchIdsSearcher(); 
	}
	
	
	
	/**
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer, AnnotationTypeDTO>> getAll_Psm_Filterable_ForSearchIds( Collection<Integer> searchIds ) throws Exception {
		
		return getAllForSearchIds_FilterableDescriptive_PsmPeptide( 
				searchIds, 
				FilterableDescriptiveAnnotationType.FILTERABLE, 
				PsmPeptideAnnotationType.PSM );
	}
	

	
	/**
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer, AnnotationTypeDTO>> getAll_Psm_Descriptive_ForSearchIds( Collection<Integer> searchIds ) throws Exception {
		
		return getAllForSearchIds_FilterableDescriptive_PsmPeptide( 
				searchIds, 
				FilterableDescriptiveAnnotationType.DESCRIPTIVE, 
				PsmPeptideAnnotationType.PSM );
	}
	

	/**
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer, AnnotationTypeDTO>> getAll_Peptide_Filterable_ForSearchIds( Collection<Integer> searchIds ) throws Exception {
		
		return getAllForSearchIds_FilterableDescriptive_PsmPeptide( 
				searchIds, 
				FilterableDescriptiveAnnotationType.FILTERABLE, 
				PsmPeptideAnnotationType.PEPTIDE );
	}
	

	
	/**
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer, AnnotationTypeDTO>> getAll_Peptide_Descriptive_ForSearchIds( Collection<Integer> searchIds ) throws Exception {
		
		return getAllForSearchIds_FilterableDescriptive_PsmPeptide( 
				searchIds, 
				FilterableDescriptiveAnnotationType.DESCRIPTIVE, 
				PsmPeptideAnnotationType.PEPTIDE );
	}
	
	

	private static final String SQL = "SELECT * "
			+ "  FROM annotation_type "
			+ " WHERE search_id IN ( #SEARCHES# ) "
			+ " AND psm_peptide_type = ? "
			+ " AND filterable_descriptive_type = ? ";
	
	

	/**
	 * @param searchIds
	 * @param filterableDescriptiveAnnotationType
	 * @param psmPeptideAnnotationType
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer, AnnotationTypeDTO>> getAllForSearchIds_FilterableDescriptive_PsmPeptide( 
			
			Collection<Integer> searchIds,
			
			FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType,
			PsmPeptideAnnotationType psmPeptideAnnotationType
			
			) throws Exception {
		
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> resultMap = new TreeMap<>();

		
		Set<Integer> searchIdsAsSet = new HashSet<>( searchIds ); //  copy to set to ensure no duplicates
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = SQL;

		String searchIdsAsCommaDelimString = StringUtils.join( searchIdsAsSet, "," );

		sql = sql.replaceAll( "#SEARCHES#", searchIdsAsCommaDelimString );
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setString(paramCounter, psmPeptideAnnotationType.value() );

			paramCounter++;
			pstmt.setString(paramCounter, filterableDescriptiveAnnotationType.value() );
			
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				Integer searchId = rs.getInt( "search_id" );
				
				Map<Integer, AnnotationTypeDTO> resultMapPerSearchId =  resultMap.get( searchId );
				
				if ( resultMapPerSearchId == null ) {
					
					resultMapPerSearchId = new HashMap<>();
					
					resultMap.put( searchId, resultMapPerSearchId );
				}

				AnnotationTypeDTO item = AnnotationTypeDAO.getInstance().populateFromResultSet( rs );
				
				resultMapPerSearchId.put( item.getId(), item );
			}
			
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
		
		
		return resultMap;
		
	}
}
