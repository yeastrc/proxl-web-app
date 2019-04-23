package org.yeastrc.xlink.www.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SavePageViewConstants_COPY {

	public static final String[] ALLOWED_PAGE_NAMES_FOR_DEFAULT_PAGE_VIEWS_ARRAY = {
		
		"/peptide",
		
		"/crosslinkProtein",
		"/looplinkProtein",
		"/allProtein",
		
		"/proteinCoverageReport",
		
		"/image",
		"/structure",
		"/qc",
		
		//  Merged:
		
		"/mergedPeptide",
		
		"/mergedCrosslinkProtein",
		"/mergedLooplinkProtein",
		"/mergedAllProtein",
		"/mergedProteinCoverageReport"
		
	};
	
	public static final List<String>  ALLOWED_PAGE_NAMES_FOR_DEFAULT_PAGE_VIEWS;
	
	static {
		List<String> allowedNames = Arrays.asList( ALLOWED_PAGE_NAMES_FOR_DEFAULT_PAGE_VIEWS_ARRAY );
		
		ALLOWED_PAGE_NAMES_FOR_DEFAULT_PAGE_VIEWS = Collections.unmodifiableList( allowedNames );
	}
	
	
	
}
