package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionIdProteinAnnotationName;

/**
 * 
 *
 */
public class CombineProteinAnnNamesForSameSeqVId {
	
	private static final Logger log = LoggerFactory.getLogger( CombineProteinAnnNamesForSameSeqVId.class);
	
	private CombineProteinAnnNamesForSameSeqVId() { }
	private static final CombineProteinAnnNamesForSameSeqVId _INSTANCE = new CombineProteinAnnNamesForSameSeqVId();
	public static CombineProteinAnnNamesForSameSeqVId getInstance() { return _INSTANCE; }
	
	/**
	 * @param input
	 * @return New ProteinSequenceVersionIdProteinAnnotationName as needed when combine annotation names
	 */
	public Set<ProteinSequenceVersionIdProteinAnnotationName> combineProteinAnnNamesForSameSeqVId( Set<ProteinSequenceVersionIdProteinAnnotationName> input ) {

		if ( input == null ||  input.isEmpty() ) {
			return input; // EARLY RETURN
		}
		{
			boolean foundDuplicate_protSeqVId = false;
			//  First see if any duplicate protSeqVIds
			Set<Integer> protSeqVIds = new HashSet<>();
			for ( ProteinSequenceVersionIdProteinAnnotationName item : input ) {
				if ( ! protSeqVIds.add( item.getProteinSequenceVersionId() ) ) {
					foundDuplicate_protSeqVId = true;
					break;
				}
			}
			if ( ! foundDuplicate_protSeqVId ) {
				return input; // EARLY RETURN
			}
		}
		Set<ProteinSequenceVersionIdProteinAnnotationName> result = new HashSet<>();
		
		Map<Integer, Set<ProteinSequenceVersionIdProteinAnnotationName>> mapKeyProteinSeqVId = new HashMap<>();
		
		for ( ProteinSequenceVersionIdProteinAnnotationName item : input ) {
			 Set<ProteinSequenceVersionIdProteinAnnotationName> mapItem = mapKeyProteinSeqVId.get( item.getProteinSequenceVersionId() );
			 if ( mapItem == null ) {
				 mapItem = new HashSet<>();
				 mapKeyProteinSeqVId.put( item.getProteinSequenceVersionId(), mapItem );
			 }
			 mapItem.add(item);
		}
		
		for ( Map.Entry<Integer, Set<ProteinSequenceVersionIdProteinAnnotationName>> mapEntry : mapKeyProteinSeqVId.entrySet() ) {
			Set<ProteinSequenceVersionIdProteinAnnotationName> mapEntryValue = mapEntry.getValue();
			List<String> annNames = new ArrayList<>( mapEntryValue.size() );
			for ( ProteinSequenceVersionIdProteinAnnotationName entry : mapEntryValue ) {
				annNames.add( entry.getAnnotationName() );
			}
			Collections.sort( annNames );
			String annotationNameResult = StringUtils.join( annNames, "," );
			ProteinSequenceVersionIdProteinAnnotationName resultItem = new ProteinSequenceVersionIdProteinAnnotationName();
			resultItem.setProteinSequenceVersionId( mapEntry.getKey() );
			resultItem.setAnnotationName( annotationNameResult );
			result.add( resultItem );
		}
		
		return result;
	}
	
	//  Test:
//	public static void main(String[] args) {
//		
//		System.out.println("main called");
//		
//		Set<ProteinSequenceVersionIdProteinAnnotationName> input = new HashSet<>();
//		ProteinSequenceVersionIdProteinAnnotationName item_1 = new ProteinSequenceVersionIdProteinAnnotationName();
//		ProteinSequenceVersionIdProteinAnnotationName item_2 = new ProteinSequenceVersionIdProteinAnnotationName();
//		
//		item_1.setProteinSequenceVersionId( 5 );
//		item_2.setProteinSequenceVersionId( 5 );
//		
//		item_1.setAnnotationName( "ccdd" );
//		item_2.setAnnotationName( "aabb" );
//		
//		input.add( item_1 );
//		input.add( item_2 );
//		
//		Set<ProteinSequenceVersionIdProteinAnnotationName> result = 
//				CombineProteinAnnNamesForSameSeqVId.getInstance().combineProteinAnnNamesForSameSeqVId( input );
//		
//		System.out.println( "result.size: " + result.size() );
//		
//		if ( ! result.isEmpty() ) {
//			ProteinSequenceVersionIdProteinAnnotationName resultEntry = result.iterator().next();
//			System.out.println( "resultEntry: ProteinSequenceVersionId: " + resultEntry.getProteinSequenceVersionId() );
//			System.out.println( "resultEntry: AnnotationName: " + resultEntry.getAnnotationName() );
//		}
//
//		System.out.println("main end");
//		
//	}
}
