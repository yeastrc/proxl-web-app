package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplaySearchLevel;
import org.yeastrc.xlink.www.searcher.SearchProgramDisplaySearcher;
import org.yeastrc.xlink.www.web_utils.GetCutoffsAppliedOnImportForSearchList;

/**
 * Used for the searchDetailsBlock.jsp and viewProject.jsp
 *
 */
public class SearchDTODetailsDisplayWrapper {
	
//	private static final Logger log = Logger.getLogger( SearchDTODetailsDisplayWrapper.class );
	
	private SearchDTO searchDTO;
	private List<SearchProgramDisplay> searchProgramDisplayList; 
	private List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList;
	private String cutoffsAppliedOnImportAllAsString;
	
	/**
	 * Not used on viewProject.jsp
	 */
	private CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel;
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public String getLinkersDisplayString() throws Exception {
		
		if ( searchDTO == null ) {
			throw new IllegalStateException( "searchDTO == null");
		}
		List<LinkerDTO> linkers = searchDTO.getLinkers();
		if ( linkers == null || linkers.isEmpty() ) {
			return "";
		}
		List<String> linkerAbbreviations = new ArrayList<>( linkers.size() );
		for ( LinkerDTO linker : linkers ) {
			linkerAbbreviations.add( linker.getAbbr() );
		}
		Collections.sort( linkerAbbreviations );
		String linkersString = linkerAbbreviations.get(0);
		if ( linkerAbbreviations.size() > 1 ) {
			//  start loop at second index
			for ( int index = 1; index < linkerAbbreviations.size(); index++ ) {
				linkersString += ", ";
				linkersString += linkerAbbreviations.get( index );
			}
		}
		return linkersString;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<SearchProgramDisplay> getSearchPrograms() throws Exception {
		if ( searchProgramDisplayList != null ) {
			return searchProgramDisplayList;
		}
		int searchId = getSearchDTO().getSearchId();
		searchProgramDisplayList = SearchProgramDisplaySearcher.getInstance().getSearchProgramDisplay( searchId );
		return searchProgramDisplayList;
	}
	
	public List<CutoffsAppliedOnImportWebDisplay> getCutoffsAppliedOnImportList() throws Exception {
		if ( cutoffsAppliedOnImportList != null ) {
			return cutoffsAppliedOnImportList;
		}
		if ( searchDTO == null ) {
			throw new IllegalStateException( "searchDTO == null");
		}
		cutoffsAppliedOnImportList = GetCutoffsAppliedOnImportForSearchList.getInstance().getCutoffsAppliedOnImportList( searchDTO.getSearchId() );
		return cutoffsAppliedOnImportList;
	}
	
	public String getCutoffsAppliedOnImportAllAsString() throws Exception {
		if ( cutoffsAppliedOnImportAllAsString != null ) {
			return cutoffsAppliedOnImportAllAsString;
		}
		List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList = this.getCutoffsAppliedOnImportList();
		if ( cutoffsAppliedOnImportList == null || ( cutoffsAppliedOnImportList.isEmpty() ) ) {
			cutoffsAppliedOnImportAllAsString = "";
			return cutoffsAppliedOnImportAllAsString;
		}
		StringBuilder peptideCutoffsOnImport = new StringBuilder();
		StringBuilder psmCutoffsOnImport = new StringBuilder();
		boolean firstPeptideCutoffsOnImport = true;
		boolean firstPsmCutoffsOnImport = true;
		for ( CutoffsAppliedOnImportWebDisplay cutoffsAppliedOnImport : cutoffsAppliedOnImportList ) {
			if ( cutoffsAppliedOnImport.isPeptideCutoff() ) {
				if ( peptideCutoffsOnImport.length() == 0 ) {
					peptideCutoffsOnImport.append( "Peptide Cutoffs: " );
				}
				if ( firstPeptideCutoffsOnImport ) {
					firstPeptideCutoffsOnImport = false;
				} else {
					peptideCutoffsOnImport.append( ", " );
				}
				peptideCutoffsOnImport.append( cutoffsAppliedOnImport.getAnnotationName() );
				peptideCutoffsOnImport.append( ": " );
				peptideCutoffsOnImport.append( cutoffsAppliedOnImport.getCutoffValue() );
			} else {
				if ( psmCutoffsOnImport.length() == 0 ) {
					psmCutoffsOnImport.append( "PSM Cutoffs: " );
				}
				if ( firstPsmCutoffsOnImport ) {
					firstPsmCutoffsOnImport = false;
				} else {
					psmCutoffsOnImport.append( ", " );
				}
				psmCutoffsOnImport.append( cutoffsAppliedOnImport.getAnnotationName() );
				psmCutoffsOnImport.append( ": " );
				psmCutoffsOnImport.append( cutoffsAppliedOnImport.getCutoffValue() );
			}
		}
		cutoffsAppliedOnImportAllAsString = peptideCutoffsOnImport + "; " + psmCutoffsOnImport;
		return cutoffsAppliedOnImportAllAsString;
	}
	
	//  Getters & Setters
	public SearchDTO getSearchDTO() {
		return searchDTO;
	}
	public void setSearchDTO(SearchDTO searchDTO) {
		this.searchDTO = searchDTO;
	}
	public CutoffPageDisplaySearchLevel getCutoffPageDisplaySearchLevel() {
		return cutoffPageDisplaySearchLevel;
	}
	public void setCutoffPageDisplaySearchLevel(
			CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel) {
		this.cutoffPageDisplaySearchLevel = cutoffPageDisplaySearchLevel;
	}
}
