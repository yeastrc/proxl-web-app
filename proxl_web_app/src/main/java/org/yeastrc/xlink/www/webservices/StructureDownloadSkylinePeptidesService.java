package org.yeastrc.xlink.www.webservices;


import org.yeastrc.proteomics.digestion.DigestionParameters;
import org.yeastrc.proteomics.digestion.DigestionProduct;
import org.yeastrc.proteomics.digestion.DigestionUtils;
import org.yeastrc.proteomics.digestion.protease.ProteaseFactory;
import org.yeastrc.proteomics.digestion.protease.proteases.IProtease;
import org.yeastrc.xlink.linker_data_processing_base.ILinker_Main;
import org.yeastrc.xlink.linker_data_processing_base.ILinkers_Main_ForSingleSearch;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProteinSequenceDAO;
import org.yeastrc.xlink.www.dao.ProteinSequenceVersionDAO;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.ProteinSequenceDTO;
import org.yeastrc.xlink.www.dto.ProteinSequenceVersionDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.linkable_positions.ILinker_Main_Objects_ForSearchId_Cached;
import org.yeastrc.xlink.www.objects.ProteinPositionPair;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/cleavageProductLookup")
public class StructureDownloadSkylinePeptidesService {

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getCleavageProductsForSkyline")
    public List<WebServiceResult> getCleavageProductsForSkyline (
            DownloadRequest downloadRequest,
            @Context HttpServletRequest request ) throws Exception {

        List<WebServiceResult> webServiceResults = new ArrayList<>();

        // linkable protein pairs, keyed on the cross-linker forming the link
        Map<String, Collection<ProteinPairContainer>> proteinPairsByLinker = getProteinPairsByLinker(
                downloadRequest.getProjectSearchIds(),
                downloadRequest.getProteinSequenceVersionIds());

        DigestionParameters digestionParameters = new DigestionParameters();
        digestionParameters.setNumMissedCleavages( 2 );
        digestionParameters.setMaxPeptideMass( 10000.0 );
        digestionParameters.setMinPeptideMass( 600.0 );

        IProtease protease = ProteaseFactory.getProteaseByName( "trypsin" );

        for( String formula : proteinPairsByLinker.keySet() ) {

            for( ProteinPairContainer proteinPairContainer : proteinPairsByLinker.get( formula ) ) {

                ProteinPositionPair proteinPair = proteinPairContainer.getProteinPositionPair();

                int protein1Id = proteinPair.getProtein1();
                int protein2Id = proteinPair.getProtein2();
                int position1 = proteinPair.getPosition1();
                int position2 = proteinPair.getPosition2();

                String proteinSequence1 = proteinPairContainer.getProtein1Sequence();
                String proteinSequence2 = proteinPairContainer.getProtein2Sequence();

                String proteinName1 = getProteinName( protein1Id, downloadRequest );
                String proteinName2 = getProteinName( protein2Id, downloadRequest );

                Collection<Integer> requiredPositions1 = new HashSet<>();
                requiredPositions1.add( position1 );

                Collection<Integer> excludedSites1 = new HashSet<>();
                excludedSites1.add( position1 );

                Collection<Integer> requiredPositions2 = new HashSet<>();
                requiredPositions2.add( position2 );

                Collection<Integer> excludedSites2 = new HashSet<>();
                excludedSites2.add( position2 );

                Collection<DigestionProduct> digestionProducts1 = DigestionUtils.digestProteinSequence(
                        proteinSequence1,
                        protease,
                        digestionParameters,
                        excludedSites1,
                        requiredPositions1 );

                Collection<DigestionProduct> digestionProducts2 = DigestionUtils.digestProteinSequence(
                        proteinSequence2,
                        protease,
                        digestionParameters,
                        excludedSites2,
                        requiredPositions2 );


                for( DigestionProduct dp1 : digestionProducts1 ) {

                    String peptideSequence1 = getPeptideSequence( proteinSequence1, dp1 );
                    int peptidePosition1 = getLinkedPositionInPeptide( position1, dp1 );

                    for( DigestionProduct dp2 : digestionProducts2 ) {

                        StringBuilder report = new StringBuilder();

                        String peptideSequence2 = getPeptideSequence( proteinSequence2, dp2 );
                        int peptidePosition2 = getLinkedPositionInPeptide( position2, dp2 );

                        report.append( peptideSequence1 + "\t" );
                        report.append( peptidePosition1 + "\t" );

                        report.append( peptideSequence2 + "\t" );
                        report.append( peptidePosition2 + "\t" );

                        report.append( proteinName1 + "(" + position1 + ")" );
                        report.append( "--" );
                        report.append( proteinName2 + "(" + position2 + ")" );
                        report.append( ":" );

                        report.append( peptideSequence1 );
                        report.append( "--" );
                        report.append( peptideSequence2 );
                        report.append( "\t" );

                        report.append( formula );

                        WebServiceResult result = new WebServiceResult();
                        result.setProteinPositionPair( proteinPair );
                        result.setReportLine( report.toString() );

                        webServiceResults.add( result );

                    }
                }
            }
        }

        return webServiceResults;
    }

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allLinkersAreSupported")
    public List<Boolean> allLinkersAreSupported (
            QueryAllLinkersSupportedRequest queryRequest,
            @Context HttpServletRequest request ) throws Exception {


        ArrayList<Boolean> retList = new ArrayList( 1 );
        retList.add( allLinkersHaveFormulaAndLinkablePositionsDefined( queryRequest.getProjectSearchIds() ) );

        return retList;
    }

    private static String getProteinName( int proteinSequenceVersionId, DownloadRequest downloadRequest ) {
        return downloadRequest.getProteinNameMap().get( String.valueOf( proteinSequenceVersionId ) );
    }

    private static int getLinkedPositionInPeptide( int proteinLinkedPosition, DigestionProduct dp ) {
        return proteinLinkedPosition - dp.getProteinPosition() + 1;
    }

    private static String getPeptideSequence( String proteinSequence, DigestionProduct dp ) {
        return proteinSequence.substring( dp.getProteinPosition() - 1, dp.getProteinPosition() - 1 + dp.getPeptideLength() );
    }

    private static boolean allLinkersHaveFormulaAndLinkablePositionsDefined( Collection<Integer> projectSearchIds ) throws Exception {

        Set<Integer> projectSearchIdsProcessedFromURL = new HashSet<>(); // add each projectSearchId as process in loop next

        List<SearchDTO> searchList = new ArrayList<>( projectSearchIds.size() );

        for ( Integer projectSearchId : projectSearchIds ) {

            if ( projectSearchIdsProcessedFromURL.add( projectSearchId ) ) {
                //  Haven't processed this projectSearchId yet in this loop so process it now

                SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
                if ( search == null ) {
                    String msg = ": No search found for projectSearchId: " + projectSearchId;
                    throw new WebApplicationException(
                            Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
                                    .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
                                    .build()
                    );
                }

                searchList.add( search );
            }
        }

        //////////

        //  Cache ILinkers_Main_ForSingleSearch per search id and set allLinkersSupportedForLinkablePositions

        Map<Integer, ILinkers_Main_ForSingleSearch> iLinkers_Main_ForSingleSearch_KeySearchId = new HashMap<>();
        {
            ILinker_Main_Objects_ForSearchId_Cached iLinker_Main_Objects_ForSearchId_Cached = ILinker_Main_Objects_ForSearchId_Cached.getInstance();
            for( SearchDTO searchDTO : searchList ) {
                int searchId = searchDTO.getSearchId();

                ILinker_Main_Objects_ForSearchId_Cached.ILinker_Main_Objects_ForSearchId_Cached_Response iLinker_Main_Objects_ForSearchId_Cached_Response =
                        iLinker_Main_Objects_ForSearchId_Cached.getSearchLinkers_ForSearchId_Response( searchId );
                ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch = iLinker_Main_Objects_ForSearchId_Cached_Response.getiLinkers_Main_ForSingleSearch();

                iLinkers_Main_ForSingleSearch_KeySearchId.put( searchId, iLinkers_Main_ForSingleSearch );

                if ( !iLinkers_Main_ForSingleSearch.isAllLinkersHave_LinkablePositions() ) {
                    return false;
                }
            }
        }


        for ( Map.Entry<Integer, ILinkers_Main_ForSingleSearch> entry : iLinkers_Main_ForSingleSearch_KeySearchId.entrySet() ) {
            ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch = entry.getValue();
            List<ILinker_Main> linker_MainList = iLinkers_Main_ForSingleSearch.getLinker_MainList();

            for ( ILinker_Main linker_Main : linker_MainList ) {

                Set<String> formulas = linker_Main.getCrosslinkFormulas();
                if( formulas == null || formulas.size() < 1 ) {
                    return false;
                }

            }
        }

        return true;
    }

    public static class WebServiceResult {

        private String reportLine;
        private ProteinPositionPair proteinPositionPair;

        public String getReportLine() {
            return reportLine;
        }

        public void setReportLine(String reportLine) {
            this.reportLine = reportLine;
        }

        public ProteinPositionPair getProteinPositionPair() {
            return proteinPositionPair;
        }

        public void setProteinPositionPair(ProteinPositionPair proteinPositionPair) {
            this.proteinPositionPair = proteinPositionPair;
        }
    }

    public static class DownloadRequest {

        private List<Integer> projectSearchIds;
        private List<Integer> proteinSequenceVersionIds;
        private Map<String, String> proteinNameMap;

        public Map<String, String> getProteinNameMap() {
            return proteinNameMap;
        }

        public void setProteinNameMap(Map<String, String> proteinNameMap) {
            this.proteinNameMap = proteinNameMap;
        }

        public List<Integer> getProjectSearchIds() {
            return projectSearchIds;
        }

        public void setProjectSearchIds(List<Integer> projectSearchIds) {
            this.projectSearchIds = projectSearchIds;
        }

        public List<Integer> getProteinSequenceVersionIds() {
            return proteinSequenceVersionIds;
        }

        public void setProteinSequenceVersionIds(List<Integer> proteinSequenceVersionIds) {
            this.proteinSequenceVersionIds = proteinSequenceVersionIds;
        }
    }

    public static class QueryAllLinkersSupportedRequest {

        private List<Integer> projectSearchIds;

        public List<Integer> getProjectSearchIds() {
            return projectSearchIds;
        }

        public void setProjectSearchIds(List<Integer> projectSearchIds) {
            this.projectSearchIds = projectSearchIds;
        }
    }

    private static class ProteinPairContainer {

        public ProteinPairContainer(ProteinPositionPair proteinPositionPair, String protein1Sequence, String protein2Sequence) {
            this.proteinPositionPair = proteinPositionPair;
            this.protein1Sequence = protein1Sequence;
            this.protein2Sequence = protein2Sequence;
        }

        private ProteinPositionPair proteinPositionPair;
        private String protein1Sequence;
        private String protein2Sequence;

        public ProteinPositionPair getProteinPositionPair() {
            return proteinPositionPair;
        }

        public String getProtein1Sequence() {
            return protein1Sequence;
        }

        public String getProtein2Sequence() {
            return protein2Sequence;
        }
    }



    private static Map<String, Collection<ProteinPairContainer>> getProteinPairsByLinker( Collection<Integer> projectSearchIds,
                                                                                               List<Integer> proteinSequenceVersionIds )
            throws Exception {

        Map<String, Collection<ProteinPairContainer>> proteinPairsByLinker = new HashMap<>();

        Set<Integer> projectSearchIdsProcessedFromURL = new HashSet<>(); // add each projectSearchId as process in loop next

        Set<Integer> searchIdsSet = new HashSet<>( projectSearchIds.size() );
        List<SearchDTO> searchList = new ArrayList<>( projectSearchIds.size() );

        for ( Integer projectSearchId : projectSearchIds ) {

            if ( projectSearchIdsProcessedFromURL.add( projectSearchId ) ) {
                //  Haven't processed this projectSearchId yet in this loop so process it now

                SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
                if ( search == null ) {
                    String msg = ": No search found for projectSearchId: " + projectSearchId;
                    throw new WebApplicationException(
                            Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
                                    .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
                                    .build()
                    );
                }

                Integer searchId = search.getSearchId();
                searchIdsSet.add( searchId );
                searchList.add( search );
            }
        }

        //////////

        //  Cache ILinkers_Main_ForSingleSearch per search id and set allLinkersSupportedForLinkablePositions

        boolean allLinkersSupportedForLinkablePositions = true;

        Map<Integer, ILinkers_Main_ForSingleSearch> iLinkers_Main_ForSingleSearch_KeySearchId = new HashMap<>();
        {
            ILinker_Main_Objects_ForSearchId_Cached iLinker_Main_Objects_ForSearchId_Cached = ILinker_Main_Objects_ForSearchId_Cached.getInstance();
            for( SearchDTO searchDTO : searchList ) {
                int searchId = searchDTO.getSearchId();

                ILinker_Main_Objects_ForSearchId_Cached.ILinker_Main_Objects_ForSearchId_Cached_Response iLinker_Main_Objects_ForSearchId_Cached_Response =
                        iLinker_Main_Objects_ForSearchId_Cached.getSearchLinkers_ForSearchId_Response( searchId );
                ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch = iLinker_Main_Objects_ForSearchId_Cached_Response.getiLinkers_Main_ForSingleSearch();

                iLinkers_Main_ForSingleSearch_KeySearchId.put( searchId, iLinkers_Main_ForSingleSearch );

                if ( ! iLinkers_Main_ForSingleSearch.isAllLinkersHave_LinkablePositions() ) {
                    allLinkersSupportedForLinkablePositions = false;
                }
            }
        }

        if ( ! allLinkersSupportedForLinkablePositions ) {
            //  Not all linkers support Linkable positions so no Linkable positions will be computed
            return proteinPairsByLinker;  //  EARLY RETURN
        }


        for( int proteinId1 : proteinSequenceVersionIds ) {
            for( int proteinId2 : proteinSequenceVersionIds ) {
                // get sequence for protein sequence version ids

                //  protein sequence version id 1
                ProteinSequenceVersionDTO proteinSequenceVersionDTO_1 = ProteinSequenceVersionDAO.getInstance().getFromId( proteinId1 );
                if ( proteinSequenceVersionDTO_1 == null ) {
                    String msg = "No proteinSequenceVersionDTO found for proteinId 1: " + proteinId1;
                    throw new ProxlWebappDataException(msg);
                }
                String proteinSequence_1 = null;
                ProteinSequenceDTO proteinSequenceDTO_1 =
                        ProteinSequenceDAO.getInstance().getProteinSequenceDTOFromDatabase( proteinSequenceVersionDTO_1.getproteinSequenceId() );
                if ( proteinSequenceDTO_1 != null ) {
                    proteinSequence_1 = proteinSequenceDTO_1.getSequence();
                }

                //  protein sequence version id 2
                ProteinSequenceVersionDTO proteinSequenceVersionDTO_2 = ProteinSequenceVersionDAO.getInstance().getFromId( proteinId2 );
                if ( proteinSequenceVersionDTO_2 == null ) {
                    String msg = "No proteinSequenceVersionDTO found for proteinId 2: " + proteinId2;
                    throw new ProxlWebappDataException(msg);
                }
                String proteinSequence_2 = null;
                ProteinSequenceDTO proteinSequenceDTO_2 =
                        ProteinSequenceDAO.getInstance().getProteinSequenceDTOFromDatabase( proteinSequenceVersionDTO_2.getproteinSequenceId() );
                if ( proteinSequenceDTO_2 != null ) {
                    proteinSequence_2 = proteinSequenceDTO_2.getSequence();
                }

                for ( Map.Entry<Integer, ILinkers_Main_ForSingleSearch> entry : iLinkers_Main_ForSingleSearch_KeySearchId.entrySet() ) {
                    ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch = entry.getValue();
                    List<ILinker_Main> linker_MainList = iLinkers_Main_ForSingleSearch.getLinker_MainList();
                    for ( ILinker_Main linker_Main : linker_MainList ) {

                        for( String formula : linker_Main.getCrosslinkFormulas() ) {

                            if (!proteinPairsByLinker.containsKey(formula)) {
                                proteinPairsByLinker.put(formula, new HashSet<>());
                            }

                            for (int position1 : linker_Main.getLinkablePositions(proteinSequence_1)) {
                                for (int position2 : linker_Main.getLinkablePositions(proteinSequence_2, proteinSequence_1, position1)) {

                                    ProteinPositionPair ppp = new ProteinPositionPair(proteinId1, position1, proteinId2, position2);

                                    // protein sequences may be flipped
                                    if( proteinId1 != ppp.getProtein1() ) {
                                        proteinPairsByLinker.get(formula).add(new ProteinPairContainer(ppp, proteinSequence_2, proteinSequence_1));
                                    } else {
                                        proteinPairsByLinker.get(formula).add(new ProteinPairContainer(ppp, proteinSequence_1, proteinSequence_2));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return proteinPairsByLinker;
    }

}
