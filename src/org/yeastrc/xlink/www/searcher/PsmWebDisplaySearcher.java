package org.yeastrc.xlink.www.searcher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.PsmDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.www.objects.PsmWebDisplay;
import org.yeastrc.xlink.www.web_utils.RetentionTimeScalingAndRounding;

public class PsmWebDisplaySearcher {

	private static final Logger log = Logger.getLogger(PsmWebDisplaySearcher.class);
	
	private PsmWebDisplaySearcher() { }
	private static final PsmWebDisplaySearcher _INSTANCE = new PsmWebDisplaySearcher();
	public static PsmWebDisplaySearcher getInstance() { return _INSTANCE; }
	
	/**
	 * @param searchId
	 * @param reportedPeptideId
	 * @param peptideQValueCutoff
	 * @param psmQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public List<PsmWebDisplay> getPsmsWebDisplay( int searchId, int reportedPeptideId, double peptideQValueCutoff, double psmQValueCutoff  ) throws Exception {
		
		List<PsmWebDisplay> psms = new ArrayList<PsmWebDisplay>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = 
				"SELECT psm.id AS psm_id, psm.charge, scan.start_scan_number AS scan_number, scan.retention_time, "
				+        "  scan.preMZ, scan_file.filename AS scan_filename "
						
				+ " FROM psm  "
				+ " LEFT OUTER JOIN percolator_psm ON psm.id = percolator_psm.psm_id"
				+ " LEFT OUTER JOIN scan ON psm.scan_id = scan.id "
				+ " LEFT OUTER JOIN scan_file ON scan.scan_file_id = scan_file.id "
				
				+ " WHERE search_id = ? AND reported_peptide_id = ? AND psm.q_value <= ? "
				
				+ " ORDER BY psm.q_value , percolator_psm.pep, percolator_psm.svm_score DESC, psm.id ";

		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, reportedPeptideId );
			pstmt.setDouble( 3, psmQValueCutoff );

			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				PsmWebDisplay psmWebDisplay = new PsmWebDisplay();
				
				psmWebDisplay.setSearchId( searchId );
				psmWebDisplay.setPeptideQValueCutoff( peptideQValueCutoff );
				psmWebDisplay.setPsmQValueCutoff( psmQValueCutoff );
				
				
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
