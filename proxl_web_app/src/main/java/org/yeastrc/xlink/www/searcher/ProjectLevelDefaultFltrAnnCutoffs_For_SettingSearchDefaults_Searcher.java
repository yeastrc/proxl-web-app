package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;
import org.yeastrc.xlink.www.dto.ProjectLevelDefaultFltrAnnCutoffs_DTO;

/**
 * Query project_level_default_fltr_ann_cutoffs_tbl by project id
 * 
 * The leaner retrieval for just computing the defaults
 * 
 * property 'id' is NOT set in the returned objects
 */
public class ProjectLevelDefaultFltrAnnCutoffs_For_SettingSearchDefaults_Searcher {

	private static final Logger log = LoggerFactory.getLogger( ProjectLevelDefaultFltrAnnCutoffs_For_SettingSearchDefaults_Searcher.class);
	private ProjectLevelDefaultFltrAnnCutoffs_For_SettingSearchDefaults_Searcher() { }
	private static final ProjectLevelDefaultFltrAnnCutoffs_For_SettingSearchDefaults_Searcher _INSTANCE = new ProjectLevelDefaultFltrAnnCutoffs_For_SettingSearchDefaults_Searcher();
	public static ProjectLevelDefaultFltrAnnCutoffs_For_SettingSearchDefaults_Searcher getInstance() { return _INSTANCE; }
	
	private static final String SQL = 
			"SELECT search_program_name, psm_peptide_type, annotation_type_name, annotation_cutoff_value "
			+ " FROM project_level_default_fltr_ann_cutoffs_tbl "
			+ " WHERE project_id = ?";

	/**
	 * property 'id' is NOT set in the returned objects
	 * 
	 * @param projectId
	 * @return 
	 * @throws Exception
	 */
	public List<ProjectLevelDefaultFltrAnnCutoffs_DTO> getAllForProjectId( int projectId ) throws Exception {
		
		List<ProjectLevelDefaultFltrAnnCutoffs_DTO> resultList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				ProjectLevelDefaultFltrAnnCutoffs_DTO resultItem = new ProjectLevelDefaultFltrAnnCutoffs_DTO();
				resultItem.setProjectId( projectId );
				{
					String psmPeptideAnnotationTypeString = rs.getString( "psm_peptide_type" );
					PsmPeptideAnnotationType psmPeptideAnnotationType = PsmPeptideAnnotationType.fromValue( psmPeptideAnnotationTypeString );
					resultItem.setPsmPeptideAnnotationType( psmPeptideAnnotationType );
				}
				resultItem.setSearchProgramName( rs.getString( "search_program_name" ) );
				resultItem.setAnnotationTypeName( rs.getString( "annotation_type_name" ) );
				resultItem.setAnnotationCutoffValue( rs.getDouble( "annotation_cutoff_value" ) );
				resultList.add( resultItem );
			}
		} catch ( Exception e ) {
			String msg = "Failed getAllForProjectId(...), projectId: " + projectId + ", sql: " + sql;
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
		return resultList;
	}

}
