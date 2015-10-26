package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProteinSequenceCoverage;
import org.yeastrc.xlink.www.searcher.MergedSearchPeptideSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.objects.SequenceCoverageData;
import org.yeastrc.xlink.www.objects.SequenceCoverageRange;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

import com.google.common.collect.Range;

@Path("/sequenceCoverage")
public class ViewerSequenceCoverageService {

	private static final Logger log = Logger.getLogger(ViewerSequenceCoverageService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getDataForProtein") 
	public SequenceCoverageData getSequenceCoverageDataForProtein( @QueryParam( "searchIds" ) List<Integer> searchIds,
										  @QueryParam( "psmQValueCutoff" ) Double psmQValueCutoff,
										  @QueryParam( "peptideQValueCutoff" ) Double peptideQValueCutoff,
										  @QueryParam( "filterNonUniquePeptides" ) String filterNonUniquePeptidesString,
										  @QueryParam( "excludeTaxonomy" ) List<Integer> excludeTaxonomy,
										  @QueryParam( "proteinId" ) int proteinId,
										  @Context HttpServletRequest request )
	throws Exception {

		if ( searchIds == null || searchIds.isEmpty() ) {

			String msg = "Provided searchIds is null or empty, searchIds = " + searchIds;

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		
		try {
			

			// Get the session first.  
			HttpSession session = request.getSession();


			if ( searchIds.isEmpty() ) {
				
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			
			for ( int searchId : searchIds ) {

				searchIdsCollection.add( searchId );
			}
			
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIds ) {

					msg += searchId + ", ";
				}				
				log.error( msg );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			if ( projectIdsFromSearchIds.size() > 1 ) {
				
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			int projectId = projectIdsFromSearchIds.get( 0 );
			

			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			//  Test access to the project id

			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);

			}



			Map<Integer, Double> coverages = new HashMap<Integer, Double>();
			Map<Integer, List<SequenceCoverageRange>> ranges = new HashMap<Integer, List<SequenceCoverageRange>>();

			SequenceCoverageData scd = new SequenceCoverageData();

			// ensure our cutoffs have some default values
			if( psmQValueCutoff == null )
				psmQValueCutoff = 0.01;
			
			if( peptideQValueCutoff == null )
				peptideQValueCutoff = 0.01;

			if( excludeTaxonomy == null ) 
				excludeTaxonomy = new ArrayList<Integer>();

			boolean filterNonUniquePeptides = false;
			if( filterNonUniquePeptidesString != null && filterNonUniquePeptidesString.equals( "on" ) )
				filterNonUniquePeptides = true;

			

			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			for( int searchId : searchIds ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {
					
					String msg = "Search not found in DB for searchId: " + searchId;
					
					log.error( msg );

					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				
				searches.add( search );
			}

			

			
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PSM_Q_VALUE_FIELD_VALUE, Double.toString( psmQValueCutoff ) );
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PEPTIDE_Q_VALUE_FIELD_VALUE, Double.toString( peptideQValueCutoff ) );

			
			

			// add these to the SCD so that we have context for the results
			scd.setExcludeTaxonomy( excludeTaxonomy );
			scd.setFilterNonUniquePeptides( filterNonUniquePeptides );
			scd.setPeptideQValueCutoff( peptideQValueCutoff );
			scd.setPsmQValueCutoff( psmQValueCutoff );
			scd.setSearches( searches );

			// first get all distinct proteins that have at least one linked peptide, given the search parameters
			NRProteinDTO protein = NRProteinDAO.getInstance().getNrProtein( proteinId );

			ProteinSequenceCoverage cov = new ProteinSequenceCoverage( protein );

			Collection<PeptideDTO> peptides = MergedSearchPeptideSearcher.getInstance().getPeptides( protein, searches, psmQValueCutoff, peptideQValueCutoff);
			for( PeptideDTO peptide : peptides ) {
				cov.addPeptide( peptide.getSequence() );
			}

			coverages.put( protein.getNrseqId(), cov.getSequenceCoverage() );

			Set<Range<Integer>> coverageRanges = cov.getRanges();
			
			List<SequenceCoverageRange> sequenceCoverageRangesTempList = new ArrayList<SequenceCoverageRange>( coverageRanges.size() );

			for( Range<Integer> r : cov.getRanges() ) {
				SequenceCoverageRange scr = new SequenceCoverageRange();
				scr.setStart( r.lowerEndpoint() );
				scr.setEnd( r.upperEndpoint() );

				sequenceCoverageRangesTempList.add( scr );
			}
			
			Collections.sort( sequenceCoverageRangesTempList, new Comparator<SequenceCoverageRange>() {

				@Override
				public int compare(SequenceCoverageRange o1, SequenceCoverageRange o2) {

					return o1.getStart() - o2.getStart();
				}
			} );
			
			List<SequenceCoverageRange> sequenceCoverageRangesOutputList = new ArrayList<SequenceCoverageRange>( coverageRanges.size() );

			SequenceCoverageRange prevSequenceCoverageRange = null;
			
			for ( SequenceCoverageRange sequenceCoverageRange : sequenceCoverageRangesTempList ) {
			
				if ( prevSequenceCoverageRange == null ) {
					
					prevSequenceCoverageRange = sequenceCoverageRange;
					
				} else {
					
					if ( ( prevSequenceCoverageRange.getEnd() + 1 ) == sequenceCoverageRange.getStart() ) {
						
						//  adjoining ranges so combine them
						
						prevSequenceCoverageRange.setEnd( sequenceCoverageRange.getEnd() );
					
					} else {
						
						//  NON adjoining ranges so add prev to list and move current to prev
						
						sequenceCoverageRangesOutputList.add( prevSequenceCoverageRange );
						
						prevSequenceCoverageRange = sequenceCoverageRange;
					}
				}
			}
			
			if ( prevSequenceCoverageRange != null ) {
				//  Add last entry
				sequenceCoverageRangesOutputList.add( prevSequenceCoverageRange );
			}
			
			ranges.put( protein.getNrseqId(), sequenceCoverageRangesOutputList );

			scd.setCoverages( coverages );
			scd.setRanges( ranges );


			return scd;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}	
	}
	
	
}
