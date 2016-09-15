package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetCutoffPageDisplayRoot;
import org.yeastrc.xlink.www.cutoff_processing_web.GetDefaultPsmPeptideCutoffs;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplayAnnotationLevel;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplayRoot;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplaySearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesAnnotationLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.ProteinCoverageData;
import org.yeastrc.xlink.www.protein_coverage.ProteinCoverageCompute;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;




/**
 * 
 *
 */
public class DownloadProteinCoverageReportAction extends Action {

	private static final Logger log = Logger.getLogger(DownloadProteinCoverageReportAction.class);
			
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {


		try {

			// our form
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm)actionForm;



			// Get the session first.  
//			HttpSession session = request.getSession();

			//   Get the project id for this search

			int[] searchIdsFromForm = form.getSearchIds();
			
			
			if ( searchIdsFromForm.length == 0 ) {
				
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}

			
			//   Get the project id for these searches
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			
			for ( int searchId : searchIdsFromForm ) {

				searchIdsCollection.add( searchId );
			}
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIdsFromForm ) {

					msg += searchId + ", ";
				}
				
				log.error( msg );

				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			if ( projectIdsFromSearchIds.size() > 1 ) {
				
				//  Invalid request, searches across projects

				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			

			int projectId = projectIdsFromSearchIds.get( 0 );
			
			request.setAttribute( "projectId", projectId ); 
			

			///////////////////////
			
			

			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {

				//  No Access Allowed for this project id

				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			


			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );



			


			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			List<Integer> searchIds = new ArrayList<Integer>();
			for( int searchId : form.getSearchIds() ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {
					
					String msg = "Percolator search id '" + searchId + "' not found in the database. User taken to home page.";
					
					log.warn( msg );
					
					//  Search not found, the data on the page they are requesting does not exist.
					//  The data on the user's previous page no longer reflects what is in the database.
					//  Take the user to the home page
					
					return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
				}
				
				searches.add( search );
				searchIds.add( searchId );
			}




			// generate file name
			String filename = "protein-coverage-report-";
			filename += StringUtils.join( form.getSearchIds(), '-' );

			DateTime dt = new DateTime();
			DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
			filename += "-" + fmt.print( dt );

			filename += ".txt";

			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);


			OutputStreamWriter writer = null;
			
			try {


				ServletOutputStream out = response.getOutputStream();

				BufferedOutputStream bos = new BufferedOutputStream(out);

				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );




				// sort our searches by ID
				Collections.sort( searches, new Comparator<SearchDTO>() {
					public int compare( SearchDTO r1, SearchDTO r2 ) {
						return r1.getId() - r2.getId();
					}
				});
				
				

				//  Jackson JSON Mapper object for JSON deserialization and serialization
				
				ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object


				
				//   Get Query JSON from the form and if not empty, deserialize it
				

				String queryJSONFromForm = form.getQueryJSON();
				
				ProteinQueryJSONRoot proteinQueryJSONRoot = null;
				
				if ( StringUtils.isNotEmpty( queryJSONFromForm ) ) {

					try {
						proteinQueryJSONRoot = jacksonJSON_Mapper.readValue( queryJSONFromForm, ProteinQueryJSONRoot.class );
						
					} catch ( JsonParseException e ) {
						
						String msg = "Failed to parse 'queryJSONFromForm', JsonParseException.  queryJSONFromForm: " + queryJSONFromForm;
						log.error( msg, e );
						throw e;
					
					} catch ( JsonMappingException e ) {
						
						String msg = "Failed to parse 'queryJSONFromForm', JsonMappingException.  queryJSONFromForm: " + queryJSONFromForm;
						log.error( msg, e );
						throw e;
						
					} catch ( IOException e ) {
						
						String msg = "Failed to parse 'queryJSONFromForm', IOException.  queryJSONFromForm: " + queryJSONFromForm;
						log.error( msg, e );
						throw e;
					}
					
				} else {
					
					
					//  Query JSON in the form is empty so create an empty object that will be populated.
					
					
					proteinQueryJSONRoot = new ProteinQueryJSONRoot();
					
					CutoffValuesRootLevel cutoffValuesRootLevel =
							GetDefaultPsmPeptideCutoffs.getInstance()
							.getDefaultPsmPeptideCutoffs( searchIdsCollection );
					
					proteinQueryJSONRoot.setCutoffs( cutoffValuesRootLevel );
					
					
				}


				////////////
				
				//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup

				Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();


				Set<Integer> excludeProtein_Ids_Set_UserInput = new HashSet<>();
				

				if ( proteinQueryJSONRoot.getExcludeTaxonomy() != null ) {

					for ( Integer taxonomyId : proteinQueryJSONRoot.getExcludeTaxonomy() ) {
					
						excludeTaxonomy_Ids_Set_UserInput.add( taxonomyId );
					}
				}

				//  First convert the protein sequence ids that come from the JS code to standard integers and put
				//   in the property excludeProteinSequenceIds
				ProteinsMergedProteinsCommon.getInstance().processExcludeProteinSequenceIdsFromJS( proteinQueryJSONRoot );

				if ( proteinQueryJSONRoot.getExcludeProteinSequenceIds() != null ) {

					for ( Integer proteinId : proteinQueryJSONRoot.getExcludeProteinSequenceIds() ) {

						excludeProtein_Ids_Set_UserInput.add( proteinId );
					}
				}
				


				CutoffPageDisplayRoot cutoffPageDisplayRoot =
						GetCutoffPageDisplayRoot.getInstance().getCutoffPageDisplayRoot( searchIdsCollection, request );



				CutoffValuesRootLevel cutoffValuesRootLevel = proteinQueryJSONRoot.getCutoffs();

				Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
						Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( searchIdsCollection, cutoffValuesRootLevel );
				
				
				SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
				


				ProteinCoverageCompute pcs = new ProteinCoverageCompute();

				pcs.setExcludedProteinSequenceIds( proteinQueryJSONRoot.getExcludeProteinSequenceIds() );
				pcs.setExcludedTaxonomyIds( proteinQueryJSONRoot.getExcludeTaxonomy() );
				
				pcs.setFilterNonUniquePeptides( proteinQueryJSONRoot.isFilterNonUniquePeptides() );
				pcs.setFilterOnlyOnePSM( proteinQueryJSONRoot.isFilterOnlyOnePSM() );
				pcs.setFilterOnlyOnePeptide( proteinQueryJSONRoot.isFilterOnlyOnePeptide() );
				
				pcs.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );

				pcs.setSearches( searches );

				List<ProteinCoverageData> pcdlist = pcs.getProteinCoverageData();

				writer.write( "# Protein Coverage Report For:\n");
				writer.write( "# Search(s): " + StringUtils.join( searchIds, ", " ) + "\n" );
				
//				So, like, "psm_q-Value(search:1)" for search 1?
						
				//   Output cutoffs used
				
				
				//   Map keyed on search id as string
				Map<String,CutoffValuesSearchLevel> cutoffValuesSearchLevelMap = cutoffValuesRootLevel.getSearches();
						
				
				for ( CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel : cutoffPageDisplayRoot.getPerSearchDataList() ) {
					
					String searchIdString = Integer.toString( cutoffPageDisplaySearchLevel.getSearchId() );
					
					CutoffValuesSearchLevel cutoffValuesSearchLevel = cutoffValuesSearchLevelMap.get( searchIdString );

					//   Map keyed on annotation type id as string
					Map<String,CutoffValuesAnnotationLevel> peptideCutoffValues = cutoffValuesSearchLevel.getPeptideCutoffValues();
					Map<String,CutoffValuesAnnotationLevel> psmCutoffValues = cutoffValuesSearchLevel.getPsmCutoffValues();
					
					///   Output Peptide cutoff values
					
					for ( CutoffPageDisplayAnnotationLevel peptideCutoffPageDisplayAnnotationLevel : cutoffPageDisplaySearchLevel.getPeptideAnnotationCutoffData() ) {
						
						String annotationTypeIdString = Integer.toString( peptideCutoffPageDisplayAnnotationLevel.getAnnotationTypeId() );
						
						CutoffValuesAnnotationLevel cutoffValuesAnnotationLevel = peptideCutoffValues.get(annotationTypeIdString);
						
						if ( cutoffValuesAnnotationLevel != null ) {
							
							//  Have cutoff value for this annotation type id so display it
							
							writer.write( "# Peptide " );
							writer.write( peptideCutoffPageDisplayAnnotationLevel.getAnnotationName() );
							writer.write( "(search:" );
							writer.write( searchIdString );
							writer.write( "):" );
							
							writer.write( cutoffValuesAnnotationLevel.getValue() );
							writer.write( "\n" );
						}
					}
					

					///   Output PSM cutoff values
					
					for ( CutoffPageDisplayAnnotationLevel psmCutoffPageDisplayAnnotationLevel : cutoffPageDisplaySearchLevel.getPsmAnnotationCutoffData() ) {
						
						String annotationTypeIdString = Integer.toString( psmCutoffPageDisplayAnnotationLevel.getAnnotationTypeId() );
						
						CutoffValuesAnnotationLevel cutoffValuesAnnotationLevel = psmCutoffValues.get(annotationTypeIdString);
						
						if ( cutoffValuesAnnotationLevel != null ) {
							
							//  Have cutoff value for this annotation type id so display it
							
							writer.write( "# PSM " );
							writer.write( psmCutoffPageDisplayAnnotationLevel.getAnnotationName() );
							writer.write( "(search:" );
							writer.write( searchIdString );
							writer.write( "):" );
							
							writer.write( cutoffValuesAnnotationLevel.getValue() );
							writer.write( "\n" );
						}
					}
					
				}
						
				
				writer.write( "# Filter out non-unique peptides: " + proteinQueryJSONRoot.isFilterNonUniquePeptides() + "\n" );
				writer.write(
						"PROTEIN\t# RESIDUES\tSEQUENCE COVERAGE\t#LINKABLE RESIDUES"
						+ "\tLinkable Residues Covered\tLinkable Residues Coverage\t"
								+ "M+L+C RESIDUES\tM+L+C SEQUENCE COVERAGE\t"
								+ "L+C RESIDUES\tL+C SEQUENCE COVERAGE\t"
								+ "M RESIDUES\tM SEQUENCE COVERAGE\t"
								+ "L RESIDUES\tL SEQUENCE COVERAGE\t"
								+ "C RESIDUES\tC SEQUENCE COVERAGE\n"
						);


				for( ProteinCoverageData pcd : pcdlist ) {
					writer.write( pcd.getName() + "\t" );
					writer.write( pcd.getNumResidues() + "\t" );
					writer.write( pcd.getSequenceCoverage() + "\t" );
					writer.write( pcd.getNumLinkableResidues() + "\t" );
					
					writer.write( pcd.getNumLinkableResiduesCovered() + "\t" );
					writer.write( pcd.getLinkableResiduesCoverageFmt() + "\t" );

					writer.write( pcd.getNumMLCResidues() + "\t" );
					writer.write( pcd.getMLCSequenceCoverage() + "\t" );

					writer.write( pcd.getNumLCResidues() + "\t" );
					writer.write( pcd.getLCSequenceCoverage() + "\t" );


					writer.write( pcd.getMonolinkedResidues() + "\t" );
					writer.write( pcd.getMSequenceCoverage() + "\t" );

					writer.write( pcd.getLooplinkedResidues() + "\t" );
					writer.write( pcd.getLSequenceCoverage() + "\t" );

					writer.write( pcd.getCrosslinkedResidues() + "\t" );
					writer.write( pcd.getCSequenceCoverage() );

					writer.write( "\n" );

				}

			} finally {
				
				try {
					if ( writer != null ) {
						writer.close();
					}

				} catch ( Exception ex ) {

					log.error( "writer.close():Exception " + ex.toString(), ex );
				}

				try {
					response.flushBuffer();
				} catch ( Exception ex ) {

					log.error( "response.flushBuffer():Exception " + ex.toString(), ex );
				}

			}
			
			return null;
			

		} catch ( Exception e ) {

			String msg = "Exception:  RemoteAddr: " + request.getRemoteAddr()  
					+ ", Exception caught: " + e.toString();
			
			log.error( msg, e );

			throw e;
		}
	}
}
