package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.StaticModDTO;

/**
 * 
 *
 */
public class StaticModDAO {
	
	private static final Logger log = Logger.getLogger(StaticModDAO.class);

	private StaticModDAO() { }
	public static StaticModDAO getInstance() { return new StaticModDAO(); }

	


	/**
	 * @param searchId
	 * @return 
	 * @throws Exception
	 */
	public List<StaticModDTO> getStaticModDTOForSearchId( int searchId ) throws Exception {


		List<StaticModDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM static_mod WHERE search_id = ? ORDER BY residue, mass";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				StaticModDTO item = populateResultObject( rs );
				results.add(item);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select StaticModDTO, matched_peptide_id: " + searchId + ", sql: " + sql;
			
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
	private StaticModDTO populateResultObject(ResultSet rs) throws SQLException {
		
		StaticModDTO returnItem = new StaticModDTO();

		returnItem.setId( rs.getInt( "id" ) );
		returnItem.setSearch_id( rs.getInt( "search_id" ) );
		returnItem.setResidue( rs.getString( "residue" ) );
		returnItem.setMass( rs.getBigDecimal( "mass" ) );
		returnItem.setMassString( rs.getString( "mass_string" ) );

		return returnItem;
	}
	
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( StaticModDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO static_mod ( search_id, residue, mass, mass_string) VALUES (?, ?, ?, ?)";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setInt( 1,  item.getSearch_id() );
			pstmt.setString( 2,  item.getResidue() );
			pstmt.setBigDecimal( 3,  item.getMass() );
			pstmt.setString( 4, item.getMassString() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert StaticModDTO" );
			
		} catch ( Exception e ) {
			
			String msg = "ERROR: item: " + item + "\n sql: " + sql;
			
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