package org.yeastrc.xlink.www.web_utils;

import java.util.List;

import org.yeastrc.xlink.dto.IsotopeLabelDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.dto.StaticModDTO;

public class PSMMassCalculatorParams {

	
	public Double getPrecursorMZ() {
		return precursorMZ;
	}
	public void setPrecursorMZ(Double precursorMZ) {
		this.precursorMZ = precursorMZ;
	}
	public PeptideDTO getPeptide1() {
		return peptide1;
	}
	public void setPeptide1(PeptideDTO peptide1) {
		this.peptide1 = peptide1;
	}
	public PeptideDTO getPeptide2() {
		return peptide2;
	}
	public void setPeptide2(PeptideDTO peptide2) {
		this.peptide2 = peptide2;
	}
	public List<SrchRepPeptPeptDynamicModDTO> getDynamicMods1() {
		return dynamicMods1;
	}
	public void setDynamicMods1(List<SrchRepPeptPeptDynamicModDTO> dynamicMods1) {
		this.dynamicMods1 = dynamicMods1;
	}
	public List<SrchRepPeptPeptDynamicModDTO> getDynamicMods2() {
		return dynamicMods2;
	}
	public void setDynamicMods2(List<SrchRepPeptPeptDynamicModDTO> dynamicMods2) {
		this.dynamicMods2 = dynamicMods2;
	}
	public IsotopeLabelDTO getLabel1() {
		return label1;
	}
	public void setLabel1(IsotopeLabelDTO label1) {
		this.label1 = label1;
	}
	public IsotopeLabelDTO getLabel2() {
		return label2;
	}
	public void setLabel2(IsotopeLabelDTO label2) {
		this.label2 = label2;
	}
	public List<StaticModDTO> getStaticMods() {
		return staticMods;
	}
	public void setStaticMods(List<StaticModDTO> staticMods) {
		this.staticMods = staticMods;
	}
	public Integer getCharge() {
		return charge;
	}
	public void setCharge(Integer charge) {
		this.charge = charge;
	}
	public Double getLinkerMass() {
		return linkerMass;
	}
	public void setLinkerMass(Double linkerMass) {
		this.linkerMass = linkerMass;
	}
	
	private Double precursorMZ;
	
	private PeptideDTO peptide1;
	private PeptideDTO peptide2;

	private List<SrchRepPeptPeptDynamicModDTO> dynamicMods1;
	private List<SrchRepPeptPeptDynamicModDTO> dynamicMods2;

	private IsotopeLabelDTO label1;
	private IsotopeLabelDTO label2;
	
	
	private List<StaticModDTO> staticMods;

	private Integer charge;
	private Double linkerMass;
	
	
}
