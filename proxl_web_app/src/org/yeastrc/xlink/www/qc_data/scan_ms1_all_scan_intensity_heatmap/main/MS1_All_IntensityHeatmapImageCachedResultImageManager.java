package org.yeastrc.xlink.www.qc_data.scan_ms1_all_scan_intensity_heatmap.main;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;
//import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;

/**
 * Cached Result Image Manager
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result image for caching
 * 
 * Singleton
 */
public class MS1_All_IntensityHeatmapImageCachedResultImageManager implements CachedDataInFileMgmtRegistrationIF {

//	private static final Logger log = Logger.getLogger( MS1_All_IntensityHeatmapImageCachedResultImageManager.class);
	
	private static final String PREFIX_FOR_CACHING = "MS1_All_IntensityHeatmapImage_";
	private static final String PREFIX_FOR_CACHING_ADDITION_FULL_IMAGE = "FULL_IMAGE_";
	private static final String PREFIX_FOR_CACHING_ADDITION_REDUCED_WIDTH = "REDUCED_WIDTH_";
	private static final String PREFIX_FOR_CACHING_ADDITION_REDUCED_WIDTH_AFTER_WIDTH = "_";

	private static final String PREFIX_FOR_CACHING_FULL_WIDTH = PREFIX_FOR_CACHING + PREFIX_FOR_CACHING_ADDITION_FULL_IMAGE;

	private static final MS1_All_IntensityHeatmapImageCachedResultImageManager instance = new MS1_All_IntensityHeatmapImageCachedResultImageManager();

	// private constructor
	private MS1_All_IntensityHeatmapImageCachedResultImageManager() {}

	/**
	 * @return Singleton instance
	 */
	public static MS1_All_IntensityHeatmapImageCachedResultImageManager getSingletonInstance() {
		return instance;
	}
	

	/**
	 * @param scanFileId
	 * @param requestedImageWidth
	 * @param imageAsBytes
	 * @throws Exception
	 */
	public byte[] retrieveImageDataFromCache( int scanFileId, Integer requestedImageWidth, String requestQueryString ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}

		List<Integer> ids = new ArrayList<>( 1 );
		ids.add( scanFileId );
		
		byte[] imageAsBytes = null;
		
		if ( requestedImageWidth == null ) {
			imageAsBytes = 
				CachedDataInFileMgmt.getSingletonInstance()
				.retrieveCachedDataFileContents(
						PREFIX_FOR_CACHING_FULL_WIDTH /* namePrefix */, 
						MS1_All_IntensityHeatmapImage.VERSION_FOR_CACHING /* version */, 
						requestQueryString, 
						ids,
						IdParamType.SCAN_FILE_ID );
		} else {
			String prefixImageWidth = getPrefixForImageWidth( requestedImageWidth );
			imageAsBytes = CachedDataInFileMgmt.getSingletonInstance()
					.retrieveCachedDataFileContents(
							prefixImageWidth /* namePrefix */, 
							MS1_All_IntensityHeatmapImage.VERSION_FOR_CACHING /* version */, 
							requestQueryString, 
							ids,
							IdParamType.SCAN_FILE_ID );
		}
		return imageAsBytes;
	}
	
	/**
	 * @param scanFileId
	 * @param requestedImageWidth
	 * @param imageAsBytes
	 * @throws Exception
	 */
	public void saveImageDataToCache( int scanFileId, Integer requestedImageWidth, byte[] imageAsBytes, String requestQueryString ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return;  //  EARLY EXIT
		}

		List<Integer> ids = new ArrayList<>( 1 );
		ids.add( scanFileId );
		
		if ( requestedImageWidth == null ) {
			CachedDataInFileMgmt.getSingletonInstance()
			.saveCachedDataFileContents(
					imageAsBytes, 
					ReplaceExistingValue.NO,
					PREFIX_FOR_CACHING_FULL_WIDTH /* namePrefix */, 
					MS1_All_IntensityHeatmapImage.VERSION_FOR_CACHING /* version */, 
					requestQueryString, 
					ids,
					IdParamType.SCAN_FILE_ID );
		} else {
			String prefixImageWidth = getPrefixForImageWidth( requestedImageWidth );
			CachedDataInFileMgmt.getSingletonInstance()
			.saveCachedDataFileContents(
					imageAsBytes, 
					ReplaceExistingValue.NO,
					prefixImageWidth /* namePrefix */, 
					MS1_All_IntensityHeatmapImage.VERSION_FOR_CACHING /* version */, 
					requestQueryString, 
					ids,
					IdParamType.SCAN_FILE_ID );
		}
		
	}

	/* 
	 * Called from CachedDataInFileMgmtRegistration
	 * 
	 * Register all prefixes and version used with CachedDataInFileMgmt
	 */
	@Override
	public void register() throws Exception {
		
		//  Register for full width
		{
			CachedDataInFileMgmtRegistration.getSingletonInstance()
			.register( PREFIX_FOR_CACHING_FULL_WIDTH, MS1_All_IntensityHeatmapImage.VERSION_FOR_CACHING );
		}
		
		//  Register for each allowed image width
		
		if ( MS1_All_IntensityHeatmapImage.ALLOWED_REQUESTED_IMAGE_WIDTHS != null 
				&& MS1_All_IntensityHeatmapImage.ALLOWED_REQUESTED_IMAGE_WIDTHS.length > 0 ) {
			
			for ( int allowedImageWidth : MS1_All_IntensityHeatmapImage.ALLOWED_REQUESTED_IMAGE_WIDTHS ) {
				String prefixAllowedImageWidth = getPrefixForImageWidth( allowedImageWidth );
				CachedDataInFileMgmtRegistration.getSingletonInstance()
				.register( prefixAllowedImageWidth, MS1_All_IntensityHeatmapImage.VERSION_FOR_CACHING );
			}
		}
	}
	
	/**
	 * @param imageWidth
	 * @return
	 */
	private String getPrefixForImageWidth( int imageWidth ) {
		String prefixAllowedImageWidth = 
				PREFIX_FOR_CACHING + PREFIX_FOR_CACHING_ADDITION_REDUCED_WIDTH + imageWidth + PREFIX_FOR_CACHING_ADDITION_REDUCED_WIDTH_AFTER_WIDTH;
		return prefixAllowedImageWidth;
	}
	
		
}

