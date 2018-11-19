package org.yeastrc.proxl.import_xml_to_db.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptide.PeptideIsotopeLabels;
import org.yeastrc.proxl_import.api.xml_dto.Peptide.PeptideIsotopeLabels.PeptideIsotopeLabel;
import org.yeastrc.proxl_import.api.xml_dto.Protein;
import org.yeastrc.proxl_import.api.xml_dto.Protein.ProteinIsotopeLabels;
import org.yeastrc.proxl_import.api.xml_dto.Protein.ProteinIsotopeLabels.ProteinIsotopeLabel;
import org.yeastrc.xlink.base.constants.IsotopeLabelsConstants;
import org.yeastrc.xlink.dao.IsotopeLabelDAO;
import org.yeastrc.xlink.dto.IsotopeLabelDTO;
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;

/**
 * 
 *
 */
public class GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile {

	private static final Logger log = Logger.getLogger(GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile.class);
	private GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile() { }
	public static GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile getInstance() { return new GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile(); }
	
	private Map<String, Integer> labelStringToId_Cache = new HashMap<>();

	/**
	 * Result
	 *
	 */
	public static class GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result {
		
		private int isotopeLabelId;
		private String isotopeLabelString;
		
		public int getIsotopeLabelId() {
			return isotopeLabelId;
		}
		public void setIsotopeLabelId(int isotopeLabelId) {
			this.isotopeLabelId = isotopeLabelId;
		}
		public String getIsotopeLabelString() {
			return isotopeLabelString;
		}
		public void setIsotopeLabelString(String isotopeLabelString) {
			this.isotopeLabelString = isotopeLabelString;
		}
	}
	
	/**
	 * @param proteinFromProxlXMLFile
	 * @return
	 * @throws Exception
	 */
	public GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result getIsotopeLabelIdFor_Protein_FromProxlXMLFile( Protein proteinFromProxlXMLFile ) throws Exception {
		
		ProteinIsotopeLabels proteinIsotopeLabels = proteinFromProxlXMLFile.getProteinIsotopeLabels();
		if ( proteinIsotopeLabels == null ) {
			GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result = new GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result();
			result.isotopeLabelId = IsotopeLabelsConstants.ID_NONE;
			return result;
		}
		ProteinIsotopeLabel proteinIsotopeLabel = proteinIsotopeLabels.getProteinIsotopeLabel();
		if ( proteinIsotopeLabel == null ) {
			GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result = new GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result();
			result.isotopeLabelId = IsotopeLabelsConstants.ID_NONE;
			return result;
		}
		
		String proteinIsotopeLabelString = proteinIsotopeLabel.getLabel();

		{
			Integer isotopeLabelId_FromCache = labelStringToId_Cache.get( proteinIsotopeLabelString );

			if ( isotopeLabelId_FromCache != null ) {
				GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result = new GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result();
				result.isotopeLabelId = isotopeLabelId_FromCache;
				result.isotopeLabelString = proteinIsotopeLabelString;
				return result;
			}
		}
		
		IsotopeLabelDTO isotopeLabelDTO = IsotopeLabelDAO.getInstance().getIsotopeLabelDTOForName( proteinIsotopeLabelString );
		
		if ( isotopeLabelDTO == null ) {
			String msg = "Protein Isotope label is invalid '" + proteinIsotopeLabelString + "'.";
			log.error( msg );
			throw new ProxlBaseDataException( msg );
		}
		
		int isotopeLabelId_FromDB = isotopeLabelDTO.getId();

		labelStringToId_Cache.put( proteinIsotopeLabelString, isotopeLabelId_FromDB );
		
		GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result = new GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result();
		result.isotopeLabelId = isotopeLabelId_FromDB;
		result.isotopeLabelString = proteinIsotopeLabelString;
		return result;
	}

	/**
	 * @param proteinFromProxlXMLFile
	 * @return
	 * @throws Exception
	 */
	public GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result getIsotopeLabelIdFor_Peptide_FromProxlXMLFile( Peptide peptideFromProxlXMLFile ) throws Exception {
		
		PeptideIsotopeLabels peptideIsotopeLabels = peptideFromProxlXMLFile.getPeptideIsotopeLabels();
		if ( peptideIsotopeLabels == null ) {
			GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result = new GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result();
			result.isotopeLabelId = IsotopeLabelsConstants.ID_NONE;
			return result;
		}
		PeptideIsotopeLabel peptideIsotopeLabel = peptideIsotopeLabels.getPeptideIsotopeLabel();
		if ( peptideIsotopeLabel == null ) {
			GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result = new GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result();
			result.isotopeLabelId = IsotopeLabelsConstants.ID_NONE;
			return result;
		}
		
		String peptideIsotopeLabelString = peptideIsotopeLabel.getLabel();

		{
			Integer isotopeLabelId_FromCache = labelStringToId_Cache.get( peptideIsotopeLabelString );

			if ( isotopeLabelId_FromCache != null ) {
				GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result = new GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result();
				result.isotopeLabelId = isotopeLabelId_FromCache;
				result.isotopeLabelString = peptideIsotopeLabelString;
				return result;
			}
		}
		
		IsotopeLabelDTO isotopeLabelDTO = IsotopeLabelDAO.getInstance().getIsotopeLabelDTOForName( peptideIsotopeLabelString );
		
		if ( isotopeLabelDTO == null ) {
			String msg = "Peptide Isotope label is invalid '" + peptideIsotopeLabelString + "'.";
			log.error( msg );
			throw new ProxlBaseDataException( msg );
		}
		
		int isotopeLabelId_FromDB = isotopeLabelDTO.getId();
		
		labelStringToId_Cache.put( peptideIsotopeLabelString, isotopeLabelId_FromDB );
		
		GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result = new GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result();
		result.isotopeLabelId = isotopeLabelId_FromDB;
		result.isotopeLabelString = peptideIsotopeLabelString;
		return result;
	}
}
