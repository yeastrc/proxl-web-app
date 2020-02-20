package org.yeastrc.xlink.www.objects;

import java.text.DecimalFormat;


public class ProteinCoverageData {

	private String name;
	private int numResidues;
	private double sequenceCoverage;

	private int numLinkableResidues;
	private int numLinkableResiduesCovered;


	private double linkableResiduesCoverage;

	private int mlcResidues;
	private int lcResidues;
	private int monolinkedResidues;
	private int looplinkedResidues;
	private int crosslinkedResidues;

	
	private DecimalFormat coverageFormat = new DecimalFormat( "#.##" );
	
	public String getLinkableResiduesCoverageFmt() {
		return coverageFormat.format( linkableResiduesCoverage );
	}
	

	
	
	public String getMLCSequenceCoverage() {
		
		double numMLCResidues_Div_NumLinkableResidues = 0;
		if ( this.getNumLinkableResidues() != 0 ) {
			numMLCResidues_Div_NumLinkableResidues = (double)this.getNumMLCResidues() / (double)this.getNumLinkableResidues();
		}
		return coverageFormat.format( numMLCResidues_Div_NumLinkableResidues );
	}

	public String getLCSequenceCoverage() {
		
		if ( this.getNumLinkableResidues() == 0 ) {
			
			return "0";
		}
		
		return coverageFormat.format( (double)this.getNumLCResidues() / (double)this.getNumLinkableResidues() );
	}
	
	public String getMSequenceCoverage() {
		
		if ( this.getNumLinkableResidues() == 0 ) {
			
			return "0";
		}
		
		return coverageFormat.format( (double)this.getMonolinkedResidues() / (double)this.getNumLinkableResidues() );
	}
	
	public String getLSequenceCoverage() {
		
		if ( this.getNumLinkableResidues() == 0 ) {
			
			return "0";
		}
		
		return coverageFormat.format( (double)this.getLooplinkedResidues() / (double)this.getNumLinkableResidues() );
	}
	
	public String getCSequenceCoverage() {
		
		if ( this.getNumLinkableResidues() == 0 ) {
			
			return "0";
		}
		return coverageFormat.format( (double)this.getCrosslinkedResidues() / (double)this.getNumLinkableResidues() );
	}
	
	
	
	
	
	
	public int getNumLinkableResiduesCovered() {
		return numLinkableResiduesCovered;
	}




	public void setNumLinkableResiduesCovered(int numLinkableResiduesCovered) {
		this.numLinkableResiduesCovered = numLinkableResiduesCovered;
	}


	public double getLinkableResiduesCoverage() {
		return linkableResiduesCoverage;
	}




	public void setLinkableResiduesCoverage(double linkableResiduesCoverage) {
		this.linkableResiduesCoverage = linkableResiduesCoverage;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumResidues() {
		return numResidues;
	}
	public void setNumResidues(int numResidues) {
		this.numResidues = numResidues;
	}
	public String getSequenceCoverage() {
		return coverageFormat.format( sequenceCoverage );
	}
	public void setSequenceCoverage(double sequenceCoverage) {
		this.sequenceCoverage = sequenceCoverage;
	}
	public int getNumLinkableResidues() {
		return numLinkableResidues;
	}
	public void setNumLinkableResidues(int numLinkableResidues) {
		this.numLinkableResidues = numLinkableResidues;
	}
	public int getNumMLCResidues() {
		return mlcResidues;
	}
	public void setNuMLCResidues(int numLinkedResidues) {
		this.lcResidues = numLinkedResidues;
	}
	public int getNumLCResidues() {
		return lcResidues;
	}
	public void setNumMLCResidues(int numLinkedResidues) {
		this.mlcResidues = numLinkedResidues;
	}
	public int getMonolinkedResidues() {
		return monolinkedResidues;
	}
	public void setMonolinkedResidues(int monolinkedResidues) {
		this.monolinkedResidues = monolinkedResidues;
	}
	public int getLooplinkedResidues() {
		return looplinkedResidues;
	}
	public void setLooplinkedResidues(int looplinkedResidues) {
		this.looplinkedResidues = looplinkedResidues;
	}
	public int getCrosslinkedResidues() {
		return crosslinkedResidues;
	}
	public void setCrosslinkedResidues(int crosslinkedResidues) {
		this.crosslinkedResidues = crosslinkedResidues;
	}
	
	
}
