package org.yeastrc.xlink.www.dto;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
















import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.yeastrc.xlink.dao.SearchFileDAO;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.dto.SearchCommentDTO;
import org.yeastrc.xlink.dto.SearchFileDTO;
import org.yeastrc.xlink.www.searcher.SearchCommentSearcher;
import org.yeastrc.xlink.www.searcher.SearchLinkerSearcher;
import org.yeastrc.xlink.www.searcher.SearchWebLinksSearcher;


/**
 * Table search
 *
 */
public class SearchDTO implements Comparable<SearchDTO> {
	
	private static final Logger log = Logger.getLogger(SearchDTO.class);

	
	/* 
	 * Default order by Search Id field
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SearchDTO o) {
		
		return this.getId() - o.getId();
	}

	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<SearchFileDTO> getFiles() throws Exception {

		List<SearchFileDTO>  fileList = 
				SearchFileDAO.getInstance().getSearchFileDTOForSearchId( id );

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
			return SearchWebLinksSearcher.getInstance().getWebLinksForSearch(this);

		} catch ( Exception e ) {

			String msg = "Exception caught in getWebLinks(): " + e.toString();

			log.error( msg, e );

			throw e;
		}
	}
	
	
	public List<LinkerDTO> getLinkersSorted() throws Exception {

		List<LinkerDTO> linkers = getLinkers();
		
		//  sort on the names
		
		Collections.sort( linkers, new Comparator<LinkerDTO>() { 

			@Override
			public int compare(LinkerDTO o1, LinkerDTO o2) {

				return o1.getName().compareTo( o2.getName() );
		
			}
		} );
		
		return linkers;
	}
	
	public List<LinkerDTO> getLinkers() throws Exception {

		try {
			return SearchLinkerSearcher.getInstance().getLinkersForSearch(this);

		} catch ( Exception e ) {

			String msg = "Exception caught in getLinkers(): " + e.toString();

			log.error( msg, e );

			throw e;
		}
	}	

	
//	public String getName() {
//		return name;
//	}
	
	public String getName() {
		if( this.name != null )
			return name;
		else
			return "Search: " + this.id;
	}

	
	public String getName_AsActuallyInObject() {
		return name;
	}

	
	
	//  Setters and Getters
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public boolean isInsertComplete() {
		return insertComplete;
	}
	public void setInsertComplete(boolean insertComplete) {
		this.insertComplete = insertComplete;
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
	public boolean isNoScanData() {
		return noScanData;
	}
	public void setNoScanData(boolean noScanData) {
		this.noScanData = noScanData;
	}



	private int id;
	private String path;
	private DateTime load_time;
	private String fastaFilename;
	private String name;
	private int projectId;
	private boolean insertComplete;
	private String directoryName;
	private int displayOrder;
	private boolean noScanData;



	
}
