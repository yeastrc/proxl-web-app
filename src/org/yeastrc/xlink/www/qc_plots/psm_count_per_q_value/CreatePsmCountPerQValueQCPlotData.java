package org.yeastrc.xlink.www.qc_plots.psm_count_per_q_value;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.searcher.QValuesFromPsmTblSearcher;


/**
 * 
 *
 */
public class CreatePsmCountPerQValueQCPlotData {

	private static final Logger log = Logger.getLogger(CreatePsmCountPerQValueQCPlotData.class);
	
	


	private static final int BIN_COUNT = 100;  //  Number of points on the chart

//	private static final double MIN_Q_VALUE = 0; // assumed zero
	private static final double MAX_Q_VALUE = 1;
	


	/**
	 * private constructor
	 */
	private CreatePsmCountPerQValueQCPlotData(){}
	
	public static CreatePsmCountPerQValueQCPlotData getInstance( ) throws Exception {
		
		CreatePsmCountPerQValueQCPlotData instance = new CreatePsmCountPerQValueQCPlotData();
		
		return instance;
	}
	

	/**
	 * @param searchId
	 * @param scanFileId
	 * @param psmQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public PsmCountPerQValueQCPlotDataJSONRoot create( List<String> scansForSelectedLinkTypes,	int searchId ) throws Exception {


		if ( scansForSelectedLinkTypes == null || scansForSelectedLinkTypes.isEmpty() ) {

			String msg = "scansForSelectedLinkTypes cannot be empty.";
			
			log.error( msg );

			throw new IllegalArgumentException( msg );
		}
		
		List<Double> qValuesForPSMsthatMeetCriteriaList = 
				QValuesFromPsmTblSearcher.getInstance().getQValues( scansForSelectedLinkTypes, searchId );
	


		//  Process data into bins

		double binSizeAsDouble = ( MAX_Q_VALUE ) / BIN_COUNT;

		
		//   First process the retention times for all PSMs for the scan file into bins, excluding scan level 1 (EXCLUDE_SCAN_LEVEL_1)
		
		int qvalueZeroCount = 0;
		int qvalueOneCount = 0;
		

		int[] qvalueCounts = new int[ BIN_COUNT ];


		for ( double qValue : qValuesForPSMsthatMeetCriteriaList ) {

			int bin = (int) ( (  qValue ) * BIN_COUNT );

			if ( bin < 0 ) {

				bin = 0;

			} else if ( bin >= BIN_COUNT ) {

				bin = BIN_COUNT - 1;
			} 

			qvalueCounts[ bin ]++;
			
			if ( qValue == 0 ) {
				
				qvalueZeroCount++;
			}
			
			if ( qValue == 1 ) {
				
				qvalueOneCount++;
			}

		}
			
		
		//  Populate objects to generate JSON
		
		PsmCountPerQValueQCPlotDataJSONRoot psmCountPerQValueQCPlotDataJSONRoot = new PsmCountPerQValueQCPlotDataJSONRoot();
		
		psmCountPerQValueQCPlotDataJSONRoot.setTotalQValueCount( qValuesForPSMsthatMeetCriteriaList.size() );
		
		psmCountPerQValueQCPlotDataJSONRoot.setQvalueZeroCount( qvalueZeroCount );
		psmCountPerQValueQCPlotDataJSONRoot.setQvalueOneCount( qvalueOneCount );
		
		
		List<PsmCountPerQValueQCPlotDataJSONChartBucket> chartBuckets = new ArrayList<>();
		psmCountPerQValueQCPlotDataJSONRoot.setChartBuckets( chartBuckets );
		
		
		
		double binHalf = binSizeAsDouble / 2 ;

		
		//  Take the data in the bins and  create "buckets" in the format required for the charting API
		
		int totalQValueCount = 0;
		
		
		for ( int binIndex = 0; binIndex < qvalueCounts.length; binIndex++ ) {
				
			PsmCountPerQValueQCPlotDataJSONChartBucket chartBucket = new PsmCountPerQValueQCPlotDataJSONChartBucket();
			
			chartBuckets.add( chartBucket );
				
			int qvalueCount = qvalueCounts[ binIndex ];
			
			totalQValueCount += qvalueCount;
			
			double binStartDouble = ( ( binIndex * binSizeAsDouble ) );

			if ( binIndex == 0 && binStartDouble < 0 ) {
				
				chartBucket.setBinStart( 0 );
			} else { 

				chartBucket.setBinStart( binStartDouble );
			}
			
			
			double binEnd = ( binIndex + 1 ) * binSizeAsDouble ;

			chartBucket.setBinEnd( binEnd );
			
			double binMiddleDouble = binStartDouble + binHalf;
			
			chartBucket.setBinCenter( binMiddleDouble );

			chartBucket.setTotalCount( totalQValueCount );
		}


		return psmCountPerQValueQCPlotDataJSONRoot;
	}


}
