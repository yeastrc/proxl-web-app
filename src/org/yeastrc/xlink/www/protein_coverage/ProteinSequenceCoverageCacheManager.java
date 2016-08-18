package org.yeastrc.xlink.www.protein_coverage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.factories.ProteinSequenceObjectFactory;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Range;

/**
 * Manage the cache of protein sequence coverages for given searches with given search
 * parameters. Keeps an in-memory cache, as well as a database cache
 * 
 * @author mriffle
 *
 */
public class ProteinSequenceCoverageCacheManager {

	private static final Logger log = Logger.getLogger( ProteinSequenceCoverageCacheManager.class );

	// only a single instance of this class can exist, get with getInstance()
	private static final ProteinSequenceCoverageCacheManager _INSTANCE = new ProteinSequenceCoverageCacheManager();
	public static ProteinSequenceCoverageCacheManager getInstance() { return _INSTANCE; }
	private ProteinSequenceCoverageCacheManager() { }
	
	
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
	public ProteinSequenceCoverage getProteinSequenceCoverageFromCache( int proteinId, SearcherCutoffValuesSearchLevel scvsl ) throws Exception {
		
		ProteinSequenceCoverageSearchParams searchParameters = ProteinSequenceCoverageSearchParams.getProteinSequenceCoverageSearchParams( scvsl, proteinId);
		
		// try memory cache first
		if( this._MEMORY_CACHE.containsKey( searchParameters ) ) {
			System.out.println( "Using memory cache." );
			return this._MEMORY_CACHE.get( searchParameters );
		}
		
		// check the database cache
		ProteinSequenceCoverage coverage = null;
		
		try {
			
			coverage = this.getProteinSequenceCoverageFromDbCache( searchParameters);
			
			this._MEMORY_CACHE.put( searchParameters, coverage );

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
		try {
			
			this.addProteinSequenceCoverageToDbCache( searchParameters, coverage );
			
		} catch ( Exception e ) {
			
			// don't die if there is a database error, but log it
			log.error( e.getMessage(), e );	
			
		}

	}
									
	
	
	
	/**
	 * Insert or replace the existing entry in the database cache for the sequence coverage in the context of the supplied search parameters
	 * 
	 * @param searchParameters
	 * @param coverage
	 */
	private void addProteinSequenceCoverageToDbCache( ProteinSequenceCoverageSearchParams searchParameters, ProteinSequenceCoverage coverage ) throws Exception  {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			ObjectMapper mapper = new ObjectMapper();	// our json serializer/de-serializer
			
			String sql = "SELECT search_param_hash_code, search_param_hash_idx, search_param_json, sequence_coverage_json FROM sequence_coverage_cache WHERE search_param_hash_code = ?";
			int maxIdx = 0;	// if there is a collision on the hashCode, use this as a secondary index
			
			boolean updatedEntry = false;		// whether or not we updated an existing entry
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE	 );
			
			pstmt.setInt( 1, searchParameters.hashCode() );
			
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				
				// ensure maxIdx stores the max idx for this hash code
				int tIdx = rs.getInt( "search_param_hash_idx" );
				if( tIdx > maxIdx ) maxIdx = tIdx;
				
				ProteinSequenceCoverageSearchParams dbParams = mapper.readValue( rs.getString( "search_param_json" ), ProteinSequenceCoverageSearchParams.class );
				if( searchParameters.equals( dbParams ) ) {
					
					updatedEntry = true;
					
					String jsonCoverage = mapper.writeValueAsString( coverage );
					rs.updateString( "sequence_coverage_json", jsonCoverage );
					rs.updateRow();
					
					break;
				}
				
			}
			
			if( !updatedEntry ) {
				
				// move to insert row, insert row using result set
				rs.moveToInsertRow();
				
				rs.updateInt( "search_param_hash_code", searchParameters.hashCode() );
				rs.updateInt( "search_param_hash_idx", maxIdx + 1 );
				rs.updateString( "search_param_json", mapper.writeValueAsString( searchParameters ) );
				
				ProteinSequenceCoverageResultsBean coverageResultsBean = new ProteinSequenceCoverageResultsBean();
				coverageResultsBean.setProteinId( coverage.getProtein().getProteinSequenceId() );
				
				/*
				 * Guava Range doesn't serialize well (at all). Load data from the ranges into my own objects
				 * and serialize those as json in the database.
				 */
				Set<CoverageRangeBean> ranges = new HashSet<>();
				for( Range<Integer> r : coverage.getRanges() ) {
					CoverageRangeBean crb = new CoverageRangeBean( r.lowerEndpoint(), r.upperEndpoint() );
					ranges.add( crb );
				}
				coverageResultsBean.setRanges( ranges );
				
				
				rs.updateString( "sequence_coverage_json", mapper.writeValueAsString( coverageResultsBean ) );
				
				rs.insertRow();
				
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

		
	}
	
	
	
	/**
	 * Attempt to get the requested protein sequence coverage from the database cache. If not found, returns null.
	 * 
	 * @param searchParameters
	 * @return
	 * @throws Exception
	 */
	private ProteinSequenceCoverage getProteinSequenceCoverageFromDbCache( ProteinSequenceCoverageSearchParams searchParameters ) throws Exception {
		
		ProteinSequenceCoverage returnedProteinCoverage = null;
		
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
				
				System.out.println( "Using db cache." );

				
				// de-serialize the ProteinSequenceCoverage from the database
				ProteinSequenceCoverageResultsBean coverageResultsBean = mapper.readValue( rs.getString( 2 ), ProteinSequenceCoverageResultsBean.class );
				
				ProteinSequenceObject ps = ProteinSequenceObjectFactory.getProteinSequenceObject( coverageResultsBean.getProteinId() );
				
				returnedProteinCoverage = new ProteinSequenceCoverage( ps );
				
				for( CoverageRangeBean crb : coverageResultsBean.getRanges() ) {
					returnedProteinCoverage.addStartEndBoundary( crb.getStart(), crb.getEnd() );
				}
				
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
		
		return returnedProteinCoverage;
	}
	

	
}
