package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;


/**
 * table linker_per_search_cleaved_crosslink_mass_tbl
 *
 */
public class DB_Insert_LinkerPerSearchCleavedCrosslinkMassDAO {

	private static final Logger log = LoggerFactory.getLogger( DB_Insert_LinkerPerSearchCleavedCrosslinkMassDAO.class);

	private DB_Insert_LinkerPerSearchCleavedCrosslinkMassDAO() { }
	public static DB_Insert_LinkerPerSearchCleavedCrosslinkMassDAO getInstance() { return new DB_Insert_LinkerPerSearchCleavedCrosslinkMassDAO(); }
	

	private final String INSERT_SQL = 
			"INSERT INTO linker_per_search_cleaved_crosslink_mass_tbl "
			+ "(search_linker_id, search_id, cleaved_crosslink_mass_double, cleaved_crosslink_mass_string, chemical_formula) "
			+ "VALUES (?, ?, ?, ?, ?)";
	
	  
	public void save( LinkerPerSearchCleavedCrosslinkMassDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = INSERT_SQL;

		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchLinkerId() );
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setDouble( counter, item.getCleavedCrosslinkMassDouble() );
			counter++;
			pstmt.setString( counter, item.getCleavedCrosslinkMassString() );
			counter++;
			pstmt.setString( counter, item.getChemicalFormula() );

			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert linker_per_search_cleaved_crosslink_mass_tbl" );
			
		} catch ( Exception e ) {
			
			String msg = "ERROR inserting linker_per_search_cleaved_crosslink_mass_tbl. database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n linker_per_search_cleaved_crosslink_mass_tbl: " + item
					+ "\nsql: " + sql;
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
