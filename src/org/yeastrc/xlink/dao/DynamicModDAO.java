package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.DynamicModDTO;

/**
 * 
 *
 */
public class DynamicModDAO {
	
	private static final Logger log = Logger.getLogger(DynamicModDAO.class);

	private DynamicModDAO() { }
	public static DynamicModDAO getInstance() { return new DynamicModDAO(); }

	


	/**
	 * @param matched_peptide_id
	 * @return 
	 * @throws Exception
	 */
	public List<DynamicModDTO> getDynamicModDTOForMatchedPeptideId( int matched_peptide_id ) throws Exception {


		List<DynamicModDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM dynamic_mod WHERE matched_peptide_id = ? ORDER BY position, mass";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, matched_peptide_id );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				DynamicModDTO item = populateResultObject( rs );
				results.add(item);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select DynamicModDTO, matched_peptide_id: " + matched_peptide_id + ", sql: " + sql;
			
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
		
		return results;
	}
	

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private DynamicModDTO populateResultObject(ResultSet rs) throws SQLException {
		
		DynamicModDTO returnItem = new DynamicModDTO();

		returnItem.setId( rs.getInt( "id" ) );
		returnItem.setMatched_peptide_id( rs.getInt( "matched_peptide_id" ) );
		returnItem.setPosition( rs.getInt( "position" ) );
		returnItem.setMass( rs.getDouble( "mass" ) );
		
		int isMonolinkInt = rs.getInt( "is_monolink" );
		
		if ( Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE == isMonolinkInt ) {
			returnItem.setMonolink( false );
		} else {
			returnItem.setMonolink( true );
		}
		
		return returnItem;
	}
	
	/**
	 * @param dmod
	 * @throws Exception
	 */
	public void save( DynamicModDTO dmod ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO dynamic_mod (matched_peptide_id, position, mass, is_monolink) VALUES (?, ?, ?, ?)";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setInt( 1,  dmod.getMatched_peptide_id() );
			pstmt.setInt( 2,  dmod.getPosition() );
			pstmt.setDouble( 3,  dmod.getMass() );

			if ( dmod.isMonolink() ) {
				pstmt.setInt( 4, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( 4, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}

			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				dmod.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert DynamicModDTO" );
			
		} catch ( Exception e ) {
			
			String msg = "ERROR: sql: " + sql;
			
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
		
	}
	
}