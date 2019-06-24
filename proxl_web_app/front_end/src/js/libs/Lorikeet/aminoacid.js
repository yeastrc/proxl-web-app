
function AminoAcid(aaCode, aaShortName, aaName, monoMass, avgMass) {
   this.code = aaCode;
   this.shortName = aaShortName;
   this.name = aaName;
   this.mono = monoMass;
   this.avg = avgMass;
   
   this.get = _getAA;
}

AminoAcid.A = new AminoAcid ("A", "Ala", "Alanine",        71.037113805,  71.0779);
AminoAcid.R = new AminoAcid ("R", "Arg", "Arginine",      156.101111050, 156.18568);
AminoAcid.N = new AminoAcid ("N", "Asn", "Asparagine",    114.042927470, 114.10264);
AminoAcid.D = new AminoAcid ("D", "Asp", "Aspartic Acid", 115.026943065, 115.0874);
AminoAcid.C = new AminoAcid ("C", "Cys", "Cysteine",      103.009184505, 103.1429);
AminoAcid.E = new AminoAcid ("E", "Glu", "Glutamine",     129.042593135, 129.11398);
AminoAcid.Q = new AminoAcid ("Q", "Gln", "Glutamic Acid", 128.058577540, 128.12922);
AminoAcid.G = new AminoAcid ("G", "Gly", "Glycine",        57.021463735,  57.05132);
AminoAcid.H = new AminoAcid ("H", "His", "Histidine",     137.058911875, 137.13928);
AminoAcid.I = new AminoAcid ("I", "Ile", "Isoleucine",    113.084064015, 113.15764);
AminoAcid.L = new AminoAcid ("L", "Leu", "Leucine",       113.084064015, 113.15764);
AminoAcid.K = new AminoAcid ("K", "Lys", "Lysine",        128.094963050, 128.17228);
AminoAcid.M = new AminoAcid ("M", "Met", "Methionine",    131.040484645, 131.19606);
AminoAcid.F = new AminoAcid ("F", "Phe", "Phenylalanine", 147.068413945, 147.17386);
AminoAcid.P = new AminoAcid ("P", "Pro", "Proline",        97.052763875,  97.11518);
AminoAcid.S = new AminoAcid ("S", "Ser", "Serine",         87.032028435,  87.0773);
AminoAcid.T = new AminoAcid ("T", "Thr", "Threonine",     101.047678505, 101.10388);
AminoAcid.W = new AminoAcid ("W", "Trp", "Tryptophan",    186.079312980, 186.2099);
AminoAcid.Y = new AminoAcid ("Y", "Tyr", "Tyrosine",      163.063328575, 163.17326);
AminoAcid.V = new AminoAcid ("V", "Val", "Valine",         99.068413945,  99.13106);

// add atomic makeups to each amino acid to support isotope label mass adjustments
// Michael Riffle
AminoAcid.A.atomicCountMap = { }
AminoAcid.A.atomicCountMap.C = 3;
AminoAcid.A.atomicCountMap.H = 7;
AminoAcid.A.atomicCountMap.N = 1;
AminoAcid.A.atomicCountMap.O = 2;

AminoAcid.R.atomicCountMap = { }
AminoAcid.R.atomicCountMap.C = 6;
AminoAcid.R.atomicCountMap.H = 14;
AminoAcid.R.atomicCountMap.N = 4;
AminoAcid.R.atomicCountMap.O = 2;

AminoAcid.N.atomicCountMap = { }
AminoAcid.N.atomicCountMap.C = 4;
AminoAcid.N.atomicCountMap.H = 8;
AminoAcid.N.atomicCountMap.N = 2;
AminoAcid.N.atomicCountMap.O = 3;

AminoAcid.D.atomicCountMap = { }
AminoAcid.D.atomicCountMap.C = 4;
AminoAcid.D.atomicCountMap.H = 7;
AminoAcid.D.atomicCountMap.N = 1;
AminoAcid.D.atomicCountMap.O = 4;

AminoAcid.C.atomicCountMap = { }
AminoAcid.C.atomicCountMap.C = 3;
AminoAcid.C.atomicCountMap.H = 7;
AminoAcid.C.atomicCountMap.N = 1;
AminoAcid.C.atomicCountMap.O = 2;
AminoAcid.C.atomicCountMap.S = 1;

AminoAcid.E.atomicCountMap = { }
AminoAcid.E.atomicCountMap.C = 5;
AminoAcid.E.atomicCountMap.H = 9;
AminoAcid.E.atomicCountMap.N = 1;
AminoAcid.E.atomicCountMap.O = 4;

AminoAcid.Q.atomicCountMap = { }
AminoAcid.Q.atomicCountMap.C = 5;
AminoAcid.Q.atomicCountMap.H = 10;
AminoAcid.Q.atomicCountMap.N = 2;
AminoAcid.Q.atomicCountMap.O = 3;

AminoAcid.G.atomicCountMap = { }
AminoAcid.G.atomicCountMap.C = 2;
AminoAcid.G.atomicCountMap.H = 5;
AminoAcid.G.atomicCountMap.N = 1;
AminoAcid.G.atomicCountMap.O = 2;

AminoAcid.H.atomicCountMap = { }
AminoAcid.H.atomicCountMap.C = 6;
AminoAcid.H.atomicCountMap.H = 9;
AminoAcid.H.atomicCountMap.N = 3;
AminoAcid.H.atomicCountMap.O = 2;

AminoAcid.I.atomicCountMap = { }
AminoAcid.I.atomicCountMap.C = 6;
AminoAcid.I.atomicCountMap.H = 13;
AminoAcid.I.atomicCountMap.N = 1;
AminoAcid.I.atomicCountMap.O = 2;

AminoAcid.L.atomicCountMap = { }
AminoAcid.L.atomicCountMap.C = 6;
AminoAcid.L.atomicCountMap.H = 13;
AminoAcid.L.atomicCountMap.N = 1;
AminoAcid.L.atomicCountMap.O = 2;

AminoAcid.K.atomicCountMap = { }
AminoAcid.K.atomicCountMap.C = 6;
AminoAcid.K.atomicCountMap.H = 14;
AminoAcid.K.atomicCountMap.N = 2;
AminoAcid.K.atomicCountMap.O = 2;

AminoAcid.M.atomicCountMap = { }
AminoAcid.M.atomicCountMap.C = 5;
AminoAcid.M.atomicCountMap.H = 11;
AminoAcid.M.atomicCountMap.N = 1;
AminoAcid.M.atomicCountMap.O = 2;
AminoAcid.M.atomicCountMap.S = 1;

AminoAcid.F.atomicCountMap = { }
AminoAcid.F.atomicCountMap.C = 9;
AminoAcid.F.atomicCountMap.H = 11;
AminoAcid.F.atomicCountMap.N = 1;
AminoAcid.F.atomicCountMap.O = 2;

AminoAcid.P.atomicCountMap = { }
AminoAcid.P.atomicCountMap.C = 5;
AminoAcid.P.atomicCountMap.H = 9;
AminoAcid.P.atomicCountMap.N = 1;
AminoAcid.P.atomicCountMap.O = 2;

AminoAcid.S.atomicCountMap = { }
AminoAcid.S.atomicCountMap.C = 3;
AminoAcid.S.atomicCountMap.H = 7;
AminoAcid.S.atomicCountMap.N = 1;
AminoAcid.S.atomicCountMap.O = 3;

AminoAcid.T.atomicCountMap = { }
AminoAcid.T.atomicCountMap.C = 4;
AminoAcid.T.atomicCountMap.H = 9;
AminoAcid.T.atomicCountMap.N = 1;
AminoAcid.T.atomicCountMap.O = 3;

AminoAcid.W.atomicCountMap = { }
AminoAcid.W.atomicCountMap.C = 11;
AminoAcid.W.atomicCountMap.H = 12;
AminoAcid.W.atomicCountMap.N = 2;
AminoAcid.W.atomicCountMap.O = 2;

AminoAcid.Y.atomicCountMap = { }
AminoAcid.Y.atomicCountMap.C = 9;
AminoAcid.Y.atomicCountMap.H = 11;
AminoAcid.Y.atomicCountMap.N = 1;
AminoAcid.Y.atomicCountMap.O = 3;

AminoAcid.V.atomicCountMap = { }
AminoAcid.V.atomicCountMap.C = 5;
AminoAcid.V.atomicCountMap.H = 11;
AminoAcid.V.atomicCountMap.N = 1;
AminoAcid.V.atomicCountMap.O = 2;


AminoAcid.aa = [];
AminoAcid.aa["A"] = AminoAcid.A;
AminoAcid.aa["R"] = AminoAcid.R;
AminoAcid.aa["N"] = AminoAcid.N;
AminoAcid.aa["D"] = AminoAcid.D;
AminoAcid.aa["C"] = AminoAcid.C;
AminoAcid.aa["E"] = AminoAcid.E;
AminoAcid.aa["Q"] = AminoAcid.Q;
AminoAcid.aa["G"] = AminoAcid.G;
AminoAcid.aa["H"] = AminoAcid.H;
AminoAcid.aa["I"] = AminoAcid.I;
AminoAcid.aa["L"] = AminoAcid.L;
AminoAcid.aa["K"] = AminoAcid.K;
AminoAcid.aa["M"] = AminoAcid.M;
AminoAcid.aa["F"] = AminoAcid.F;
AminoAcid.aa["P"] = AminoAcid.P;
AminoAcid.aa["S"] = AminoAcid.S;
AminoAcid.aa["T"] = AminoAcid.T;
AminoAcid.aa["W"] = AminoAcid.W;
AminoAcid.aa["Y"] = AminoAcid.Y;
AminoAcid.aa["V"] = AminoAcid.V;

AminoAcid.get = _getAA;

function _getAA(aaCode) {
   if(AminoAcid.aa[aaCode])
      return AminoAcid.aa[aaCode];
   else
      return new AminoAcid(aaCode, aaCode, 0.0, 0.0);
}

export { AminoAcid }

