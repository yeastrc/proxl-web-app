package org.yeastrc.xlink.www.lorikeet_dto;

import java.util.List;

public class LorikeetPerPeptideData {

	private String sequence;
	private List<LorikeetVariableMod> variableMods;

	private double ntermMod = 0; // additional mass to be added to the n-term
	private double ctermMod = 0; // additional mass to be added to the c-term
	
	private String label;
	
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public List<LorikeetVariableMod> getVariableMods() {
		return variableMods;
	}
	public void setVariableMods(List<LorikeetVariableMod> variableMods) {
		this.variableMods = variableMods;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public double getNtermMod() {
		return ntermMod;
	}
	public void setNtermMod(double ntermMod) {
		this.ntermMod = ntermMod;
	}
	public double getCtermMod() {
		return ctermMod;
	}
	public void setCtermMod(double ctermMod) {
		this.ctermMod = ctermMod;
	}
	
}
