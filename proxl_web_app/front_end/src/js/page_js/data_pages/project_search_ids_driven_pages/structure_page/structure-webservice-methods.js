"use strict";

export class StructureWebserviceMethods {

    /**
     * Sends the request out for a lookup
     */
    static doLinkablePositionsLookup ( { proteins, projectSearchIds, startCallout, endCallout } ) {

        return new Promise( (resolve, reject ) => {

            if( startCallout ) { startCallout(); }

            const url = "services/linkablePositions/getLinkablePositionsBetweenProteins";

            const requestData = {
                projectSearchIds: projectSearchIds,
                proteins: proteins
            };

            $.ajax({
                type: "GET",
                url: url,
                traditional: true,  //  Force traditional serialization of the data sent
                //   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
                //   So proteinIdsToGetSequence array is passed as "proteinIdsToGetSequence=<value>" which is what Jersey expects
                data: requestData,
                dataType: "json",
                success: function (data) {
                    if( endCallout ) { endCallout(); }
                    resolve( data );
                },
                failure: function (errMsg) {
                    if( endCallout ) { endCallout(); }
                    handleAJAXFailure(errMsg);
                    reject();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if( endCallout ) { endCallout(); }
                    handleAJAXError(jqXHR, textStatus, errorThrown);
                    reject();
                }
            });
        });
    }

}