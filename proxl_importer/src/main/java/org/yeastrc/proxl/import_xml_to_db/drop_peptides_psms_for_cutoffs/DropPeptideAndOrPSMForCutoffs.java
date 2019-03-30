package org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterableReportedPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterableReportedPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide.ReportedPeptideAnnotations;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
/**
 * 
 *
 */
public class DropPeptideAndOrPSMForCutoffs {

	private static final Logger log = LoggerFactory.getLogger( DropPeptideAndOrPSMForCutoffs.class);
	//  private constructor
	private DropPeptideAndOrPSMForCutoffs() { }
	public static DropPeptideAndOrPSMForCutoffs getInstance() { return new DropPeptideAndOrPSMForCutoffs(); }
	
	/**
	 * @param reportedPeptide
	 * @param dropPeptidePSMCutoffValues
	 * @return
	 * @throws ProxlImporterInteralException
	 * @throws ProxlImporterDataException
	 */
	public boolean dropPeptideForCmdLineCutoffs( ReportedPeptide reportedPeptide, DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues  ) throws ProxlImporterInteralException, ProxlImporterDataException {
		//  first process the peptide level annotations
		Map<String,Map<String,DropPeptidePSMCutoffValue>> dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName = 
				dropPeptidePSMCutoffValues.getDropPeptideCutoffValueKeyedOnSearchPgmNameAnnName();
		if ( dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName != null && ( ! dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName.isEmpty() ) ) {
			ReportedPeptideAnnotations reportedPeptideAnnotations = reportedPeptide.getReportedPeptideAnnotations();
			if ( reportedPeptideAnnotations != null ) {
				FilterableReportedPeptideAnnotations filterableReportedPeptideAnnotations = 
						reportedPeptideAnnotations.getFilterableReportedPeptideAnnotations();
				if ( filterableReportedPeptideAnnotations != null ) {
					List<FilterableReportedPeptideAnnotation> filterableReportedPeptideAnnotationList =
							filterableReportedPeptideAnnotations.getFilterableReportedPeptideAnnotation();
					if ( filterableReportedPeptideAnnotationList != null && ( ! filterableReportedPeptideAnnotationList.isEmpty() ) ) {
						for ( FilterableReportedPeptideAnnotation filterableReportedPeptideAnnotation : filterableReportedPeptideAnnotationList ) {
							String searchProgramName = filterableReportedPeptideAnnotation.getSearchProgram();
							String annotationName = filterableReportedPeptideAnnotation.getAnnotationName();
							Map<String,DropPeptidePSMCutoffValue> dropPeptideCutoffValueKeyedOnAnnName =
									dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName.get( searchProgramName );
							if ( dropPeptideCutoffValueKeyedOnAnnName != null ) {
								DropPeptidePSMCutoffValue dropPeptideCutoffValue = dropPeptideCutoffValueKeyedOnAnnName.get( annotationName );
								if ( dropPeptideCutoffValue != null ) {
									BigDecimal filterableReportedPeptideAnnotationValue = filterableReportedPeptideAnnotation.getValue();
									BigDecimal dropPeptideCutoffValueValue = dropPeptideCutoffValue.getCutoffValue();
									if ( dropPeptideCutoffValue.getAnnotationTypeFilterDirection()
											== FilterDirectionType.BELOW ) {
										//  Only values <= the cutoff are allowed
										if ( filterableReportedPeptideAnnotationValue.compareTo( dropPeptideCutoffValueValue ) > 0 ) {
											//  The annotation value is larger than the cutoff value so drop this record
											return true;  //  EARLY EXIT
										}
									} else if ( dropPeptideCutoffValue.getAnnotationTypeFilterDirection()
												== FilterDirectionType.ABOVE ) {
										//  Only values >= the cutoff are allowed
										if ( filterableReportedPeptideAnnotationValue.compareTo( dropPeptideCutoffValueValue ) < 0 ) {
											//  The annotation value is smaller than the cutoff value so drop this record
											return true;  //  EARLY EXIT
										}
									} else {
										String msg = "Unexepcted value for dropPeptideCutoffValue.getAnnotationTypeFilterDirection()."
												+ "  Value of: " + dropPeptideCutoffValue.getAnnotationTypeFilterDirection().toString();
										log.error( msg );
										throw new ProxlImporterInteralException(msg);
									}
								}
							}
						}
					}
				}
			}
		}
		//  No Reported Peptide level cutoffs caused the Reported Peptide to be dropped.
		//  Now check if all the PSMs for this reported peptide will be dropped.  If yes, then this reported peptide is also dropped.
		Psms psms = reportedPeptide.getPsms();
		if ( psms == null ) {
			String msg = "reportedPeptide.getPsms() cannot return null.  "
					+ "reportedPeptide.getReportedPeptideString(): " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		List<Psm> psmList = psms.getPsm();
		if ( psmList == null || psmList.isEmpty() ) {
			String msg = "psms.getPsm() cannot return null or be empty.  "
					+ "reportedPeptide.getReportedPeptideString(): " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		for (  Psm psm : psmList ) {
			if ( ! dropPSMForCmdLineCutoffs( psm, dropPeptidePSMCutoffValues ) ) {
				//  There is at least one PSM not dropped so do not drop this Reported Peptide
				return false;  //  EARLY EXIT
			}
		}
		//  Return true since all PSMs would be dropped
		return true;
	}
	
	/**
	 * @param psm
	 * @param dropPeptidePSMCutoffValues
	 * @return
	 * @throws ProxlImporterInteralException
	 */
	public boolean dropPSMForCmdLineCutoffs( Psm psm, DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues  ) throws ProxlImporterInteralException {
		 Map<String,Map<String,DropPeptidePSMCutoffValue>> dropPSMCutoffValueKeyedOnSearchPgmNameAnnName =
				 dropPeptidePSMCutoffValues.getDropPSMCutoffValueKeyedOnSearchPgmNameAnnName();
		if ( dropPSMCutoffValueKeyedOnSearchPgmNameAnnName != null && ( ! dropPSMCutoffValueKeyedOnSearchPgmNameAnnName.isEmpty() ) ) {
			FilterablePsmAnnotations filterablePsmAnnotations = 
					psm.getFilterablePsmAnnotations();
			if ( filterablePsmAnnotations != null ) {
				List<FilterablePsmAnnotation> filterablePsmAnnotationList =
						filterablePsmAnnotations.getFilterablePsmAnnotation();
				if ( filterablePsmAnnotationList != null && ( ! filterablePsmAnnotationList.isEmpty() ) ) {
					for ( FilterablePsmAnnotation filterablePsmAnnotation : filterablePsmAnnotationList ) {
						String searchProgramName = filterablePsmAnnotation.getSearchProgram();
						String annotationName = filterablePsmAnnotation.getAnnotationName();
						Map<String,DropPeptidePSMCutoffValue> dropPSMCutoffValueKeyedOnAnnName =
								dropPSMCutoffValueKeyedOnSearchPgmNameAnnName.get( searchProgramName );
						if ( dropPSMCutoffValueKeyedOnAnnName != null ) {
							DropPeptidePSMCutoffValue dropPsmCutoffValue = dropPSMCutoffValueKeyedOnAnnName.get( annotationName );
							if ( dropPsmCutoffValue != null ) {
								BigDecimal filterablePsmAnnotationValue = filterablePsmAnnotation.getValue();
								BigDecimal dropPsmCutoffValueValue = dropPsmCutoffValue.getCutoffValue();
								if ( dropPsmCutoffValue.getAnnotationTypeFilterDirection()
										== FilterDirectionType.BELOW ) {
									//  Only values <= the cutoff are allowed
											if ( filterablePsmAnnotationValue.compareTo( dropPsmCutoffValueValue ) > 0 ) {
												//  The annotation value is larger than the cutoff value so drop this PSM
												return true;  //  EARLY EXIT
											}
								} else if ( dropPsmCutoffValue.getAnnotationTypeFilterDirection()
										== FilterDirectionType.ABOVE ) {
									//  Only values >= the cutoff are allowed
									if ( filterablePsmAnnotationValue.compareTo( dropPsmCutoffValueValue ) < 0 ) {
										//  The annotation value is smaller than the cutoff value so drop this PSM
										return true;  //  EARLY EXIT
									}
								} else {
									String msg = "Unexepcted value for dropPsmCutoffValue.getAnnotationTypeFilterDirection()."
											+ "  Value of: " + dropPsmCutoffValue.getAnnotationTypeFilterDirection().toString();
									log.error( msg );
									throw new ProxlImporterInteralException(msg);
								}
							}
						}
					}
				}
			}
		}
		//  Do Not drop this PSM
		return false;
	}
}
