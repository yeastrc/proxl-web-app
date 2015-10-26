package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collection;
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
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkablePositionsForLinkers;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.searcher.LinkersForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.MergedSearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.utils.TaxonomyUtils;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.objects.ImageViewerData;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;



@Path("/imageViewer")
public class ViewerProteinDataService {

	private static final Logger log = Logger.getLogger(ViewerProteinDataService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getProteinData") 
	public ImageViewerData getViewerData( @QueryParam( "searchIds" ) List<Integer> searchIds,
										  @QueryParam( "psmQValueCutoff" ) Double psmQValueCutoff,
										  @QueryParam( "peptideQValueCutoff" ) Double peptideQValueCutoff,
										  @QueryParam( "filterNonUniquePeptides" ) String filterNonUniquePeptidesString,
										  @QueryParam( "filterOnlyOnePSM" ) String filterOnlyOnePSMString,
										  @QueryParam( "filterOnlyOnePeptide" ) String filterOnlyOnePeptideString,
										  @QueryParam( "excludeTaxonomy" ) List<Integer> excludeTaxonomy,
										  @QueryParam( "excludeType" ) List<Integer> excludeType,
										  @Context HttpServletRequest request )
	throws Exception {

//		if (true)
//		throw new Exception("Forced Error");
		
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



			ImageViewerData ivd = new ImageViewerData();

			if( psmQValueCutoff == null )
				psmQValueCutoff = 0.01;
			
			if( peptideQValueCutoff == null )
				peptideQValueCutoff = 0.01;

			if( excludeTaxonomy == null ) 
				excludeTaxonomy = new ArrayList<Integer>();

			boolean filterNonUniquePeptides = false;
			if( filterNonUniquePeptidesString != null && filterNonUniquePeptidesString.equals( "on" ) )
				filterNonUniquePeptides = true;

			boolean filterOnlyOnePSM = false;
			if( "on".equals( filterOnlyOnePSMString ) )
				filterOnlyOnePSM = true;

			boolean filterOnlyOnePeptide = false;
			if( "on".equals( filterOnlyOnePeptideString ) )
				filterOnlyOnePeptide = true;

			

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


			
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PSM_Q_VALUE_FIELD_VALUE, Double.toString( psmQValueCutoff ) );
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PEPTIDE_Q_VALUE_FIELD_VALUE, Double.toString( peptideQValueCutoff ) );

			
			
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



			// first build a collection of types to include
			Collection<Integer> includedTypes = new HashSet<Integer>();
			includedTypes.add( XLinkUtils.TYPE_CROSSLINK );
			includedTypes.add( XLinkUtils.TYPE_DIMER );
			includedTypes.add( XLinkUtils.TYPE_LOOPLINK );
			includedTypes.add( XLinkUtils.TYPE_MONOLINK );
			includedTypes.add( XLinkUtils.TYPE_UNLINKED );

			if( excludeType == null )
				excludeType = new ArrayList<Integer>(0);

			for( int type : excludeType ) {
				if( type != XLinkUtils.TYPE_UNLINKED )
					includedTypes.remove( type );
			}


			// build a collection of the potentially included proteins
			Map<Integer, MergedSearchProtein> includedProteins = new HashMap<Integer, MergedSearchProtein>();

			for( int type : includedTypes ) {
				Collection<MergedSearchProtein> mp = MergedSearchProteinSearcher.getInstance().getProteinsWithLinkType( searches, type, psmQValueCutoff, peptideQValueCutoff);
				for( MergedSearchProtein mrp : mp ) {
					includedProteins.put( mrp.getNrProtein().getNrseqId(), mrp );
				}
			}

			if ( log.isDebugEnabled() ) {

				log.debug( "Number of initially-included proteins: " + includedProteins.keySet().size() );
			}

			// remove all proteins that have a peptide of at least one of the included excluded types
			for( int type: excludeType ) {
				if( type != XLinkUtils.TYPE_UNLINKED ) {
					Collection<MergedSearchProtein> mp = MergedSearchProteinSearcher.getInstance().getProteinsWithLinkType( searches, type, psmQValueCutoff, peptideQValueCutoff);

					if ( log.isDebugEnabled() ) {

						log.debug( "Removing proteins of type: " + type );
					}

					for( MergedSearchProtein mrp : mp ) {
						includedProteins.remove( mrp.getNrProtein().getNrseqId() );
					}
				} else {

					// if one of the excluded types is "no links", then remove all proteins that _only_ have unlinked and/or dimer peptides

					// create collection of proteins with unlinked or dimer peptides
					Collection<MergedSearchProtein> mp = MergedSearchProteinSearcher.getInstance().getProteinsWithLinkType( searches, XLinkUtils.TYPE_UNLINKED, psmQValueCutoff, peptideQValueCutoff);
					Collection<Integer> onlyUnlinkedProteins = new HashSet<Integer>();
					for( MergedSearchProtein mrp : mp ) {
						onlyUnlinkedProteins.add( mrp.getNrProtein().getNrseqId() );
					}

					mp = MergedSearchProteinSearcher.getInstance().getProteinsWithLinkType( searches, XLinkUtils.TYPE_DIMER, psmQValueCutoff, peptideQValueCutoff);
					for( MergedSearchProtein mrp : mp ) {
						onlyUnlinkedProteins.add( mrp.getNrProtein().getNrseqId() );
					}

					// remove from this set any proteins that have peptides of any type other than unlinked or dimer
					// since I've already removed proteins with the excludedTypes, I only need to remove proteins with peptides
					// belonging to any of the included types (except unlinked)
					for( int type2 : includedTypes ) {
						if( type2 == XLinkUtils.TYPE_UNLINKED ) continue;
						if( type2 == XLinkUtils.TYPE_DIMER ) continue;

						Collection<MergedSearchProtein> mp2 = MergedSearchProteinSearcher.getInstance().getProteinsWithLinkType( searches, type2, psmQValueCutoff, peptideQValueCutoff);
						for( MergedSearchProtein mrp : mp2 ) {
							onlyUnlinkedProteins.remove( mrp.getNrProtein().getNrseqId() );
						}
					}

					// onlyUnlinkedProteins should now contain only proteins that only have unlinked peptides
					// remove these from the included proteins
					for( int unlinkedProtein : onlyUnlinkedProteins ) {
						includedProteins.remove( unlinkedProtein );
					}				
				}
			}

			if ( log.isDebugEnabled() ) {

				log.debug( "Number of finally-included proteins: " + includedProteins.keySet().size() );
			}

			// create the collection of proteins we're going to include
			Collection<MergedSearchProtein> proteins = new ArrayList<MergedSearchProtein>();
			for( int pid : includedProteins.keySet() )
				proteins.add( includedProteins.get( pid ) );


			// build list of taxonomies to show in exclusion list
			Map<Integer, String> taxonomies = new HashMap<Integer,String>();
			for( MergedSearchProtein mp : proteins ) {
				if( taxonomies.containsKey( mp.getNrProtein().getTaxonomyId() ) ) continue;
				taxonomies.put( mp.getNrProtein().getTaxonomyId(), TaxonomyUtils.getTaxonomyName( mp.getNrProtein().getTaxonomyId() ) );
			}
			ivd.setTaxonomies( taxonomies );


			// remove all proteins that are in the excluded taxonomy
			Collection<MergedSearchProtein> proteins2 = new HashSet<MergedSearchProtein>();
			proteins2.addAll( proteins );

			for( int taxy : excludeTaxonomy ) {
				for( MergedSearchProtein mp : proteins2 ) {
					if( mp.getNrProtein().getTaxonomyId() == taxy )
						proteins.remove( mp );
				}
			}


			//  Map of linkablePositions where the key is the protein id and the value is the collection of linkable positions
			Map<Integer, Collection<Integer>> proteinIdslinkablePositionsMap = new HashMap<Integer, Collection<Integer>>();
			

			List<LinkerDTO>  linkerList = LinkersForSearchIdsSearcher.getInstance().getLinkersForSearchIds( searchIds );
			
			
			if ( linkerList == null || linkerList.isEmpty() ) {
				
				String errorMsgSearchIdList = null;
				
				for ( Integer searchId : searchIds ) {
					
					if ( errorMsgSearchIdList == null ) {
						
						errorMsgSearchIdList = searchId.toString();
					} else {
						
						errorMsgSearchIdList += "," + searchId.toString();
					}
				}
				String msg = "No linkers found for Search Ids: " + errorMsgSearchIdList;
				log.error( msg );
//				throw new Exception(msg);
			}
			
			
			Set<String> linkerAbbrSet = new HashSet<>();
			
			for ( LinkerDTO linker : linkerList ) {
				
				String linkerAbbr = linker.getAbbr();
				
				linkerAbbrSet.add( linkerAbbr );
			}
						
			
			// add locations of all linkablePositions in the found proteins

			for( MergedSearchProtein mp : proteins ) {

				String proteinSequence = mp.getNrProtein().getSequence();

				Collection<Integer> linkablePositionsForProtein = GetLinkablePositionsForLinkers.getLinkablePositionsForProteinSequenceAndLinkerAbbrSet( proteinSequence, linkerAbbrSet );
				
				proteinIdslinkablePositionsMap.put( mp.getNrProtein().getNrseqId(), linkablePositionsForProtein );
			}
			ivd.setLinkablePositions( proteinIdslinkablePositionsMap ); 

			// guild maps of protein lengths and protein names for the found proteins
			Map<Integer, Integer> proteinLengths = new HashMap<Integer, Integer>();
			Map<Integer, String> proteinNames = new HashMap<Integer, String>();

			for( MergedSearchProtein mp : proteins ) {
				proteinLengths.put( mp.getNrProtein().getNrseqId(), mp.getNrProtein().getSequence().length() );
				proteinNames.put( mp.getNrProtein().getNrseqId(), mp.getName() );
			}

			// sort the proteinNames map by value
			Ordering<Integer> valueComparator = Ordering.from(new SortIgnoreCase() ).onResultOf(Functions.forMap(proteinNames));
			Map<Integer,String> sortedProteinNames = ImmutableSortedMap.copyOf(proteinNames, valueComparator);


			ivd.setProteinLengths( proteinLengths );
			ivd.setProteinNames( proteinNames );
			ivd.setProteins( sortedProteinNames.keySet() );

			ivd.setPsmQValueCutoff( psmQValueCutoff );
			ivd.setPeptideQValueCutoff( peptideQValueCutoff );
			ivd.setExcludeTaxonomy( excludeTaxonomy );
			ivd.setExcludeType( excludeType );
			ivd.setSearches( searches );
			ivd.setFilterNonUniquePeptides( filterNonUniquePeptides );
			ivd.setFilterOnlyOnePSM( filterOnlyOnePSM );
			ivd.setFilterOnlyOnePeptide( filterOnlyOnePeptide );



			return ivd;
			
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}
	
    public class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }
}
