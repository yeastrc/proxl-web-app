package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * The folders and searches to display on the project page
 *
 */
public class ProjectPageFoldersSearches {

	private List<SearchDTODetailsDisplayWrapper> searchesNotInFolders;
	private List<ProjectPageSingleFolder> folders;
	private boolean noSearchesFound;
	
	public List<SearchDTODetailsDisplayWrapper> getSearchesNotInFolders() {
		return searchesNotInFolders;
	}
	public void setSearchesNotInFolders(List<SearchDTODetailsDisplayWrapper> searchesNotInFolders) {
		this.searchesNotInFolders = searchesNotInFolders;
	}
	public List<ProjectPageSingleFolder> getFolders() {
		return folders;
	}
	public void setFolders(List<ProjectPageSingleFolder> folders) {
		this.folders = folders;
	}
	public boolean isNoSearchesFound() {
		return noSearchesFound;
	}
	public void setNoSearchesFound(boolean noSearchesFound) {
		this.noSearchesFound = noSearchesFound;
	}
}
