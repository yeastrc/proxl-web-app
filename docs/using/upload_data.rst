===========================================
Upload Data
===========================================

To upload data into proxl, the data must first be converted
into proxl XML then uploaded via the proxl web site.

Convert output to proxl XML
-------------------------------
To simplify support for as many software pipelines as possible, we have developed a
XML specification dubbed proxl XML for describing the cross-linking proteomics results
from any software pipeline. In addition to the scores, themselves,
the specification allows the creator of the document to specifiy which programs were used
(e.g., Kojak or pLink), what types of scores those programs generate (e.g. p-values or XCorrs), and
how to treat those scores (e.g., smaller is better, default cutoffs, sort order, descriptions).
Because a description of the scores and how they are treated are
part of the specification, itself, nearly any conceivable software pipeline can have its results
represented as proxl XML.

Converters have been developed for several cross-linking proteomics pipelines. Click on the name of the
software, below, to download and learn more about the respective converter.

    * `iProphet (TPP) <https://github.com/yeastrc/proxl-import-iprophet>`_
    * `Kojak <https://github.com/yeastrc/proxl-import-kojak>`_
    * `Crux <https://github.com/yeastrc/proxl-import-crux>`_
    * `pLink <https://github.com/yeastrc/proxl-import-plink>`_
    * `StavroX <https://github.com/yeastrc/proxl-import-stavrox>`_
    * `xQuest <https://github.com/yeastrc/proxl-import-xquest>`_

If you are using one of the software packages listed above, download the runnable file and
follow its instructions to convert your data to proxl XML. If you encounter any issues or
have any questions running any of these software, please email us at mriffle@uw.edu.

If you are not using one of the software packages listed above, please visit our
:doc:`/install/converter_guide` page for information about how to develop a converter.
We are happy to answer any questions, or work with you directly on the development
of any new converter.

Import Data
-------------------------------
Click the [+] icon next to "Upload Data" on the project overview page to expand
the data upload section and view upload status.

.. image:: /images/data-upload-section.png

To upload a proxl XML file, click the "Import Proxl XML File" button:

.. image:: /images/import-data-1.png

Add a description for the search (may be edited later), and click the "+Add Proxl XML File" link to initiate
a file selection dialog. Select the proxl XML file you would like to upload. You will see the following:

.. image:: /images/import-data-2.png

Below the description, the file name of the uploaded proxl XML file is given. The red "X" icon may be clicked to
delete that file and upload a different one. To the right of the file name is the upload status.

Below the file name is a link labeled "+Add Scan File". This may be used to optionally upload a mzML or mzXML file (or multiple  files) containing
the scan data that was searched. Viewing spectra associated with PSMs is only available if a scan file is uploaded with the proxl XML file.

Clicking the "+Add Scan File" link opens a file selection dialog. Upon selecting a file, it will begin uploading to the server--a
progress bar will be visible. Once the upload is complete, you will see the following:

.. image:: /images/import-data-3.png

To deleted an uploaded scan file, click the "X" icon next to its file name. If multiple scan files were used in the search, you may continue to
upload additional scan files by clicking the "+Add Scan File" link.

To submit the data to proxl for processing and import, click the "Submit Upload" button.


Import Status
-------------------------------
If not expanded, click the [+] icon next to "Upload Data" on the project overview page to expand
the data upload section and view upload status.

The number of pending queued uploads for this project is listed next to the "Upload Data" section header. The
pending uploads are listed individually under the "Pending" section of the "Upload Data" section:

.. image:: /images/import-data-4.png

Clicking the "X" icon will remove this upload from the queue, effectively canceling the upload. This is only available
if processing of this upload request has not yet begun.

Once completed, the upload will be moved from the "Pending" list to the "History" list, and the data for the new upload
will be available in the "Explore Data" section:

.. image:: /images/import-data-5.png
