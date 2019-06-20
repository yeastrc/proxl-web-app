package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;

/**
 * Query project_level_default_fltr_ann_cutoffs_tbl by project id
 * Also include values from project_level_default_fltr_ann_cutoffs_cutoff_as_string_tbl
 *
 * This includes the annotationCutoffValueString since is specifically for the Management on the Project Page.
 * (Actually annotationCutoffValueString is not currently used on the page)
 */
public class ProjectLevelDefaultFltrAnnCutoffs_For_DisplayOnMgmtPage_Searcher {

	private static final Logger log = LoggerFactory.getLogger( ProjectLevelDefaultFltrAnnCutoffs_For_DisplayOnMgmtPage_Searcher.class);
	private ProjectLevelDefaultFltrAnnCutoffs_For_DisplayOnMgmtPage_Searcher() { }
	private static final ProjectLevelDefaultFltrAnnCutoffs_For_DisplayOnMgmtPage_Searcher _INSTANCE = new ProjectLevelDefaultFltrAnnCutoffs_For_DisplayOnMgmtPage_Searcher();
	public static ProjectLevelDefaultFltrAnnCutoffs_For_DisplayOnMgmtPage_Searcher getInstance() { return _INSTANCE; }
	
	/**
	 * 
	 *
	 */
	public static final class ResultItem {
		
		private int id;
		private PsmPeptideAnnotationType psmPeptideAnnotationType;
		private String searchProgramName;
		private String annotationTypeName;
		private double annotationCutoffValue;
		private String annotationCutoffValueString;
		
		public int getId() {
			return id;
		}
		public PsmPeptideAnnotationType getPsmPeptideAnnotationType() {
			return psmPeptideAnnotationType;
		}
		public String getSearchProgramName() {
			return searchProgramName;
		}
		public String getAnnotationTypeName() {
			return annotationTypeName;
		}
		public double getAnnotationCutoffValue() {
			return annotationCutoffValue;
		}
		public String getAnnotationCutoffValueString() {
			return annotationCutoffValueString;
		}
		public void setId(int id) {
			this.id = id;
		}
		public void setPsmPeptideAnnotationType(PsmPeptideAnnotationType psmPeptideAnnotationType) {
			this.psmPeptideAnnotationType = psmPeptideAnnotationType;
		}
		public void setSearchProgramName(String searchProgramName) {
			this.searchProgramName = searchProgramName;
		}
		public void setAnnotationTypeName(String annotationTypeName) {
			this.annotationTypeName = annotationTypeName;
		}
		public void setAnnotationCutoffValue(double annotationCutoffValue) {
			this.annotationCutoffValue = annotationCutoffValue;
		}
		public void setAnnotationCutoffValueString(String annotationCutoffValueString) {
			this.annotationCutoffValueString = annotationCutoffValueString;
		}
	}

	private static final String SQL = 
			"SELECT main_tbl.id , main_tbl.search_program_name, main_tbl.psm_peptide_type, main_tbl.annotation_type_name, main_tbl.annotation_cutoff_value, value_string_tbl.annotation_cutoff_value_string "
			+ " FROM project_level_default_fltr_ann_cutoffs_tbl AS main_tbl "
			+ " INNER JOIN project_level_default_fltr_ann_cutoffs_cutoff_as_string_tbl AS value_string_tbl"
			+ 	" ON main_tbl.id = value_string_tbl.project_level_default_fltr_ann_cutoffs_id "
			+ " WHERE main_tbl.project_id = ?";

	/**
	 * @param projectId
	 * @return 
	 * @throws Exception
	 */
	public List<ResultItem> getAllForProjectId( int projectId ) throws Exception {
		
		List<ResultItem> resultList = new ArrayList<>();
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
				ResultItem resultItem = new ResultItem();
				resultItem.setId( rs.getInt( "id" ) );
				{
					String psmPeptideAnnotationTypeString = rs.getString( "psm_peptide_type" );
					PsmPeptideAnnotationType psmPeptideAnnotationType = PsmPeptideAnnotationType.fromValue( psmPeptideAnnotationTypeString );
					resultItem.setPsmPeptideAnnotationType( psmPeptideAnnotationType );
				}
				resultItem.setSearchProgramName( rs.getString( "search_program_name" ) );
				resultItem.setAnnotationTypeName( rs.getString( "annotation_type_name" ) );
				resultItem.setAnnotationCutoffValue( rs.getDouble( "annotation_cutoff_value" ) );
				resultItem.setAnnotationCutoffValueString( rs.getString( "annotation_cutoff_value_string" ) );
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
