package org.yeastrc.xlink.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;

/**
 * 
 *
 */
public class NumPeptidesForProteinCriteriaSearcher {

	private static final Logger log = Logger.getLogger(NumPeptidesForProteinCriteriaSearcher.class);
	
	private NumPeptidesForProteinCriteriaSearcher() { }
	private static final NumPeptidesForProteinCriteriaSearcher _INSTANCE = new NumPeptidesForProteinCriteriaSearcher();
	public static NumPeptidesForProteinCriteriaSearcher getInstance() { return _INSTANCE; }


	private final String SQL_CROSSLINK_FIRST_PART = 

			"SELECT COUNT(distinct psm.reported_peptide_id) " 

		+ "FROM psm  "
		+ "INNER JOIN crosslink ON psm.id = crosslink.psm_id ";

	private final String SQL_CROSSLINK_WHERE_START = 

			" WHERE psm.search_id = ?  "
					+ " AND crosslink.nrseq_id_1 = ? AND crosslink.nrseq_id_2 = ? "
					+ " AND crosslink.protein_1_position = ? AND crosslink.protein_2_position = ? ";
	
	

	/**
	 * Get the number of distinct peptides (that is, distinct pair of crosslinked peptides) found that identified the given crosslinked proteins/positions
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param peptideCutoff
	 * @param nrseqId_protein_1
	 * @param nrseqId_protein_2
	 * @param position_protein_1
	 * @param position_protein_2
	 * @return
	 * @throws Exception
	 */
	public int getNumLinkedPeptidesForCrosslink(   
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein_1,
			int nrseqId_protein_2,
			int position_protein_1,
			int position_protein_2
			
			) throws Exception {
		
		int count = 0;

		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

		////////////
		
		//  All cutoffs are default?
		
		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
//		boolean onlyDefaultPsmCutoffs = true;
		
		

		//   Check if any Peptide Cutoffs are default filters
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
				break;
			}
		}
		
		
		//   Check if all Peptide Cutoffs are default values
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
				break;
			}
		}

		//   Check if all Psm Cutoffs are default values
		
//		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPsmCutoffs = false;
//				break;
//			}
//		}
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		//////////////////////
		
		/////   Start building the SQL
		
		

		
		final String sqlFirstPart = SQL_CROSSLINK_FIRST_PART;
		
		final String sqlWhereStartPart = SQL_CROSSLINK_WHERE_START;

		
		final String sql = getSQL( peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
				sqlFirstPart, sqlWhereStartPart);
		
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
	
			pstmt = conn.prepareStatement( sql );

			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein_1 );
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein_2 );
			paramCounter++;
			pstmt.setInt( paramCounter,  position_protein_1 );
			paramCounter++;
			pstmt.setInt( paramCounter,  position_protein_2 );

			//  Must be last
			setCutoffParams( searchId, peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
					pstmt, paramCounter );
			
			rs = pstmt.executeQuery();
			
			rs.next();
			count = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPsmsForCrosslink( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}



	///////////////////////////////////////////////////////////////


	private final String SQL_CROSSLINK_UNIQUE_FIRST_PART = 

			"SELECT COUNT(distinct psm.reported_peptide_id) " 

		+ " FROM psm  "
		+ " INNER JOIN crosslink ON psm.id = crosslink.psm_id "
		
		+ " INNER JOIN nrseq_database_peptide_protein AS ndpp_1 ON crosslink.peptide_1_id = ndpp_1.peptide_id "
		+ " INNER JOIN nrseq_database_peptide_protein AS ndpp_2 ON crosslink.peptide_2_id = ndpp_2.peptide_id ";                

	private final String SQL_CROSSLINK_UNIQUE_WHERE_START = 

			" WHERE psm.search_id = ?  "
					+ " AND crosslink.nrseq_id_1 = ? AND crosslink.nrseq_id_2 = ? "
					+ " AND crosslink.protein_1_position = ? AND crosslink.protein_2_position = ? "
					+ " AND ndpp_2.nrseq_database_id = ?  " 
					+ " AND ndpp_1.is_unique = 'Y' AND ndpp_2.is_unique='Y' ";
	
	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this crosslink
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param nrseqId_protein_1
	 * @param nrseqId_protein_2
	 * @param position_protein_1
	 * @param position_protein_2
	 * @param fastaFileDatabaseId
	 * @return
	 * @throws Exception
	 */
	public int getNumUniqueLinkedPeptidesForCrosslink( 
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein_1,
			int nrseqId_protein_2,
			int position_protein_1,
			int position_protein_2,
			int fastaFileDatabaseId

			) throws Exception {
		
		
		int count = 0;
		
		

		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();


		////////////
		
		//  All cutoffs are default?
		
		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
//		boolean onlyDefaultPsmCutoffs = true;
		
		

		//   Check if any Peptide Cutoffs are default filters
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
				break;
			}
		}
		
		
		//   Check if all Peptide Cutoffs are default values
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
				break;
			}
		}

		//   Check if all Psm Cutoffs are default values
		
//		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPsmCutoffs = false;
//				break;
//			}
//		}
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		


		//////////////////////
		
		/////   Start building the SQL
		
		
		final String sql = getSQL( peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
				SQL_CROSSLINK_UNIQUE_FIRST_PART, SQL_CROSSLINK_UNIQUE_WHERE_START);		
		

		
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );
			
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein_1 );
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein_2 );
			paramCounter++;
			pstmt.setInt( paramCounter,  position_protein_1 );
			paramCounter++;
			pstmt.setInt( paramCounter,  position_protein_2 );

			paramCounter++;
			pstmt.setInt( paramCounter,  fastaFileDatabaseId );


			// Process PSM And Peptide Cutoffs for WHERE

			setCutoffParams(searchId, peptideCutoffValuesList,
					psmCutoffValuesList, defaultPeptideCutoffs, pstmt,
					paramCounter);
			
			
			rs = pstmt.executeQuery();
			if( rs.next() )
				count = rs.getInt( 1 );

		} catch ( Exception e ) {
			
			String msg = "Exception in getNumUniqueLinkedPeptidesForCrosslink( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}

	////////////////////////////////////////////////////////////////////
	
	//////////   LOOPLINK
	
	///////////////////////////////////////////////////

	private final String SQL_LOOPLINK_FIRST_PART = 
			"SELECT COUNT(distinct psm.reported_peptide_id) "

			+ "FROM psm  "
			+ "INNER JOIN looplink ON psm.id = looplink.psm_id ";

	private final String SQL_LOOPLINK_WHERE_START = 

			" WHERE psm.search_id = ?  "
			+ " AND looplink.nrseq_id = ? "
			+ " AND looplink.protein_position_1 = ? AND looplink.protein_position_2 = ? ";
	
	
	/**
	 * Get the number of distinct peptides (that is, distinct pair of crosslinked peptides) found that identified the given crosslinked proteins/positions
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param nrseqId_protein
	 * @param protein_position_1
	 * @param protein_position_2
	 * @return
	 * @throws Exception
	 */
	public int getNumPeptidesForLooplink( 
			
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein,
			int protein_position_1,
			int protein_position_2

			) throws Exception {
		
		int count = 0;
		
		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

		////////////
		
		//  All cutoffs are default?
		
		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
//		boolean onlyDefaultPsmCutoffs = true;
		
		

		//   Check if any Peptide Cutoffs are default filters
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
				break;
			}
		}
		
		
		//   Check if all Peptide Cutoffs are default values
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
				break;
			}
		}

		//   Check if all Psm Cutoffs are default values
		
//		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPsmCutoffs = false;
//				break;
//			}
//		}
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		//////////////////////
		
		/////   Start building the SQL
		
		

		
		final String sqlFirstPart = SQL_LOOPLINK_FIRST_PART;
		
		final String sqlWhereStartPart = SQL_LOOPLINK_WHERE_START;

		
		final String sql = getSQL( peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
				sqlFirstPart, sqlWhereStartPart);
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );


			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein );
			paramCounter++;
			pstmt.setInt( paramCounter,  protein_position_1 );
			paramCounter++;
			pstmt.setInt( paramCounter,  protein_position_2 );

			//  Must be last
			setCutoffParams( searchId, peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
					pstmt, paramCounter );


			rs = pstmt.executeQuery();
			
			rs.next();
			count = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPeptidesForLooplink( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}
	

	////////////////////////////////////////////////////////////////////////////////////
	
	private final String SQL_LOOPLINK_UNIQUE_FIRST_PART = 

			"SELECT COUNT(distinct psm.reported_peptide_id) " 

		+ " FROM psm  "
		+ " INNER JOIN looplink ON psm.id = looplink.psm_id "
		
		+ " INNER JOIN nrseq_database_peptide_protein AS ndpp ON looplink.peptide_id = ndpp.peptide_id ";

	private final String SQL_LOOPLINK_UNIQUE_WHERE_START = 

			" WHERE psm.search_id = ?  "
					+ " AND looplink.nrseq_id = ?  "
					+ " AND looplink.protein_position_1 = ? AND looplink.protein_position_2 = ? "
					+ " AND ndpp.nrseq_database_id = ?  " 
					+ " AND ndpp.is_unique = 'Y' ";
	
	
	

	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this looplink
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param nrseqId_protein
	 * @param protein_position_1
	 * @param protein_position_2
	 * @param fastaFileDatabaseId
	 * @return
	 * @throws Exception
	 */
	public int getNumUniquePeptidesForLooplink( 

			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein,
			int protein_position_1,
			int protein_position_2,
			int fastaFileDatabaseId


			 ) throws Exception {
		
		int count = 0;
		

		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

		////////////
		
		//  All cutoffs are default?
		
		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
//		boolean onlyDefaultPsmCutoffs = true;
		
		

		//   Check if any Peptide Cutoffs are default filters
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
				break;
			}
		}
		
		
		//   Check if all Peptide Cutoffs are default values
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
				break;
			}
		}

		//   Check if all Psm Cutoffs are default values
		
//		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPsmCutoffs = false;
//				break;
//			}
//		}
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		
		final String sql = getSQL( peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
				SQL_LOOPLINK_UNIQUE_FIRST_PART, SQL_LOOPLINK_UNIQUE_WHERE_START );		
		
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			

			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );
			
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein );
			paramCounter++;
			pstmt.setInt( paramCounter,  protein_position_1 );
			paramCounter++;
			pstmt.setInt( paramCounter,  protein_position_2 );

			paramCounter++;
			pstmt.setInt( paramCounter,  fastaFileDatabaseId );


			// Process PSM And Peptide Cutoffs for WHERE

			setCutoffParams( searchId, peptideCutoffValuesList,
					psmCutoffValuesList, defaultPeptideCutoffs, 
					pstmt, paramCounter);
			
			
			
			rs = pstmt.executeQuery();
			if( rs.next() )
				count = rs.getInt( 1 );

		} catch ( Exception e ) {
			
			String msg = "Exception in getNumUniquePeptidesForLooplink( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}

	
	
	////////////////////////////////////////////////////////
	
	/////////////    MONOLINK

	///////////////////////////////////////////////////

	private final String SQL_MONOLINK_FIRST_PART = 
			"SELECT COUNT(distinct psm.reported_peptide_id) " 

			+ "FROM psm  "
			+ "INNER JOIN monolink ON psm.id = monolink.psm_id ";

	private final String SQL_MONOLINK_WHERE_START = 

			" WHERE psm.search_id = ?  "
			+ " AND monolink.nrseq_id = ? "
			+ " AND monolink.protein_position = ? ";
	
	
	/**
	 * Get the number of distinct peptides (that is, distinct pair of crosslinked peptides) found that identified the given crosslinked proteins/positions
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param nrseqId_protein
	 * @param protein_position
	 * @return
	 * @throws Exception
	 */
	public int getNumPeptidesForMonolink( 
			
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein,
			int protein_position

			) throws Exception {
		
		int count = 0;
		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

		////////////
		
		//  All cutoffs are default?
		
		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
//		boolean onlyDefaultPsmCutoffs = true;
		
		

		//   Check if any Peptide Cutoffs are default filters
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
				break;
			}
		}
		
		
		//   Check if all Peptide Cutoffs are default values
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
				break;
			}
		}

		//   Check if all Psm Cutoffs are default values
		
//		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPsmCutoffs = false;
//				break;
//			}
//		}
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		//////////////////////
		
		/////   Start building the SQL
		
		

		
		final String sqlFirstPart = SQL_MONOLINK_FIRST_PART;
		
		final String sqlWhereStartPart = SQL_MONOLINK_WHERE_START;

		
		final String sql = getSQL( peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
				sqlFirstPart, sqlWhereStartPart);
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );


			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein );
			paramCounter++;
			pstmt.setInt( paramCounter,  protein_position );

			//  Must be last
			setCutoffParams( searchId, peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
					pstmt, paramCounter );


			rs = pstmt.executeQuery();
			
			rs.next();
			count = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPeptidesForMonolink( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}
	

	////////////////////////////////////////////////////////////////////////////////////
	

	private final String SQL_MONOLINK_UNIQUE_FIRST_PART = 

			"SELECT COUNT(distinct psm.reported_peptide_id) " 

		+ " FROM psm  "
		+ " INNER JOIN monolink ON psm.id = monolink.psm_id "
		
		+ " INNER JOIN nrseq_database_peptide_protein AS ndpp ON monolink.peptide_id = ndpp.peptide_id ";

	private final String SQL_MONOLINK_UNIQUE_WHERE_START = 

			" WHERE psm.search_id = ?  "
					+ " AND monolink.nrseq_id = ?  "
					+ " AND monolink.protein_position = ? "
					+ " AND ndpp.nrseq_database_id = ?  " 
					+ " AND ndpp.is_unique = 'Y' ";
	
	
	

	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this monolink
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param nrseqId_protein
	 * @param protein_position
	 * @param fastaFileDatabaseId
	 * @return
	 * @throws Exception
	 */
	public int getNumUniquePeptidesForMonolink( 

			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein,
			int protein_position,
			int fastaFileDatabaseId


			 ) throws Exception {
		
		int count = 0;
		
	
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

		////////////
		
		//  All cutoffs are default?
		
		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
//		boolean onlyDefaultPsmCutoffs = true;
		
		

		//   Check if any Peptide Cutoffs are default filters
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
				break;
			}
		}
		
		
		//   Check if all Peptide Cutoffs are default values
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
				break;
			}
		}

		//   Check if all Psm Cutoffs are default values
		
//		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPsmCutoffs = false;
//				break;
//			}
//		}
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		
		final String sql = getSQL( peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
				SQL_MONOLINK_UNIQUE_FIRST_PART, SQL_MONOLINK_UNIQUE_WHERE_START );		
		
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			

			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );
			
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein );
			paramCounter++;
			pstmt.setInt( paramCounter,  protein_position );

			paramCounter++;
			pstmt.setInt( paramCounter,  fastaFileDatabaseId );


			// Process PSM And Peptide Cutoffs for WHERE

			setCutoffParams( searchId, peptideCutoffValuesList,
					psmCutoffValuesList, defaultPeptideCutoffs, 
					pstmt, paramCounter);
			
			
			
			rs = pstmt.executeQuery();
			if( rs.next() )
				count = rs.getInt( 1 );

		} catch ( Exception e ) {
			
			String msg = "Exception in getNumUniquePeptidesForMonolink( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}

	
	//////////////////////////////////////////////////
	
	//////////////      DIMER

	private final String SQL_DIMER_FIRST_PART = 

			"SELECT COUNT(distinct psm.reported_peptide_id) " 

		+ "FROM psm  "
		+ "INNER JOIN dimer ON psm.id = dimer.psm_id ";

	private final String SQL_DIMER_WHERE_START = 

			" WHERE psm.search_id = ?  "
					+ " AND dimer.nrseq_id_1 = ? AND dimer.nrseq_id_2 = ? ";
	
	

	/**
	 * Get the number of distinct peptides (that is, distinct pair of dimered peptides) found that identified the given dimered proteins/positions
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param peptideCutoff
	 * @param nrseqId_protein_1
	 * @param nrseqId_protein_2
	 * @return
	 * @throws Exception
	 */
	public int getNumLinkedPeptidesForDimer(   
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein_1,
			int nrseqId_protein_2
			
			) throws Exception {
		
		int count = 0;

		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

		////////////
		
		//  All cutoffs are default?
		
		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
//		boolean onlyDefaultPsmCutoffs = true;
		
		

		//   Check if any Peptide Cutoffs are default filters
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
				break;
			}
		}
		
		
		//   Check if all Peptide Cutoffs are default values
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
				break;
			}
		}

		//   Check if all Psm Cutoffs are default values
		
//		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPsmCutoffs = false;
//				break;
//			}
//		}
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		//////////////////////
		
		/////   Start building the SQL
		
		

		
		final String sqlFirstPart = SQL_DIMER_FIRST_PART;
		
		final String sqlWhereStartPart = SQL_DIMER_WHERE_START;

		
		final String sql = getSQL( peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
				sqlFirstPart, sqlWhereStartPart);
		
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
	
			pstmt = conn.prepareStatement( sql );

			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein_1 );
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein_2 );

			//  Must be last
			setCutoffParams( searchId, peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
					pstmt, paramCounter );
			
			rs = pstmt.executeQuery();
			
			rs.next();
			count = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPsmsForDimer( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}



	///////////////////////////////////////////////////////////////


	private final String SQL_DIMER_UNIQUE_FIRST_PART = 

			"SELECT COUNT(distinct psm.reported_peptide_id) " 

		+ " FROM psm  "
		+ " INNER JOIN dimer ON psm.id = dimer.psm_id "
		
		+ " INNER JOIN nrseq_database_peptide_protein AS ndpp_1 ON dimer.peptide_1_id = ndpp_1.peptide_id "
		+ " INNER JOIN nrseq_database_peptide_protein AS ndpp_2 ON dimer.peptide_2_id = ndpp_2.peptide_id ";                

	private final String SQL_DIMER_UNIQUE_WHERE_START = 

			" WHERE psm.search_id = ?  "
					+ " AND dimer.nrseq_id_1 = ? AND dimer.nrseq_id_2 = ? "
					+ " AND ndpp_2.nrseq_database_id = ?  " 
					+ " AND ndpp_1.is_unique = 'Y' AND ndpp_2.is_unique='Y' ";
	

	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this dimer
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param nrseqId_protein_1
	 * @param nrseqId_protein_2
	 * @param fastaFileDatabaseId
	 * @return
	 * @throws Exception
	 */
	public int getNumUniqueLinkedPeptidesForDimer( 
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein_1,
			int nrseqId_protein_2,
			int fastaFileDatabaseId

			) throws Exception {
		
		
		int count = 0;
		
		

		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();


		////////////
		
		//  All cutoffs are default?
		
		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
//		boolean onlyDefaultPsmCutoffs = true;
		
		

		//   Check if any Peptide Cutoffs are default filters
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
				break;
			}
		}
		
		
		//   Check if all Peptide Cutoffs are default values
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
				break;
			}
		}

		//   Check if all Psm Cutoffs are default values
		
//		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPsmCutoffs = false;
//				break;
//			}
//		}
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		


		//////////////////////
		
		/////   Start building the SQL
		
		
		final String sql = getSQL( peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
				SQL_DIMER_UNIQUE_FIRST_PART, SQL_DIMER_UNIQUE_WHERE_START);		
		

		
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );
			
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein_1 );
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein_2 );

			paramCounter++;
			pstmt.setInt( paramCounter,  fastaFileDatabaseId );


			// Process PSM And Peptide Cutoffs for WHERE

			setCutoffParams(searchId, peptideCutoffValuesList,
					psmCutoffValuesList, defaultPeptideCutoffs, pstmt,
					paramCounter);
			
			
			rs = pstmt.executeQuery();
			if( rs.next() )
				count = rs.getInt( 1 );

		} catch ( Exception e ) {
			
			String msg = "Exception in getNumUniqueLinkedPeptidesForDimer( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}


	
	////////////////////////////////////////////////////////
	
	/////////////    UNLINKED

	///////////////////////////////////////////////////

	private final String SQL_UNLINKED_FIRST_PART = 
			"SELECT COUNT(distinct psm.reported_peptide_id) " 

			+ "FROM psm  "
			+ "INNER JOIN unlinked ON psm.id = unlinked.psm_id ";

	private final String SQL_UNLINKED_WHERE_START = 

			" WHERE psm.search_id = ?  "
			+ " AND unlinked.nrseq_id = ? ";
	
	
	/**
	 * Get the number of distinct peptides (that is, distinct pair of crosslinked peptides) found that identified the given crosslinked proteins/positions
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param nrseqId_protein
	 * @param protein_position
	 * @return
	 * @throws Exception
	 */
	public int getNumPeptidesForUnlinked( 
			
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein

			) throws Exception {
		
		int count = 0;
		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

		////////////
		
		//  All cutoffs are default?
		
		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
//		boolean onlyDefaultPsmCutoffs = true;
		
		

		//   Check if any Peptide Cutoffs are default filters
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
				break;
			}
		}
		
		
		//   Check if all Peptide Cutoffs are default values
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
				break;
			}
		}

		//   Check if all Psm Cutoffs are default values
		
//		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPsmCutoffs = false;
//				break;
//			}
//		}
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		//////////////////////
		
		/////   Start building the SQL
		
		

		
		final String sqlFirstPart = SQL_UNLINKED_FIRST_PART;
		
		final String sqlWhereStartPart = SQL_UNLINKED_WHERE_START;

		
		final String sql = getSQL( peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
				sqlFirstPart, sqlWhereStartPart);
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );


			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein );

			//  Must be last
			setCutoffParams( searchId, peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
					pstmt, paramCounter );


			rs = pstmt.executeQuery();
			
			rs.next();
			count = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPeptidesForUnlinked( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}
	

	////////////////////////////////////////////////////////////////////////////////////
	

	private final String SQL_UNLINKED_UNIQUE_FIRST_PART = 

			"SELECT COUNT(distinct psm.reported_peptide_id) " 

		+ " FROM psm  "
		+ " INNER JOIN unlinked ON psm.id = unlinked.psm_id "
		
		+ " INNER JOIN nrseq_database_peptide_protein AS ndpp ON unlinked.peptide_id = ndpp.peptide_id ";

	private final String SQL_UNLINKED_UNIQUE_WHERE_START = 

			" WHERE psm.search_id = ?  "
					+ " AND unlinked.nrseq_id = ?  "
					+ " AND ndpp.nrseq_database_id = ?  " 
					+ " AND ndpp.is_unique = 'Y' ";
	
	
	

	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this unlinked
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param nrseqId_protein
	 * @param fastaFileDatabaseId
	 * @return
	 * @throws Exception
	 */
	public int getNumUniquePeptidesForUnlinked( 

			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein,
			int fastaFileDatabaseId


			 ) throws Exception {
		
		int count = 0;
		
	
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

		////////////
		
		//  All cutoffs are default?
		
		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
//		boolean onlyDefaultPsmCutoffs = true;
		
		

		//   Check if any Peptide Cutoffs are default filters
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
				break;
			}
		}
		
		
		//   Check if all Peptide Cutoffs are default values
		
		for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
				break;
			}
		}

		//   Check if all Psm Cutoffs are default values
		
//		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
//			
//			if ( ! item.annotationValueMatchesDefault() ) {
//				
//				onlyDefaultPsmCutoffs = false;
//				break;
//			}
//		}
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		
		final String sql = getSQL( peptideCutoffValuesList, psmCutoffValuesList, defaultPeptideCutoffs, 
				SQL_UNLINKED_UNIQUE_FIRST_PART, SQL_UNLINKED_UNIQUE_WHERE_START );		
		
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			

			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );
			
			paramCounter++;
			pstmt.setInt( paramCounter,  nrseqId_protein );

			paramCounter++;
			pstmt.setInt( paramCounter,  fastaFileDatabaseId );


			// Process PSM And Peptide Cutoffs for WHERE

			setCutoffParams( searchId, peptideCutoffValuesList,
					psmCutoffValuesList, defaultPeptideCutoffs, 
					pstmt, paramCounter);
			
			
			
			rs = pstmt.executeQuery();
			if( rs.next() )
				count = rs.getInt( 1 );

		} catch ( Exception e ) {
			
			String msg = "Exception in getNumUniquePeptidesForUnlinked( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}


	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Get Peptide count SQL for All Types
	 * 
	 * @param peptideCutoffValuesList
	 * @param psmCutoffValuesList
	 * @param defaultPeptideCutoffs
	 * @param sqlFirstPart
	 * @param sqlWhereStartPart
	 * @return
	 * @throws Exception 
	 */
	private String getSQL(
			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList,
			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList,
			Yes_No__NOT_APPLICABLE_Enum defaultPeptideCutoffs,
			final String sqlFirstPart, final String sqlWhereStartPart) throws Exception {
		
		
		
		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		


		sqlSB.append( sqlFirstPart );
		

		{

//			if ( ! onlyDefaultPsmCutoffs ) { //  Can only use this if psm table or a lookup has a flag for "met default cutoffs"
				
				
				// NOT CURRENTLY APPLY SINCE "if" just above commented out:     Non-Default PSM cutoffs so have to query on the cutoffs


				//  Add inner join for each PSM cutoff

				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );
					
					
					//  If slow, use psm_filterable_annotation__generic_lookup and put more limits in query on search, reported peptide, and maybe link type

					sqlSB.append( " psm_filterable_annotation__generic_lookup AS psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " psm.id = "  );

					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".psm_id" );

				}

//			}
		
		}
		
		
		{
			
			if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.YES ) {
			
				
				//  Defaults so join unified_rp__rep_pept__search__generic_lookup 
				//       which has a flag for peptide meets defaults

				sqlSB.append( " INNER JOIN " );

				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup " );

				sqlSB.append( " ON "  );

				sqlSB.append( " psm.search_id = "  );

				sqlSB.append( "unified_rp__rep_pept__search__generic_lookup.search_id" );

				sqlSB.append( " AND " );


				sqlSB.append( " psm.reported_peptide_id = "  );

				sqlSB.append( "unified_rp__rep_pept__search__generic_lookup.reported_peptide_id" );
				
			
			} else if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {

				//  Non-Default PSM cutoffs so have to query on the cutoffs

				//  Add inner join for each Peptide cutoff

				for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );

					sqlSB.append( " srch__rep_pept__annotation AS srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " psm.search_id = "  );

					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );


					sqlSB.append( " psm.reported_peptide_id = "  );

					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );

				}
			}
		}
		

		///////////
		
		sqlSB.append( sqlWhereStartPart );
		
		//////////
		

		// Process PSM Cutoffs for WHERE

		{

			
//			if ( onlyDefaultPsmCutoffs ) {
//				
//				//   Only Default PSM Cutoffs chosen so criteria simply the Peptides where the PSM count for the default cutoffs is > zero
//				
//
//				sqlSB.append( " AND " );
//
//
//				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff > 0 " );
//
//				
//			} else {

				
				//  Non-Default PSM cutoffs so have to query on the cutoffs

				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {


					AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

					counter++;

					sqlSB.append( " AND " );

					sqlSB.append( " ( " );

					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".value_double " );

					if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
						
						String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
						log.error( msg );
						throw new Exception(msg);
					}
					
					if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {

						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

					} else {

						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

					}

					sqlSB.append( " ? " );

					sqlSB.append( " ) " );
				}
//			}
		}
		

		//  Process Peptide Cutoffs for WHERE

		{

			if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE ) {

				//  No WHERE criteria for defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE
				
				//     There are no Peptide cutoffs to apply
				
				
			
			} else if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.YES ) {

				//   Only Default Peptide Cutoffs chosen so criteria simply the Peptides where the defaultPeptideCutoffs is yes

				sqlSB.append( " AND " );


				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.peptide_meets_default_cutoffs = '" );
				sqlSB.append( Yes_No__NOT_APPLICABLE_Enum.YES.value() );
				sqlSB.append( "' " );

				
			} else if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {

				
				//  Non-Default Peptide cutoffs so have to query on the cutoffs

				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

					AnnotationTypeDTO AnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();

					counter++;

					sqlSB.append( " AND " );

					sqlSB.append( " ( " );


					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );

					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".value_double " );
					
					if ( AnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
						
						String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + AnnotationTypeDTO.getId();
						log.error( msg );
						throw new Exception(msg);
					}
					

					if ( AnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {

						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

					} else {

						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

					}

					sqlSB.append( "? " );

					sqlSB.append( " ) " );
				}
			}
		}
		
		String sql = sqlSB.toString();
		
		return sql;
	}
	
	


	/**
	 * Get Peptide count Set PreparedStatement Params for All Types
	 * 
	 * @param searchId
	 * @param peptideCutoffValuesList
	 * @param psmCutoffValuesList
	 * @param defaultPeptideCutoffs
	 * @param pstmt
	 * @param paramCounter
	 * @throws SQLException
	 */
	private void setCutoffParams(
			int searchId,
			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList,
			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList,
			Yes_No__NOT_APPLICABLE_Enum defaultPeptideCutoffs,
			PreparedStatement pstmt, int paramCounter) throws SQLException {
		
		
		// Process PSM Cutoffs for WHERE


		{
			
//				if ( ! onlyDefaultPsmCutoffs ) {
				
				//  PSM Cutoffs are not the default 
				

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {

					AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

					paramCounter++;
					pstmt.setInt( paramCounter, srchPgmFilterablePsmAnnotationTypeDTO.getId() );

					paramCounter++;
					pstmt.setDouble( paramCounter, searcherCutoffValuesPsmAnnotationLevel.getAnnotationCutoffValue() );
				}
//				}
		}
		


		// Process Peptide Cutoffs for WHERE


		{

			if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {
				
				//  Non-Default Peptide cutoffs so have to query on the cutoffs

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

					AnnotationTypeDTO AnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();

					paramCounter++;
					pstmt.setInt( paramCounter, searchId );

					paramCounter++;
					pstmt.setInt( paramCounter, AnnotationTypeDTO.getId() );

					paramCounter++;
					pstmt.setDouble( paramCounter, searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationCutoffValue() );
				}
			}
		}
	}
	

}