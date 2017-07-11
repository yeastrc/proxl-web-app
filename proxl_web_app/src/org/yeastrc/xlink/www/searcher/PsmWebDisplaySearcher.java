package org.yeastrc.xlink.www.searcher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.PsmDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.web_utils.RetentionTimeScalingAndRounding;

/**
 * 
 *
 */
public class PsmWebDisplaySearcher {
	
	private static final Logger log = Logger.getLogger(PsmWebDisplaySearcher.class);
	private PsmWebDisplaySearcher() { }
	private static final PsmWebDisplaySearcher _INSTANCE = new PsmWebDisplaySearcher();
	public static PsmWebDisplaySearcher getInstance() { return _INSTANCE; }
	
	private static final String SQL_MAIN = 
			"SELECT psm.id AS psm_id, psm.charge, psm.scan_number AS scan_number, scan.retention_time, "
			+        "  scan.preMZ, search_scan_filename.filename AS scan_filename "
			+ " FROM psm  "
			+ " LEFT OUTER JOIN search_scan_filename ON psm.search_scan_filename_id = search_scan_filename.id "
			+ " LEFT OUTER JOIN scan ON psm.scan_id = scan.id ";
	
	private static final String SQL_WHERE_START =  " WHERE psm.search_id = ? AND psm.reported_peptide_id = ?  ";
	private static final String SQL_ORDER_BY =   " ORDER BY psm.id ";
	/**
	 * @param searchId
	 * @param reportedPeptideId
	 * @param searcherCutoffValuesSearchLevel - PSM and Peptide cutoffs for a search id
	 * @return
	 * @throws Exception
	 */
	public List<PsmWebDisplayWebServiceResult> getPsmsWebDisplay( int searchId, int reportedPeptideId, SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel  ) throws Exception {
		List<PsmWebDisplayWebServiceResult> psms = new ArrayList<PsmWebDisplayWebServiceResult>();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
		StringBuilder sqlSB = new StringBuilder( 1000 );
		//////////////////////
		/////   Start building the SQL
		sqlSB.append( SQL_MAIN );
		{
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
		}
		///////////
		sqlSB.append( SQL_WHERE_START );
		//////////
		// Process PSM Cutoffs for WHERE
		{
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
		}
		sqlSB.append( SQL_ORDER_BY );
		String sql = sqlSB.toString();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			paramCounter++;
			pstmt.setInt( paramCounter, reportedPeptideId );
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
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				PsmWebDisplayWebServiceResult psmWebDisplay = new PsmWebDisplayWebServiceResult();
				psmWebDisplay.setSearchId( searchId );
				psmWebDisplay.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
				PsmDTO psmDTO = PsmDAO.getInstance().getPsmDTO( rs.getInt( "psm_id" ) );
				psmWebDisplay.setPsmDTO( psmDTO );
				int charge = rs.getInt( "charge" );
				if ( ! rs.wasNull() ) {
					psmWebDisplay.setCharge( charge );
				}
				int scanNumber = rs.getInt( "scan_number" );
				if ( ! rs.wasNull() ) {
					psmWebDisplay.setScanNumber( scanNumber );
				}
				psmWebDisplay.setScanFilename( rs.getString( "scan_filename" ) );
				BigDecimal retentionTime = rs.getBigDecimal( "retention_time" );
				if ( retentionTime != null ) {
					psmWebDisplay.setRetentionTime( retentionTime );
					//  Get the retention time in minutes
					BigDecimal retentionInMinutesRounded = RetentionTimeScalingAndRounding.retentionTimeToMinutesRounded( psmWebDisplay.getRetentionTime() );
					psmWebDisplay.setRetentionTimeMinutesRounded( retentionInMinutesRounded );
				}
				BigDecimal preMZ = rs.getBigDecimal( "preMZ" );
				if ( preMZ != null ) {
					psmWebDisplay.setPreMZ( preMZ );
					//  Round the preMZ
					String preMZRoundedString = null;
					if ( preMZ != null ) {
						// first param to setScale is the number of decimal places to keep  
						BigDecimal preMZRounded = preMZ.setScale( 5, RoundingMode.HALF_UP );
						preMZRoundedString = preMZRounded.toString();  // convert to string so trailing zeros are preserved
					}
					psmWebDisplay.setPreMZRounded( preMZRoundedString );
				}
				psms.add( psmWebDisplay );
			}
		} catch ( Exception e ) {
			String msg = "Exception in getPsmsInternal( ... ): sql: " + sql;
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
		return psms;
	}
}