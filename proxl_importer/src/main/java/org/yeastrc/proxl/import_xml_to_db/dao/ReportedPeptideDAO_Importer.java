package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * table reported_peptide
 *
 */
public class ReportedPeptideDAO_Importer {
	
	private static final Logger log = Logger.getLogger(ReportedPeptideDAO_Importer.class);
	private ReportedPeptideDAO_Importer() { }
	public static ReportedPeptideDAO_Importer getInstance() { return new ReportedPeptideDAO_Importer(); }
	
	/**
	 * Get the peptide DTO corresponding to supplied sequence. If no matching
	 * peptide is found in the database, it is inserted and a populated DTO
	 * returned. If already in the database, DTO is populated from database
	 * and returned.
	 * 
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public ReportedPeptideDTO getReportedPeptideDTO_OrSave( String sequence ) throws Exception {
		ReportedPeptideDTO plpeptide = new ReportedPeptideDTO();
		plpeptide.setSequence( sequence );
		plpeptide.setId( getReportedPeptideIdForSequence( sequence ) );
		if( plpeptide.getId() == 0 )
			saveToDatabase( plpeptide );
		return plpeptide;
	}
	
	/**
	 * Get the id for the supplied reported peptide sequence (as it appears in analysis program
	 * output) from the database. Returns 0 if not found.
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public int getReportedPeptideIdForSequence( String sequence ) throws Exception {
		int id = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT id FROM reported_peptide WHERE sequence = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, sequence );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				id = rs.getInt( 1 );
			}
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
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
		return id;
	}

	/**
	 * @param peptide
	 * @throws Exception
	 */
	public void saveToDatabase( ReportedPeptideDTO peptide ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "INSERT INTO reported_peptide (sequence ) VALUES (?)";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setString( 1, peptide.getSequence() );
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				peptide.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert reported_peptide for " + peptide.getSequence() );
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
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
	}
}
