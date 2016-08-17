/**
 * MzXmlHeader.java
 * @author Vagisha Sharma
 * Jun 22, 2009
 * @version 1.0
 */
package org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.dto;

//import java.util.ArrayList;
//import java.util.List;

import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.constants_enums.DataConversionType;

//import org.yeastrc.ms.domain.general.MsEnzymeIn;
//import org.yeastrc.ms.domain.run.DataConversionType;
//import org.yeastrc.ms.domain.run.MsRunIn;
//import org.yeastrc.ms.domain.run.RunFileFormat;

/**
 * 
 */
public class MzML_MzXmlHeader { // implements MsRunIn {

    
    private String fileName;
    private String creationDate;
    private String extractor;
    private String extractorVersion;
    private String extractorOptions;
    private String instrumentModel;
    private String instrumentSN;
    private String instrumentVendor;
    private String acquisionMethod;
    private String comment;
    private DataConversionType dataConversionType = DataConversionType.UNKNOWN;

    //  removed for Crosslinks
    
//  private String sha1Sum;

//    private RunFileFormat runFileFormat = RunFileFormat.MZXML;
    
    public void setFileName(String fileName) {
    	
    	this.fileName = fileName;
    			
//    	WAS
//    			
//        int idx = fileName.toLowerCase().lastIndexOf("."+RunFileFormat.MZXML.name().toLowerCase());
//        if(idx == -1) {
//            idx = fileName.toLowerCase().lastIndexOf("."+RunFileFormat.MZML.name().toLowerCase());
//            if(idx == -1) {
//                this.fileName = fileName;
//            } else {
//                this.fileName = fileName.substring(0, idx);
//                runFileFormat = RunFileFormat.MZML;
//            }
//        } else {
//            this.fileName = fileName.substring(0, idx);
//        }
    	
    }
    
    //  removed for Crosslinks

//  // MzXML files don't have any enzyme information
//  public List<MsEnzymeIn> getEnzymeList() {
//      return new ArrayList<MsEnzymeIn>(0);
//  }

    //  removed for Crosslinks

//    public RunFileFormat getRunFileFormat() {
//        return runFileFormat;
//    }

    
    public String getFileName() {
        return fileName;
    }
    
//    public void setSha1Sum(String sha1Sum) {
//        this.sha1Sum = sha1Sum;
//    }
//    
//    
//    public String getSha1Sum() {
//        return this.sha1Sum;
//    }
    
    public String getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getInstrumentModel() {
        return instrumentModel;
    }
    
    public void setInstrumentModel(String instrumentModel) {
        this.instrumentModel = instrumentModel;
    }
    
    public String getInstrumentSN() {
        return instrumentSN;
    }
    
    public void setInstrumentSN(String instrumentSN) {
        this.instrumentSN = instrumentSN;
    }
    
    public String getConversionSW() {
        return extractor;
    }
    
    public void setConversionSW(String software) {
        this.extractor = software;
    }
    
    public String getConversionSWVersion() {
        return extractorVersion;
    }
    
    public void setConversionSWVersion(String version) {
        this.extractorVersion = version;
    }
    
    public String getConversionSWOptions() {
        return extractorOptions;
    }
    
    public void setConversionSWOptions(String options) {
        this.extractorOptions = options;
    }
    
    public String getAcquisitionMethod() {
        return acquisionMethod;
    }
    
    public void setAcquisionMethod(String acquisitionMethod) {
        this.acquisionMethod = acquisitionMethod;
    }
    
    public void setDataConversionType(DataConversionType dataConversionType) {
        this.dataConversionType = dataConversionType;
    }
    
    public DataConversionType getDataConversionType() {
        return this.dataConversionType;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getInstrumentVendor() {
        return instrumentVendor;
    }

    public void setInstrumentVendor(String vendor) {
        this.instrumentVendor = vendor;
    }


}
