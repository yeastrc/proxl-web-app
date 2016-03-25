package org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_link_per_peptide_generic_lookup_records.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_CrossLinkRepPeptSearchGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_DimerRepPeptSearchGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_LoopLinkRepPeptSearchGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_MonoLinkRepPeptSearchGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_UnlinkedRepPeptSearchGenericLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.xlink.dao.CrosslinkDAO;
import org.yeastrc.xlink.dao.DimerDAO;
import org.yeastrc.xlink.dao.LooplinkDAO;
import org.yeastrc.xlink.dao.UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO;
import org.yeastrc.xlink.dao.UnlinkedDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.CrosslinkDTO;
import org.yeastrc.xlink.dto.CrosslinkRepPeptSearchGenericLookupDTO;
import org.yeastrc.xlink.dto.DimerDTO;
import org.yeastrc.xlink.dto.DimerRepPeptSearchGenericLookupDTO;
import org.yeastrc.xlink.dto.LooplinkDTO;
import org.yeastrc.xlink.dto.LooplinkRepPeptSearchGenericLookupDTO;
import org.yeastrc.xlink.dto.MonolinkRepPeptSearchGenericLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO;
import org.yeastrc.xlink.dto.UnlinkedDTO;
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
		
		CrosslinkDAO crosslinkDAO = CrosslinkDAO.getInstance();
		LooplinkDAO looplinkDAO = LooplinkDAO.getInstance();
		DimerDAO dimerDAO = DimerDAO.getInstance();
		UnlinkedDAO unlinkedDAO = UnlinkedDAO.getInstance();
		
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
		
	    
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = SQL_MAIN;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );


			pstmt = conn.prepareStatement( SQL_COUNT );
			
			pstmt.setInt( 1, searchId );
			
			rs = pstmt.executeQuery();
			
			rs.next();
			
			int totalRecordCount = rs.getInt( "count" );
			
			rs.close();
			rs = null;
			pstmt.close();
			pstmt = null;
			
			if ( log.isInfoEnabled() ) {

				log.info( "addLinkPerPeptideGenericLookupRecordsPerSearchId:  Record count to process: " + totalRecordCount );
			}
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, searchId );
			
			rs = pstmt.executeQuery();
			
			int recordCount = 0;
			
			
			while ( rs.next() ) {
				
				recordCount++;
				
				
				
				UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO lookupItem =
						unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO.populateFromResultSet( rs );
				
				if ( lookupItem.getLinkType() == XLinkUtils.TYPE_CROSSLINK ) {
					
					processCrosslink( searchId, crosslinkDAO, db_Insert_CrossLinkRepPeptSearchGenericLookupDAO, lookupItem );

				} else if ( lookupItem.getLinkType() == XLinkUtils.TYPE_LOOPLINK ) {

					processLooplink( searchId, looplinkDAO, db_Insert_LoopLinkRepPeptSearchGenericLookupDAO, lookupItem );

				} else if ( lookupItem.getLinkType() == XLinkUtils.TYPE_DIMER ) {

					processDimer( searchId, dimerDAO, db_Insert_DimerRepPeptSearchGenericLookupDAO, lookupItem );

				} else if ( lookupItem.getLinkType() == XLinkUtils.TYPE_UNLINKED ) {

					processUnlinked( searchId, unlinkedDAO, db_Insert_UnlinkedRepPeptSearchGenericLookupDAO, lookupItem );

				}
				
				processMonolink( searchId, db_Insert_MonoLinkRepPeptSearchGenericLookupDAO, conn, lookupItem );
				

				if ( recordCount % 5000 == 0 ) {
					
					if ( log.isInfoEnabled() ) {

						log.info( "processed " + recordCount + " of " + totalRecordCount );
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
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		

	    ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();

		
	}

	private void processCrosslink(
			int searchId,
			CrosslinkDAO crosslinkDAO,
			DB_Insert_CrossLinkRepPeptSearchGenericLookupDAO db_Insert_CrossLinkRepPeptSearchGenericLookupDAO,
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO lookupItem)
			throws Exception, ProxlImporterDataException {
		List<CrosslinkDTO> linkDTOList = crosslinkDAO.getAllCrosslinkDTOForPsmId( lookupItem.getSamplePsmId() );
		
		if ( linkDTOList.isEmpty() ) {
			
			String msg = "no crosslink records found for psmId: " + lookupItem.getSamplePsmId();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		List<CrosslinkRepPeptSearchGenericLookupDTO> insertedItems = new ArrayList<>();
		
		for ( CrosslinkDTO linkDTO : linkDTOList ) {

			CrosslinkRepPeptSearchGenericLookupDTO insertItem = new CrosslinkRepPeptSearchGenericLookupDTO();

			insertItem.setSearchId( searchId );
			insertItem.setReportedPeptideId( lookupItem.getReportedPeptideId() );
			insertItem.setProteinId_1( linkDTO.getProtein1().getNrseqId() );
			insertItem.setProteinId_2( linkDTO.getProtein2().getNrseqId() );
			insertItem.setProtein_1_position( linkDTO.getProtein1Position() );
			insertItem.setProtein_2_position( linkDTO.getProtein2Position() );

			insertItem.setAllRelatedPeptidesUniqueForSearch( lookupItem.isAllRelatedPeptidesUniqueForSearch() );
			insertItem.setPsmNumAtDefaultCutoff( lookupItem.getPsmNumAtDefaultCutoff() );
			insertItem.setPeptideMeetsDefaultCutoffs( lookupItem.getPeptideMeetsDefaultCutoffs() );
			
			//  Don't insert the same item twice.  The link table has duplicates
			
			boolean itemAlreadyInserted = false;
			
			for ( CrosslinkRepPeptSearchGenericLookupDTO alreadyInserted : insertedItems ) {
				
				if ( 	   alreadyInserted.getProteinId_1() == insertItem.getProteinId_1()
						&& alreadyInserted.getProteinId_2() == insertItem.getProteinId_2()
						&& alreadyInserted.getProtein_1_position() == insertItem.getProtein_1_position()
						&& alreadyInserted.getProtein_2_position() == insertItem.getProtein_2_position()
						) {
				
					itemAlreadyInserted = true;
				}
			}
			
			if ( itemAlreadyInserted ) {
				
				continue;  // Skip inserting insertItem
			}

			db_Insert_CrossLinkRepPeptSearchGenericLookupDAO.saveToDatabase( insertItem );
			
			insertedItems.add( insertItem );
		}
	}

	private void processLooplink(
			int searchId,
			LooplinkDAO looplinkDAO,
			DB_Insert_LoopLinkRepPeptSearchGenericLookupDAO db_Insert_LoopLinkRepPeptSearchGenericLookupDAO,
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO lookupItem)
			throws Exception, ProxlImporterDataException {
		List<LooplinkDTO> linkDTOList = looplinkDAO.getAllLooplinkDTOForPsmId( lookupItem.getSamplePsmId() );

		if ( linkDTOList.isEmpty() ) {

			String msg = "no looplink records found for psmId: " + lookupItem.getSamplePsmId();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}

		List<LooplinkRepPeptSearchGenericLookupDTO> insertedItems = new ArrayList<>();

		for ( LooplinkDTO linkDTO : linkDTOList ) {

			LooplinkRepPeptSearchGenericLookupDTO insertItem = new LooplinkRepPeptSearchGenericLookupDTO();

			insertItem.setSearchId( searchId );
			insertItem.setReportedPeptideId( lookupItem.getReportedPeptideId() );
			insertItem.setProteinId( linkDTO.getProtein().getNrseqId() );
			insertItem.setProteinPosition_1( linkDTO.getProteinPosition1() );
			insertItem.setProteinPosition_2( linkDTO.getProteinPosition2() );

			insertItem.setAllRelatedPeptidesUniqueForSearch( lookupItem.isAllRelatedPeptidesUniqueForSearch() );
			insertItem.setPsmNumAtDefaultCutoff( lookupItem.getPsmNumAtDefaultCutoff() );
			insertItem.setPeptideMeetsDefaultCutoffs( lookupItem.getPeptideMeetsDefaultCutoffs() );

			//  Don't insert the same item twice.  The link table has duplicates

			boolean itemAlreadyInserted = false;

			for ( LooplinkRepPeptSearchGenericLookupDTO alreadyInserted : insertedItems ) {

				if ( 	   alreadyInserted.getProteinId() == insertItem.getProteinId()
						&& alreadyInserted.getProteinPosition_1() == insertItem.getProteinPosition_1()
						&& alreadyInserted.getProteinPosition_2() == insertItem.getProteinPosition_2()
						) {

					itemAlreadyInserted = true;
				}
			}

			if ( itemAlreadyInserted ) {

				continue;  // Skip inserting insertItem
			}

			db_Insert_LoopLinkRepPeptSearchGenericLookupDAO.saveToDatabase( insertItem );

			insertedItems.add( insertItem );
		}
	}

	private void processDimer(
			int searchId,
			DimerDAO dimerDAO,
			DB_Insert_DimerRepPeptSearchGenericLookupDAO db_Insert_DimerRepPeptSearchGenericLookupDAO,
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO lookupItem)
			throws Exception, ProxlImporterDataException {
		List<DimerDTO> linkDTOList = dimerDAO.getAllDimerDTOForPsmId( lookupItem.getSamplePsmId() );
		
		if ( linkDTOList.isEmpty() ) {
			
			String msg = "no dimer records found for psmId: " + lookupItem.getSamplePsmId();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		List<DimerRepPeptSearchGenericLookupDTO> insertedItems = new ArrayList<>();
		
		for ( DimerDTO linkDTO : linkDTOList ) {

			DimerRepPeptSearchGenericLookupDTO insertItem = new DimerRepPeptSearchGenericLookupDTO();

			insertItem.setSearchId( searchId );
			insertItem.setReportedPeptideId( lookupItem.getReportedPeptideId() );
			insertItem.setProteinId_1( linkDTO.getProtein1().getNrseqId() );
			insertItem.setProteinId_2( linkDTO.getProtein2().getNrseqId() );

			insertItem.setAllRelatedPeptidesUniqueForSearch( lookupItem.isAllRelatedPeptidesUniqueForSearch() );
			insertItem.setPsmNumAtDefaultCutoff( lookupItem.getPsmNumAtDefaultCutoff() );
			insertItem.setPeptideMeetsDefaultCutoffs( lookupItem.getPeptideMeetsDefaultCutoffs() );
			
			//  Don't insert the same item twice.  The link table has duplicates
			
			boolean itemAlreadyInserted = false;
			
			for ( DimerRepPeptSearchGenericLookupDTO alreadyInserted : insertedItems ) {
				
				if ( 	   alreadyInserted.getProteinId_1() == insertItem.getProteinId_1()
						&& alreadyInserted.getProteinId_2() == insertItem.getProteinId_2()
						) {
				
					itemAlreadyInserted = true;
				}
			}
			
			if ( itemAlreadyInserted ) {
				
				continue;  // Skip inserting insertItem
			}

			db_Insert_DimerRepPeptSearchGenericLookupDAO.saveToDatabase( insertItem );
			
			insertedItems.add( insertItem );
		}
	}

	private void processUnlinked(
			int searchId,
			UnlinkedDAO unlinkedDAO,
			DB_Insert_UnlinkedRepPeptSearchGenericLookupDAO db_Insert_UnlinkedRepPeptSearchGenericLookupDAO,
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO lookupItem)
			throws Exception, ProxlImporterDataException {
		List<UnlinkedDTO> linkDTOList = unlinkedDAO.getAllUnlinkedDTOForPsmId( lookupItem.getSamplePsmId() );
				
		if ( linkDTOList.isEmpty() ) {

			String msg = "no unlinked records found for psmId: " + lookupItem.getSamplePsmId();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}

		List<UnlinkedRepPeptSearchGenericLookupDTO> insertedItems = new ArrayList<>();

		for ( UnlinkedDTO linkDTO : linkDTOList ) {

			UnlinkedRepPeptSearchGenericLookupDTO insertItem = new UnlinkedRepPeptSearchGenericLookupDTO();

			insertItem.setSearchId( searchId );
			insertItem.setReportedPeptideId( lookupItem.getReportedPeptideId() );
			insertItem.setProteinId( linkDTO.getProtein().getNrseqId() );

			insertItem.setAllRelatedPeptidesUniqueForSearch( lookupItem.isAllRelatedPeptidesUniqueForSearch() );
			insertItem.setPsmNumAtDefaultCutoff( lookupItem.getPsmNumAtDefaultCutoff() );
			insertItem.setPeptideMeetsDefaultCutoffs( lookupItem.getPeptideMeetsDefaultCutoffs() );

			//  Don't insert the same item twice.  The link table has duplicates

			boolean itemAlreadyInserted = false;

			for ( UnlinkedRepPeptSearchGenericLookupDTO alreadyInserted : insertedItems ) {

				if ( 	   alreadyInserted.getProteinId() == insertItem.getProteinId()
						) {

					itemAlreadyInserted = true;
				}
			}

			if ( itemAlreadyInserted ) {

				continue;  // Skip inserting insertItem
			}

			db_Insert_UnlinkedRepPeptSearchGenericLookupDAO.saveToDatabase( insertItem );

			insertedItems.add( insertItem );
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
		
		try {
			
			
			pstmtMonolink = conn.prepareStatement( sqlMonolink );
			
			pstmtMonolink.setInt( 1, searchId );
			pstmtMonolink.setInt( 2, lookupItem.getReportedPeptideId() );
			
			rsMonolink = pstmtMonolink.executeQuery();

			while ( rsMonolink.next() ) {

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
	}
}
