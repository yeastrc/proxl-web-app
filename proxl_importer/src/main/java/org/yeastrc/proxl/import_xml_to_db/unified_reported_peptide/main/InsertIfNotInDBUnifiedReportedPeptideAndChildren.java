package org.yeastrc.proxl.import_xml_to_db.unified_reported_peptide.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.xlink.dao.UnifiedReportedPeptideLookupDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepIsotopeLabelLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_UnifiedRepPepDynamicModLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_UnifiedRepPepIsotopeLabelLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_UnifiedRepPepMatchedPeptideLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_UnifiedReportedPeptideLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;

/**
 * 
 *
 */
public class InsertIfNotInDBUnifiedReportedPeptideAndChildren {

	private static final Logger log = LoggerFactory.getLogger( InsertIfNotInDBUnifiedReportedPeptideAndChildren.class);
	// private constructor
	private InsertIfNotInDBUnifiedReportedPeptideAndChildren() { }
	public static InsertIfNotInDBUnifiedReportedPeptideAndChildren getInstance() { 
		return new InsertIfNotInDBUnifiedReportedPeptideAndChildren(); 
	}
	
	/**
	 * @param unifiedReportedPeptideObj
	 * @throws Exception
	 */
	public UnifiedReportedPeptideLookupDTO insertIfNotInDBUnifiedReportedPeptideAndChildren( int linkType, List<PerPeptideData> perPeptideDataList ) throws Exception {
		//  Must commit transactions before can lock tables, which is done in this method
		ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
		Z_Internal_UnifiedReportedPeptide_Holder z_Internal_UnifiedReportedPeptide_Holder 
			= Z_Internal_ProcessUnifiedReportedPeptideObj.getInstance().processUnifiedReportedPeptideObj( linkType, perPeptideDataList );
		UnifiedReportedPeptideLookupDAO unifiedReportedPeptideDAO = UnifiedReportedPeptideLookupDAO.getInstance();
		DB_Insert_UnifiedRepPepMatchedPeptideLookupDAO db_Insert_UnifiedRepPepMatchedPeptideLookupDAO = DB_Insert_UnifiedRepPepMatchedPeptideLookupDAO.getInstance();
		DB_Insert_UnifiedRepPepDynamicModLookupDAO db_Insert_UnifiedRepPepDynamicModLookupDAO = DB_Insert_UnifiedRepPepDynamicModLookupDAO.getInstance();
		DB_Insert_UnifiedRepPepIsotopeLabelLookupDAO db_Insert_UnifiedRepPepIsotopeLabelLookupDAO = DB_Insert_UnifiedRepPepIsotopeLabelLookupDAO.getInstance();
		DB_Insert_UnifiedReportedPeptideLookupDAO db_Insert_UnifiedReportedPeptideLookupDAO = DB_Insert_UnifiedReportedPeptideLookupDAO.getInstance();
		Connection dbConnection = null;
		UnifiedReportedPeptideLookupDTO unifiedReportedPeptideDTO = z_Internal_UnifiedReportedPeptide_Holder.getUnifiedReportedPeptideDTO();
		String unifiedReportedPeptideSequence = unifiedReportedPeptideDTO.getUnifiedSequence();
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			dbConnection.setAutoCommit(false);
			
			lockRequiredTables(dbConnection);
			Integer unifiedReportedPeptideId = null;
			try {
				unifiedReportedPeptideId =
						unifiedReportedPeptideDAO.getReportedPeptideIdForSequence(unifiedReportedPeptideSequence, dbConnection);
			} catch( Exception eg ) {
				String msg = "Failed to get unifiedReportedPeptideDTO.  unifiedReportedPeptideSequence: " + unifiedReportedPeptideSequence;
				log.error( msg, eg );
				throw new Exception( msg, eg );
			}
			if ( unifiedReportedPeptideId != null ) {
				//  Set retrieved id into object
				unifiedReportedPeptideDTO.setId( unifiedReportedPeptideId );
				return unifiedReportedPeptideDTO; //  EARLY EXIT
			}
			try {
				db_Insert_UnifiedReportedPeptideLookupDAO.saveToDatabase( unifiedReportedPeptideDTO, dbConnection );
			} catch( Exception eg ) {
				String msg = "Failed to save unifiedReportedPeptideDTO.  unifiedReportedPeptideSequence: " + unifiedReportedPeptideSequence;
				log.error( msg, eg );
				throw new Exception( msg, eg );
			}
			List<Z_Internal_UnifiedRpMatchedPeptide_Holder> unifiedRpMatchedPeptide_HolderList
				= z_Internal_UnifiedReportedPeptide_Holder.getZ_Internal_UnifiedRpMatchedPeptide_HolderList();
			for ( Z_Internal_UnifiedRpMatchedPeptide_Holder unifiedRpMatchedPeptide_Holder : unifiedRpMatchedPeptide_HolderList ) {
				
				UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptideDTO = unifiedRpMatchedPeptide_Holder.getUnifiedRpMatchedPeptideDTO();
				unifiedRpMatchedPeptideDTO.setUnifiedReportedPeptideId( unifiedReportedPeptideDTO.getId() );
				db_Insert_UnifiedRepPepMatchedPeptideLookupDAO.save( unifiedRpMatchedPeptideDTO, dbConnection );
				
				List<Z_Internal_UnifiedRpDynamicMod_Holder> unifiedRpDynamicMod_Holder_List
					= unifiedRpMatchedPeptide_Holder.getZ_Internal_UnifiedRpDynamicMod_Holder_List();
				if ( unifiedRpDynamicMod_Holder_List != null ) {
					for ( Z_Internal_UnifiedRpDynamicMod_Holder unifiedRpDynamicMod_Holder : unifiedRpDynamicMod_Holder_List ) {
						UnifiedRepPepDynamicModLookupDTO unifiedRpDynamicModDTO = unifiedRpDynamicMod_Holder.getUnifiedRpDynamicModDTO();
						unifiedRpDynamicModDTO.setRpMatchedPeptideId( unifiedRpMatchedPeptideDTO.getId() );
						db_Insert_UnifiedRepPepDynamicModLookupDAO.save( unifiedRpDynamicModDTO, dbConnection );
					}
				}
				
				List<Z_Internal_UnifiedRpIsotopeLabel_Holder> z_Internal_UnifiedRpIsotopeLabel_Holder_List =
						unifiedRpMatchedPeptide_Holder.getZ_Internal_UnifiedRpIsotopeLabel_Holder_List();
				if ( z_Internal_UnifiedRpIsotopeLabel_Holder_List != null ) {
					for ( Z_Internal_UnifiedRpIsotopeLabel_Holder unifiedRpIsotopeLabel_Holder : z_Internal_UnifiedRpIsotopeLabel_Holder_List ) {
						UnifiedRepPepIsotopeLabelLookupDTO unifiedRpIsotopeLabelDTO = unifiedRpIsotopeLabel_Holder.getUnifiedRepPepIsotopeLabelLookupDTO();
						unifiedRpIsotopeLabelDTO.setRpMatchedPeptideId( unifiedRpMatchedPeptideDTO.getId() );
						db_Insert_UnifiedRepPepIsotopeLabelLookupDAO.save( unifiedRpIsotopeLabelDTO, dbConnection );
					}
				}
				
			}
			dbConnection.commit();
		} catch ( Exception e ) {
			String msg = "Failed insertIfNotInDBUnifiedReportedPeptideAndChildren(...)";
			System.out.println( msg );
			System.err.println( msg );
			log.error( msg , e);
			if ( dbConnection != null ) {
				try {
					dbConnection.rollback();
				} catch (Exception ex) {
					String msgRollback = "Rollback Exception:  insertIfNotInDBUnifiedReportedPeptideAndChildren(...) Exception:  See Syserr or Sysout for original exception: Rollback Exception, tables are in an inconsistent state. '" + ex.toString();
					System.out.println( msgRollback );
					System.err.println( msgRollback );
					log.error( msgRollback, ex );
					throw new Exception( msgRollback, ex );
				}
			}
			throw e;
		} finally {
			try {
				if( dbConnection != null ) {
					unlockAllTable(dbConnection);
				}
			} finally {
				if( dbConnection != null ) {
					try {
						dbConnection.setAutoCommit(true);  /// reset for next user of dbConnectionection
					} catch (Exception ex) {
						String msg = "Failed dbConnection.setAutoCommit(true) in addNewScanFileDBTransactionService(...)";
						System.out.println( msg );
						System.err.println( msg );
						throw new Exception(msg);
					}
					try { dbConnection.close(); } 
					catch(Throwable t ) { ; }
					dbConnection = null;
				}
			}
		}
		return unifiedReportedPeptideDTO;
	}
		
	private static String lockTablesForWriteSQL 
		= "LOCK TABLES unified_reported_peptide_lookup WRITE, unified_rep_pep_matched_peptide_lookup WRITE, "
				+ " unified_rep_pep_dynamic_mod_lookup WRITE, unified_rep_pep_isotope_label_lookup WRITE";
	/**
	 * @param dbConnection
	 * @throws Exception
	 */
	private void lockRequiredTables( Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		try {
			pstmt = dbConnection.prepareStatement( lockTablesForWriteSQL );
			pstmt.executeUpdate();
		} catch (Exception sqlEx) {
			log.error("lockRequiredTables: Exception '" + sqlEx.toString() + ".\nSQL = " + lockTablesForWriteSQL , sqlEx);
			throw sqlEx;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		}
	}
	
	private static String unlockAllTableSQL = "UNLOCK TABLES";
	/**
	 * Unlock All Tables
	 * @throws Exception
	 */
	public void unlockAllTable( Connection dbConnectionection ) throws Exception {
		PreparedStatement pstmt = null;
		try {
			pstmt = dbConnectionection.prepareStatement( unlockAllTableSQL );
			pstmt.executeUpdate();
		} catch (Exception sqlEx) {
			log.error("unlockAllTable: Exception '" + sqlEx.toString() + ".\nSQL = " + unlockAllTableSQL , sqlEx);
			throw sqlEx;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		}
	}
}
