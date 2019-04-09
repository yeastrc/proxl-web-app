package org.yeastrc.xlink.www.forms;



/**
 * Extensions to MergedSearchViewProteinsForm for downloads
 * 
 * Not used in all downloads, only where extensions are needed
 *
 */
public class DownloadMergedSearchViewProteinsForm extends MergedSearchViewProteinsForm {

	private static final long serialVersionUID = 1L;

	private String selectedCrosslinksLooplinksMonolinksJSON;

	public String getSelectedCrosslinksLooplinksMonolinksJSON() {
		return selectedCrosslinksLooplinksMonolinksJSON;
	}
	public void setSelectedCrosslinksLooplinksMonolinksJSON(String selectedCrosslinksLooplinksMonolinksJSON) {
		this.selectedCrosslinksLooplinksMonolinksJSON = selectedCrosslinksLooplinksMonolinksJSON;
	}

	
}
