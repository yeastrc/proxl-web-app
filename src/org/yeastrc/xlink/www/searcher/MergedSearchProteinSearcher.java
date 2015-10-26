package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.MergedSearchProteinDoublePosition;
import org.yeastrc.xlink.www.objects.MergedSearchProteinPosition;


public class MergedSearchProteinSearcher {

	private static final Logger log = Logger.getLogger(MergedSearchProteinSearcher.class);

	private MergedSearchProteinSearcher() { }
	private static final MergedSearchProteinSearcher _INSTANCE = new MergedSearchProteinSearcher();
	public static MergedSearchProteinSearcher getInstance() { return _INSTANCE; }
	
	private String getPsmColumn( int type ) throws Exception {
		if( type == XLinkUtils.TYPE_CROSSLINK ) { return "bestCrosslinkPSMQValue"; }
		if( type == XLinkUtils.TYPE_DIMER ) { return "bestDimerPSMQValue"; }
		if( type == XLinkUtils.TYPE_LOOPLINK ) { return "bestLooplinkPSMQValue"; }
		if( type == XLinkUtils.TYPE_MONOLINK ) { return "bestMonolinkPSMQValue"; }
		if( type == XLinkUtils.TYPE_UNLINKED ) { return "bestUnlinkedPSMQValue"; }
		
		throw new Exception( "Unknown link type: " + type );
	}
	
	private String getPeptideColumn( int type ) throws Exception {
		if( type == XLinkUtils.TYPE_CROSSLINK ) { return "bestCrosslinkPeptideQValue"; }
		if( type == XLinkUtils.TYPE_DIMER ) { return "bestDimerPeptideQValue"; }
		if( type == XLinkUtils.TYPE_LOOPLINK ) { return "bestLooplinkPeptideQValue"; }
		if( type == XLinkUtils.TYPE_MONOLINK ) { return "bestMonolinkPeptideQValue"; }
		if( type == XLinkUtils.TYPE_UNLINKED ) { return "bestUnlinkedPeptideQValue"; }
		
		throw new Exception( "Unknown link type: " + type );
	}
	
	/**
	 * Gets the collection of proteins from the given searches that have at least one of the supplied link types that meets the cutoffs
	 * @param searches
	 * @param types
	 * @param psmQValueCutoff
	 * @param peptideQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public Collection<MergedSearchProtein> getProteinsWithLinkType( Collection<SearchDTO> searches, Collection<Integer> types, double psmQValueCutoff, double peptideQValueCutoff ) throws Exception {
		
		if ( log.isDebugEnabled() ) {

			log.debug( "Getting proteins with type: " + StringUtils.join( types, "," ) );
		}
		
		Collection<MergedSearchProtein> proteins = new HashSet<MergedSearchProtein>();
		Collection<Integer> proteinIds = new HashSet<Integer>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT nrseq_id FROM search_protein_lookup WHERE search_id IN (#SEARCHES#)";
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			// add in the columns to filter on based on the type of peptides they're interested in
			
			sql += " AND (";
			
			boolean first = true;
			
			for( int i : types ) {
				
				if( !first )
					sql += " OR ";
				else first = false;
				
				sql += "("
				
				//  Assumption is that the PSM column not being NULL will indicate that there is data 
				//   for this link type.  Peptide column can be null as a valid value for Not Applicable.
				
				+ getPsmColumn( i ) + " IS NOT NULL AND " + getPsmColumn( i ) + " <= ?"
				+ " AND ( " + getPeptideColumn( i ) + " <= ?  OR " + getPeptideColumn( i ) + " IS NULL ) "
				
				
				+ ")";
			}
			
			sql += ")";
			
//			if ( log.isDebugEnabled() ) {
//
//				log.debug( "getProteinsWithLinkType, SQL: " + sql );
//			}
			
			pstmt = conn.prepareStatement( sql );

			for( int i = 0; i < types.size(); i++ ) {
				pstmt.setDouble( ( i * 2 ) + 1, psmQValueCutoff );
				pstmt.setDouble( ( i * 2 ) + 2, peptideQValueCutoff );				
			}

			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				proteinIds.add( rs.getInt( 1 ) );
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

		for( int pid : proteinIds ) {
			proteins.add( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( pid ) ) );
		}
		
		if ( log.isDebugEnabled() ) {

			log.debug( "Got " + proteins.size() + " proteins." );
		}
		
		return proteins;
	}
	
	/**
	 * Returns the proteins in the supplied searches that have at least one peptide with a type corresponding
	 * to the supplied type, given the supplied cutoffs
	 * @param searches
	 * @param type
	 * @param psmQValueCutoff
	 * @param peptideQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public Collection<MergedSearchProtein> getProteinsWithLinkType( Collection<SearchDTO> searches, int type, double psmQValueCutoff, double peptideQValueCutoff ) throws Exception {
		if ( log.isDebugEnabled() ) {

			log.debug( "Getting proteins with type: " + type );
		}
		
		Collection<MergedSearchProtein> proteins = new HashSet<MergedSearchProtein>();
		Collection<Integer> proteinIds = new HashSet<Integer>();
		
		String psmColumn = null;
		String peptideColumn = null;
		
		if( type == XLinkUtils.TYPE_CROSSLINK ) {
			psmColumn = "bestCrosslinkPSMQValue";
			peptideColumn = "bestCrosslinkPeptideQValue";
		} else if( type == XLinkUtils.TYPE_DIMER ) {
			psmColumn = "bestDimerPSMQValue";
			peptideColumn = "bestDimerPeptideQValue";
		} else if( type == XLinkUtils.TYPE_LOOPLINK ) {
			psmColumn = "bestLooplinkPSMQValue";
			peptideColumn = "bestLooplinkPeptideQValue";
		} else if( type == XLinkUtils.TYPE_MONOLINK ) {
			psmColumn = "bestMonolinkPSMQValue";
			peptideColumn = "bestMonolinkPeptideQValue";
		} else if( type == XLinkUtils.TYPE_UNLINKED ) {
			psmColumn = "bestUnlinkedPSMQValue";
			peptideColumn = "bestUnlinkedPeptideQValue";
		} else {
			throw new Exception( "uknown link type" );
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			//  Assumption is that the PSM column not being NULL will indicate that there is data 
			//   for this link type.  Peptide column can be null as a valid value for Not Applicable.

			String sql = "SELECT nrseq_id FROM search_protein_lookup WHERE search_id IN (#SEARCHES#) "
					+ " AND "
					+  " ( " + psmColumn + " IS NOT NULL AND " + psmColumn + " <= ? "
					+           " AND ( " + peptideColumn + " IS NULL OR " + peptideColumn + "<= ? ) "
					+  " ) "
					;
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setDouble( 1 , psmQValueCutoff );
			pstmt.setDouble( 2,  peptideQValueCutoff );

			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				proteinIds.add( rs.getInt( 1 ) );
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

		for( int pid : proteinIds ) {
			proteins.add( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( pid ) ) );
		}
		
		if ( log.isDebugEnabled() ) {

			log.debug( "Got " + proteins.size() + " proteins." );
		}
		
		return proteins;
	}
	
	
	/**
	 * For the given collection of searches, the given peptide, and given position in that peptide, find all proteins
	 * and the respective position(s) in those proteins to which that peptide and position match.
	 * @param searches
	 * @param peptide
	 * @param position
	 * @return
	 * @throws Exception
	 */
	public List<MergedSearchProteinPosition> getProteinPositions( Collection<SearchDTO> searches, PeptideDTO peptide, int position ) throws Exception {
		
		if (  peptide == null ) {
			
			String msg = "'peptide' parameter cannot be null";
			
			log.error( msg );
			
			throw new IllegalArgumentException( msg );
		}
		
		
		
		List<MergedSearchProteinPosition> proteinPositions = new ArrayList<MergedSearchProteinPosition>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"(SELECT a.nrseq_id_1 AS nseq, a.protein_1_position AS pos " +
						"FROM crosslink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id IN (#SEARCHES#) AND a.peptide_1_id = ? AND a.peptide_1_position = ?) " +

						"UNION " +

						"(SELECT a.nrseq_id_2 AS nseq, a.protein_2_position AS pos " +
						"FROM crosslink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id IN (#SEARCHES#) AND a.peptide_2_id = ? AND a.peptide_2_position = ?) " +

						"ORDER BY nseq, pos";

			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, peptide.getId() );
			pstmt.setInt( 2, position );
			pstmt.setInt( 3, peptide.getId() );
			pstmt.setInt( 4, position );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				MergedSearchProteinPosition mrpp = new MergedSearchProteinPosition();
				mrpp.setPosition( rs.getInt( 2 ) );
				mrpp.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
				proteinPositions.add( mrpp );
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
		
		return proteinPositions;
	}
	
	
	
	
	/**
	 * For the given collection of searches, the given peptide, and given position in that peptide, find all proteins
	 * and the respective position(s) in those proteins to which that peptide and position match.
	 * @param searches
	 * @param peptide
	 * @param position
	 * @return
	 * @throws Exception
	 */
	public List<MergedSearchProteinDoublePosition> getProteinPositions( Collection<SearchDTO> searches, PeptideDTO peptide, int position1, int position2 ) throws Exception {
		List<MergedSearchProteinDoublePosition> proteinPositions = new ArrayList<MergedSearchProteinDoublePosition>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"SELECT DISTINCT a.nrseq_id AS nseq, a.protein_position_1 AS pos1, a.protein_position_2 AS pos2 " +
						"FROM looplink AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id IN (#SEARCHES#) AND a.peptide_id = ? AND a.peptide_position_1 = ? AND a.peptide_position_2 = ?" +

						" ORDER BY nseq, pos1, pos2";

			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, peptide.getId() );
			pstmt.setInt( 2, position1 );
			pstmt.setInt( 3, position2 );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				MergedSearchProteinDoublePosition mrpp = new MergedSearchProteinDoublePosition();
				mrpp.setPosition1( rs.getInt( 2 ) );
				mrpp.setPosition2( rs.getInt( 3 ) );
				mrpp.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
				proteinPositions.add( mrpp );
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
		
		return proteinPositions;
	}

	

	public List<MergedSearchProteinPosition> getProteinForUnlinked( Collection<SearchDTO> searches, PeptideDTO peptide ) throws Exception {
		
		
		List<MergedSearchProteinPosition> proteinPositions = new ArrayList<MergedSearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
						"(SELECT DISTINCT a.nrseq_id AS nseq " +
						"FROM unlinked AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
						"WHERE b.search_id IN (#SEARCHES#) AND a.peptide_id = ?) " +

						"ORDER BY nseq";
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1, peptide.getId() );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				MergedSearchProteinPosition mrpp = new MergedSearchProteinPosition();
				mrpp.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );

				
				proteinPositions.add( mrpp );
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
		
		return proteinPositions;
	}
	

	public List<MergedSearchProteinPosition> getProteinForDimer( Collection<SearchDTO> searches, PeptideDTO peptide ) throws Exception {
		List<MergedSearchProteinPosition> proteinPositions = new ArrayList<MergedSearchProteinPosition>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = 
					"(SELECT a.nrseq_id_1 AS nseq " +
					"FROM dimer AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
					"WHERE b.search_id  IN (#SEARCHES#) AND a.peptide_1_id = ? ) " +

					"UNION DISTINCT " +

					"(SELECT a.nrseq_id_2 AS nseq " +
					"FROM dimer AS a INNER JOIN psm AS b ON a.psm_id = b.id " +
					"WHERE b.search_id  IN (#SEARCHES#) AND a.peptide_2_id = ? ) " +

					"ORDER BY nseq";
			
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, peptide.getId() );
			pstmt.setInt( 2, peptide.getId() );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				MergedSearchProteinPosition mrpp = new MergedSearchProteinPosition();
				mrpp.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );

				
				proteinPositions.add( mrpp );
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
		
		return proteinPositions;
	}
	
}
