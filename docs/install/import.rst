How to Import Data
===========================================

The high-level procedure for importing data into ProXL is:

    1. Convert output to ProXL XML.
    2. Run the import program.
    
More details follow below.

Convert output to ProXL XML
-------------------------------
To simplify support for as many software pipelines as possible, we have developed a
XML specification dubbed ProXL XML for describing the cross-linking proteomics results
from any software pipeline. In addition to the scores, themselves,
the specification allows the creator of the document to specifiy which programs were used
(e.g., Kojak or pLink), what types of scores those programs generate (e.g. p-values or XCorrs),
how to treat those scores (e.g., smaller is better, default cutoffs, sort order, descriptions), and
other annotations of the search. Because a description of the scores and how they are treated are
part of the specification, itself, nearly any conceivable software pipeline can have its results
represented as ProXL XML.

Converters have been developed for several cross-linking proteomics pipelines. Click on the name of the
software, below, to learn more about the respective converter.

    * `iProphet (TPP) <https://github.com/yeastrc/proxl-import-iprophet>`_
    * `Kojak <https://github.com/yeastrc/proxl-import-kojak>`_
    * `Crux <https://github.com/yeastrc/proxl-import-crux>`_
    * `pLink <https://github.com/yeastrc/proxl-import-plink>`_
    * `StavroX <https://github.com/yeastrc/proxl-import-stavrox>`_
    * `xQuest <https://github.com/yeastrc/proxl-import-xquest>`_

If you are using one of the software packages listed above, download the runnable file and
follow its instructions to convert your data to ProXL XML. If you encounter any issues or
have any questions running any of these software, please email us at proxl-help@yeastrc.org.

If you are not using one of the software packages listed above, please visit our
:doc:`/install/converter_guide` page.

Run the import program
--------------------------------------
Data are imported into the database by running the import program using the ProXL XML
file and (optionally) the spectral file (mzML or mzXML) as input. The latest version
and documentation for the import program may be found at
`<https://github.com/yeastrc/proxl-import-xml-to-db>`_.
