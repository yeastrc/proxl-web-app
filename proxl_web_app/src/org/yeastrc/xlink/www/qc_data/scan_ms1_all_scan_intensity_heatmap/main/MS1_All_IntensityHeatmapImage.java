package org.yeastrc.xlink.www.qc_data.scan_ms1_all_scan_intensity_heatmap.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.JAI;
import java.awt.image.renderable.ParameterBlock;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.yeastrc.heatmap.impl.MultiColorMapper;
import org.yeastrc.xlink.dao.ScanFileMS_1_IntensityBinnedSummedDataDAO;
import org.yeastrc.xlink.dto.ScanFileMS_1_IntensityBinnedSummedDataDTO;
import org.yeastrc.xlink.utils.ZipUnzipByteArray;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.ms1_binned_summed_intensities.main.MS1_BinnedSummedIntensitiesProcessing;
import org.yeastrc.xlink.ms1_binned_summed_intensities.objects.MS1_IntensitiesBinnedSummedMapToJSONRoot;
import org.yeastrc.xlink.ms1_binned_summed_intensities.objects.MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot;

/**
 * Create a PNG image for the MS1 Intensity
 *
 */
public class MS1_All_IntensityHeatmapImage {

	private static final Logger log = Logger.getLogger(MS1_All_IntensityHeatmapImage.class);
	
	/**
	 *  Must be in ascending order since searching using Arrays.binarySearch
	 */
	private static final int[] ALLOWED_REQUESTED_IMAGE_WIDTHS = { 1500 };

	private static final Color[] PLOTTED_PIXEL_COLORS = 
		{ Color.white, new Color(82,46,155), new Color(255,246,11), Color.red };
	

	private static final String GREATER_THAN_OR_EQUAL_UNICODE_CHARACTER = "\u2265"; // (>= as single character)
	private static final String LESS_THAN_OR_EQUAL_UNICODE_CHARACTER = "\u2264"; // (<= as single character)

	private static final String X_AXIS_LABEL = "Retention Time (s)";

	private static final  String Y_AXIS_LABEL = "M/Z";
	
	
	//  Max pixel intensity per 8 bit channel for 24 bit color 
	private static final int PIXEL_INTENSITY_PER_CHANNEL = (int) ( Math.pow( 2, 8 ) );
	
	/**
	 * Since displayed pixel is zero to ( # possible pixel value - 1 )
	 */
	private final static int PIXEL_INTENSITY_PER_CHANNEL_MINUS_ONE = PIXEL_INTENSITY_PER_CHANNEL - 1;

	private static final int TICK_MARK_LENGTH = 4;
	private static final int TICK_MARK_VERTICAL_AXIS_VERTICAL_OFFSET = 4;
	private static final int TICK_MARK_HORIZONTAL_AXIS_VERTICAL_OFFSET = 1;
	
	private static final int IMAGE_MARGIN_LEFT = 60;
	private static final int IMAGE_MARGIN_RIGHT = 130;
	private static final int IMAGE_MARGIN_TOP = 20;
	private static final int IMAGE_MARGIN_BOTTOM = 30;

	private static final int HORIZONTAL_AXIS_LABEL_Y_OFFSET_FROM_IMAGE_BOTTOM = IMAGE_MARGIN_BOTTOM - 10; //  Measured up from bottom edge of image

	
	private static final int VERTICAL_AXIS_LABEL_X_OFFSET_FROM_IMAGE = 20;
	
	private static final int LEGEND_LABEL_VERTICAL_OFFSET_FROM_TOP_MARGIN = 10; // Label above Legend
	//  Top of Legend position
	private static final int LEGEND_VERTICAL_OFFSET_FROM_TOP_MARGIN = LEGEND_LABEL_VERTICAL_OFFSET_FROM_TOP_MARGIN + 15;

	private static final int LEGEND_OFFSET_FROM_IMAGE = 10;
	private static final int LEGEND_WIDTH = 10;
	private static final int LEGEND_LABELS_SIGNIFICANT_DIGITS = 4;

	
	
	/**
	 * private constructor
	 */
	private MS1_All_IntensityHeatmapImage(){}
	public static MS1_All_IntensityHeatmapImage getInstance( ) throws Exception {
		MS1_All_IntensityHeatmapImage instance = new MS1_All_IntensityHeatmapImage();
		return instance;
	}

	/**
	 * Returned object
	 *
	 */
	public static class MS1_All_IntensityHeatmapImageResult {

		byte[] imageAsBytes;

		public byte[] getImageAsBytes() {
			return imageAsBytes;
		}
		public void setImageAsBytes(byte[] imageAsBytes) {
			this.imageAsBytes = imageAsBytes;
		}
	}
	
	/**
	 * @param requestedImageWidth
	 * @return
	 */
	public boolean isRequestedImageWidthAllowed( int requestedImageWidth ) {
		if ( Arrays.binarySearch( ALLOWED_REQUESTED_IMAGE_WIDTHS, requestedImageWidth ) >= 0 ) {
			return true;
		}
		return false;
	}

	/**
	 * @param scanFileId
	 * @param ImageSize
	 * @return
	 * @throws Exception
	 */
	public MS1_All_IntensityHeatmapImageResult getHeatmap( int scanFileId, Integer requestedImageWidth ) throws Exception {

		if ( requestedImageWidth != null ) {
			if ( ! isRequestedImageWidthAllowed( requestedImageWidth ) ) {
				// It is expected that calling programs will call isRequestedImageWidthAllowed(...) first
				//  to validate that the value is allowed
				String msg = "requestedImageWidth is not an allowed value: " + requestedImageWidth
						+ ", allowed values: " + ALLOWED_REQUESTED_IMAGE_WIDTHS;
				log.error( msg );
				throw new IllegalArgumentException( msg );
			}
		}
		
		MS1_IntensitiesBinnedSummedMapToJSONRoot ms1_IntensitiesBinnedSummedMapToJSONRoot =
				getMS1_IntensitiesBinnedSummedMapToJSONRoot( scanFileId );

		if ( ms1_IntensitiesBinnedSummedMapToJSONRoot == null ) {
			
			return new MS1_All_IntensityHeatmapImageResult();
		}
		
		BufferedImage bufferedImage = 
				getImage( requestedImageWidth, ms1_IntensitiesBinnedSummedMapToJSONRoot, scanFileId /* for logging */ );

		ByteArrayOutputStream baos = new ByteArrayOutputStream(); // ( imageWidth * imageHeight * 4 );

		ImageIO.write( bufferedImage, "PNG", baos );

		baos.close();

		byte[] imageAsBytes = baos.toByteArray();
		
		MS1_All_IntensityHeatmapImageResult resultObj = new MS1_All_IntensityHeatmapImageResult();
		
		resultObj.setImageAsBytes( imageAsBytes );
		return resultObj;
	}
	
	/**
	 * @param requestedImageWidth
	 * @param ms1_IntensitiesBinnedSummedMapToJSONRoot
	 * @return
	 * @throws ProxlWebappInternalErrorException
	 * @throws Exception
	 */
	public BufferedImage getImage(
			Integer requestedImageWidth,
			MS1_IntensitiesBinnedSummedMapToJSONRoot ms1_IntensitiesBinnedSummedMapToJSONRoot,
			Integer scanFileId /* for logging */ )
			throws ProxlWebappInternalErrorException, Exception {

		CreateFullSizeImageForMS1IntensityDataResult createFullSizeImageForMS1IntensityDataResult = 
				createFullSizeImageForMS1IntensityData( 
						ms1_IntensitiesBinnedSummedMapToJSONRoot, PLOTTED_PIXEL_COLORS, scanFileId /* for logging */ );
		
		//  Add tick marks, legends, labels, etc
		BufferedImage bufferedImage = 
				createImageWithAdditions( requestedImageWidth, ms1_IntensitiesBinnedSummedMapToJSONRoot, createFullSizeImageForMS1IntensityDataResult, PLOTTED_PIXEL_COLORS );
		
		return bufferedImage;
	}
	
	/**
	 * @param newImageWidth
	 * @param bufferedImage
	 * @return
	 */
	public BufferedImage createResizedImageForWidth( final int newImageWidth, final BufferedImage bufferedImage ) throws Exception {

		//  Resize the image to the image width
		
		int currentWidth = bufferedImage.getWidth();
		
		if ( currentWidth == newImageWidth ) {
			return bufferedImage;
		}
		
		boolean increasingSize = false;
		if ( newImageWidth > currentWidth ) {
			increasingSize = true;
		}
		
		BufferedImage bufferedImageResized = bufferedImage;
		
		//  If reducing size of image, incrementally scale image by cutting size in half 
		//  until at desired image size
		
		while ( true ) {
		
			float xScale = 0.5f;
			int bufferedImageWidth = bufferedImageResized.getWidth();
			if ( increasingSize || bufferedImageWidth * xScale < newImageWidth ) {
				xScale = (float) newImageWidth / bufferedImageWidth;
			}
			
			float yScale = xScale;

			ParameterBlock pb = new ParameterBlock();
			pb.addSource( bufferedImageResized ); // The source image
			pb.add(xScale);         // The xScale
			pb.add(yScale);         // The yScale
			pb.add(0.0F);           // The x translation
			pb.add(0.0F);           // The y translation
			pb.add(new InterpolationBilinear()); // The interpolation

			bufferedImageResized = JAI.create("scale", pb, null).getAsBufferedImage();
			int bufferedImageWidthAfterShrunk = bufferedImageResized.getWidth();
			if ( increasingSize || bufferedImageWidthAfterShrunk <= newImageWidth ) {
				break;
			}
		}
		
		// For debugging, write out the image at this point
//		OutputStream osWithAdditions = new FileOutputStream( "ms1_intensitiesResizedCoreChart.png" );
//		ImageIO.write( bufferedImage, "PNG", osWithAdditions );
//		osWithAdditions.close();
		
		int bufferedImageResizedWidth = bufferedImageResized.getWidth();
		int bufferedImageResizedHeight = bufferedImageResized.getHeight();
		
		WritableRaster bufferedImageResizedRaster = bufferedImageResized.getRaster();
		int[] pixel = new int[ 3 ];
		
		//  Remove all black rows from bottom of result image
		int rowCountToRemove = 0;
		
		for ( int y_Index = bufferedImageResizedHeight - 1; y_Index > 0; y_Index-- ) {
			boolean foundAllBlack = true;
			for ( int x_Index = 0; x_Index < bufferedImageResizedWidth; x_Index++ ) {
				pixel = bufferedImageResizedRaster.getPixel( x_Index, y_Index, pixel );
				for ( int pixelIndex = 0; pixelIndex < pixel.length; pixelIndex++ ) {
					if ( pixel[ pixelIndex ] != 0 ) {
						foundAllBlack = false;
						break;
					}
				}
				if ( ! foundAllBlack ) {
					break;
				}
			}
			if ( ! foundAllBlack ) {
				break;
			}
			rowCountToRemove++;
		}
		
		if ( rowCountToRemove > 0 ) {
			log.warn( "Removing " + rowCountToRemove + " rows from bottom of rescaled image since all black" );
			int subImageHeight = bufferedImageResizedHeight - rowCountToRemove;
			bufferedImageResized = bufferedImageResized.getSubimage( 0, 0, bufferedImageResizedWidth, subImageHeight );
		}
		
		return bufferedImageResized;
	}
	
	/**
	 * @param ms1_IntensitiesBinnedSummedMapToJSONRoot
	 * @param pixelPlottedColors
	 * @return
	 * @throws ProxlWebappInternalErrorException
	 */
	public CreateFullSizeImageForMS1IntensityDataResult createFullSizeImageForMS1IntensityData(
			MS1_IntensitiesBinnedSummedMapToJSONRoot ms1_IntensitiesBinnedSummedMapToJSONRoot,
			Color[] pixelPlottedColors,
			Integer scanFileId /* for logging */ )
			throws ProxlWebappInternalErrorException {
		
		MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot summaryData =
				ms1_IntensitiesBinnedSummedMapToJSONRoot.getSummaryData();
		Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap =
				ms1_IntensitiesBinnedSummedMapToJSONRoot.getMs1_IntensitiesBinnedSummedMap();

		long binnedSummedIntensityCount = summaryData.getBinnedSummedIntensityCount();

		double intensityBinnedMinActual = summaryData.getIntensityBinnedMin();
		double intensityBinnedMaxActual = summaryData.getIntensityBinnedMax();
		
		if ( intensityBinnedMinActual < 1 ) {
			//  intensityBinnedMinActual cannot be zero since taking the log of zero is problematic
			log.warn( "intensityBinnedMinActual < 1"
					+ " so setting intensityBinnedMinActual = 1. "
					+ "intensityBinnedMinActual was: " + intensityBinnedMinActual
					+ ", scanFileId: " + scanFileId );
			intensityBinnedMinActual = 1;
		}

		if ( log.isDebugEnabled() ) {
			log.debug( "summaryData.getRtBinMax(): " + summaryData.getRtBinMax() );
			log.debug( "summaryData.getRtMaxPossibleValue(): " + summaryData.getRtMaxPossibleValue() );

			log.debug( "summaryData.getMzBinMax(): " + summaryData.getMzBinMax() );
			log.debug( "summaryData.getMzMaxPossibleValue(): " + summaryData.getMzMaxPossibleValue() );

			log.debug( "intensityBinnedMinActual: " + intensityBinnedMinActual );
			log.debug( "intensityBinnedMaxActual: " + intensityBinnedMaxActual );

			log.debug( "intensityBinned Min Max being used for processing (Currently natural log of min and max):" );
		}

		/////////////////////////////
		
		if ( binnedSummedIntensityCount > Integer.MAX_VALUE ) {
			String msg = "binnedSummedIntensityCount  > Integer.MAX_VALUE so unable to process the data since put it into an array.  "
					+ "binnedSummedIntensityCount: " + binnedSummedIntensityCount;
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}

		int binnedSummedIntensityCountInt = (int) binnedSummedIntensityCount;

		double[] intensityArrayForSort = new double[ binnedSummedIntensityCountInt ];

		int intensityArrayForSortIndex = 0;
		for ( Map.Entry<Long, Map<Long, Double>> rtEntry : ms1_IntensitiesBinnedSummedMap.entrySet() ) {
			for ( Map.Entry<Long, Double> mzEntry : rtEntry.getValue().entrySet() ) {
				intensityArrayForSort[ intensityArrayForSortIndex ] = mzEntry.getValue();
				intensityArrayForSortIndex++;
			}
		}
		//   Available in Java 8  
		//	Arrays.parallelSort( intensityArrayForSort );
		Arrays.sort( intensityArrayForSort );

		// Get a DescriptiveStatistics instance
		DescriptiveStatistics stats = new DescriptiveStatistics();
		 
		// Add the data from the array
		for( int i = 0; i < intensityArrayForSort.length; i++) {
		        stats.addValue(intensityArrayForSort[i]);
		}

		final int FIRST_QUARTER_PERCENTILE = 25;
		final int THIRD_QUARTER_PERCENTILE = 75;
		final double QUARTILE_MULTIPLIER = 2;
		
		// Compute some statistics
		double firstquarter = stats.getPercentile( FIRST_QUARTER_PERCENTILE );
		double thirdquarter = stats.getPercentile( THIRD_QUARTER_PERCENTILE );
		 
		
		double iqr =  thirdquarter - firstquarter; // Interquartile Range
		double intensityMinBasedOnPercentilesActual = firstquarter - QUARTILE_MULTIPLIER * iqr;
		double intensityMaxBasedOnPercentilesActual = thirdquarter + QUARTILE_MULTIPLIER * iqr;
		
		if ( intensityMinBasedOnPercentilesActual < intensityBinnedMinActual ) {
			if ( log.isDebugEnabled() ) {
				log.debug( "intensityMinBasedOnPercentilesActual < intensityBinnedMinActual so setting intensityMinBasedOnPercentilesActual = intensityBinnedMinActual.  "
						+ "intensityMinBasedOnPercentilesActual: " + intensityMinBasedOnPercentilesActual 
						+ ", intensityBinnedMinActual: " + intensityBinnedMinActual );
			}
			intensityMinBasedOnPercentilesActual = intensityBinnedMinActual;
		}
		if ( intensityMaxBasedOnPercentilesActual > intensityBinnedMaxActual ) {
			if ( log.isDebugEnabled() ) {
				log.debug( "intensityMaxBasedOnPercentilesActual > intensityBinnedMaxActual so setting intensityMaxBasedOnPercentilesActual = intensityBinnedMaxActual.  "
						+ "intensityMinBasedOnPercentilesActual: " + intensityMinBasedOnPercentilesActual 
						+ ", intensityBinnedMaxActual: " + intensityBinnedMaxActual );
			}
			intensityMaxBasedOnPercentilesActual = intensityBinnedMaxActual;
		}
		
		double intensityMinBasedOnPercentilesUse = getIntensityUseFromIntensity( intensityMinBasedOnPercentilesActual );
		double intensityMaxBasedOnPercentilesUse = getIntensityUseFromIntensity( intensityMaxBasedOnPercentilesActual );
		
		if ( intensityMinBasedOnPercentilesActual < 0 ) {
			intensityMinBasedOnPercentilesUse = 0;
			log.warn( "intensityMinBasedOnPercentilesActual < 0 so setting intensityMinBasedOnPercentilesUse = 0."
					+ "  intensityMinBasedOnPercentilesActual: " + intensityMinBasedOnPercentilesActual );
		}
		
		if ( log.isDebugEnabled() ) {
			log.debug( "intensityMinBasedOnPercentilesActual: " + intensityMinBasedOnPercentilesActual 
					+ ", intensityMinBasedOnPercentilesUse: " + intensityMinBasedOnPercentilesUse 
					+ ", intensityMaxBasedOnPercentilesActual: " + intensityMaxBasedOnPercentilesActual
					+ ", intensityMaxBasedOnPercentilesUse: " + intensityMaxBasedOnPercentilesUse
					);
		}

		MultiColorMapper colorMapper = new MultiColorMapper( intensityMinBasedOnPercentilesUse, intensityMaxBasedOnPercentilesUse, pixelPlottedColors );

		BufferedImage bufferedImage = createFullSizeImageOnlyDataActualImage(
				ms1_IntensitiesBinnedSummedMapToJSONRoot, 
				intensityMinBasedOnPercentilesUse, 
				colorMapper );
		
		CreateFullSizeImageForMS1IntensityDataResult result = new CreateFullSizeImageForMS1IntensityDataResult();
		
		result.bufferedImage = bufferedImage;
		
		result.intensityMax_ForPlotting = intensityMaxBasedOnPercentilesUse;
		result.intensityMin_ForPlotting = intensityMinBasedOnPercentilesUse;
		
		result.intensityMax_Actual = getRawIntensityFromIntensityUse( result.intensityMax_ForPlotting );
		result.intensityMin_Actual = getRawIntensityFromIntensityUse( result.intensityMin_ForPlotting );
		
		return result;
	}

	/**
	 * Result from method createFullSizeImageForMS1IntensityData
	 *
	 */
	public static class CreateFullSizeImageForMS1IntensityDataResult {
		BufferedImage bufferedImage;
		double intensityMax_Actual;
		double intensityMin_Actual;

		double intensityMax_ForPlotting;
		double intensityMin_ForPlotting;
		
		public BufferedImage getBufferedImage() {
			return bufferedImage;
		}
		public void setBufferedImage(BufferedImage bufferedImage) {
			this.bufferedImage = bufferedImage;
		}
		public double getIntensityMax_Actual() {
			return intensityMax_Actual;
		}
		public void setIntensityMax_Actual(double intensityMax_Actual) {
			this.intensityMax_Actual = intensityMax_Actual;
		}
		public double getIntensityMin_Actual() {
			return intensityMin_Actual;
		}
		public void setIntensityMin_Actual(double intensityMin_Actual) {
			this.intensityMin_Actual = intensityMin_Actual;
		}
		public double getIntensityMax_ForPlotting() {
			return intensityMax_ForPlotting;
		}
		public void setIntensityMax_ForPlotting(double intensityMax_ForPlotting) {
			this.intensityMax_ForPlotting = intensityMax_ForPlotting;
		}
		public double getIntensityMin_ForPlotting() {
			return intensityMin_ForPlotting;
		}
		public void setIntensityMin_ForPlotting(double intensityMin_ForPlotting) {
			this.intensityMin_ForPlotting = intensityMin_ForPlotting;
		}
	}

	/**
	 * @param ms1_IntensitiesBinnedSummedMapToJSONRoot
	 * @param intensityMinUse
	 * @param colorMapper
	 * @return
	 * @throws ProxlWebappInternalErrorException
	 */
	public BufferedImage createFullSizeImageOnlyDataActualImage(
			MS1_IntensitiesBinnedSummedMapToJSONRoot ms1_IntensitiesBinnedSummedMapToJSONRoot,
			double intensityMinUse,
			MultiColorMapper colorMapper) throws ProxlWebappInternalErrorException {
		
		Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap = ms1_IntensitiesBinnedSummedMapToJSONRoot.getMs1_IntensitiesBinnedSummedMap(); 
		MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot summaryData = ms1_IntensitiesBinnedSummedMapToJSONRoot.getSummaryData();

		int imageWidth = (int) ( summaryData.getRtMaxPossibleValue() - summaryData.getRtBinMin() );
		int imageHeight = (int) ( summaryData.getMzMaxPossibleValue() - summaryData.getMzBinMin() );

		BufferedImage bufferedImage = createBufferedImageForWidthHeight( imageWidth, imageHeight );


		WritableRaster raster = bufferedImage.getRaster();
		int[] nextPixel = new int[ 3 ];

		nextPixel[0] = PIXEL_INTENSITY_PER_CHANNEL_MINUS_ONE;
		nextPixel[1] = PIXEL_INTENSITY_PER_CHANNEL_MINUS_ONE;
		nextPixel[2] = PIXEL_INTENSITY_PER_CHANNEL_MINUS_ONE;

		//  Init image to white
		for ( int x = 0; x < imageWidth; x++ ) {
			for ( int y = 0; y < imageHeight; y++ ) {
				raster.setPixel(x, y, nextPixel );
			}
		}

//		int intensityTop10percentCounter = 0;
//		int intensityTop20percentCounter = 0;
//
//		int intensityRedBetween_100_150 = 0;

		for ( Map.Entry<Long, Map<Long, Double>> rtEntry : ms1_IntensitiesBinnedSummedMap.entrySet() ) {
			long retentionTimeBin = rtEntry.getKey();

			int xAxis = (int) ( retentionTimeBin - summaryData.getRtBinMin() );

			for ( Map.Entry<Long, Double> mzEntry : rtEntry.getValue().entrySet() ) {
				long mzBin = mzEntry.getKey();

				//  flipping y axis so min mzBin is at the bottom of the image
				int yAxis = imageHeight - ( (int) ( mzBin - summaryData.getMzBinMin() ) ) - 1;

				double intensityActual = mzEntry.getValue();
				
				double intensityUse = intensityActual;

				//  Get converted intensity used for graphing.  May be no change or otherwise.   getIntensityUseFromIntensity(...) at top of this file
				intensityUse = getIntensityUseFromIntensity( intensityActual );
				
				double intensityUse_MinusMinBasedOnPercentiles = intensityUse - intensityMinUse;

				//  Update counters
//				if ( intensityUse_MinusMinBasedOnPercentiles > intensity_Max_Minus_Min_BasedOnPercentilesUse * 0.9 ) { 
//					intensityTop10percentCounter++;
//				}
//				if ( intensityUse_MinusMinBasedOnPercentiles > intensity_Max_Minus_Min_BasedOnPercentilesUse * 0.8 ) { 
//					intensityTop20percentCounter++;
//				}
				
				Color color = colorMapper.getColor( intensityUse_MinusMinBasedOnPercentiles );
				
				//  The values for the next pixel
				int pixelIntensity_Red = color.getRed();
				int pixelIntensity_Green = color.getGreen();
				int pixelIntensity_Blue = color.getBlue();
				
				nextPixel[0] = pixelIntensity_Red;
				nextPixel[1] = pixelIntensity_Green;
				nextPixel[2] = pixelIntensity_Blue;
				
				try {
					raster.setPixel( xAxis, yAxis, nextPixel );
				} catch ( Exception e ) {
					String msg = "Failed to add pixel at xAxis: " + xAxis 
							+ ", yAxis: " + yAxis 
							+ ", nextPixel: " + nextPixel
							+ ", imageWidth: " + imageWidth 
							+ ", imageHeight: " + imageHeight;
					log.error( msg );
					throw new ProxlWebappInternalErrorException( msg );
				}
			}
		}
		return bufferedImage;
	}
	

	
	/**
	 * 
	 * Add tick marks, legends, labels, etc
	 * 
	 * @param bufferedImage
	 * @return
	 * @throws Exception
	 */
	public BufferedImage createImageWithAdditions( 
			Integer requestedImageWidth,
			MS1_IntensitiesBinnedSummedMapToJSONRoot ms1_IntensitiesBinnedSummedMapToJSONRoot,
			CreateFullSizeImageForMS1IntensityDataResult createFullSizeImageForMS1IntensityDataResult,
			Color[] colors ) throws Exception {
		
		BufferedImage bufferedImage = createFullSizeImageForMS1IntensityDataResult.bufferedImage;
		
		MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot summaryData =
				ms1_IntensitiesBinnedSummedMapToJSONRoot.getSummaryData();

		int bufferedImageWidth = bufferedImage.getWidth();
		int bufferedImageHeight = bufferedImage.getHeight();
		
		if ( requestedImageWidth != null ) {
			
			//  adjust width and height of data image based on requestedImageWidth

			int newImageWidth = requestedImageWidth - IMAGE_MARGIN_LEFT - IMAGE_MARGIN_RIGHT;

			bufferedImage = createResizedImageForWidth( newImageWidth, bufferedImage );
			
			bufferedImageWidth = newImageWidth;
			bufferedImageHeight = (int) ( bufferedImage.getHeight() * ( (float) newImageWidth / bufferedImage.getWidth() ) );
		}
		
		
		//  Create new larger BufferedImage with space for scale bars and legend
		BufferedImage bufferedImageWithAdditions = createBufferedImageForWidthHeight(
				bufferedImageWidth + IMAGE_MARGIN_LEFT + IMAGE_MARGIN_RIGHT, 
				bufferedImageHeight + IMAGE_MARGIN_TOP + IMAGE_MARGIN_BOTTOM );

		int bufferedImageWidthWithAdditions = bufferedImageWithAdditions.getWidth();
		int bufferedImageHeightWithAdditions = bufferedImageWithAdditions.getHeight();
		
		WritableRaster rasterWithAdditions = bufferedImageWithAdditions.getRaster();

		int[] nextPixelWithAdditions = new int[3];

		nextPixelWithAdditions[0] = PIXEL_INTENSITY_PER_CHANNEL_MINUS_ONE;
		nextPixelWithAdditions[1] = PIXEL_INTENSITY_PER_CHANNEL_MINUS_ONE;
		nextPixelWithAdditions[2] = PIXEL_INTENSITY_PER_CHANNEL_MINUS_ONE;

		//  Init image to white
		for ( int x = 0; x < bufferedImageWidthWithAdditions; x++ ) {
			for ( int y = 0; y < bufferedImageHeightWithAdditions; y++ ) {
				rasterWithAdditions.setPixel(x, y, nextPixelWithAdditions );
			}
		}

		Graphics2D graphicsObj = null;
		
		try {
			graphicsObj = bufferedImageWithAdditions.createGraphics();
			
			 RenderingHints renderingHints = new RenderingHints(
			            RenderingHints.KEY_TEXT_ANTIALIASING,
			            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			 graphicsObj.setRenderingHints( renderingHints );

			//  Will auto scale bufferedImage if needed 
			boolean isDrawn = graphicsObj.drawImage( bufferedImage, IMAGE_MARGIN_LEFT, IMAGE_MARGIN_TOP, bufferedImageWidth, bufferedImageHeight, null /* Callback */ );
			if ( ! isDrawn ) {
				throw new ProxlWebappInternalErrorException( "graphicsObj.drawImage(...) returned false" );
			}

			//  Draw black box around main chart data
			graphicsObj.setColor( Color.BLACK );
			// left 
			graphicsObj.drawLine( IMAGE_MARGIN_LEFT - 1, IMAGE_MARGIN_TOP - 1, IMAGE_MARGIN_LEFT - 1, IMAGE_MARGIN_TOP + bufferedImageHeight );
			// right
			graphicsObj.drawLine( IMAGE_MARGIN_LEFT + bufferedImageWidth, IMAGE_MARGIN_TOP - 1, IMAGE_MARGIN_LEFT + bufferedImageWidth, IMAGE_MARGIN_TOP + bufferedImageHeight );
			// top
			graphicsObj.drawLine( IMAGE_MARGIN_LEFT - 1, IMAGE_MARGIN_TOP - 1, IMAGE_MARGIN_LEFT + bufferedImageWidth, IMAGE_MARGIN_TOP - 1 );
			// bottom
			graphicsObj.drawLine( IMAGE_MARGIN_LEFT - 1, IMAGE_MARGIN_TOP + bufferedImageHeight, IMAGE_MARGIN_LEFT + bufferedImageWidth, IMAGE_MARGIN_TOP + bufferedImageHeight );

			Font font = graphicsObj.getFont(); // get current font
			
			String fontName = font.getName();
			int fontSize = font.getSize();
			
			if ( log.isDebugEnabled() ) {
				log.debug( "fontName: " + fontName + ", fontSize: " + fontSize );
			}
			
			Font fontSize12 = font.deriveFont( 12 );
			//  Change to current font, size 12
			graphicsObj.setFont( fontSize12 );

			// get metrics from the graphics
			FontMetrics fontMetrics = graphicsObj.getFontMetrics( graphicsObj.getFont() ); // from current font
			// get the height of a line of text in this
			// font and render context
//			int fontHeight = fontMetrics.getHeight();
			
			NumberFormat numberFormat = NumberFormat.getInstance();

			//  X axis label
			
			int x_axis_Label_ApproxWidth = getApproxStringWidth( X_AXIS_LABEL, fontMetrics );

			graphicsObj.drawString( X_AXIS_LABEL, 
					IMAGE_MARGIN_LEFT + ( bufferedImageWidth / 2 ) - ( x_axis_Label_ApproxWidth / 2 ), 
					IMAGE_MARGIN_TOP + bufferedImageHeight + HORIZONTAL_AXIS_LABEL_Y_OFFSET_FROM_IMAGE_BOTTOM );

			//  Draw X Axis Tick Marks and Labels

			//  Draw left tick mark and label
			drawHorizontalAxisTickMarkAndLabel( 
					numberFormat.format( summaryData.getRtBinMin() ),
					IMAGE_MARGIN_LEFT, 
					IMAGE_MARGIN_TOP + bufferedImageHeight, 
					graphicsObj, fontMetrics );

			//  Draw right tick mark and label
			drawHorizontalAxisTickMarkAndLabel( 
					numberFormat.format( summaryData.getRtBinMax() ),
					IMAGE_MARGIN_LEFT + bufferedImageWidth, 
					IMAGE_MARGIN_TOP + bufferedImageHeight, 
					graphicsObj, fontMetrics );
			
			
			//  Draw Y Axis Label text on Y Axis, rotated
			AffineTransform originalAffineTransform = graphicsObj.getTransform();
			graphicsObj.rotate( - Math.PI / 2 );
			
			//  Since Rotated, all X and Y are reversed
			
			//  Y axis label

			int y_axis_Label_ApproxWidth = getApproxStringWidth( Y_AXIS_LABEL, fontMetrics );

			graphicsObj.drawString( 
					Y_AXIS_LABEL,
					- ( IMAGE_MARGIN_TOP + ( bufferedImageHeight / 2 ) + ( y_axis_Label_ApproxWidth / 2 ) ), 
					IMAGE_MARGIN_LEFT - VERTICAL_AXIS_LABEL_X_OFFSET_FROM_IMAGE );
			
			//  Finish Rotation
			
			//  set back to previous/normal transform
			graphicsObj.setTransform( originalAffineTransform );
			
			//  Draw Y Axis Tick Marks and Labels
			graphicsObj.setColor( Color.BLACK );

			//  Draw top tick mark and label
			drawVerticalAxisTickMarkAndLabel( 
					numberFormat.format( summaryData.getMzBinMax() ), 
					IMAGE_MARGIN_TOP,
					graphicsObj, 
					fontMetrics );

			//  Draw bottom tick mark and label
			drawVerticalAxisTickMarkAndLabel( 
					numberFormat.format( summaryData.getMzBinMin() ), 
					IMAGE_MARGIN_TOP + bufferedImageHeight - 2,
					graphicsObj, 
					fontMetrics );
			
			//  Text above color legend on right
			
			graphicsObj.drawString( 
					"Total Ion Current",
					IMAGE_MARGIN_LEFT + bufferedImageWidth + LEGEND_OFFSET_FROM_IMAGE, 
					IMAGE_MARGIN_TOP + LEGEND_LABEL_VERTICAL_OFFSET_FROM_TOP_MARGIN );
			
			//  Draw color legend on right
			drawLegend( 
					IMAGE_MARGIN_LEFT + bufferedImageWidth + LEGEND_OFFSET_FROM_IMAGE,
					IMAGE_MARGIN_TOP + LEGEND_VERTICAL_OFFSET_FROM_TOP_MARGIN,
					IMAGE_MARGIN_LEFT + bufferedImageWidth + LEGEND_OFFSET_FROM_IMAGE + LEGEND_WIDTH,
					IMAGE_MARGIN_TOP + bufferedImageHeight,
					createFullSizeImageForMS1IntensityDataResult,
					colors, 
					bufferedImageWithAdditions,
					graphicsObj );
			
		} finally {
			if ( graphicsObj != null ) {
				graphicsObj.dispose();
			}
		}
		
		if ( log.isDebugEnabled() ) {

			int bufferedImageWithAdditionsWidth = bufferedImageWithAdditions.getWidth();
			int bufferedImageWithAdditionsHeight = bufferedImageWithAdditions.getHeight();
			
			log.debug( 
					"bufferedImageWidth: " + bufferedImageWidth
					+ ", bufferedImageHeight: " + bufferedImageHeight
					+ ", bufferedImageWithAdditionsWidth: " + bufferedImageWithAdditionsWidth 
					+ ", bufferedImageWithAdditionsHeight: " + bufferedImageWithAdditionsHeight );
		}
		
		return bufferedImageWithAdditions;
	}

	/**
	 * @param tickMarkLabel
	 * @param tickMark_center_X
	 * @param tickMark_top_Y
	 * @param graphicsObj
	 * @param fontMetrics
	 */
	public void drawHorizontalAxisTickMarkAndLabel(
			String tickMarkLabel,
			int tickMark_center_X,
			int tickMark_top_Y,
			Graphics2D graphicsObj,
			FontMetrics fontMetrics) {
		
		int fontAscent = fontMetrics.getAscent();
		
		//  Skip drawing tick mark
//		graphicsObj.drawLine( tickMark_center_X, tickMark_top_Y, tickMark_center_X, tickMark_top_Y  + TICK_MARK_LENGTH);
		
		int tickMarkLabelApproxWidth = getApproxStringWidth( tickMarkLabel, fontMetrics );

		int tickMarkLabel_X = tickMark_center_X - ( tickMarkLabelApproxWidth / 2 );
		int tickMarkLabel_Y = tickMark_top_Y + fontAscent + TICK_MARK_LENGTH + TICK_MARK_HORIZONTAL_AXIS_VERTICAL_OFFSET;
				
		graphicsObj.drawString( tickMarkLabel, tickMarkLabel_X, tickMarkLabel_Y );
	}
	
	/**
	 * @param tickMarkLabel
	 * @param tickMark_Y
	 * @param graphicsObj
	 * @param fontMetrics
	 */
	public void drawVerticalAxisTickMarkAndLabel(
			String tickMarkLabel,
			int tickMark_Y,
			Graphics2D graphicsObj,
			FontMetrics fontMetrics) {
		
		//  Skip drawing tick mark
//		graphicsObj.drawLine( IMAGE_MARGIN_LEFT - 1 - TICK_MARK_LENGTH, tickMark_Y, IMAGE_MARGIN_LEFT - 1, tickMark_Y );
		
		int tickMarkLabelApproxWidth = getApproxStringWidth( tickMarkLabel, fontMetrics );

		int tickMarkLabel_X = IMAGE_MARGIN_LEFT - 1 - TICK_MARK_LENGTH - tickMarkLabelApproxWidth;
		int tickMarkLabel_Y = tickMark_Y + TICK_MARK_VERTICAL_AXIS_VERTICAL_OFFSET;
				
		graphicsObj.drawString( tickMarkLabel, tickMarkLabel_X, tickMarkLabel_Y );
	}

	/**
	 * @param stringForWidth
	 * @param metrics
	 * @return
	 */
	public int getApproxStringWidth( String stringForWidth, FontMetrics metrics) {
		// get the advance of the text in this font and render context.
		//  Advance width is the distance from the origin of the text to the position of a subsequently rendered string.
		int advance = metrics.stringWidth( stringForWidth );
		return advance;
	}
	
	/**
	 * Draw Vertical Legend
	 * 
	 * @param start_X
	 * @param start_Y
	 * @param end_X
	 * @param end_Y
	 * @param createFullSizeImageForMS1IntensityDataResult
	 * @param colors
	 * @param bufferedImage
	 */
	public void drawLegend( 
			int start_X, int start_Y, int end_X, int end_Y, 
			CreateFullSizeImageForMS1IntensityDataResult createFullSizeImageForMS1IntensityDataResult,
			Color[] colors, 
			BufferedImage bufferedImage,
			Graphics2D graphicsObj ) {
		
		int x_Count = end_X - start_X + 1;
		int y_Count = end_Y - start_Y + 1;
		
		MultiColorMapper mapper = new MultiColorMapper( 1.0, (double) y_Count, colors );

        WritableRaster raster = bufferedImage.getRaster();

        for ( int y_index = 1; y_index <= y_Count; y_index++ ) {
        	Color color = mapper.getColor( y_index + 1.0 );
        	int[] nextPixel = { color.getRed(), color.getGreen(), color.getBlue() };

        	for ( int x_index = 0; x_index < x_Count; x_index++ ) {
        		raster.setPixel( x_index + start_X,  y_Count - y_index + start_Y, nextPixel );
        	}
        }

		//  Draw black box around legend
		graphicsObj.setColor( Color.BLACK );
		// left 
		graphicsObj.drawLine( start_X - 1, start_Y - 1, start_X - 1, end_Y + 1 );
		// right
		graphicsObj.drawLine( end_X + 1, start_Y - 1, end_X + 1, end_Y + 1 );
		// top
		graphicsObj.drawLine( start_X - 1, start_Y - 1, end_X + 1, start_Y - 1 );
		// bottom
		graphicsObj.drawLine( start_X - 1, end_Y + 1, end_X + 1, end_Y + 1 );

//		Font font = graphicsObj.getFont(); // get current font
//		
//		String fontName = font.getName();
//		int fontSize = font.getSize();
//		
//		if ( log.isDebugEnabled() ) {
//			log.debug( "fontName: " + fontName + ", fontSize: " + fontSize );
//		}
//		
//		Font fontSize12 = font.deriveFont( 12 );
//		//  Change to current font, size 12
//		graphicsObj.setFont( fontSize12 );

		//  Draw Y Axis Tick Marks
		graphicsObj.setColor( Color.BLACK );
		
		BigDecimal tickMarkValueToSignificantDigits = null;
		String tickMarkLabel = null;
		MathContext mcForPrecision = new MathContext( LEGEND_LABELS_SIGNIFICANT_DIGITS ); // number of signicant digits

		tickMarkValueToSignificantDigits = new BigDecimal( createFullSizeImageForMS1IntensityDataResult.intensityMax_Actual, mcForPrecision );
		tickMarkLabel = GREATER_THAN_OR_EQUAL_UNICODE_CHARACTER + " " + tickMarkValueToSignificantDigits.toString();
		
		drawLegendTickMarkAndLabel( 
				tickMarkLabel, 
				end_X + 1,
				start_Y,
				graphicsObj );

		tickMarkValueToSignificantDigits = new BigDecimal( createFullSizeImageForMS1IntensityDataResult.intensityMin_Actual, mcForPrecision );
		tickMarkLabel = LESS_THAN_OR_EQUAL_UNICODE_CHARACTER + " " + tickMarkValueToSignificantDigits.toString();

		drawLegendTickMarkAndLabel( 
				tickMarkLabel, 
				end_X + 1,
				end_Y,
				graphicsObj );

		graphicsObj.drawString( "Log Scale", end_X + TICK_MARK_LENGTH + 1, start_Y + ( ( end_Y - start_Y ) / 2 ) );
	}

	/**
	 * @param tickMarkLabelValue
	 * @param tickMark_Start_X
	 * @param tickMark_Y
	 * @param graphicsObj
	 */
	public void drawLegendTickMarkAndLabel(
			String tickMarkLabelValue,
			int tickMark_Start_X,
			int tickMark_Y,
			Graphics2D graphicsObj ) {
		
		//  Draw top tick mark and label
//		graphicsObj.drawLine( tickMark_Start_X, tickMark_Y, tickMark_Start_X + TICK_MARK_LENGTH, tickMark_Y );
		
		int tickMarkLabel_X = tickMark_Start_X + TICK_MARK_LENGTH + 2;
		int tickMarkLabel_Y = tickMark_Y + TICK_MARK_VERTICAL_AXIS_VERTICAL_OFFSET;
				
		graphicsObj.drawString( tickMarkLabelValue, tickMarkLabel_X, tickMarkLabel_Y );
	}
	
	
	/**
	 * @param imageWidth
	 * @param imageHeight
	 * @return
	 */
	public BufferedImage createBufferedImageForWidthHeight(int imageWidth, int imageHeight) {
		
		//  Create 24 bit color scale image
		BufferedImage bufferedImage =  new BufferedImage( imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB );
		return bufferedImage;
	}
	
	/**
	 * !!!!  Update getRawIntensityFromIntensityUse(...) when update this method
	 * 
	 * Convert raw intensity to intensity used for graphing.  Currently using natural log
	 * @param intensity
	 * @return
	 */
	private static double getIntensityUseFromIntensity( double intensity ) {
		double intensityUse = Math.log( intensity );  // If change, update next method  getRawIntensityFromIntensityUse
		return intensityUse;
	}

	/**
	 * !!!!  Update getIntensityUseFromIntensity(...) when update this method
	 * 
	 * Convert intensity used for graphing to raw intensity.  Currently using natural log.
	 * 
	 * Inverse of method getIntensityUseFromIntensity( intensity )
	 * @param intensity
	 * @return
	 */
	private static double getRawIntensityFromIntensityUse( double intensityUse ) {
		double intensityRaw = Math.exp( intensityUse );  // If change, update previous method getIntensityUseFromIntensity
		return intensityRaw;
	}
	

	/**
	 * @param scanFileId
	 * @return null if not in db
	 * @throws Exception
	 */
	private MS1_IntensitiesBinnedSummedMapToJSONRoot getMS1_IntensitiesBinnedSummedMapToJSONRoot( int scanFileId ) throws Exception {

		ScanFileMS_1_IntensityBinnedSummedDataDTO scanFileMS_1_IntensityBinnedSummedDataDTO =
				ScanFileMS_1_IntensityBinnedSummedDataDAO.getFromScanFileId( scanFileId );
		if ( scanFileMS_1_IntensityBinnedSummedDataDTO == null ) {
			return null;
		}
		byte[] dataJSON_Gzipped = scanFileMS_1_IntensityBinnedSummedDataDTO.getDataJSON_Gzipped();
		byte[] dataJSON = ZipUnzipByteArray.getInstance().unzipByteArray( dataJSON_Gzipped );

		MS1_IntensitiesBinnedSummedMapToJSONRoot ms1_IntensitiesBinnedSummedMapToJSONRoot =
				MS1_BinnedSummedIntensitiesProcessing.getInstance().getMainObjectFromBytes( dataJSON );

		return ms1_IntensitiesBinnedSummedMapToJSONRoot;
	}
}
