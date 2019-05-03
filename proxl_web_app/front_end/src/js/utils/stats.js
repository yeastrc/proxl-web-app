
"use strict";

export class StatsUtils {

    /**
     * Get probability of an intersection of size ixnSize or larger by chance given the sizes
     * of set A, set B, and the universe from which they were drawn.
     * @param setASize
     * @param setBSize
     * @param ixnSize
     * @param universeSize
     * @returns {number}
     */
    static getHypergeometricPValue({ setASize, setBSize, ixnSize, universeSize }) {

        setASize = parseInt( setASize );
        setBSize = parseInt( setBSize );
        ixnSize = parseInt( ixnSize );
        universeSize = parseInt( universeSize );

        let pp = 0.000;

        const m = (setASize < setBSize) ? setASize : setBSize;

        for (let i = 0; i < ( (m - ixnSize) + 1); i++) {
            pp += Math.exp(P(m-i, setASize-m+i, setBSize-m+i, universeSize-setBSize-setASize+m-i));
        }

        return pp;


        // private helper functions
        function gammaln(xx) {
            const cof = [76.18009173, -86.50532033, 24.01409822, -1.231739516, 0.120858003e-2, -0.536382e-5];

            let x = xx - 1.0;
            let tmp = x + 5.5;
            tmp-= (x+0.5)*Math.log(tmp);
            let ser = 1.0;

            for(let j = 0; j <= 5; j++) {
                x += 1.0;
                ser+= cof[j]/x;
            }

            return -tmp+Math.log(2.50662827465*ser);
        }

        function P(x, y, z, N) {
            return gammaln(x+y+1)+gammaln(x+z+1)+gammaln(z+N+1)+gammaln(y+N+1)+
                -1*(gammaln(x+1)+gammaln(y+1)+gammaln(z+1)+gammaln(N+1)+
                    gammaln(x+y+z+N+1));
        }

    }

}