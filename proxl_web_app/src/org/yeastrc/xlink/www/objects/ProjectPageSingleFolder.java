package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * A folder and it's searches to display on the project page
 *
 */
public class ProjectPageSingleFolder {
	
	private int id;
	private String folderName;
	private List<SearchDTODetailsDisplayWrapper> searches = new ArrayList<>();

	public void addSearchWrapper( SearchDTODetailsDisplayWrapper searchWrapper ) {
		searches.add( searchWrapper );
	}
	
	public List<SearchDTODetailsDisplayWrapper> getSearches() {
		return searches;
	}
	public void setSearches(List<SearchDTODetailsDisplayWrapper> searches) {
		this.searches = searches;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
