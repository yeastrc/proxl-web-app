package org.yeastrc.xlink.linker_data_processing_base;

import java.util.List;

import org.yeastrc.xlink.enum_classes.SearchLinkerProteinTerminusType;

/**
 * Definition of a single linker - Specifically and only:  What residues and protein termini are linkable
 * 
 * Internal to Package - Package Private - used in class Linker_Main, created in class Linker_Main_SingleLinker_Factory
 * 
 * What residues and protein termini are linkable
 * 
 *
 */
class Z_SingleLinkerDefinition_Internal {

	LinkerPerSide linkerPerSide_1;
	LinkerPerSide linkerPerSide_2;
	
	static class LinkerPerSide {

		List<String> linkableResidueList;
		List<LinkableProteinTerminus> linkableProteinTerminusList;
	}
	
	static class LinkableProteinTerminus {
		SearchLinkerProteinTerminusType proteinTerminus_c_n;
		int distanceFromTerminus; // 0 indicates at the terminus
	}
}
