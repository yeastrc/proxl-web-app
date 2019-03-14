package org.yeastrc.xlink.www.dto;

import java.util.List;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.yeastrc.xlink.dao.SearchFileProjectSearchDAO;
import org.yeastrc.xlink.dto.SearchCommentDTO;
import org.yeastrc.xlink.dto.SearchFileProjectSearchDTO;
import org.yeastrc.xlink.www.searcher.SearchCommentSearcher;
import org.yeastrc.xlink.www.searcher.SearchWebLinksSearcher;

/**
 * Table search
 *
 */
public class SearchDTO implements Comparable<SearchDTO> {
	private static final Logger log = Logger.getLogger(SearchDTO.class);

	//  equals(...) on projectSearchId and searchId 
	private int projectSearchId;
	private int searchId;
	private String path;
	private DateTime load_time;
	private String fastaFilename;
	private String name;
	private int projectId;
	private String directoryName;
	private int displayOrder;
	private boolean hasScanData;
	
	///////  Constructors
	
	public SearchDTO() {
		
	}
	
	public SearchDTO( Search_Core_DTO search_Core_DTO ) {
		this.projectSearchId = search_Core_DTO.getProjectSearchId();
		this.searchId = search_Core_DTO.getSearchId();
		this.path = search_Core_DTO.getPath();
		this.load_time = search_Core_DTO.getLoad_time();
		this.fastaFilename = search_Core_DTO.getFastaFilename();
		this.name = search_Core_DTO.getName();
		this.projectId = search_Core_DTO.getProjectId();
		this.directoryName = search_Core_DTO.getDirectoryName();
		this.displayOrder = search_Core_DTO.getDisplayOrder();
		this.hasScanData = search_Core_DTO.isHasScanData();
	}
	
	
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchDTO other = (SearchDTO) obj;
		if (projectSearchId != other.projectSearchId)
			return false;
		if (searchId != other.searchId)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + projectSearchId;
		result = prime * result + searchId;
		return result;
	}

	/* 
	 * Default order by Search Id field
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SearchDTO o) {
		return this.getSearchId() - o.getSearchId();
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<SearchFileProjectSearchDTO> getFiles() throws Exception {
		List<SearchFileProjectSearchDTO> fileList =
				SearchFileProjectSearchDAO.getInstance().getSearchFileProjectSearchDTOForProjectSearchId( projectSearchId );
		return fileList;
	}
	
	public String getFormattedLoadTime() {
		try {
			return this.load_time.toString( DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss") );
		} catch ( RuntimeException e ) {
			String msg = "Exception caught in getFormattedLoadTime(): " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	public List<SearchCommentDTO> getComments() throws Exception {
		try {
			return SearchCommentSearcher.getInstance().getCommentsForSearch( this );
		} catch ( Exception e ) {
			String msg = "Exception caught in getComments(): " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	public List<SearchWebLinksDTO> getWebLinks() throws Exception {
		try {
			return SearchWebLinksSearcher.getInstance().getWebLinksForSearch( this.projectSearchId );
		} catch ( Exception e ) {
			String msg = "Exception caught in getWebLinks(): " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
//	public List<LinkerDTO> getLinkersSorted() throws Exception {
//		List<LinkerDTO> linkers = getLinkers();
//		//  sort on the names
//		Collections.sort( linkers, new Comparator<LinkerDTO>() { 
//			@Override
//			public int compare(LinkerDTO o1, LinkerDTO o2) {
//				return o1.getName().compareTo( o2.getName() );
//			}
//		} );
//		return linkers;
//	}
	
//	public List<LinkerDTO> getLinkers() throws Exception {
//		try {
//			Linkers_ForSearchId_Response linkers_ForSearchId_Response =
//					Cached_Linkers_ForSearchId.getInstance()
//					.getLinkers_ForSearchId_Response( this.searchId );
//			return linkers_ForSearchId_Response.getLinkersForSearchIdList();
//		} catch ( Exception e ) {
//			String msg = "Exception caught in getLinkers(): " + e.toString();
//			log.error( msg, e );
//			throw e;
//		}
//	}	
	
//	public String getName() {
//		return name;
//	}
	public String getName() {
		if( this.name != null )
			return name;
		else
			return "Search: " + this.searchId;
	}
	public String getName_AsActuallyInObject() {
		return name;
	}
	//  Setters and Getters
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int id) {
		this.searchId = id;
	}
	public DateTime getLoad_time() {
		return load_time;
	}
	public void setLoad_time(DateTime load_time) {
		this.load_time = load_time;
	}
	public String getFastaFilename() {
		return fastaFilename;
	}
	public void setFastaFilename(String fastaFilename) {
		this.fastaFilename = fastaFilename;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDirectoryName() {
		return directoryName;
	}
	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}
	public int getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	public boolean isHasScanData() {
		return hasScanData;
	}
	public void setHasScanData(boolean hasScanData) {
		this.hasScanData = hasScanData;
	}
	
}
