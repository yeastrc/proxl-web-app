package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl_import.api.xml_dto.CleavedCrosslinkMass;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMass;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMasses;
import org.yeastrc.proxl_import.api.xml_dto.LinkedEnd;
import org.yeastrc.proxl_import.api.xml_dto.LinkedEnds;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.ProteinTermini;
import org.yeastrc.proxl_import.api.xml_dto.ProteinTerminus;
import org.yeastrc.proxl_import.api.xml_dto.ProteinTerminusDesignation;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Residues;

/**
 * 
 *
 */
public class ValidateLinkers {

	private static final Logger log = LoggerFactory.getLogger(  ValidateLinkers.class );
	private ValidateLinkers() { }
	public static ValidateLinkers getInstance() {
		return new ValidateLinkers();
	}
	
	/**
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	public void validateLinkers( ProxlInput proxlInput ) throws ProxlImporterDataException {

		// Save Linker mapping for search
		Linkers proxlInputLinkers = proxlInput.getLinkers();
		if ( proxlInputLinkers == null ) {
			String msg = "at least one linker is required";
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		List<Linker> proxlInputLinkerList = proxlInputLinkers.getLinker();
		if ( proxlInputLinkerList.isEmpty() ) {
			String msg = "at least one linker is required";
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}

		
		//  Ensure no duplicate linker abbr 
		Set<String> linkerAbbrSet = new HashSet<>();
		
		
		for ( Linker proxlInputLinker : proxlInputLinkerList ) {
			
			String linkerAbbr = proxlInputLinker.getName();
			
			if ( linkerAbbr == null ) {
				String msg = "linker element does not have value for name attribute";
				throw new ProxlImporterDataException( msg );
			}
			if ( linkerAbbr.length() == 0 ) {
				String msg = "linker element name attribute is emty string";
				throw new ProxlImporterDataException( msg );
			}
			
			if ( ! linkerAbbrSet.add( linkerAbbr ) ) {
				String msg = "More than one linker with same name/abbreviation: '"
						+ linkerAbbr + "'";
				log.error( "" + msg );
				throw new ProxlImporterDataException( msg );
			}
			
			validateLinker( proxlInputLinker );
		}	
		
	}
	
	/**
	 * @param proxlInputLinker
	 * @throws ProxlImporterDataException 
	 */
	private void validateLinker( Linker proxlInputLinker ) throws ProxlImporterDataException {

		String proxlInputLinkerName = proxlInputLinker.getName();

		proxlInputLinker.getSpacerArmLength();  ////////////  NEW
		
		validateCrosslinkMasses(proxlInputLinker, proxlInputLinkerName);
		
		validateLinkedEnds(proxlInputLinker, proxlInputLinkerName);
	}
	
	/**
	 * @param proxlInputLinker
	 * @param proxlInputLinkerName
	 * @throws ProxlImporterDataException
	 */
	private void validateCrosslinkMasses( Linker proxlInputLinker, String proxlInputLinkerName ) throws ProxlImporterDataException {
		
		CrosslinkMasses crosslinkMasses = proxlInputLinker.getCrosslinkMasses();
		
		if ( crosslinkMasses != null ) {

			List<CrosslinkMass> crosslinkMassList = crosslinkMasses.getCrosslinkMass();
			if ( ! crosslinkMassList.isEmpty() ) {
				Set<BigDecimal> crosslinkerMasses = new HashSet<>();
				boolean foundAnyChemicalFormula = false;
				boolean allHaveChemicalFormula = true;
				for ( CrosslinkMass crosslinkMass : crosslinkMassList ) {
					if ( ! crosslinkerMasses.add( crosslinkMass.getMass() ) ) {
						// CrosslinkerMass: same mass in more than one entry
						String msg = "More than one <crosslink_mass> has same value for 'mass'. 'mass': " + crosslinkMass.getMass() + ",  Linker Name: " + proxlInputLinkerName;
						log.error( msg );
						throw new ProxlImporterDataException( msg );
					}
					if ( StringUtils.isNotEmpty( crosslinkMass.getChemicalFormula() ) ) {
						foundAnyChemicalFormula = true;
					} else {
						allHaveChemicalFormula = false;
					}
				}
				if ( foundAnyChemicalFormula && ( ! allHaveChemicalFormula ) ) {
					// Mismatched data  so error
					String msg = "At least one <crosslink_mass> has attribute 'chemical_formula' populated but not all of them have it populated.  Linker Name: " + proxlInputLinkerName;
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				}
			}

			List<CleavedCrosslinkMass> cleavedCrosslinkMassList = crosslinkMasses.getCleavedCrosslinkMass();
			if ( ! cleavedCrosslinkMassList.isEmpty() ) {
				Set<BigDecimal> cleavedCrosslinkMasses = new HashSet<>();
				boolean foundAnyChemicalFormula = false;
				boolean allHaveChemicalFormula = true;
				for ( CleavedCrosslinkMass cleavedCrosslinkMass : cleavedCrosslinkMassList ) {
					if ( ! cleavedCrosslinkMasses.add( cleavedCrosslinkMass.getMass() ) ) {
						// CleavedCrosslinkMass: same mass in more than one entry
						String msg = "More than one <cleaved_crosslink_mass> has same value for 'mass'. 'mass': " + cleavedCrosslinkMass.getMass() + ",  Linker Name: " + proxlInputLinkerName;
						log.error( msg );
						throw new ProxlImporterDataException( msg );
					}
					// cleavedCrosslinkMass.getMass();
					if ( StringUtils.isNotEmpty( cleavedCrosslinkMass.getChemicalFormula() ) ) {
						foundAnyChemicalFormula = true;
					} else {
						allHaveChemicalFormula = false;
					}
				}
				if ( foundAnyChemicalFormula && ( ! allHaveChemicalFormula ) ) {
					// Mismatched data  so error
					String msg = "At least one <cleaved_crosslink_mass> has attribute 'chemical_formula' populated but not all of them have it populated.  Linker Name: " + proxlInputLinkerName;
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				}
			}
		}
	}
	
	/**
	 * @param proxlInputLinker
	 * @param proxlInputLinkerName
	 * @throws ProxlImporterDataException
	 */
	private void validateLinkedEnds(Linker proxlInputLinker, String proxlInputLinkerName) throws ProxlImporterDataException {
		
		LinkedEnds linkedEnds = proxlInputLinker.getLinkedEnds();  ////////////  NEW
		
		if ( linkedEnds != null ) {
			List<LinkedEnd> linkedEndList = linkedEnds.getLinkedEnd();
		
			for ( LinkedEnd linkedEnd : linkedEndList ) {
				
				List<ProteinTerminus> proteinTerminusList = null;
				List<String> residueList = null;

				ProteinTermini proteinTermini = linkedEnd.getProteinTermini();
				Residues residues = linkedEnd.getResidues();
				if ( proteinTermini != null  ) {
					proteinTerminusList = proteinTermini.getProteinTerminus();
				}
				if ( residues != null  ) {
					residueList = residues.getResidue();
				}
				
				if ( ( proteinTerminusList == null || proteinTerminusList.isEmpty() ) 
						&& ( residueList == null || residueList.isEmpty() ) ) {
					// No values in proteinTerminusList or residueList so error
					String msg = "Under specific <linked_end>, there are no <protein_terminus> under <protein_termini> and no <residue> under <residues>.  Linker Name: " + proxlInputLinkerName;
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				}
				
				if ( proteinTerminusList != null && ( ! proteinTerminusList.isEmpty() ) ) {
					
					Set<ProteinTerminusPartsHolder> proteinTerminusPartsHolderSet = new HashSet<>();
					for ( ProteinTerminus proteinTerminus : proteinTerminusList ) {

						ProteinTerminusDesignation terminusEndXMLFile = proteinTerminus.getTerminusEnd();
						if ( terminusEndXMLFile == null ) {
							String msg = "<protein_terminus> attribute 'terminus_end' is not populated."
									+".  Linker name: " + proxlInputLinker.getName();
							log.error( msg );
							throw new ProxlImporterDataException( msg );
						}
						if ( terminusEndXMLFile == ProteinTerminusDesignation.C ) {
						} else if ( terminusEndXMLFile == ProteinTerminusDesignation.N ) {
						} else {
							String msg = "<protein_terminus> attribute 'terminus_end' is not an expected value, is not '" 
									+ ProteinTerminusDesignation.C.value()
									+ "' or '"
									+ ProteinTerminusDesignation.N.value()
									+ "'.  value: " + terminusEndXMLFile.value()
									+".  Linker name: " + proxlInputLinker.getName();
							log.error( msg );
							throw new ProxlImporterDataException( msg );
						}
						
						ProteinTerminusPartsHolder proteinTerminusPartsHolder = new ProteinTerminusPartsHolder();
						proteinTerminusPartsHolder.terminusEnd = proteinTerminus.getTerminusEnd();
						proteinTerminusPartsHolder.distanceFromTerminus = proteinTerminus.getDistanceFromTerminus();
						
						if ( ! proteinTerminusPartsHolderSet.add( proteinTerminusPartsHolder ) ) {
							String msg = "More than one <protein_terminus> under the same <protein_termini> has the same values.  Linker Name: " + proxlInputLinkerName;
							log.error( msg );
							throw new ProxlImporterDataException( msg );
						}
					}
				}
				if ( residueList != null && ( ! residueList.isEmpty() ) ) {
					
					Set<String> residueSet = new HashSet<>();
					for ( String residue : residueList ) {
						
						Validate_Value_Of_Type_AminoAcidResidue.validate_Value_Of_Type_AminoAcidResidue(residue, "<residue>", "  Linker Name: " + proxlInputLinkerName );
						
						if ( ! residueSet.add( residue ) ) {
							String msg = "More than one <residue> under the same <residues> has the same values.  Linker Name: " + proxlInputLinkerName;
							log.error( msg );
							throw new ProxlImporterDataException( msg );
						}
					}
				}
			}
		}
	}
	
	/**
	 *
	 */
	private static class ProteinTerminusPartsHolder {
		
		ProteinTerminusDesignation terminusEnd;
		BigInteger distanceFromTerminus;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((distanceFromTerminus == null) ? 0 : distanceFromTerminus.hashCode());
			result = prime * result + ((terminusEnd == null) ? 0 : terminusEnd.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ProteinTerminusPartsHolder other = (ProteinTerminusPartsHolder) obj;
			if (distanceFromTerminus == null) {
				if (other.distanceFromTerminus != null)
					return false;
			} else if (!distanceFromTerminus.equals(other.distanceFromTerminus))
				return false;
			if (terminusEnd != other.terminusEnd)
				return false;
			return true;
		}
	}
	
}
