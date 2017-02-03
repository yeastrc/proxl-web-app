package org.yeastrc.proxl.import_xml_to_db.dto;

import java.util.List;











import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.yeastrc.xlink.dao.SearchFileDAO;
import org.yeastrc.xlink.dto.SearchFileDTO;


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
	public boolean isHasScanData() {
		return hasScanData;
	}
	public void setHasScanData(boolean hasScanData) {
		this.hasScanData = hasScanData;
	}




	private int id;
	private String path;
	private DateTime load_time;
	private String fastaFilename;
	private String directoryName;
	private boolean hasScanData;




	
}
