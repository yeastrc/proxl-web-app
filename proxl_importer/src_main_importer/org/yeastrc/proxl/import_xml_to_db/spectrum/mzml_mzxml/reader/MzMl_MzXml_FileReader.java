package org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.reader;


import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.systemsbiology.jrap.stax.DataProcessingInfo;
import org.systemsbiology.jrap.stax.MSInstrumentInfo;
import org.systemsbiology.jrap.stax.MSXMLSequentialParser;
import org.systemsbiology.jrap.stax.MZXMLFileInfo;
import org.systemsbiology.jrap.stax.Scan;
import org.systemsbiology.jrap.stax.ScanHeader;
import org.systemsbiology.jrap.stax.SoftwareInfo;
import org.yeastrc.proxl.import_xml_to_db.spectrum.common.exceptions.DataProviderException;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.constants_enums.DataConversionType;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.dto.MzML_MzXmlHeader;
//import org.yeastrc.ms.domain.run.DataConversionType;
//import org.yeastrc.ms.domain.run.MsRunIn;
//import org.yeastrc.ms.domain.run.MsScanIn;
//import org.yeastrc.ms.domain.run.mzxml.MzXmlScan;
//import org.yeastrc.ms.parser.DataProviderException;
//import org.yeastrc.ms.parser.MzXmlDataProvider;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.dto.MzML_MzXmlScan;

/**
 * Copied from MSDaPl, was MzXmlFileReader
 * 
 * Used to process MzML and MzXml files
 *
 */
public class MzMl_MzXml_FileReader { // implements MzXmlDataProvider {
	
	private static final Logger log = Logger.getLogger(MzMl_MzXml_FileReader.class);

//    private String sha1Sum;
    private String filename;
    private MSXMLSequentialParser parser;  //  MSXMLSequentialParser from ISB code
    private int numScans = 0;
    private int numScansRead = 0;
    private int lastMs1ScanNumber = -1;
//    private boolean isCentroided = false;
    private DataConversionType dataConvType = DataConversionType.UNKNOWN;
    
    private static final Pattern rtPattern = Pattern.compile("^PT(\\d+\\.?\\d*)S$"); 
    
    
    /**
     * @param filePath
     * @param sha1Sum
     * @throws DataProviderException
     */
    public void open(String filePath
//    		, 
//    		String sha1Sum
    		)
            throws DataProviderException {
    	
    	
//        this.sha1Sum = sha1Sum;
        this.filename = new File(filePath).getName();
        parser = new MSXMLSequentialParser();  //  MSXMLSequentialParser from ISB code
        try {
            parser.open(filePath);
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("Could not find file: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new DataProviderException("Error reading file: "+filePath, e);
        
        } catch ( Exception e ) {
        	
        	throw new DataProviderException("Error Opening or reading file: "+filePath, e);
        }
        
        numScans = parser.getScanCount();
    }
    
    public void close() {
        if(parser != null)
            parser.close();
    }

    public String getFileName() {
        return filename;
    }

    /**
     * Get Next Scan
     * 
     * @return
     * @throws Exception 
     */
    public MzML_MzXmlScan /* MsScanIn */ getNextScan() throws Exception {
    	
    	
        if(numScansRead >= numScans)
            return null;
        Scan scan = null;
        if(parser.hasNextScan()) {
            try {
                scan = parser.getNextScan();
            }
            catch (XMLStreamException e) {
                throw new DataProviderException("Error reading scan.", e);
            }
        }
        if(scan == null)
            return null;
        
        ScanHeader header = scan.getHeader();
        MzML_MzXmlScan mScan = new MzML_MzXmlScan();
        mScan.setMsLevel(header.getMsLevel());
        mScan.setStartScanNum(header.getNum());
        mScan.setEndScanNum(header.getNum());
        
        if(header.getMsLevel() == 1) {
            this.lastMs1ScanNumber = mScan.getStartScanNum();
        }
        else if(header.getMsLevel() > 1) {
            
            if(header.getPrecursorScanNum() > 0) {
                if(header.getPrecursorScanNum() != this.lastMs1ScanNumber) {
                    throw new DataProviderException("last MS1 scan: "+this.lastMs1ScanNumber+
                            " is not the same as precursor scan number: "+header.getPrecursorScanNum()+
                            " for scan: "+header.getNum());
                }
                mScan.setPrecursorScanNum(header.getPrecursorScanNum());
            }
            else {
                mScan.setPrecursorScanNum(this.lastMs1ScanNumber);
            }
            mScan.setPrecursorMz(new BigDecimal(header.getPrecursorMz()));
        }
        mScan.setRetentionTime(getRetentionTime(header));
        
        
//        if(header.getCentroided() != -1)
//            mScan.setDataConversionType(getDataConversionType(header.getCentroided()));
//        else
//            mScan.setDataConversionType(this.dataConvType);
        
        int mzMLCentroided = header.getCentroided();
        
        int scanCentroided = mzMLCentroided;
        
        if ( scanCentroided != 1 ) {
        	
        	scanCentroided = 0;
        }
        
        mScan.setIsCentroided( scanCentroided );
        
        
        
        

        mScan.setPeakCount(header.getPeaksCount());
        

        double[][] mzInt = scan.getMassIntensityList();
        
        
        int peaksCount = header.getPeaksCount();

        
        int mzIntLength = mzInt.length;
        
        
        if ( mzIntLength != 2 ) {
        	
        	String msg = "Data ERROR: mzIntLength != 2, scan number: " + header.getNum() + ", filename: " + filename;
        	log.error( msg );
        	throw new Exception(msg);
        }
        
        int mzIntIndexZeroLength = mzInt[0].length;
        int mzIntIndexOneLength = mzInt[1].length;
        
//        if ( mzIntIndexZeroLength != peaksCount ) {
//        	
//        	String msg = "Data ERROR: mzIntIndexZeroLength != peaksCount, mzIntIndexOneLength: "
//        			+ mzIntIndexZeroLength + ", peaksCount: " + peaksCount 
//        			+ ", scan number: " + header.getNum() + ", filename: " + filename;
//        	log.error( msg );
//        	throw new Exception(msg);
//        }
//        
//        if ( mzIntIndexOneLength != peaksCount ) {
//        	
//        	String msg = "Data ERROR: mzIntIndexOneLength != peaksCount, mzIntIndexOneLength: "
//        			+ mzIntIndexOneLength + ", peaksCount: " + peaksCount 
//        			+ ", scan number: " + header.getNum() + ", filename: " + filename;
//        	log.error( msg );
//        	throw new Exception(msg);
//        }
        
        
        ////  Validate all values at index > header.getPeaksCount() is zero.
        
        for ( int mzIntFirstIndex =0; mzIntFirstIndex < mzInt.length; mzIntFirstIndex++ ) {
        	
        	for ( int index = header.getPeaksCount(); index < mzInt[ mzIntFirstIndex ].length; index++ ) {

        		double value = mzInt[ mzIntFirstIndex ][ index ];
        		
        		if ( value != 0 ) {
        			
        			String msg = "Data ERROR: mzInt value for index >= peaksCount is not zero, is: " + value
        					+ ", mzIntIndexOneLength: " + mzIntIndexOneLength + ", peaksCount: " + peaksCount 
        					+ ", scan number: " + header.getNum() + ", filename: " + filename;
        			log.error( msg );
        			throw new Exception(msg);
        		}
        	}
        }        
        
		if ( peaksCount > mzInt[0].length || peaksCount > mzInt[1].length ) {
			
			String msg = "Data ERROR: peaksCount is greater than provided peaks arrays."
					+ ", mzIntIndexZeroLength: " + mzIntIndexZeroLength 
					+ ", mzIntIndexOneLength: " + mzIntIndexOneLength 
					+ ", peaksCount: " + peaksCount 
					+ ", scan number: " + header.getNum() + ", filename: " + filename;
			log.error( msg );
			throw new Exception(msg);
		}
		
        
        // Peak 0 mass = list[0][0], peak 0 intensity = list[1][0]
        // Peak 1 mass = list[0][1], peak 1 intensity = list[1][1]
        for(int index = 0; index < header.getPeaksCount(); index++) {
            double mz = mzInt[0][index];
            double intensity = mzInt[1][index];
            mScan.addPeak(mz, (float)intensity);
        }
        return mScan;
    }

    
    /**
     * @param header
     * @return
     */
    private BigDecimal getRetentionTime(ScanHeader header) {
        // In the schema, retentionTime is "xs:duration" 
        // http://www.w3schools.com/Schema/schema_dtypes_date.asp
        String rt = header.getRetentionTime();
        if(rt == null)  return null;
        rt = rt.trim();
        
        Matcher m = rtPattern.matcher(rt);
        if(m.matches()) {
            String time = m.group(1);
            if(time != null) {
                return new BigDecimal(time);
            }
        }
        return null;
    }

    /**
     * @return
     * @throws DataProviderException
     */
    public MzML_MzXmlHeader /* MsRunIn */ getRunHeader() throws DataProviderException {
    	
        MZXMLFileInfo info = parser.getFileHeader();
        MzML_MzXmlHeader run = new MzML_MzXmlHeader();
        
        DataProcessingInfo dpInfo = info.getDataProcessing();
        
        dataConvType  = getDataConversionType(dpInfo.getCentroided());
        run.setDataConversionType(dataConvType);
        
        List<SoftwareInfo> swList = dpInfo.getSoftwareUsed();
        // TODO handle multiple software info.
        if(swList.size() > 0) {
            for(SoftwareInfo si: swList) {
                if(si.type.equalsIgnoreCase("conversion")) {
                    run.setConversionSW(swList.get(0).name);
                    run.setConversionSWVersion(swList.get(0).version);
                }
            }
        }
        
        MSInstrumentInfo msiInfo = info.getInstrumentInfo();
        run.setInstrumentModel(msiInfo.getModel());
        run.setInstrumentVendor(msiInfo.getManufacturer());
      
        run.setFileName(this.filename);
//        run.setSha1Sum(sha1Sum);
        
        
        return run;
    }

    /**
     * @param centroided
     * @return
     */
    private DataConversionType getDataConversionType(int centroided) {
        
        if (centroided == DataProcessingInfo.NO)
            return DataConversionType.NON_CENTROID;
        else if (centroided == DataProcessingInfo.YES)
            return DataConversionType.CENTROID;
        else {
        	return DataConversionType.NON_CENTROID;  // Hard code to Non_Centroid per Mike Riffle since for mzML files only centroid Yes is indicated. 
//            return DataConversionType.UNKNOWN;
        }
        
    }
    
    
    
//    public static void main(String[] args) {
//        
//        String rt = "PT60.1361S";
//        rt = rt.trim();
//        
//        Matcher m = rtPattern.matcher(rt);
//        if(m.matches()) {
//            String time = m.group(1);
//            if(time != null) {
//                BigDecimal bd =  new BigDecimal(time);
//                System.out.println(bd);
//            }
//        }
//    }
}
