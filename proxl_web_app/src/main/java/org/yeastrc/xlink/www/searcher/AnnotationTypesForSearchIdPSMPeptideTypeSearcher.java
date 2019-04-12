package org.yeastrc.xlink.www.searcher;

/**
 * Get AnnotationTypeDTO records for Search Id
 *
 */
public class AnnotationTypesForSearchIdPSMPeptideTypeSearcher {

//	private static final Logger log = LoggerFactory.getLogger( AnnotationTypesForSearchIdPSMPeptideTypeSearcher.class);
//	private AnnotationTypesForSearchIdPSMPeptideTypeSearcher() { }
//	public static AnnotationTypesForSearchIdPSMPeptideTypeSearcher getInstance() { return new AnnotationTypesForSearchIdPSMPeptideTypeSearcher(); }
//	
//	/**
//	 * Get the annotation_type records for the search id from the database
//	 * 
//	 * @param searchId
//	 * @return
//	 * @throws Exception
//	 */
//	public List<AnnotationTypeDTO> get_PSM_Filterable_ForSearchId( int searchId ) throws Exception {
//		return getForSearchIdPsmPeptideTypeFilterableDescType( searchId, PsmPeptideAnnotationType.PSM, FilterableDescriptiveAnnotationType.FILTERABLE );
//	}
//	/**
//	 * Get the annotation_type records for the search id from the database
//	 * 
//	 * @param searchId
//	 * @return
//	 * @throws Exception
//	 */
//	public List<AnnotationTypeDTO> get_Peptide_Filterable_ForSearchId( int searchId ) throws Exception {
//		return getForSearchIdPsmPeptideTypeFilterableDescType( searchId, PsmPeptideAnnotationType.PEPTIDE, FilterableDescriptiveAnnotationType.FILTERABLE );
//	}
//	
//	private static final String SEARCH_SQL = 
//			"SELECT annotation_type.* "
//			+ "FROM annotation_type "
//			+ " WHERE annotation_type.search_id = ? AND annotation_type.psm_peptide_type = ? AND filterable_descriptive_type = ?";
//	/**
//	 * Get the annotation_type records for the search id from the database
//	 * 
//	 * @param searchId
//	 * @param psmPeptideAnnotationType
//	 * @param filterableDescriptiveAnnotationType
//	 * @return
//	 * @throws Exception
//	 */
//	public List<AnnotationTypeDTO> getForSearchIdPsmPeptideTypeFilterableDescType( 
//			int searchId,
//			PsmPeptideAnnotationType psmPeptideAnnotationType,
//			FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType
//			) throws Exception {
//		List<AnnotationTypeDTO> results = new ArrayList<>();
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		final String sql = SEARCH_SQL;
//		try {
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//			pstmt = conn.prepareStatement( sql );
//			int paramCounter = 0;
//			paramCounter++;
//			pstmt.setInt( paramCounter, searchId );
//			paramCounter++;
//			pstmt.setString( paramCounter, psmPeptideAnnotationType.value() );
//			paramCounter++;
//			pstmt.setString( paramCounter, filterableDescriptiveAnnotationType.value() );
//			rs = pstmt.executeQuery();
//			while( rs.next() ) {
//				//  Can use this since only fields in result set are from table annotation_type
//				AnnotationTypeDTO item = 
//						AnnotationTypeDAO.getInstance().populateFromResultSet( rs );
//				results.add( item );
//			}
//		} catch ( Exception e ) {
//			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
//			throw e;
//		} finally {
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//		}
//		return results;
//	}
}