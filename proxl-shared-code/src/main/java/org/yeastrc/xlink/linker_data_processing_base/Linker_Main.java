package org.yeastrc.xlink.linker_data_processing_base;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchMonolinkMassDTO;
import org.yeastrc.xlink.enum_classes.SearchLinkerProteinTerminusType;
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;
import org.yeastrc.xlink.exceptions.ProxlBaseInternalErrorException;
import org.yeastrc.xlink.linker_data_processing_base.Z_SingleLinkerDefinition_Internal.LinkableProteinTerminus;
import org.yeastrc.xlink.linker_data_processing_base.Z_SingleLinkerDefinition_Internal.LinkerPerSide;
import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin.ILinker_Builtin_Linker;

/**
 * Main Linker processing for a single linker abbreviation for a single search
 * 
 * Package Private.  Created by Linker_Main_Factory.
 * 
 * Holds reference to ILinker_Builtin_Linker if the Linker Abbreviation corresponds to a built in linker
 *
 */
class Linker_Main implements ILinker_Main {

	private static final Logger log = LoggerFactory.getLogger(  Linker_Main.class );

	/////////
	
	private String linkerAbbreviation;
	private int searchId;
	private ILinker_Builtin_Linker linker_Builtin_Linker;
	private Z_SingleLinkerDefinition_Internal z_SingleLinkerDefinition_Internal;

	private Set<String> crosslinkFormulaSet;
	private String crosslinkFormulaOnlyOne; // Populated if only one
	
	private Double spacerArmLength; //  From DB: May be null
	private String spacerArmLengthString; //  From DB: May be null
	
	
	///  Data from DB
	
	private List<LinkerPerSearchMonolinkMassDTO> linkerPerSearchMonolinkMassDTOList;
	private List<LinkerPerSearchCrosslinkMassDTO> linkerPerSearchCrosslinkMassDTOList;
	private List<LinkerPerSearchCleavedCrosslinkMassDTO> linkerPerSearchCleavedCrosslinkMassDTOList;
	
	/**
	 * Constructor
	 * 
	 * @param linkerAbbreviation
	 * @param linker_Builtin_Linker
	 */
	Linker_Main( 
			String linkerAbbreviation, 
			ILinker_Builtin_Linker linker_Builtin_Linker, 
			Z_SingleLinkerDefinition_Internal z_SingleLinkerDefinition_Internal,
			List<LinkerPerSearchCrosslinkMassDTO> linkerPerSearchCrosslinkMassDTOList,
			List<LinkerPerSearchMonolinkMassDTO> linkerPerSearchMonolinkMassDTOList,
			List<LinkerPerSearchCleavedCrosslinkMassDTO> linkerPerSearchCleavedCrosslinkMassDTOList
			) {
		super();
		this.linkerAbbreviation = linkerAbbreviation;
		this.linker_Builtin_Linker = linker_Builtin_Linker;
		this.z_SingleLinkerDefinition_Internal = z_SingleLinkerDefinition_Internal;
		
		this.linkerPerSearchCrosslinkMassDTOList = linkerPerSearchCrosslinkMassDTOList;
		this.linkerPerSearchMonolinkMassDTOList = linkerPerSearchMonolinkMassDTOList;
		this.linkerPerSearchCleavedCrosslinkMassDTOList = linkerPerSearchCleavedCrosslinkMassDTOList;

		{
			boolean foundcrosslinkFormulasOnDBEntries = true;
		
			if ( linkerPerSearchCrosslinkMassDTOList != null && ( ! linkerPerSearchCrosslinkMassDTOList.isEmpty() ) ) {
				
				Set<String> crosslinkFormulaSet_Local = new HashSet<>();
				for ( LinkerPerSearchCrosslinkMassDTO item : linkerPerSearchCrosslinkMassDTOList ) {
					if ( StringUtils.isNotEmpty( item.getChemicalFormula() ) ) {
						crosslinkFormulaSet_Local.add( item.getChemicalFormula() );
					} else {
						foundcrosslinkFormulasOnDBEntries = false;
					}
				}
				if ( foundcrosslinkFormulasOnDBEntries ) {
					this.crosslinkFormulaSet = Collections.unmodifiableSet( crosslinkFormulaSet_Local );
					if ( crosslinkFormulaSet_Local.size() == 1 ) {
						this.crosslinkFormulaOnlyOne = crosslinkFormulaSet_Local.iterator().next(); // Populated if only one
					}
				}
			}
			if ( ! foundcrosslinkFormulasOnDBEntries ) {
				if ( linker_Builtin_Linker != null ) {
					//  Get crosslinker chemical formulas from builtin linker, if linker_Builtin_Linker populated and returns values
					Set<String> crosslinkFormulaSet_Local = linker_Builtin_Linker.getCrosslinkFormulas();
					if ( crosslinkFormulaSet_Local != null ) {
						this.crosslinkFormulaSet = Collections.unmodifiableSet( crosslinkFormulaSet_Local );
						if ( crosslinkFormulaSet_Local.size() == 1 ) {
							this.crosslinkFormulaOnlyOne = crosslinkFormulaSet_Local.iterator().next(); // Populated if only one
						}
					}
				}
			}
		}
		
	}

	/////////
	
	@Override
	public String getLinkerAbbreviation() {
		return linkerAbbreviation;
	}

	@Override
	public int getSearchId() {
		return searchId;
	}
	
	/**
	 * Package Private
	 * @return
	 */
	boolean isLinkablePositionsAvailable() {
		
		if ( z_SingleLinkerDefinition_Internal != null || linker_Builtin_Linker != null ) {
			return true;
		}
		return false;
	}

	/**
	 * Get all theoretically linkable positions (N-terminal residue is position 1)
	 * in the supplied protein sequence for this crosslinker
	 * 
	 * !!  Returns Null if not available for Search or for Linker in Search
	 * 
	 * @param proteinSequence
	 * @return Null if not available for Search or for Linker in Search: A collection of integers corresponding to linkable protein positions
	 * @throws Exception
	 */
	@Override
	public Set<Integer> getLinkablePositions(String proteinSequence) throws Exception {
		
		if ( z_SingleLinkerDefinition_Internal != null ) {
			//  Have Linkable residues and protein termini from DB for this linker for this search
			
			Set<Integer> linkablePositions = new HashSet<>();
			getLinkablePositions_ForLinkerSide( proteinSequence, z_SingleLinkerDefinition_Internal.linkerPerSide_1, linkablePositions );
			getLinkablePositions_ForLinkerSide( proteinSequence, z_SingleLinkerDefinition_Internal.linkerPerSide_2, linkablePositions );
			
			return linkablePositions;  //  EARLY EXIT
		}

		if ( linker_Builtin_Linker != null ) {
			return linker_Builtin_Linker.getLinkablePositions( proteinSequence );  //  EARLY EXIT
		}
		
		return null;
	}
	

	/**
	 * Get all theoretically linkable positions (N-terminal residue is position 1)
	 * in the querySequence given that one end is known to be the subjectPosition in
	 * the subjectSequence. If the residue at the subjectPosition in the subjectSequence
	 * is, itself, not a linkable position, an empty collection is returned.
	 * 
	 * !!  Returns Null if not available for Search or for Linker in Search
	 * 
	 * @param querySequence
	 * @param subjectSequence
	 * @param subjectPosition
	 * @return Null if not available for Search or for Linker in Search
	 * @throws Exception
	 */
	@Override
	public Set<Integer> getLinkablePositions(String querySequence, String subjectSequence, int subjectPosition) throws Exception {

		if ( subjectPosition < 1 || subjectPosition > subjectSequence.length() ) {
			//  Invalid Subject position
			String msg = "getLinkablePositions(String querySequence, String subjectSequence, int subjectPosition): subjectPosition < 1 || subjectPosition > subjectSequence.length(): subjectPosition: "
					+ subjectPosition
					+ ", subjectSequence: " + subjectSequence;
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		if ( z_SingleLinkerDefinition_Internal != null ) {
			//  Have Linkable residues and protein termini from DB for this linker for this search
			Set<Integer> linkablePositions = new HashSet<>();
			
			if ( isProteinLinkableAtPosition_ForLinkerSide( subjectSequence, subjectPosition, z_SingleLinkerDefinition_Internal.linkerPerSide_1 ) ) {
				//  Side 1 is linkable for subjectSequence and subjectPosition so add linkable positions for side 2
				getLinkablePositions_ForLinkerSide( querySequence, z_SingleLinkerDefinition_Internal.linkerPerSide_2, linkablePositions );
			}

			if ( isProteinLinkableAtPosition_ForLinkerSide( subjectSequence, subjectPosition, z_SingleLinkerDefinition_Internal.linkerPerSide_2 ) ) {
				//  Side 2 is linkable for subjectSequence and subjectPosition so add linkable positions for side 1
				getLinkablePositions_ForLinkerSide( querySequence, z_SingleLinkerDefinition_Internal.linkerPerSide_1, linkablePositions );
			}
			
			return linkablePositions;  //  EARLY EXIT
		}
		
		if ( linker_Builtin_Linker != null ) {
			return linker_Builtin_Linker.getLinkablePositions( querySequence, subjectSequence, subjectPosition );
		}
		return null;
	}

	/**
	 * @param proteinSequence
	 * @param linkerPerSide
	 * @param linkablePositions
	 */
	private void getLinkablePositions_ForLinkerSide( String proteinSequence, LinkerPerSide linkerPerSide, Set<Integer> linkablePositions ) {
		
		if ( linkerPerSide.linkableProteinTerminusList != null ) {
			for ( LinkableProteinTerminus linkableProteinTerminus : linkerPerSide.linkableProteinTerminusList ) {
				if ( linkableProteinTerminus.proteinTerminus_c_n == SearchLinkerProteinTerminusType.N ) {
					//  'N' terminus
					int linkablePosition = linkableProteinTerminus.distanceFromTerminus + 1; // + 1 since position is 1 based
					linkablePositions.add( linkablePosition );
				} else if ( linkableProteinTerminus.proteinTerminus_c_n == SearchLinkerProteinTerminusType.C ) {
					//  'C' terminus
					int linkablePosition = proteinSequence.length() - linkableProteinTerminus.distanceFromTerminus;  // position is 1 based
					linkablePositions.add( linkablePosition );
				} else {
					String msg = "linkableProteinTerminus.proteinTerminus_c_n is not a valid value, is: " + linkableProteinTerminus.proteinTerminus_c_n;
					log.error( msg );
					throw new ProxlBaseInternalErrorException(msg);
				}
			}
		}

		if ( linkerPerSide.linkableResidueList != null ) {
			for ( String residue : linkerPerSide.linkableResidueList ) {
				int index = proteinSequence.indexOf( residue );
				while (index >= 0) {
					int linkablePosition = index + 1;
					linkablePositions.add( linkablePosition );
				    index = proteinSequence.indexOf( residue, index + 1);
				}
			}
		}
	}
	
	/**
	 * @param proteinSequence
	 * @param linkerPerSide
	 * @param linkablePositions
	 */
	private boolean isProteinLinkableAtPosition_ForLinkerSide( String subjectSequence, int subjectPosition, LinkerPerSide linkerPerSide ) {

		if ( subjectPosition < 1 || subjectPosition > subjectSequence.length() ) {
			//  Invalid Subject position
			String msg = "isProteinLinkableAtPosition_ForLinkerSide( String subjectSequence, int subjectPosition, LinkerPerSide linkerPerSide ): subjectPosition < 1 || subjectPosition > subjectSequence.length(): subjectPosition: "
					+ subjectPosition
					+ ", subjectSequence: " + subjectSequence;
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		if ( linkerPerSide.linkableProteinTerminusList != null ) {
			for ( LinkableProteinTerminus linkableProteinTerminus : linkerPerSide.linkableProteinTerminusList ) {
				if ( linkableProteinTerminus.proteinTerminus_c_n == SearchLinkerProteinTerminusType.N ) {
					//  'N' terminus
					int linkablePosition = linkableProteinTerminus.distanceFromTerminus + 1; // + 1 since position is 1 based
					if ( linkablePosition == subjectPosition ) {
						//  Found match for subjectPosition so return true
						return true; // EARLY EXIT
					}
				} else if ( linkableProteinTerminus.proteinTerminus_c_n == SearchLinkerProteinTerminusType.C ) {
					//  'C' terminus
					int linkablePosition = subjectSequence.length() - linkableProteinTerminus.distanceFromTerminus;  // position is 1 based
					if ( linkablePosition == subjectPosition ) {
						//  Found match for subjectPosition so return true
						return true; // EARLY EXIT
					}
				} else {
					String msg = "linkableProteinTerminus.proteinTerminus_c_n is not a valid value, is: " + linkableProteinTerminus.proteinTerminus_c_n;
					log.error( msg );
					throw new ProxlBaseInternalErrorException(msg);
				}
			}
		}

		if ( linkerPerSide.linkableResidueList != null ) {
			int indexForPosition = subjectPosition - 1; // subjectPosition is 1 based
			String residueAtPosition = subjectSequence.substring( indexForPosition, indexForPosition + 1 );
			for ( String linkableResidue : linkerPerSide.linkableResidueList ) {
				if ( residueAtPosition.equals( linkableResidue ) ) {
					//  Found match for subjectPosition so return true
					return true; // EARLY EXIT
				}
			}
		}
		
		return false; // Not Found, return false
	}
	
	
	/////////////////////
	
	/**
	 * Get the length, in Angstroms of this crosslinker
	 * 
	 * @return Null if not available for Search or for Linker in Search
	 */
	@Override
	public Double getLinkerLength() {

		if ( true ) {
			throw new RuntimeException( "UNTESTED CODE: getLinkerLength()" );
		}
		
		/////////////  UNTESTED CODE

		if ( spacerArmLength != null ) {
			return spacerArmLength;
		}
		if ( linker_Builtin_Linker != null ) {
			return linker_Builtin_Linker.getLinkerLength();
		}
		return null;
	}

	/**
	 * Get the formula of the cross-linker after it has linked. Essentially the formula of the spacer arm
	 * after the cross-link reaction.
	 * 
	 * @return Null if not available for Search or for Linker in Search
	 */
	@Override
	public Set<String> getCrosslinkFormulas() {
		
		return crosslinkFormulaSet; // set in constructor
	}

	/**
	 * Attempt to get the cross link formula for the given mass. For linkers with
	 * multiple cross-link masses, an attempt is made to find the correct formula
	 * for the supplied mass. If none is found, an exception is thrown.
	 * 
	 * For linkers with only one formula, that formula is always returned.
	 * 
	 * @param mass
	 * @return Null if not available for Search or for Linker in Search
	 * @throws Exception
	 */
	@Override
	public String getCrosslinkFormula(double queryMass) throws Exception {

		if ( true ) {
			throw new RuntimeException( "UNTESTED CODE: getCrosslinkFormula(double queryMass)" );
		}
		
		/////////////  UNTESTED CODE

		if ( crosslinkFormulaOnlyOne != null ) {
			//  from DB: Only 1 chemical formula so return it
			return crosslinkFormulaOnlyOne; // EARLY RETURN
		}; 

		if ( linkerPerSearchCrosslinkMassDTOList != null && ( ! linkerPerSearchCrosslinkMassDTOList.isEmpty() ) && crosslinkFormulaSet != null ) {
			// From DB: Have Crosslinker masses and they have chemical formulas so search for queryMass
			for ( LinkerPerSearchCrosslinkMassDTO item : linkerPerSearchCrosslinkMassDTOList ) {
				if ( item.getCrosslinkMassDouble() == queryMass ) {
					if ( StringUtils.isNotEmpty( item.getChemicalFormula() ) ) {
						return item.getChemicalFormula();
					}
				}
			}
			
			throw new ProxlBaseDataException( "No Formula found searching database for queryMass: " + queryMass + ", for linker: " + this.linkerAbbreviation );
			// return null;  // Formula not found
		}

		if ( linker_Builtin_Linker != null ) {
			return linker_Builtin_Linker.getCrosslinkFormula( queryMass ); // EARLY RETURN
		}
		return null;  // Formula not available
	}
	
	////////
	
	//  Getters for DB data
	
	@Override
	public List<LinkerPerSearchMonolinkMassDTO> getLinkerPerSearchMonolinkMassDTOList() {
		return linkerPerSearchMonolinkMassDTOList;
	}
	@Override
	public List<LinkerPerSearchCrosslinkMassDTO> getLinkerPerSearchCrosslinkMassDTOList() {
		return linkerPerSearchCrosslinkMassDTOList;
	}
	@Override
	public List<LinkerPerSearchCleavedCrosslinkMassDTO> getLinkerPerSearchCleavedCrosslinkMassDTOList() {
		return linkerPerSearchCleavedCrosslinkMassDTOList;
	}

	
	///////////////////////
	
	//  Package Private Setters
	
	void setSearchId(int searchId) {
		this.searchId = searchId;
	}

	void setSpacerArmLength(Double spacerArmLength) {
		this.spacerArmLength = spacerArmLength;
	}

	void setSpacerArmLengthString(String spacerArmLengthString) {
		this.spacerArmLengthString = spacerArmLengthString;
	}


}
