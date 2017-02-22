package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.FolderForProjectDAO;
import org.yeastrc.xlink.www.dao.FolderProjectSearchDAO;
import org.yeastrc.xlink.www.dto.FolderForProjectDTO;
import org.yeastrc.xlink.www.dto.FolderProjectSearchDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.objects.ProjectPageFoldersSearches;
import org.yeastrc.xlink.www.objects.ProjectPageSingleFolder;
import org.yeastrc.xlink.www.objects.SearchDTODetailsDisplayWrapper;
import org.yeastrc.xlink.www.searcher.SearchSearcher;

/**
 * For the project page, retrieve searches and put in folders
 *
 */
public class ViewProjectSearchesInFolders {

	private static final Logger log = Logger.getLogger( ViewProjectSearchesInFolders.class );
			
	// private constructor
	private ViewProjectSearchesInFolders() { }
	
	public static ViewProjectSearchesInFolders getInstance() {
		return new ViewProjectSearchesInFolders();
	}

	/**
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public ProjectPageFoldersSearches getProjectPageFoldersSearches( int projectId ) throws Exception {

		ProjectPageFoldersSearches projectPageFoldersSearches = new ProjectPageFoldersSearches();

		List<SearchDTO> searches = SearchSearcher.getInstance().getSearchsForProjectId( projectId );
		if ( searches.isEmpty() ) {
			projectPageFoldersSearches.setNoSearchesFound( true );
			return projectPageFoldersSearches; //  EARLY EXIT
		}
		
		projectPageFoldersSearches.setNoSearchesFound( false );
		
		List<SearchDTODetailsDisplayWrapper> searchDTODetailsDisplayWrapperList = new ArrayList<>( searches.size() );
		for ( SearchDTO search : searches ) {
			SearchDTODetailsDisplayWrapper searchDTODetailsDisplayWrapper = new SearchDTODetailsDisplayWrapper();
			searchDTODetailsDisplayWrapper.setSearchDTO(search);
			searchDTODetailsDisplayWrapperList.add(searchDTODetailsDisplayWrapper);
		}

		//  Get data for Put searches into folders
		
		List<FolderForProjectDTO> folderForProjectList = FolderForProjectDAO.getInstance().getFolderForProjectDTO_ForProjectId( projectId );
		List<FolderProjectSearchDTO> folderProjectSearchList = FolderProjectSearchDAO.getInstance().getFolderProjectSearchDTO_ForProjectId( projectId );
		//  Sort folders
		Collections.sort( folderForProjectList, new Comparator<FolderForProjectDTO>() {
			@Override
			public int compare(FolderForProjectDTO o1, FolderForProjectDTO o2) {
				return o1.getDisplayOrder() - o2.getDisplayOrder();
			}
		});
		//  ProjectPageSingleFolder objects into a map keyed on folderId
		Map<Integer,ProjectPageSingleFolder> projectPageSingleFolder_KeyedFolderId_Map = new HashMap<>( folderForProjectList.size() );
		for ( FolderForProjectDTO folderForProjectItem : folderForProjectList ) {
			ProjectPageSingleFolder projectPageSingleFolder = new ProjectPageSingleFolder();
			projectPageSingleFolder.setId( folderForProjectItem.getId() );
			projectPageSingleFolder.setFolderName( folderForProjectItem.getName() );
			projectPageSingleFolder.setSearches( new ArrayList<>( searches.size() ) );
			projectPageSingleFolder_KeyedFolderId_Map.put( folderForProjectItem.getId(), projectPageSingleFolder );
		}
		//  FolderProjectSearchDTO objects into a map keyed on project_search_id
		Map<Integer,FolderProjectSearchDTO> folderProjectSearchDTO_KeyedProjectSearchId_Map = new HashMap<>( folderProjectSearchList.size() );
		for ( FolderProjectSearchDTO folderProjectSearchItem : folderProjectSearchList ) {
			folderProjectSearchDTO_KeyedProjectSearchId_Map.put( folderProjectSearchItem.getProjectSearchId(), folderProjectSearchItem );
		}
		
		//  Put searches into folders

		//  The searches that are not in any folders
		List<SearchDTODetailsDisplayWrapper> searchesNotInAnyFolders = new ArrayList<>( searches.size() );

		for ( SearchDTODetailsDisplayWrapper searchWrapperItem : searchDTODetailsDisplayWrapperList ) {
			SearchDTO search = searchWrapperItem.getSearchDTO();
			FolderProjectSearchDTO folderProjectSearchDTO = folderProjectSearchDTO_KeyedProjectSearchId_Map.get( search.getProjectSearchId() );
			if ( folderProjectSearchDTO == null ) {
				//  search_project Not in any folder
				searchesNotInAnyFolders.add( searchWrapperItem );
				continue;  // EARLY CONTINUE to next list entry
			}
			ProjectPageSingleFolder projectPageSingleFolder = projectPageSingleFolder_KeyedFolderId_Map.get( folderProjectSearchDTO.getFolderId() );
			if ( projectPageSingleFolder == null ) {
				//  No folder entry for folder id.  This is an error that the database should not allow due to foreign key constraints
				//  Log this and put the search in the "NotInFolders".
				String msg = "Folder record not found for folder id: " + folderProjectSearchDTO.getFolderId();
				log.error( msg );
				searchesNotInAnyFolders.add( searchWrapperItem );
				continue;  // EARLY CONTINUE to next list entry
			}
			projectPageSingleFolder.addSearchWrapper( searchWrapperItem );
		}

		//  Generate list of folders
		List<ProjectPageSingleFolder> projectPageSingleFolderList = new ArrayList<>( folderForProjectList.size() );
		for ( FolderForProjectDTO folderForProjectItem : folderForProjectList ) {
			ProjectPageSingleFolder projectPageSingleFolder = projectPageSingleFolder_KeyedFolderId_Map.get( folderForProjectItem.getId() );
			if ( projectPageSingleFolder == null ) {
				String msg = "Unexpected projectPageSingleFolder_KeyedFolderId_Map.get( folderForProjectItem.getId() ); returned null";
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			sortSearchesOnDisplayOrder( projectPageSingleFolder.getSearches() );
			projectPageSingleFolderList.add( projectPageSingleFolder );
		}
		sortSearchesOnDisplayOrder( searchesNotInAnyFolders );

		projectPageFoldersSearches.setSearchesNotInFolders( searchesNotInAnyFolders );
		projectPageFoldersSearches.setFolders( projectPageSingleFolderList );
		
		return projectPageFoldersSearches;
	}
	
	/**
	 * @param searchWrapperList - Sort on Display order
	 */
	private void sortSearchesOnDisplayOrder( List<SearchDTODetailsDisplayWrapper> searchWrapperList ) {
		Collections.sort( searchWrapperList, new Comparator<SearchDTODetailsDisplayWrapper>() {
			@Override
			public int compare(SearchDTODetailsDisplayWrapper o1, SearchDTODetailsDisplayWrapper o2) {
				return o1.getSearchDTO().getDisplayOrder() - o2.getSearchDTO().getDisplayOrder();
			}
		});
		
	}
}
