package org.yeastrc.xlink.www.lorikeet_dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 */
public class LorikeetRootData {

	private int charge;
	private double precursorMz;
	
	/**
	 * Filename for display
	 */
	private String fileName;

	/**
	 * Scan Number
	 */
	private int scanNum;

	/**
     * Peptide Sequence
     */
	private String sequence;
    
	/**
	 * Each sublist is one peak with [<mz>,intensity]
	 */
	private List<List<Object>> peaks;
	
	/**
	 * Each sublist is one peak with [<mz>,intensity]
	 */
	private List<List<Object>> ms1peaks;


	/**
	 * Static Mods 
	 */
	private List<LorikeetStaticMod> staticMods; 




	/**
	 * Variable Mods / Dynamic Mods
	 */
	private List<LorikeetVariableMod> variableMods; 

	
	/**
	 * Data for a Loop Link - Cross Links Project
	 */
	private LorikeetLoopLinkData loopLinkDataInputFormat;  //  Lorikeet internal object property name: loopLinkData
	
	
	
	/**
	 * Data for a Cross Link - Cross Links Project
	 */
	private LorikeetCrossLinkData crossLinkDataInputFormat;  //  Lorikeet internal object property name: crossLinkData

	
	/**
	 * Data for a Dimer - Cross Links Project
	 */
	private LorikeetDimerData dimerDataInputFormat;  //  Lorikeet will ignore this property, the setup JS code will handle this


	private String label;		// stable isotope label name
	
	/**
	 * 
	 */
	private int height;
	
	private int width;
	
//	jpeak.add( peak.getMz() );
//	jpeak.add( peak.getIntensity() );
//
//	    private float mz;
//    private float intensity;

    /**
     * Add to "peaks" list
     * @param mz
     * @param intensity
     */
    public void addPeak( double mz, float intensity ) {
    	
    	if ( peaks == null ) {
    		
    		peaks = new ArrayList<List<Object>>();
    	}
    	
    	List<Object> peakItem = new ArrayList<>();
    	
    	peakItem.add( mz );
    	peakItem.add( intensity );
    	
    	peaks.add( peakItem );
    }
    

    /**
     * Add to "ms1peaks" list
     * @param mz
     * @param intensity
     */
    public void addMs1Peak( double mz, float intensity ) {
    	
    	if ( ms1peaks == null ) {
    		
    		ms1peaks = new ArrayList<List<Object>>();
    	}
    	
    	List<Object> peakItem = new ArrayList<>();
    	
    	peakItem.add( mz );
    	peakItem.add( intensity );
    	
    	ms1peaks.add( peakItem );
    }

    
	/**
	 * Each sublist is one peak with [<mz>,intensity]
	 * @return
	 */
	public List<List<Object>> getMs1peaks() {
		return ms1peaks;
	}

	/**
	 * Each sublist is one peak with [<mz>,intensity]
	 * @param ms1peaks
	 */
	public void setMs1peaks(List<List<Object>> ms1peaks) {
		this.ms1peaks = ms1peaks;
	}

	
    

	/**
	 * Data for a Loop Link - Input to Lorikeet Format - Cross Links Project
	 * @return
	 */
	public LorikeetLoopLinkData getLoopLinkDataInputFormat() {
		return loopLinkDataInputFormat;
	}


	/**
	 * Data for a Loop Link - Input to Lorikeet Format - Cross Links Project
	 * @param loopLinkDataInputFormat
	 */
	public void setLoopLinkDataInputFormat(
			LorikeetLoopLinkData loopLinkDataInputFormat) {
		this.loopLinkDataInputFormat = loopLinkDataInputFormat;
	}


	/**
	 * Data for a Cross Link - Cross Links Project
	 * @return
	 */
	public LorikeetCrossLinkData getCrossLinkDataInputFormat() {
		return crossLinkDataInputFormat;
	}
	/**
	 * Data for a Cross Link - Cross Links Project
	 * @param crossLinkDataInputFormat
	 */
	public void setCrossLinkDataInputFormat(
			LorikeetCrossLinkData crossLinkDataInputFormat) {
		this.crossLinkDataInputFormat = crossLinkDataInputFormat;
	}
	
	
	/**
	 * Filename for display
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**Filename for display
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

    /**
     * Scan Number
     * @return
     */
    public int getScanNum() {
		return scanNum;
	}

	/**
	 * Scan Number
	 * @param scanNum
	 */
	public void setScanNum(int scanNum) {
		this.scanNum = scanNum;
	}


	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public double getPrecursorMz() {
		return precursorMz;
	}

	public void setPrecursorMz(double precursorMz) {
		this.precursorMz = precursorMz;
	}

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
	
	
	public List<LorikeetStaticMod> getStaticMods() {
		return staticMods;
	}


	public void setStaticMods(List<LorikeetStaticMod> staticMods) {
		this.staticMods = staticMods;
	}
	
	
	public List<List<Object>> getPeaks() {
		return peaks;
	}


	public void setPeaks(List<List<Object>> peaks) {
		this.peaks = peaks;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public LorikeetDimerData getDimerDataInputFormat() {
		return dimerDataInputFormat;
	}


	public void setDimerDataInputFormat(LorikeetDimerData dimerDataInputFormat) {
		this.dimerDataInputFormat = dimerDataInputFormat;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}




}



//lorikeetOptions: Object
//charge: 0
//height: 500
//peaks: Array[15026]
//precursorMz: "927.475524902"
//sequence: "KPAVAVSSQQMESCR"
//variableMods: Array[0]
//width: 500