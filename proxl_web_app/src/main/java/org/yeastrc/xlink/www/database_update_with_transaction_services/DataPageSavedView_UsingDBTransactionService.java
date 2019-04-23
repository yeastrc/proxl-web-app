package org.yeastrc.xlink.www.database_update_with_transaction_services;

import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.constants.SavePageViewConstants;
import org.yeastrc.xlink.www.dao.DataPageSavedViewAssocProjectSearchIdDAO;
import org.yeastrc.xlink.www.dao.DataPageSavedViewDAO;
import org.yeastrc.xlink.www.dto.DataPageSavedViewAssocProjectSearchIdDTO;
import org.yeastrc.xlink.www.dto.DataPageSavedViewDTO;

/**
 * 
 *
 */
public class DataPageSavedView_UsingDBTransactionService {

	private static final Logger log = LoggerFactory.getLogger( DataPageSavedView_UsingDBTransactionService.class);
	//  private constructor
	private DataPageSavedView_UsingDBTransactionService() { }
	/**
	 * @return newly created instance
	 */
	public static DataPageSavedView_UsingDBTransactionService getInstance() { 
		return new DataPageSavedView_UsingDBTransactionService(); 
	}
	
	/**
	 * @param dataPageSavedViewDTO
	 * @throws Exception
	 */
	public void addNew_dataPageSavedView_UsingDBTransactionService( 
			DataPageSavedViewDTO dataPageSavedViewDTO, 
			List<Integer> projectSearchIds ) throws Exception {
		
		String pageName = dataPageSavedViewDTO.getPageName();
		if ( ! SavePageViewConstants.ALLOWED_PAGE_NAMES_FOR_DEFAULT_PAGE_VIEWS.contains( pageName ) ) {
			String msg = "pageName is not valid: " + pageName;
			log.error( msg );
			throw new Exception(msg);
		}
		{
			Connection dbConnection = null;
			try {
				dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
				dbConnection.setAutoCommit(false);
								
				DataPageSavedViewDAO.getInstance().save( dataPageSavedViewDTO, dbConnection );

				for ( Integer projectSearchId : projectSearchIds ) {
					DataPageSavedViewAssocProjectSearchIdDTO item = new DataPageSavedViewAssocProjectSearchIdDTO();
					item.setAssocMainId( dataPageSavedViewDTO.getId() );
					item.setProjectSearchId( projectSearchId );
					DataPageSavedViewAssocProjectSearchIdDAO.getInstance().save( item, dbConnection );
				}
				
				dbConnection.commit();
			} catch ( Exception e ) {
				String msg = "Failed addNew_dataPageSavedView_UsingDBTransactionService(...)";
				log.error( msg, e );
				if ( dbConnection != null ) {
					dbConnection.rollback();
				}
				throw e;
			} finally {
				if( dbConnection != null ) {
					try {
						dbConnection.setAutoCommit(true);  /// reset for next user of connection
					} catch (Exception ex) {
						String msg = "Failed dbConnection.setAutoCommit(true) in addNew_dataPageSavedView_UsingDBTransactionService(...)";
						log.error( msg );
						throw new Exception(msg);
					}
					try { dbConnection.close(); } catch( Throwable t ) { ; }
					dbConnection = null;
				}
			}
		}
	}
}
