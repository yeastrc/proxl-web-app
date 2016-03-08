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
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searcher_result_objects.NumPeptidesPSMsForProteinCriteriaResult;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * 
 *
 */
public class NumPeptidesPSMsForProteinCriteriaSearcher {

	private NumPeptidesPSMsForProteinCriteriaSearcher() { }
	public static NumPeptidesPSMsForProteinCriteriaSearcher getInstance() { return new NumPeptidesPSMsForProteinCriteriaSearcher(); }

	private static final Logger log = Logger.getLogger(NumPeptidesPSMsForProteinCriteriaSearcher.class);
	
	
	private static final String SQL_LINK_TABLE_ALIAS = "link_table";
	

	private final String PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS = "psm_fltrbl_tbl_";
	

	
	
	///////////////////////////////////////////////////////////////

	private final String SQL_CROSSLINK_TABLE_NAME = "crosslink";

	private final String SQL_CROSSLINK_WHERE_FOR_LINK_TABLE = 

			"  " + SQL_LINK_TABLE_ALIAS + ".nrseq_id_1 = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".nrseq_id_2 = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".protein_1_position = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".protein_2_position = ? ";
	

	private final String SQL_CROSSLINK_PEPTIDE_ID_1_FIELD_NAME_FOR_LINK_TABLE = "peptide_1_id";
	private final String SQL_CROSSLINK_PEPTIDE_ID_2_FIELD_NAME_FOR_LINK_TABLE = "peptide_2_id";

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
	public NumPeptidesPSMsForProteinCriteriaResult getNumPeptidesPSMsForCrosslink( 
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein_1,
			int nrseqId_protein_2,
			int position_protein_1,
			int position_protein_2,
			int fastaFileDatabaseId

			) throws Exception {
		
		
		

		int[] linkTableParams = {
				
				nrseqId_protein_1,
				nrseqId_protein_2,
				position_protein_1,
				position_protein_2
		};
				
		return getCount( 
				searchId, 
				searcherCutoffValuesSearchLevel, 
				linkTableParams, 
				XLinkUtils.CROSS_TYPE_STRING, 
				SQL_CROSSLINK_TABLE_NAME, 
				SQL_CROSSLINK_WHERE_FOR_LINK_TABLE,
				SQL_CROSSLINK_PEPTIDE_ID_1_FIELD_NAME_FOR_LINK_TABLE,
				SQL_CROSSLINK_PEPTIDE_ID_2_FIELD_NAME_FOR_LINK_TABLE,
				fastaFileDatabaseId ) ;
		
	}

	////////////////////////////////////////////////////////////////////
	
	//////////   LOOPLINK
	
	///////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////
	

	private final String SQL_LOOPLINK_TABLE_NAME = "looplink";

	private final String SQL_LOOPLINK_WHERE_FOR_LINK_TABLE = 

	"  " + SQL_LINK_TABLE_ALIAS + ".nrseq_id = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".protein_position_1 = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".protein_position_2 = ? ";
	

	private final String SQL_LOOPLINK_PEPTIDE_ID_1_FIELD_NAME_FOR_LINK_TABLE = "peptide_id";
	private final String SQL_LOOPLINK_PEPTIDE_ID_2_FIELD_NAME_FOR_LINK_TABLE = null;

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
	public NumPeptidesPSMsForProteinCriteriaResult getNumPeptidesPSMsForLooplink( 

			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein,
			int protein_position_1,
			int protein_position_2,
			int fastaFileDatabaseId


			 ) throws Exception {
		
		int[] linkTableParams = {
				
				nrseqId_protein,
				protein_position_1,
				protein_position_2
		};
		

		return getCount( 
				searchId, 
				searcherCutoffValuesSearchLevel, 
				linkTableParams, 
				XLinkUtils.LOOP_TYPE_STRING, 
				SQL_LOOPLINK_TABLE_NAME, 
				SQL_LOOPLINK_WHERE_FOR_LINK_TABLE,
				SQL_LOOPLINK_PEPTIDE_ID_1_FIELD_NAME_FOR_LINK_TABLE,
				SQL_LOOPLINK_PEPTIDE_ID_2_FIELD_NAME_FOR_LINK_TABLE,
				fastaFileDatabaseId ) ;
		
	}

	
	
	////////////////////////////////////////////////////////
	
	/////////////    MONOLINK

	///////////////////////////////////////////////////



	////////////////////////////////////////////////////////////////////////////////////
	

	private final String SQL_MONOLINK_TABLE_NAME = "monolink";

	private final String SQL_MONOLINK_WHERE_FOR_LINK_TABLE = 

	"  " + SQL_LINK_TABLE_ALIAS + ".nrseq_id = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".protein_position = ? ";


	private final String SQL_MONOLINK_PEPTIDE_ID_1_FIELD_NAME_FOR_LINK_TABLE = "peptide_id";
	private final String SQL_MONOLINK_PEPTIDE_ID_2_FIELD_NAME_FOR_LINK_TABLE = null;

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
	public NumPeptidesPSMsForProteinCriteriaResult getNumPeptidesPSMsForMonolink( 

			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein,
			int protein_position,
			int fastaFileDatabaseId


			 ) throws Exception {


		int[] linkTableParams = {
				
				nrseqId_protein,
				protein_position
		};
		

		return getCount( 
				searchId, 
				searcherCutoffValuesSearchLevel, 
				linkTableParams, 
				null /* link type */, 
				SQL_MONOLINK_TABLE_NAME, 
				SQL_MONOLINK_WHERE_FOR_LINK_TABLE,
				SQL_MONOLINK_PEPTIDE_ID_1_FIELD_NAME_FOR_LINK_TABLE,
				SQL_MONOLINK_PEPTIDE_ID_2_FIELD_NAME_FOR_LINK_TABLE,
				fastaFileDatabaseId ) ;
		

	}

	
	//////////////////////////////////////////////////
	
	//////////////      DIMER

	private final String SQL_DIMER_TABLE_NAME = "dimer";

	private final String SQL_DIMER_WHERE_FOR_LINK_TABLE = 

	"  " + SQL_LINK_TABLE_ALIAS + ".nrseq_id_1 = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".nrseq_id_2 = ? ";

	private final String SQL_DIMER_PEPTIDE_ID_1_FIELD_NAME_FOR_LINK_TABLE = "peptide_1_id";
	private final String SQL_DIMER_PEPTIDE_ID_2_FIELD_NAME_FOR_LINK_TABLE = "peptide_2_id";

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
	public NumPeptidesPSMsForProteinCriteriaResult getNumPeptidesPSMsForDimer( 
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein_1,
			int nrseqId_protein_2,
			int fastaFileDatabaseId

			) throws Exception {
		
		int[] linkTableParams = {
				
				nrseqId_protein_1,
				nrseqId_protein_2
		};

		return getCount( 
				searchId, 
				searcherCutoffValuesSearchLevel, 
				linkTableParams, 
				XLinkUtils.DIMER_TYPE_STRING, 
				SQL_DIMER_TABLE_NAME, 
				SQL_DIMER_WHERE_FOR_LINK_TABLE,
				SQL_DIMER_PEPTIDE_ID_1_FIELD_NAME_FOR_LINK_TABLE,
				SQL_DIMER_PEPTIDE_ID_2_FIELD_NAME_FOR_LINK_TABLE,
				fastaFileDatabaseId ) ;
	}


	
	////////////////////////////////////////////////////////
	
	/////////////    UNLINKED

	///////////////////////////////////////////////////


	private final String SQL_UNLINKED_TABLE_NAME = "unlinked";

	private final String SQL_UNLINKED_WHERE_FOR_LINK_TABLE = 

	"  " + SQL_LINK_TABLE_ALIAS + ".nrseq_id = ? ";


	private final String SQL_UNLINKED_PEPTIDE_ID_1_FIELD_NAME_FOR_LINK_TABLE = "peptide_id";
	private final String SQL_UNLINKED_PEPTIDE_ID_2_FIELD_NAME_FOR_LINK_TABLE = null;

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
	public NumPeptidesPSMsForProteinCriteriaResult getNumPeptidesPSMsForUnlinked( 

			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int nrseqId_protein,
			int fastaFileDatabaseId

			 ) throws Exception {
		
		int[] linkTableParams = {
				
				nrseqId_protein
		};

		return getCount( 
				searchId, 
				searcherCutoffValuesSearchLevel, 
				linkTableParams, 
				XLinkUtils.UNLINKED_TYPE_STRING, 
				SQL_UNLINKED_TABLE_NAME, 
				SQL_UNLINKED_WHERE_FOR_LINK_TABLE,
				SQL_UNLINKED_PEPTIDE_ID_1_FIELD_NAME_FOR_LINK_TABLE,
				SQL_UNLINKED_PEPTIDE_ID_2_FIELD_NAME_FOR_LINK_TABLE,
				fastaFileDatabaseId ) ;
	}



	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	
	
	private NumPeptidesPSMsForProteinCriteriaResult getCount( 
			
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int[] linkTableParams,
			String linkTypeString,  // null for monolink
			String linkTableName,
			String sqlWhereForlinkTable,
			String peptide_1_fieldName,
			String peptide_2_fieldName, //  may be null
			int fastaFileDatabaseId ) throws Exception {

		if ( linkTableParams == null ) {
			
			String msg = "linkTableParams cannot be null";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		int peptideCount = 0;
		int uniquePeptideCount = 0;
		int psmCount = 0;

		ParamsPreProcesingResult paramsPreProcesingResult  = paramsPreProcesing( searcherCutoffValuesSearchLevel );


		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		//////////////////////

		/////   Start building the SQL

		String sql = null;


		sql = getSQL( 
				paramsPreProcesingResult, 
				linkTypeString, 
				linkTableName, 
				peptide_1_fieldName,
				peptide_2_fieldName, //  may be null
				sqlWhereForlinkTable );
		
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );

			int paramCounter = 0;

			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );

			for ( int linkTableParam : linkTableParams ) {

				paramCounter++;
				pstmt.setInt( paramCounter,  linkTableParam );
			}

			// Process PSM And Peptide Cutoffs for WHERE

			//  Must be last

			setCutoffParams( searchId, 
					paramsPreProcesingResult,
					pstmt, 
					paramCounter );

			rs = pstmt.executeQuery();
			
			int prevReportedPeptideId = -999;

			while( rs.next() ) {

				int reportedPeptideId = rs.getInt( "reported_peptide_id" );
				
				if ( reportedPeptideId == prevReportedPeptideId ) {
					
					String msg = "reported peptide already processed: " + reportedPeptideId
							+ ", This reported peptide id is in the DB with different peptide ids";
					log.error( msg );
					throw new ProxlBaseDataException(msg);
				}
				
				int psmCountForReportedPeptideSearchId = 
						PsmCountForSearchIdReportedPeptideIdSearcher.getInstance()
						.getPsmCountForSearchIdReportedPeptideId( reportedPeptideId, searchId, searcherCutoffValuesSearchLevel );
				
				if ( psmCountForReportedPeptideSearchId > 0 ) {

					peptideCount++;
					psmCount += psmCountForReportedPeptideSearchId;
					
					
					
					//  Get if reported peptide is unique


					boolean reportedPeptideIsUnique = false;
					
					boolean peptide_1_IsUnique = false;
					boolean peptide_2_IsUnique = false;

					int peptide_1_Id = rs.getInt( peptide_1_fieldName );

					if ( isPeptideUnique( peptide_1_Id, fastaFileDatabaseId ) ) {
						
						peptide_1_IsUnique = true;
					}
					
					if ( peptide_2_fieldName == null ) {
						
						if ( peptide_1_IsUnique ) {
							
							reportedPeptideIsUnique = true;
						}
						
					} else {
						

						int peptide_2_Id = rs.getInt( peptide_2_fieldName );
						
						if ( peptide_2_Id == peptide_1_Id ) {
							
							 peptide_2_IsUnique = peptide_1_IsUnique;
							 
						} else {

							if ( isPeptideUnique( peptide_2_Id, fastaFileDatabaseId ) ) {

								peptide_2_IsUnique = true;
							}
						}
						
						if ( peptide_1_IsUnique && peptide_2_IsUnique ) {
							
							reportedPeptideIsUnique = true;
						}
					}

					if ( reportedPeptideIsUnique ) {
					
						uniquePeptideCount++;
					}					
										
				}
				
				prevReportedPeptideId = reportedPeptideId;
			}

		} catch ( Exception e ) {

			String msg = "Exception in internal getCount( ... ): sql: " + sql;

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

		NumPeptidesPSMsForProteinCriteriaResult result = new NumPeptidesPSMsForProteinCriteriaResult();
		
		result.setNumPeptides(peptideCount);
		result.setNumUniquePeptides(uniquePeptideCount);
		result.setNumPSMs(psmCount);

		return result;
		
	}
	
	/**
	 * @param peptideId
	 * @param fastaFileDatabaseId
	 * @return
	 * @throws Exception 
	 */
	private boolean isPeptideUnique( int peptideId, int fastaFileDatabaseId ) throws Exception {
		
		return PeptideUniqueSearcher.getInstance().isPeptideUniqueForDatabaseId( peptideId, fastaFileDatabaseId );
	}

	

	
	private class ParamsPreProcesingResult {

		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList;
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList;

		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		boolean onlyDefaultPsmCutoffs = true;
		
	}
	
	

	/**
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	private ParamsPreProcesingResult paramsPreProcesing( SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {
		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

		////////////
		
		//  All cutoffs are default?
		
		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		
		boolean onlyDefaultPsmCutoffs = true;
		
		

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
		
		for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {
			
			if ( ! item.annotationValueMatchesDefault() ) {
				
				onlyDefaultPsmCutoffs = false;
				break;
			}
		}
		
		ParamsPreProcesingResult paramsPreProcesingResult = new ParamsPreProcesingResult();
		
		paramsPreProcesingResult.peptideCutoffValuesList = peptideCutoffValuesList;
		paramsPreProcesingResult.psmCutoffValuesList = psmCutoffValuesList;

		paramsPreProcesingResult.defaultPeptideCutoffs = defaultPeptideCutoffs;
		paramsPreProcesingResult.onlyDefaultPsmCutoffs = onlyDefaultPsmCutoffs;
		
		return paramsPreProcesingResult;
	}
	

	
	
	
	/**
	 * 
	 * 
	 * @param paramsPreProcesingResult
	 * @param linkTypeString
	 * @param linkTableName
	 * @param sqlWhereForlinkTable
	 * @return
	 * @throws Exception
	 */
	private String getSQL(
			
			ParamsPreProcesingResult paramsPreProcesingResult,
			
			String linkTypeString, // null for monolink
			
			String linkTableName, 

			String peptide_1_fieldName,
			String peptide_2_fieldName, //  may be null

			String sqlWhereForlinkTable
			 ) throws Exception {
		
		
		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = paramsPreProcesingResult.peptideCutoffValuesList;
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = paramsPreProcesingResult.psmCutoffValuesList;
		Yes_No__NOT_APPLICABLE_Enum defaultPeptideCutoffs = paramsPreProcesingResult.defaultPeptideCutoffs;
		boolean onlyDefaultPsmCutoffs = paramsPreProcesingResult.onlyDefaultPsmCutoffs;
		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		sqlSB.append( "SELECT distinct unified_rp__rep_pept__search__generic_lookup.reported_peptide_id AS reported_peptide_id "  );
		
		sqlSB.append( " , " );
		sqlSB.append( SQL_LINK_TABLE_ALIAS );
		sqlSB.append( "." );
		sqlSB.append( peptide_1_fieldName );

		if ( peptide_2_fieldName != null ) {

			sqlSB.append( " , " );
			sqlSB.append( SQL_LINK_TABLE_ALIAS );
			sqlSB.append( "." );
			sqlSB.append( peptide_2_fieldName );
		}

		sqlSB.append( "  FROM unified_rp__rep_pept__search__generic_lookup  " );
		sqlSB.append( "INNER JOIN " );
		sqlSB.append( linkTableName );
		sqlSB.append( " AS " );
		sqlSB.append( SQL_LINK_TABLE_ALIAS );
		sqlSB.append( " ON unified_rp__rep_pept__search__generic_lookup.sample_psm_id = " );
		sqlSB.append( SQL_LINK_TABLE_ALIAS );
		sqlSB.append( ".psm_id " );
		
		

		{

			if ( ! onlyDefaultPsmCutoffs ) { //  Can only use this if psm table or a lookup has a flag for "met default cutoffs"
				

				//  Add inner join for each PSM cutoff

				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );

					sqlSB.append( " unified_rp__rep_pept__search__best_psm_value_generic_lookup AS " );
					
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );
				}

			}
		
		}
		
		
		{
			
			if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {

				//  Non-Default PSM cutoffs so have to query on the cutoffs

				//  Add inner join for each Peptide cutoff

				for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );

					sqlSB.append( " srch__rep_pept__annotation AS srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );

				}
			}
		}
		

		///////////
		
		sqlSB.append( " WHERE unified_rp__rep_pept__search__generic_lookup.search_id = ? AND " );
		
		sqlSB.append( sqlWhereForlinkTable );
		
		
		//////////
		

		// Process PSM Cutoffs for WHERE

		{

			
			if ( onlyDefaultPsmCutoffs ) {
				
				//   Only Default PSM Cutoffs chosen so criteria simply the Peptides where the PSM count for the default cutoffs is > zero
				

				sqlSB.append( " AND " );


				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff > 0 " );

				
			} else {

				
				//  Non-Default PSM cutoffs so have to query on the cutoffs

				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {



					AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

					counter++;

					sqlSB.append( " AND " );

					sqlSB.append( " ( " );


					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_psm_value_for_ann_type_id " );

					if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() 
							== FilterDirectionType.ABOVE ) {

						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

					} else {

						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

					}

					sqlSB.append( " ? " );

					sqlSB.append( " ) " );
				}
			}
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
				
				if ( linkTypeString != null ) {

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.link_type = '" );
					sqlSB.append( linkTypeString );
					sqlSB.append( "' " );
					
//					if ( log.isInfoEnabled() ) {
//						log.info( "Using unified_rp__rep_pept__search__generic_lookup.link_type = '" + linkTypeString + "' ");
//					}
				}

				
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
		
		sqlSB.append( " ORDER BY reported_peptide_id " );
		
		
		
		String sql = sqlSB.toString();
		

//		if ( linkTypeString != null ) {
//
//			
//			if ( log.isInfoEnabled() ) {
//				log.info( "Using unified_rp__rep_pept__search__generic_lookup.link_type = '" + linkTypeString + "'. "
//						+ " SQL:  " + sql );
//			}
//		}
		
		
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
			ParamsPreProcesingResult paramsPreProcesingResult,
			PreparedStatement pstmt, 
			int paramCounter) throws SQLException {

		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = paramsPreProcesingResult.peptideCutoffValuesList;
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = paramsPreProcesingResult.psmCutoffValuesList;
		Yes_No__NOT_APPLICABLE_Enum defaultPeptideCutoffs = paramsPreProcesingResult.defaultPeptideCutoffs;
		boolean onlyDefaultPsmCutoffs = paramsPreProcesingResult.onlyDefaultPsmCutoffs;

		
		// Process PSM Cutoffs for WHERE


		{
			
			if ( ! onlyDefaultPsmCutoffs ) {
				
				//  PSM Cutoffs are not the default 
				

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {

					AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

					paramCounter++;
					pstmt.setInt( paramCounter, searchId );
					
					paramCounter++;
					pstmt.setInt( paramCounter, srchPgmFilterablePsmAnnotationTypeDTO.getId() );

					paramCounter++;
					pstmt.setDouble( paramCounter, searcherCutoffValuesPsmAnnotationLevel.getAnnotationCutoffValue() );
				}
			}
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
