package org.yeastrc.xlink.www.qc_plots.psm_count_per_q_value;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.QCPlotConstants;
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
	 * @param selectedLinkTypes
	 * @param searchId
	 * @param psmQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public PsmCountPerQValueQCPlotDataJSONRoot create( Set<String> selectedLinkTypes,	int searchId, Double psmQValueCutoff ) throws Exception {

		double psmQValueMaxForPuttingInBins = MAX_Q_VALUE;
		
		if ( psmQValueCutoff != null ) {
			
			psmQValueMaxForPuttingInBins = psmQValueCutoff;
		}
		


		//  Process data into bins

		double binSizeAsDouble = ( psmQValueMaxForPuttingInBins ) / BIN_COUNT;


		if ( selectedLinkTypes == null || selectedLinkTypes.isEmpty() ) {

			String msg = "selectedLinkTypes cannot be empty.";
			
			log.error( msg );

			throw new IllegalArgumentException( msg );
		}
		
		PsmCountPerQValueQCPlotDataJSONPerType crosslinkData = null;
		PsmCountPerQValueQCPlotDataJSONPerType looplinkData = null;
		PsmCountPerQValueQCPlotDataJSONPerType unlinkedData = null;
		
		PsmCountPerQValueQCPlotDataJSONPerType alllinkData = null;

		
		int dataArraySize = 0;
		

		
		for ( String selectedLinkType : selectedLinkTypes ) {

			List<String> selectedDBLinkTypes = null;

			if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__ALL_PSM.equals( selectedLinkType ) ) {
				
				
			} else if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__CROSSLINK_PSM.equals( selectedLinkType ) ) {
				
				selectedDBLinkTypes = new ArrayList<>();
				selectedDBLinkTypes.add( XLinkUtils.CROSS_TYPE_STRING );
				
			} else if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__LOOPLINK_PSM.equals( selectedLinkType ) ) {
				
				selectedDBLinkTypes = new ArrayList<>();
				selectedDBLinkTypes.add( XLinkUtils.LOOP_TYPE_STRING );
				
			} else if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__UNLINKED_PSM.equals( selectedLinkType ) ) {
				
				selectedDBLinkTypes = new ArrayList<>();
				selectedDBLinkTypes.add( XLinkUtils.DIMER_TYPE_STRING );
				selectedDBLinkTypes.add( XLinkUtils.UNLINKED_TYPE_STRING );
				
			}
		
			List<Double> qValuesForPSMsthatMeetCriteriaList = 
				QValuesFromPsmTblSearcher.getInstance().getQValues( selectedDBLinkTypes, searchId, psmQValueCutoff );
			
			int totalCountForType = qValuesForPSMsthatMeetCriteriaList.size();
			
			if ( psmQValueCutoff != null ) {
				
				totalCountForType = QValuesFromPsmTblSearcher.getInstance().getQValuesCount( selectedDBLinkTypes, searchId );
			}

			PsmCountPerQValueQCPlotDataJSONPerType linkData = null;

			if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__ALL_PSM.equals( selectedLinkType ) ) {

				alllinkData = new PsmCountPerQValueQCPlotDataJSONPerType();
				linkData = alllinkData;

			} else if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__CROSSLINK_PSM.equals( selectedLinkType ) ) {

				crosslinkData = new PsmCountPerQValueQCPlotDataJSONPerType();
				linkData = crosslinkData;

			} else if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__LOOPLINK_PSM.equals( selectedLinkType ) ) {

				looplinkData = new PsmCountPerQValueQCPlotDataJSONPerType();
				linkData = looplinkData;

			} else if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__UNLINKED_PSM.equals( selectedLinkType ) ) {

				unlinkedData = new PsmCountPerQValueQCPlotDataJSONPerType();
				linkData = unlinkedData;
			}
			
			
			int qvalueZeroCount = 0;

			int[] qvalueCounts = new int[ BIN_COUNT ];


			for ( double qValue : qValuesForPSMsthatMeetCriteriaList ) {

				double qValueFraction = qValue / psmQValueMaxForPuttingInBins;

				
				int bin = (int) ( (  qValueFraction ) * BIN_COUNT );

				if ( bin < 0 ) {

					bin = 0;

				} else if ( bin >= BIN_COUNT ) {

					bin = BIN_COUNT - 1;
				} 

				qvalueCounts[ bin ]++;

				if ( qValue == 0 ) {

					qvalueZeroCount++;
				}

			}


			List<PsmCountPerQValueQCPlotDataJSONChartBucket> chartBuckets = new ArrayList<>();

			
			PsmCountPerQValueQCPlotDataJSONChartBucket chartBucketZero = new PsmCountPerQValueQCPlotDataJSONChartBucket();
			chartBucketZero.setBinEnd( 0 );
			chartBucketZero.setTotalCount( qvalueZeroCount );
			
			
			chartBuckets.add( chartBucketZero );



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

				chartBucket.setTotalCount( totalQValueCount );
			}
			
			linkData.setChartBuckets( chartBuckets );
			
			dataArraySize = chartBuckets.size();
			

			linkData.setTotalCountForType( totalCountForType );
			
		}
		
		
		//  Populate objects to generate JSON
		
		PsmCountPerQValueQCPlotDataJSONRoot psmCountPerQValueQCPlotDataJSONRoot = new PsmCountPerQValueQCPlotDataJSONRoot();
		
		psmCountPerQValueQCPlotDataJSONRoot.setDataArraySize( dataArraySize );
		
		psmCountPerQValueQCPlotDataJSONRoot.setAlllinkData( alllinkData );
		psmCountPerQValueQCPlotDataJSONRoot.setCrosslinkData( crosslinkData );
		psmCountPerQValueQCPlotDataJSONRoot.setLooplinkData( looplinkData );
		psmCountPerQValueQCPlotDataJSONRoot.setUnlinkedData( unlinkedData );

		return psmCountPerQValueQCPlotDataJSONRoot;
	}


}
