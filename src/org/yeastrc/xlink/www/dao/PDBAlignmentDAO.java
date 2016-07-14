package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.PDBAlignmentDTO;

public class PDBAlignmentDAO {

	private static final Logger log = Logger.getLogger(PDBAlignmentDAO.class);

	private PDBAlignmentDAO() { }
	private static final PDBAlignmentDAO _INSTANCE = new PDBAlignmentDAO();
	public static PDBAlignmentDAO getInstance() { return _INSTANCE; }
	
	public PDBAlignmentDTO getPDBAlignment( int id ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM pdb_alignment WHERE id = ?";
		
		PDBAlignmentDTO pa = new PDBAlignmentDTO();
		
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( !rs.next() )
				throw new Exception( "could not find pdb alignment with id: " + id );
			
			pa.setId( id );
			
			
			//  Change to aligned_experimental_sequence after field rename
			pa.setAlignedExperimentalSequence( rs.getString( "aligned_nrseq_sequence" ) );
			
			pa.setAlignedPDBSequence( rs.getString( "aligned_pdb_sequence" ) );
			pa.setChainId( rs.getString( "chain_id" ) );
			pa.setProteinSequenceId( rs.getInt( "protein_sequence_id" ) );
			pa.setPdbFileId( rs.getInt( "pdb_file_id" ) );

		} catch ( Exception e ) {

			log.error( "ERROR: getPDBAlignment ", e );

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
		
		
		return pa;		
	}
	
	
	/**
	 * @param pa
	 * @return
	 * @throws Exception
	 */
	public int savePDBAlignment( PDBAlignmentDTO pa ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	
		
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
		
			// if the pa has an ID, assume it's already in the database and update the database
			// also assume the pdb_file_id, chain_id and nrseq_id are not changing if this is the
			// case
			if( pa.getId() != 0 ) {
				
				//  Change from aligned_nrseq_sequence to aligned_experimental_sequence after field rename
				
				String sql = "UPDATE pdb_alignment SET aligned_pdb_sequence = ?, aligned_nrseq_sequence = ? WHERE id = ?";
				pstmt = conn.prepareStatement( sql );
				pstmt.setString( 1, pa.getAlignedPDBSequence() );
				pstmt.setString( 2,  pa.getAlignedExperimentalSequence() );
				pstmt.setInt( 3, pa.getId() );
				
				pstmt.executeUpdate();
				
				return pa.getId();
				
			} else {
				
				// inserting a new PDB alignment
				

				//  Change from aligned_nrseq_sequence to aligned_experimental_sequence after field rename

				final String sql = "INSERT INTO pdb_alignment"
						+ " (pdb_file_id, chain_id, protein_sequence_id, aligned_pdb_sequence, aligned_nrseq_sequence ) " 
						+ " VALUES ( ?, ?, ?, ?, ? )";

				pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
				
				int counter = 0;

				counter++;
				pstmt.setInt( counter, pa.getPdbFileId() );
				counter++;
				pstmt.setString( counter, pa.getChainId() );
				counter++;
				pstmt.setInt( counter, pa.getProteinSequenceId() );
				counter++;
				pstmt.setString( counter, pa.getAlignedPDBSequence() );
				counter++;
				pstmt.setString( counter, pa.getAlignedExperimentalSequence() );
				
				pstmt.executeUpdate();
				
				rs = pstmt.getGeneratedKeys();

				if( rs.next() ) {
					pa.setId( rs.getInt( 1 ) );
				} else {
					
					String msg = "Failed to insert PDBAlignmentDTO, generated key not found.";
					
					log.error( msg );
					
					throw new Exception( msg );
				}
				
				return pa.getId();
			}


		} catch ( Exception e ) {

			log.error( "ERROR: savePDBAlignment ", e );

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
	
	
	/**
	 * @param alignmentId
	 * @throws Exception
	 */
	public void deletePDBAlignment( int alignmentId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "DELETE FROM pdb_alignment WHERE id = ?";
		
		
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, alignmentId );
			
			pstmt.executeUpdate();
			


		} catch ( Exception e ) {

			log.error( "ERROR: deletePDBAlignment ", e );

			throw e;

		} finally {
			
			// be sure database handles are closed
			
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
