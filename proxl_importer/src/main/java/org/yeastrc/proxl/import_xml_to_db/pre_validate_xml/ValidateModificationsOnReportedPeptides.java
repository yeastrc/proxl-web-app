/*
 * Original author: Daniel Jaschob <djaschob .at. uw.edu>
 *                  
 * Copyright 2019 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl_import.api.xml_dto.Modification;
import org.yeastrc.proxl_import.api.xml_dto.Modifications;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;

/**
 * Validate that all Reported Peptide level modifications marked as N or C terminus have positions at the ends of the Peptide String
 *
 */
public class ValidateModificationsOnReportedPeptides {

	private static final Logger log = LoggerFactory.getLogger( ValidateModificationsOnReportedPeptides.class );

	private ValidateModificationsOnReportedPeptides() { }
	public static ValidateModificationsOnReportedPeptides getInstance() {
		return new ValidateModificationsOnReportedPeptides();
	}


	/**
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	public void validateModificationsOnReportedPeptides( ProxlInput proxlInput ) throws ProxlImporterDataException {

		ReportedPeptides reportedPeptides = proxlInput.getReportedPeptides();

		if ( reportedPeptides != null ) {

			List<ReportedPeptide> reportedPeptideList =	reportedPeptides.getReportedPeptide();

			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {

				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {

					Peptides peptides = reportedPeptide.getPeptides();

					List<Peptide> peptideList = peptides.getPeptide();

					for ( Peptide peptide : peptideList) {

						String peptideSequence = peptide.getSequence();

						int peptideSequenceLength = peptideSequence.length();

						Modifications modifications = peptide.getModifications();

						if ( modifications != null ) {

							List<Modification> peptideModificationList = modifications.getModification();
							if ( peptideModificationList != null && ( ! peptideModificationList.isEmpty() ) ) {
								for ( Modification modification : peptideModificationList ) {

									if ( ( modification.isIsNTerminal() != null && modification.isIsNTerminal() ) 
											&& ( modification.isIsCTerminal() != null && modification.isIsCTerminal() ) ) {
										String msg = "Peptide Modification: Not Allowed: 'is_n_terminal' and 'is_c_terminal' are both populated and true. Reported Peptide: " + reportedPeptide.getReportedPeptideString();
										log.error( msg );
										throw new ProxlImporterDataException( msg );
									}

									if ( modification.isIsNTerminal() != null && modification.isIsNTerminal() ) {
										//  Comment out to allow position
//										if ( modification.getPosition() != null ) {
//											String msg = "Peptide Modification Position is populated when modification is marked as 'n' terminal. Position: " + modification.getPosition().intValue()
//													+ " Reported Peptide: " + reportedPeptide.getReportedPeptideString();
//											log.error( msg );
//											throw new ProxlImporterDataException( msg );
//										}
										//  Since allowing position, validate it.  Not really needed since will be ignored.
										if ( modification.getPosition() != null ) {
											validatePositionValue( modification, peptideSequenceLength, reportedPeptide);
										}
										//  No changed needed outside this class since in the import code the position is overlaid if the N or C terminus flag is set
									} else if ( modification.isIsCTerminal() != null && modification.isIsCTerminal() ) {
										//  Comment out to allow position
//										if ( modification.getPosition()!= null ) {
//											String msg = "Peptide Modification Position is populated when modification is marked as 'c' terminal. Position: " + modification.getPosition().intValue()
//													+ " Reported Peptide: " + reportedPeptide.getReportedPeptideString();
//											log.error( msg );
//											throw new ProxlImporterDataException( msg );
//										}
										//  Since allowing position, validate it.  Not really needed since will be ignored.
										if ( modification.getPosition() != null ) {
											validatePositionValue( modification, peptideSequenceLength, reportedPeptide);
										}
										//  No changed needed outside this class since in the import code the position is overlaid if the N or C terminus flag is set
									} else {

										if ( modification.getPosition() == null ) {
											String msg = "Peptide Modification Position is null or not assigned and at least one of 'is_n_terminal' or 'is_c_terminal' is not populated and true.  Reported Peptide: " + reportedPeptide.getReportedPeptideString();
											log.error( msg );
											throw new ProxlImporterDataException( msg );
										}
									
										validatePositionValue( modification, peptideSequenceLength, reportedPeptide );
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * @param modification
	 * @param peptideSequenceLength
	 * @param reportedPeptide
	 * @throws ProxlImporterDataException
	 */
	private void validatePositionValue( Modification modification, int peptideSequenceLength, ReportedPeptide reportedPeptide ) throws ProxlImporterDataException {
		
		if ( modification.getPosition().intValue() < 1 ) {
			String msg = "Peptide Modification Position is < 1. peptide Modification Position: " 
					+ modification.getPosition()
					+ ", Reported Peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
		if ( modification.getPosition().intValue() > peptideSequenceLength ) {
			String msg = "Peptide Modification Position is > peptide Sequence Length. peptide Modification Position: " 
					+ modification.getPosition()
					+ ", peptide Sequence Length: " + peptideSequenceLength
					+ ", Reported Peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
	}
}
