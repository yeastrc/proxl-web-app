package org.yeastrc.xlink.www.qc_plots.psm_count_for_score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.QCPlotConstants;
import org.yeastrc.xlink.www.searcher.ScoreCountFromPsmTblSearcher;


/**
 * 
 *
 */
public class CreatePsmCountsVsScoreQCPlotData {

	private static final Logger log = Logger.getLogger(CreatePsmCountsVsScoreQCPlotData.class);
	
	


	private static final int BIN_COUNT = 100;  //  Number of points on the chart



	/**
	 * private constructor
	 */
	private CreatePsmCountsVsScoreQCPlotData(){}
	
	public static CreatePsmCountsVsScoreQCPlotData getInstance( ) throws Exception {
		
		CreatePsmCountsVsScoreQCPlotData instance = new CreatePsmCountsVsScoreQCPlotData();
		
		return instance;
	}

	/**
	 * @param selectedLinkTypes
	 * @param searchId
	 * @param annotationTypeId
	 * @param psmScoreCutoff
	 * @return
	 * @throws Exception
	 */
	public PsmCountsVsScoreQCPlotDataJSONRoot create( Set<String> selectedLinkTypes, int searchId, int annotationTypeId, Double psmScoreCutoff ) throws Exception {

		if ( selectedLinkTypes == null || selectedLinkTypes.isEmpty() ) {

			String msg = "selectedLinkTypes cannot be empty.";
			
			log.error( msg );

			throw new IllegalArgumentException( msg );
		}
		
		
		//  Keyed on Selected Link Type
		Map<String, List<Double>> scoreValuesForSelectedTypesMap = new HashMap<>();

		
		for ( String selectedLinkType : selectedLinkTypes ) {

			List<String> selectedDBLinkTypes = null;

			if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__ALL_PSM.equals( selectedLinkType ) ) {
				
				
			} else if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__CROSSLINK_PSM.equals( selectedLinkType ) ) {
				
				selectedDBLinkTypes = new ArrayList<>();
				selectedDBLinkTypes.add( XLinkUtils.CROSS_TYPE_STRING );
				
			} else if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__LOOPLINK_PSM.equals( selectedLinkType ) ) {
				
				selectedDBLinkTypes = new ArrayList<>();
				selectedDBLinkTypes.add( XLinkUtils.LOOP_TYPE_STRING );
				
			} else if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__UNLINKED_PSM.equals( selectedLinkType ) ) {
				
				selectedDBLinkTypes = new ArrayList<>();
				selectedDBLinkTypes.add( XLinkUtils.DIMER_TYPE_STRING );
				selectedDBLinkTypes.add( XLinkUtils.UNLINKED_TYPE_STRING );
				
			}
		
			List<Double> scoreValuesForPSMsthatMeetCriteriaList = 
				ScoreCountFromPsmTblSearcher.getInstance().getScoreValues( selectedDBLinkTypes, searchId, annotationTypeId, psmScoreCutoff );
			
			scoreValuesForSelectedTypesMap.put( selectedLinkType, scoreValuesForPSMsthatMeetCriteriaList );
			
		}
		
		double maxScore = 0;
		double minScore = 0;
		
		boolean firstEntry = true;
		
		for ( Map.Entry<String, List<Double>> entry : scoreValuesForSelectedTypesMap.entrySet() ) {
			
			List<Double> scoreValuesForPSMsthatMeetCriteriaList = entry.getValue();
			
			for ( Double scoreValue : scoreValuesForPSMsthatMeetCriteriaList ) {
				
				if ( firstEntry ) {

					firstEntry = false;

					maxScore = 0;
					minScore = 0;

				} else {
					
					if ( maxScore < scoreValue ) {
						
						maxScore = scoreValue;
					}
					
					if ( minScore > scoreValue ) {
						
						minScore = scoreValue;
					}
				}
			}
		}
		
		
	
		double scoreMaxForPuttingInBins = maxScore;
		
		if ( psmScoreCutoff != null ) {
			
			scoreMaxForPuttingInBins = psmScoreCutoff;
		}
		

		double scoreMinForPuttingInBins = minScore;
		

		//  Process data into bins

		double binSizeAsDouble = ( scoreMaxForPuttingInBins - scoreMinForPuttingInBins ) / BIN_COUNT;



		PsmCountsVsScoreQCPlotDataJSONPerType crosslinkData = null;
		PsmCountsVsScoreQCPlotDataJSONPerType looplinkData = null;
		PsmCountsVsScoreQCPlotDataJSONPerType unlinkedData = null;
		
		PsmCountsVsScoreQCPlotDataJSONPerType alllinkData = null;

		
		int dataArraySize = 0;

		
		for ( String selectedLinkType : selectedLinkTypes ) {

			List<String> selectedDBLinkTypes = null;

			if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__ALL_PSM.equals( selectedLinkType ) ) {
				
				
			} else if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__CROSSLINK_PSM.equals( selectedLinkType ) ) {
				
				selectedDBLinkTypes = new ArrayList<>();
				selectedDBLinkTypes.add( XLinkUtils.CROSS_TYPE_STRING );
				
			} else if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__LOOPLINK_PSM.equals( selectedLinkType ) ) {
				
				selectedDBLinkTypes = new ArrayList<>();
				selectedDBLinkTypes.add( XLinkUtils.LOOP_TYPE_STRING );
				
			} else if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__UNLINKED_PSM.equals( selectedLinkType ) ) {
				
				selectedDBLinkTypes = new ArrayList<>();
				selectedDBLinkTypes.add( XLinkUtils.DIMER_TYPE_STRING );
				selectedDBLinkTypes.add( XLinkUtils.UNLINKED_TYPE_STRING );
				
			}
			
			List<Double> scoreValuesForPSMsthatMeetCriteriaList = scoreValuesForSelectedTypesMap.get( selectedLinkType );
			
			
			int totalCountForType = scoreValuesForPSMsthatMeetCriteriaList.size();
			
			if ( psmScoreCutoff != null ) {
				
				totalCountForType = ScoreCountFromPsmTblSearcher.getInstance().getScoreCount( selectedDBLinkTypes, searchId, annotationTypeId );
			}

			PsmCountsVsScoreQCPlotDataJSONPerType linkData = null;

			if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__ALL_PSM.equals( selectedLinkType ) ) {

				alllinkData = new PsmCountsVsScoreQCPlotDataJSONPerType();
				linkData = alllinkData;

			} else if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__CROSSLINK_PSM.equals( selectedLinkType ) ) {

				crosslinkData = new PsmCountsVsScoreQCPlotDataJSONPerType();
				linkData = crosslinkData;

			} else if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__LOOPLINK_PSM.equals( selectedLinkType ) ) {

				looplinkData = new PsmCountsVsScoreQCPlotDataJSONPerType();
				linkData = looplinkData;

			} else if ( QCPlotConstants.PSM_COUNT_VS_SCORE_PLOT__UNLINKED_PSM.equals( selectedLinkType ) ) {

				unlinkedData = new PsmCountsVsScoreQCPlotDataJSONPerType();
				linkData = unlinkedData;
			}
			
			
			int scoreValueMinValueCount = 0;

			int[] scoreValueCounts = new int[ BIN_COUNT ];


			for ( double scoreValue : scoreValuesForPSMsthatMeetCriteriaList ) {

				double scoreValueFraction = ( scoreValue - scoreMinForPuttingInBins ) / ( scoreMaxForPuttingInBins - scoreMinForPuttingInBins );

				
				int bin = (int) ( (  scoreValueFraction ) * BIN_COUNT );

				if ( bin < 0 ) {

					bin = 0;

				} else if ( bin >= BIN_COUNT ) {

					bin = BIN_COUNT - 1;
				} 

				scoreValueCounts[ bin ]++;

				if ( scoreValue == scoreMinForPuttingInBins ) {

					scoreValueMinValueCount++;
				}

			}


			List<PsmCountsVsScoreQCPlotDataJSONChartBucket> chartBuckets = new ArrayList<>();

			
			
			PsmCountsVsScoreQCPlotDataJSONChartBucket chartBucketMinValue = new PsmCountsVsScoreQCPlotDataJSONChartBucket();
			chartBucketMinValue.setBinEnd( scoreMinForPuttingInBins );
			chartBucketMinValue.setTotalCount( scoreValueMinValueCount );
			
			
			chartBuckets.add( chartBucketMinValue );



			//  Take the data in the bins and  create "buckets" in the format required for the charting API

			int totalScoreValueCount = 0;


			for ( int binIndex = 0; binIndex < scoreValueCounts.length; binIndex++ ) {

				PsmCountsVsScoreQCPlotDataJSONChartBucket chartBucket = new PsmCountsVsScoreQCPlotDataJSONChartBucket();

				chartBuckets.add( chartBucket );

				int scoreValueCount = scoreValueCounts[ binIndex ];

				totalScoreValueCount += scoreValueCount;

				double binStartDouble = ( ( binIndex * binSizeAsDouble ) + scoreMinForPuttingInBins );

				if ( binIndex == 0 && binStartDouble < 0 ) {

					chartBucket.setBinStart( 0 );
				} else { 

					chartBucket.setBinStart( binStartDouble );
				}


				double binEnd = ( binIndex + 1 ) * binSizeAsDouble + scoreMinForPuttingInBins ;

				chartBucket.setBinEnd( binEnd );

				chartBucket.setTotalCount( totalScoreValueCount );
			}
			
			linkData.setChartBuckets( chartBuckets );
			
			dataArraySize = chartBuckets.size();
			

			linkData.setTotalCountForType( totalCountForType );
			
		}
		
		
		//  Populate objects to generate JSON
		
		PsmCountsVsScoreQCPlotDataJSONRoot psmCountsVsScoreQCPlotDataJSONRoot = new PsmCountsVsScoreQCPlotDataJSONRoot();
		
		psmCountsVsScoreQCPlotDataJSONRoot.setDataArraySize( dataArraySize );
		
		psmCountsVsScoreQCPlotDataJSONRoot.setAlllinkData( alllinkData );
		psmCountsVsScoreQCPlotDataJSONRoot.setCrosslinkData( crosslinkData );
		psmCountsVsScoreQCPlotDataJSONRoot.setLooplinkData( looplinkData );
		psmCountsVsScoreQCPlotDataJSONRoot.setUnlinkedData( unlinkedData );

		return psmCountsVsScoreQCPlotDataJSONRoot;
	}


}
