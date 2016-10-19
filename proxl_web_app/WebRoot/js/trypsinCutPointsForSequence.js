
//  trypsinCutPointsForSequence.js





//JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

///////////////////////////////////////////




// Trypsin cuts at

// [KR]^P (after K or R) (already implemented)
// WKP (between K and P)
// MRP (between R and P)

// Trypsin does NOT cut at (I put a . where cleavage would be expected to occur based on the above rules, but it actually doesn't in these cases.)

// [CD]K.D ( after K )
// CK.[HY] ( after K )
// CR.K ( after R )
// RR.[HR] ( after Second R )


//  

function computeCutPoints( proteinSequence ) {

	try {

		var cutPointItems = [];


		if ( proteinSequence.length < 2 ) {

			return;
		}


		var prevChar = ' ';
		var currChar = ' ';
		var nextChar = proteinSequence.charAt( 0 );

		var lastIndexToProcess = proteinSequence.length - 1;


		for ( var index = 0; index < lastIndexToProcess; index++ ) {

			var startOfCutPoint = index + 1;  //  add one since index is Zero based and startOfCutPoint is One based

			var cutPointCenter = startOfCutPoint + 0.5; //  add 0.5 since cut point after first letter


			prevChar = currChar;
			currChar = nextChar;
			nextChar = proteinSequence.charAt( index + 1 );

			currChar = proteinSequence.charAt( index );

			if ( index === 0 ) {

				//  Special processing for first position

				if ( ( currChar === 'K' || currChar === 'R' ) && nextChar != 'P' ) {

					//  If first letter in sequence not followed by 'P'

					cutPointItems.push( cutPointCenter );
				}
			} else {

				//  First the "include" rules for where cut points are located

				if ( ( ( currChar === 'K' || currChar === 'R' ) && nextChar != 'P' )

						|| ( prevChar === 'W' && currChar === 'K' && nextChar === 'P' )
						|| ( prevChar === 'M' && currChar === 'R' && nextChar === 'P' ) ) {



					//  Now process the "excludes"

//					Trypsin does NOT cut at (I put a . where cleavage would be expected to occur based on the above rules, but it actually doesn't in these cases.)
					//
//					[CD]K.D ( after K )
//					CK.[HY] ( after K )
//					CR.K ( after R )
//					RR.[HR] ( after Second R )

					if ( ( ( prevChar === 'C' || prevChar === 'D' ) && currChar === 'K' && nextChar === 'D' )

							|| ( prevChar === 'C' && currChar === 'K' && ( nextChar === 'H' || nextChar === 'Y' ) )
							|| ( prevChar === 'C' && currChar === 'R' && nextChar === 'K' )
							|| ( prevChar === 'R' && currChar === 'R' && ( nextChar === 'H' || nextChar === 'R' ) ) ) {

						//  Exclude if this "if" statement is true

					} else {

						cutPointItems.push( cutPointCenter );

					}
				}
			}
		}

		return cutPointItems;
		
	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}


