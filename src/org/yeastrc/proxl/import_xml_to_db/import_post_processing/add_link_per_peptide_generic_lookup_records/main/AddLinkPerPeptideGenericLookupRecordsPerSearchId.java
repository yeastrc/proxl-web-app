package org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_link_per_peptide_generic_lookup_records.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_CrossLinkRepPeptSearchGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_DimerRepPeptSearchGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_LoopLinkRepPeptSearchGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_MonoLinkRepPeptSearchGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_UnlinkedRepPeptSearchGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.xlink.dao.UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.CrosslinkRepPeptSearchGenericLookupDTO;
import org.yeastrc.xlink.dto.DimerRepPeptSearchGenericLookupDTO;
import org.yeastrc.xlink.dto.LooplinkRepPeptSearchGenericLookupDTO;
import org.yeastrc.xlink.dto.MonolinkRepPeptSearchGenericLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO;
import org.yeastrc.xlink.dto.UnlinkedRepPeptSearchGenericLookupDTO;
import org.yeastrc.xlink.utils.XLinkUtils;


public class AddLinkPerPeptideGenericLookupRecordsPerSearchId {


	private static final Logger log = Logger.getLogger(AddLinkPerPeptideGenericLookupRecordsPerSearchId.class);

	// private constructor
	private AddLinkPerPeptideGenericLookupRecordsPerSearchId() { }
	
	public static AddLinkPerPeptideGenericLookupRecordsPerSearchId getInstance() { 
		return new AddLinkPerPeptideGenericLookupRecordsPerSearchId(); 
	}
	

	private static final String SQL_COUNT = 
			"SELECT COUNT(*) AS count "
			+ " FROM unified_rp__rep_pept__search__generic_lookup " 
					
			+ " WHERE  search_id = ? ";
			

	private static final String SQL_MAIN = 
			"SELECT *"
			+ " FROM unified_rp__rep_pept__search__generic_lookup " 
					
			+ " WHERE  search_id = ? ";
			


	private static final String SQL_MONOLINK = 
			"SELECT DISTINCT monolink.nrseq_id , monolink.protein_position "
			+ " FROM monolink INNER JOIN psm ON monolink.psm_id = psm.id " 
					
			+ " WHERE  psm.search_id = ? AND psm.reported_peptide_id = ? ";
			
	

	/**
	 * @param searchId
	 * @throws Exception
	 */
	public void addLinkPerPeptideGenericLookupRecordsPerSearchId( int searchId ) throws Exception {
		
		if ( log.isInfoEnabled() ) {

			log.info( "Starting addLinkPerPeptideGenericLookupRecordsPerSearchId for search id: " + searchId );
		}

	    ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();

	    

		UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO =
				UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO.getInstance();
				
		DB_Insert_CrossLinkRepPeptSearchGenericLookupDAO db_Insert_CrossLinkRepPeptSearchGenericLookupDAO =
				DB_Insert_CrossLinkRepPeptSearchGenericLookupDAO.getInstance();

		DB_Insert_LoopLinkRepPeptSearchGenericLookupDAO db_Insert_LoopLinkRepPeptSearchGenericLookupDAO =
				DB_Insert_LoopLinkRepPeptSearchGenericLookupDAO.getInstance();
		
		DB_Insert_DimerRepPeptSearchGenericLookupDAO db_Insert_DimerRepPeptSearchGenericLookupDAO =
				DB_Insert_DimerRepPeptSearchGenericLookupDAO.getInstance();


		DB_Insert_UnlinkedRepPeptSearchGenericLookupDAO db_Insert_UnlinkedRepPeptSearchGenericLookupDAO =
				DB_Insert_UnlinkedRepPeptSearchGenericLookupDAO.getInstance();

		DB_Insert_MonoLinkRepPeptSearchGenericLookupDAO db_Insert_MonoLinkRepPeptSearchGenericLookupDAO =
				DB_Insert_MonoLinkRepPeptSearchGenericLookupDAO.getInstance();
		
	    
		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = SQL_MAIN;
		
		try {

			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			int totalRecordCount = 0;

			if ( log.isInfoEnabled() ) {


				pstmt = dbConnection.prepareStatement( SQL_COUNT );

				pstmt.setInt( 1, searchId );

				rs = pstmt.executeQuery();

				rs.next();

				totalRecordCount = rs.getInt( "count" );

				rs.close();
				rs = null;
				pstmt.close();
				pstmt = null;
			

				log.info( "addLinkPerPeptideGenericLookupRecordsPerSearchId:  Record count to process: " + totalRecordCount );
			}
			
			pstmt = dbConnection.prepareStatement( sql );
			
			pstmt.setInt( 1, searchId );
			
			rs = pstmt.executeQuery();
			
			int recordCount = 0;
			
			
			while ( rs.next() ) {
				
				recordCount++;
				
				
				
				UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO lookupItem =
						unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO.populateFromResultSet( rs );
				
				if ( lookupItem.getLinkType() == XLinkUtils.TYPE_CROSSLINK ) {
					
					processCrosslink( searchId, dbConnection, db_Insert_CrossLinkRepPeptSearchGenericLookupDAO, lookupItem );

				} else if ( lookupItem.getLinkType() == XLinkUtils.TYPE_LOOPLINK ) {

					processLooplink( searchId, dbConnection, db_Insert_LoopLinkRepPeptSearchGenericLookupDAO, lookupItem );

				} else if ( lookupItem.getLinkType() == XLinkUtils.TYPE_DIMER ) {

					processDimer( searchId, dbConnection, db_Insert_DimerRepPeptSearchGenericLookupDAO, lookupItem );

				} else if ( lookupItem.getLinkType() == XLinkUtils.TYPE_UNLINKED ) {

					processUnlinked( searchId, dbConnection, db_Insert_UnlinkedRepPeptSearchGenericLookupDAO, lookupItem );

				} else {
					
					String linkTypeSTring = XLinkUtils.getTypeString( lookupItem.getLinkType() );
					
					String msg = "Unknown Link Type: " + linkTypeSTring;
					log.error( msg );
					
					throw new ProxlImporterDataException( msg );
				}
				
				processMonolink( searchId, db_Insert_MonoLinkRepPeptSearchGenericLookupDAO, dbConnection, lookupItem );
				

				if ( log.isInfoEnabled() ) {
					
					if ( recordCount % 5000 == 0 ) {
					
						log.info( "addLinkPerPeptideGenericLookupRecordsPerSearchId:  processed " + recordCount + " of " + totalRecordCount );
					}
				}

			}
			
		} catch ( Exception e ) {
			
			String msg = "addLinkPerPeptideGenericLookupRecordsPerSearchId(), sql: " + sql;
			
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
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
		

	    ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();

		if ( log.isInfoEnabled() ) {

			log.info( "Finished addLinkPerPeptideGenericLookupRecordsPerSearchId for search id: " + searchId );
		}
		
	}

	
	
	//  SELECT DISTINCT to remove duplicates that exist in the crosslink table
	
	private static final String SQL_CROSSLINK = 
			"SELECT DISTINCT nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position FROM crosslink WHERE psm_id = ?";

//	private static final String SQL_CROSSLINK_COUNT = 
//			"SELECT COUNT( DISTINCT nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position ) AS count FROM crosslink WHERE psm_id = ?";
		
	
	/**
	 * @param searchId
	 * @param dbConnection
	 * @param db_Insert_CrossLinkRepPeptSearchGenericLookupDAO
	 * @param lookupItem
	 * @throws Exception
	 * @throws ProxlImporterDataException
	 */
	private void processCrosslink(
			
			int searchId,
			
			Connection dbConnection,
			
			DB_Insert_CrossLinkRepPeptSearchGenericLookupDAO db_Insert_CrossLinkRepPeptSearchGenericLookupDAO,
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO lookupItem)
			throws Exception, ProxlImporterDataException {
		
		
//		int totalRecordCount = -1;
				
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQL_CROSSLINK;
		
		int foundRecordCount = 0;
		
		try {

			//  Get a count of the records to process
//			if ( log.isInfoEnabled() ) {
//
//				pstmt = dbConnection.prepareStatement( SQL_CROSSLINK_COUNT );
//
//				pstmt.setInt( 1, lookupItem.getSamplePsmId() );
//
//				rs = pstmt.executeQuery();
//
//				rs.next();
//
//				totalRecordCount = rs.getInt( "count" );
//
//				rs.close();
//				rs = null;
//				pstmt.close();
//				pstmt = null;
//			
//
//				log.info( "processCrosslink: Reported Peptide Id: \t" + lookupItem.getReportedPeptideId()
//						+ "\t, Record count to process: \t" + totalRecordCount );
//			}
			
			pstmt = dbConnection.prepareStatement( sql );
			
			pstmt.setInt( 1, lookupItem.getSamplePsmId() );
			
			rs = pstmt.executeQuery();
			
			
			while ( rs.next() ) {
				
				foundRecordCount++;

				CrosslinkRepPeptSearchGenericLookupDTO insertItem = new CrosslinkRepPeptSearchGenericLookupDTO();
				
				insertItem.setSearchId( searchId );
				insertItem.setReportedPeptideId( lookupItem.getReportedPeptideId() );
				insertItem.setProteinId_1( rs.getInt( "nrseq_id_1" ) );
				insertItem.setProteinId_2( rs.getInt( "nrseq_id_2" ) );
				insertItem.setProtein_1_position( rs.getInt( "protein_1_position" ) );
				insertItem.setProtein_2_position( rs.getInt( "protein_2_position" ) );

				insertItem.setAllRelatedPeptidesUniqueForSearch( lookupItem.isAllRelatedPeptidesUniqueForSearch() );
				insertItem.setPsmNumAtDefaultCutoff( lookupItem.getPsmNumAtDefaultCutoff() );
				insertItem.setPeptideMeetsDefaultCutoffs( lookupItem.getPeptideMeetsDefaultCutoffs() );

				db_Insert_CrossLinkRepPeptSearchGenericLookupDAO.saveToDatabase( insertItem );


				if ( log.isInfoEnabled() ) {
					
					if ( foundRecordCount % 100000 == 0 ) {
						
						String logMsg =  "processCrosslink:  processed " + foundRecordCount + ",  Reported Peptide Id: \t" + lookupItem.getReportedPeptideId();
					
//						if ( totalRecordCount > 0 ) {
//							
//							logMsg += " of " + totalRecordCount;
//						}

						log.info( logMsg );
					}
				}


			}

		} catch ( Exception e ) {
			
			String msg = "processCrosslink(), sql: " + sql;
			
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

			//  Skip Close DB Connection, done elsewhere
		}
		
		if ( foundRecordCount == 0 ) {
			
			String msg = "no crosslink records found for psmId: " + lookupItem.getSamplePsmId();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		
		if ( log.isInfoEnabled() ) {

			if ( foundRecordCount > 10000 ) {
				log.info( "processCrosslink: > 10,000 crosslink records processed. Reported Peptide Id: \t" + lookupItem.getReportedPeptideId()
						+ "\t, Record count processed: \t" + foundRecordCount );
			}
		}		
	}

	
	
	//  SELECT DISTINCT to remove duplicates that exist in the table
	
	private static final String SQL_LOOPLINK =   
			"SELECT DISTINCT nrseq_id, protein_position_1, protein_position_2 FROM looplink WHERE psm_id = ?";

	/**
	 * @param searchId
	 * @param dbConnection
	 * @param db_Insert_LoopLinkRepPeptSearchGenericLookupDAO
	 * @param lookupItem
	 * @throws Exception
	 * @throws ProxlImporterDataException
	 */
	private void processLooplink(
			int searchId,
			
			Connection dbConnection,
			
			DB_Insert_LoopLinkRepPeptSearchGenericLookupDAO db_Insert_LoopLinkRepPeptSearchGenericLookupDAO,
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO lookupItem)
	
			throws Exception, ProxlImporterDataException {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQL_LOOPLINK;
		
		int foundRecordCount = 0;
		
		try {
			
			pstmt = dbConnection.prepareStatement( sql );
			
			pstmt.setInt( 1, lookupItem.getSamplePsmId() );
			
			rs = pstmt.executeQuery();

			while ( rs.next() ) {

				foundRecordCount++;
				
				LooplinkRepPeptSearchGenericLookupDTO insertItem = new LooplinkRepPeptSearchGenericLookupDTO();

				insertItem.setSearchId( searchId );
				insertItem.setReportedPeptideId( lookupItem.getReportedPeptideId() );
				
				insertItem.setProteinId( rs.getInt( "nrseq_id" ) );
				insertItem.setProteinPosition_1( rs.getInt( "protein_position_1" ) );
				insertItem.setProteinPosition_2( rs.getInt( "protein_position_2" ) );

				insertItem.setAllRelatedPeptidesUniqueForSearch( lookupItem.isAllRelatedPeptidesUniqueForSearch() );
				insertItem.setPsmNumAtDefaultCutoff( lookupItem.getPsmNumAtDefaultCutoff() );
				insertItem.setPeptideMeetsDefaultCutoffs( lookupItem.getPeptideMeetsDefaultCutoffs() );

				if ( log.isInfoEnabled() ) {
					
					if ( foundRecordCount % 100000 == 0 ) {
						
						String logMsg =  "processLooplink:  processed " + foundRecordCount + ",  Reported Peptide Id: \t" + lookupItem.getReportedPeptideId();
					
//						if ( totalRecordCount > 0 ) {
//							
//							logMsg += " of " + totalRecordCount;
//						}

						log.info( logMsg );
					}
				}
				
				db_Insert_LoopLinkRepPeptSearchGenericLookupDAO.saveToDatabase( insertItem );
			}

		} catch ( Exception e ) {
			
			String msg = "processLooplink(), sql: " + sql;
			
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
			
			//  Skip Close DB Connection, done elsewhere
		}

		if ( foundRecordCount == 0 ) {

			String msg = "no looplink records found for psmId: " + lookupItem.getSamplePsmId();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}

		if ( foundRecordCount > 10000 ) {
			log.info( "processLooplink: > 10,000 crosslink records processed. Reported Peptide Id: \t" + lookupItem.getReportedPeptideId()
					+ "\t, Record count processed: \t" + foundRecordCount );
		}
	}
	
	
	

	
	//  SELECT DISTINCT to remove duplicates that exist in the dimer table
	
	private static final String SQL_DIMER = 
			"SELECT DISTINCT nrseq_id_1, nrseq_id_2 FROM dimer WHERE psm_id = ?";

	/**
	 * @param searchId
	 * @param dbConnection
	 * @param db_Insert_DimerRepPeptSearchGenericLookupDAO
	 * @param lookupItem
	 * @throws Exception
	 * @throws ProxlImporterDataException
	 */
	private void processDimer(
			
			int searchId,
			Connection dbConnection,
			DB_Insert_DimerRepPeptSearchGenericLookupDAO db_Insert_DimerRepPeptSearchGenericLookupDAO,
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO lookupItem)
			throws Exception, ProxlImporterDataException {
		

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQL_DIMER;
		
		int foundRecordCount = 0;
		
		try {

			pstmt = dbConnection.prepareStatement( sql );
			
			pstmt.setInt( 1, lookupItem.getSamplePsmId() );
			
			rs = pstmt.executeQuery();
			
			
			while ( rs.next() ) {
				
				foundRecordCount++;

				DimerRepPeptSearchGenericLookupDTO insertItem = new DimerRepPeptSearchGenericLookupDTO();

				insertItem.setSearchId( searchId );
				insertItem.setReportedPeptideId( lookupItem.getReportedPeptideId() );
				insertItem.setProteinId_1( rs.getInt( "nrseq_id_1" ) );
				insertItem.setProteinId_2( rs.getInt( "nrseq_id_2" ) );

				insertItem.setAllRelatedPeptidesUniqueForSearch( lookupItem.isAllRelatedPeptidesUniqueForSearch() );
				insertItem.setPsmNumAtDefaultCutoff( lookupItem.getPsmNumAtDefaultCutoff() );
				insertItem.setPeptideMeetsDefaultCutoffs( lookupItem.getPeptideMeetsDefaultCutoffs() );

				db_Insert_DimerRepPeptSearchGenericLookupDAO.saveToDatabase( insertItem );

				if ( log.isInfoEnabled() ) {
					
					if ( foundRecordCount % 100000 == 0 ) {
						
						String logMsg =  "processDimer:  processed " + foundRecordCount + ",  Reported Peptide Id: \t" + lookupItem.getReportedPeptideId();
					
//						if ( totalRecordCount > 0 ) {
//							
//							logMsg += " of " + totalRecordCount;
//						}

						log.info( logMsg );
					}
				}
			}

		} catch ( Exception e ) {
			
			String msg = "processDimer(), sql: " + sql;
			
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

			//  Skip Close DB Connection, done elsewhere
		}
		
		if ( foundRecordCount == 0 ) {
			
			String msg = "no dimer records found for psmId: " + lookupItem.getSamplePsmId();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}

		if ( foundRecordCount > 10000 ) {
			log.info( "processDimer: > 10,000 crosslink records processed. Reported Peptide Id: \t" + lookupItem.getReportedPeptideId()
					+ "\t, Record count processed: \t" + foundRecordCount );
		}

	}
	

	//  SELECT DISTINCT to remove duplicates that exist in the table
	
	private static final String SQL_UNLINKED =   
			"SELECT DISTINCT nrseq_id FROM unlinked WHERE psm_id = ?";

	
	private void processUnlinked(
			int searchId,
			
			Connection dbConnection,
			
			DB_Insert_UnlinkedRepPeptSearchGenericLookupDAO db_Insert_UnlinkedRepPeptSearchGenericLookupDAO,
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO lookupItem)
			throws Exception, ProxlImporterDataException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQL_UNLINKED;
		
		int foundRecordCount = 0;
		
		try {
			
			pstmt = dbConnection.prepareStatement( sql );
			
			pstmt.setInt( 1, lookupItem.getSamplePsmId() );
			
			rs = pstmt.executeQuery();

			while ( rs.next() ) {

				foundRecordCount++;

				UnlinkedRepPeptSearchGenericLookupDTO insertItem = new UnlinkedRepPeptSearchGenericLookupDTO();

				insertItem.setSearchId( searchId );
				insertItem.setReportedPeptideId( lookupItem.getReportedPeptideId() );
				insertItem.setProteinId( rs.getInt( "nrseq_id" ) );

				insertItem.setAllRelatedPeptidesUniqueForSearch( lookupItem.isAllRelatedPeptidesUniqueForSearch() );
				insertItem.setPsmNumAtDefaultCutoff( lookupItem.getPsmNumAtDefaultCutoff() );
				insertItem.setPeptideMeetsDefaultCutoffs( lookupItem.getPeptideMeetsDefaultCutoffs() );

				db_Insert_UnlinkedRepPeptSearchGenericLookupDAO.saveToDatabase( insertItem );
				

				if ( log.isInfoEnabled() ) {
					
					if ( foundRecordCount % 100000 == 0 ) {
						
						String logMsg =  "processUnlinked:  processed " + foundRecordCount + ",  Reported Peptide Id: \t" + lookupItem.getReportedPeptideId();
					
//						if ( totalRecordCount > 0 ) {
//							
//							logMsg += " of " + totalRecordCount;
//						}

						log.info( logMsg );
					}
				}
			}

		} catch ( Exception e ) {
			
			String msg = "processUnlinked(), sql: " + sql;
			
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
			
			//  Skip Close DB Connection, done elsewhere
		}

		if ( foundRecordCount == 0 ) {

			String msg = "no unlinked records found for psmId: " + lookupItem.getSamplePsmId();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}

		if ( foundRecordCount > 10000 ) {
			log.info( "processUnlinked: > 10,000 crosslink records processed. Reported Peptide Id: \t" + lookupItem.getReportedPeptideId()
					+ "\t, Record count processed: \t" + foundRecordCount );
		}

	}

	private void processMonolink(
			int searchId,
			DB_Insert_MonoLinkRepPeptSearchGenericLookupDAO db_Insert_MonoLinkRepPeptSearchGenericLookupDAO,
			Connection conn,
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO lookupItem)
			throws SQLException, Exception {
		PreparedStatement pstmtMonolink = null;
		ResultSet rsMonolink = null;
		
		final String sqlMonolink = SQL_MONOLINK;
		

		int foundRecordCount = 0;
		
		try {
			
			
			pstmtMonolink = conn.prepareStatement( sqlMonolink );
			
			pstmtMonolink.setInt( 1, searchId );
			pstmtMonolink.setInt( 2, lookupItem.getReportedPeptideId() );
			
			rsMonolink = pstmtMonolink.executeQuery();

			while ( rsMonolink.next() ) {

				foundRecordCount++;
				
				int nrseqId = rsMonolink.getInt( "nrseq_id" );
				int proteinPosition = rsMonolink.getInt( "protein_position" );
				
				MonolinkRepPeptSearchGenericLookupDTO insertItem = new MonolinkRepPeptSearchGenericLookupDTO();

				insertItem.setSearchId( searchId );
				insertItem.setReportedPeptideId( lookupItem.getReportedPeptideId() );
				insertItem.setProteinId( nrseqId );
				insertItem.setProteinPosition( proteinPosition );

				insertItem.setAllRelatedPeptidesUniqueForSearch( lookupItem.isAllRelatedPeptidesUniqueForSearch() );
				insertItem.setPsmNumAtDefaultCutoff( lookupItem.getPsmNumAtDefaultCutoff() );
				insertItem.setPeptideMeetsDefaultCutoffs( lookupItem.getPeptideMeetsDefaultCutoffs() );

				db_Insert_MonoLinkRepPeptSearchGenericLookupDAO.saveToDatabase( insertItem );
				


				if ( log.isInfoEnabled() ) {
					
					if ( foundRecordCount % 100000 == 0 ) {
						
						String logMsg =  "processUnlinked:  processed " + foundRecordCount + ",  Reported Peptide Id: \t" + lookupItem.getReportedPeptideId();
					
						log.info( logMsg );
					}
				}
			}

		} finally {
			
			// be sure database handles are closed
			if( rsMonolink != null ) {
				try { rsMonolink.close(); } catch( Throwable t ) { ; }
				rsMonolink = null;
			}
			
			if( pstmtMonolink != null ) {
				try { pstmtMonolink.close(); } catch( Throwable t ) { ; }
				pstmtMonolink = null;
			}
			
		}
		
		if ( foundRecordCount > 10000 ) {
			log.info( "processUnlinked: > 10,000 crosslink records processed. Reported Peptide Id: \t" + lookupItem.getReportedPeptideId()
					+ "\t, Record count processed: \t" + foundRecordCount );
		}
	}
}
