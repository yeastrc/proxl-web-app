package org.yeastrc.xlink.www.protein_coverage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Manage the cache of protein sequence coverages for given searches with given search
 * parameters. Keeps an in-memory cache, as well as a database cache
 * 
 * @author mriffle
 *
 */
public class ProteinSequenceCoverageCacheManager {

	private static final Logger log = Logger.getLogger( ProteinSequenceCoverageCacheManager.class );

	
	/*
	 * The in-memory cache of discovered protein sequence coverages. This map is keyed on the search parameter object
	 * (i.e., filter cutoffs). This key points to a map keyed on protein Ids which point to the protein sequence
	 * coverage object describing that protein's sequence coverage for the given search and cutoffs.
	 */
	private Map<ProteinSequenceCoverageSearchParams, ProteinSequenceCoverage> _MEMORY_CACHE = new HashMap<>();
	

	
	/**
	 * Attempt to get the requested protein sequence coverage from the cache. It will first try the in-memory cache.
	 * If not found, it will try the database cache. If found there, it will put into the in-memory cache.
	 * If not found in either, it will return null.
	 * 
	 * @param proteinId
	 * @param searchParameters
	 * @return
	 */
	public ProteinSequenceCoverage getProteinSequenceCoverageFromCache( int proteinId, SearcherCutoffValuesSearchLevel scvsl ) {
		
		ProteinSequenceCoverageSearchParams searchParameters = ProteinSequenceCoverageSearchParams.getProteinSequenceCoverageSearchParams( scvsl, proteinId);
		
		// try memory cache first
		if( this._MEMORY_CACHE.containsKey( searchParameters ) )
			return this._MEMORY_CACHE.get( searchParameters );
		
		// check the database cache
		ProteinSequenceCoverage coverage = null;
		
		try {
			
			coverage = this.getProteinSequenceCoverageFromDbCache( searchParameters);
		
		} catch ( Exception e ) {
			
			// no reason to die if an exception occurred, as protein sequence can be calculated w/o the cache
			// however, we'd like to know about it, so log it
			
			log.error( e.getMessage(), e );			
			
		}
		
		return coverage;
	}
	

	
	/**
	 * Add the supplied protein sequence coverage data to the cache. It will add to both the datbase and in-memory cache--replacing
	 * existing data for that protein and search parameters.
	 * 
	 * @param proteinId
	 * @param searchParameters
	 */
	public void addProteinSequenceCoverageToCache( int proteinId, SearcherCutoffValuesSearchLevel scvsl, ProteinSequenceCoverage coverage ) {
		
		ProteinSequenceCoverageSearchParams searchParameters = ProteinSequenceCoverageSearchParams.getProteinSequenceCoverageSearchParams( scvsl, proteinId);

		// replace into the memory cache
		this._MEMORY_CACHE.put( searchParameters, coverage );
		
		// replace into the database cache
		

	}
									
	
	/**
	 * Replace the existing entry in the database cache for the sequence coverage in the context of the supplied search parameters
	 * 
	 * @param searchParameters
	 * @param coverage
	 */
	private void addProteinSequenceCoverageToDbCache( ProteinSequenceCoverageSearchParams searchParameters, ProteinSequenceCoverage coverage ) throws Exception  {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			ObjectMapper mapper = new ObjectMapper();	// our json de-serializer
			
			String sql = "SELECT search_param_json, sequence_coverage_json FROM sequence_coverage_cache WHERE search_param_hash_code = ?";
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			
			//todo finish this
			
			
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
	 * Attempt to get the requested protein sequence coverage from the database cache. If not found, returns null.
	 * 
	 * @param searchParameters
	 * @return
	 * @throws Exception
	 */
	private ProteinSequenceCoverage getProteinSequenceCoverageFromDbCache( ProteinSequenceCoverageSearchParams searchParameters ) throws Exception {
		
		ProteinSequenceCoverage psc = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
		
			ObjectMapper mapper = new ObjectMapper();	// our json de-serializer
			
			String sql = "SELECT search_param_json, sequence_coverage_json FROM sequence_coverage_cache WHERE search_param_hash_code = ?";
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, searchParameters.hashCode() );
			
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				
				// de-serialize cached search parameters and sequence coverage
				ProteinSequenceCoverageSearchParams dbParams = mapper.readValue( rs.getString( 1 ), ProteinSequenceCoverageSearchParams.class );

				// if the search parameters aren't the same, that means we have a hashCode collision. Try
				// next one in database, if it exists.
				if( !searchParameters.equals( dbParams ) )
					continue;
				
				// de-serialize the ProteinSequenceCoverage from the database
				psc = mapper.readValue( rs.getString( 2 ), ProteinSequenceCoverage.class );				
			}
			
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
		
		return psc;
	}
	

	
}
