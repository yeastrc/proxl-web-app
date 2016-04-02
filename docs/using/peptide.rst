==================
Peptides View Page
==================

.. image:: /images/peptide-page.png

The peptide view page provides a table view of the data at the peptide level.
That is, all peptides (and crosslinked peptide pairs) identified by the search
software may be viewed on this page--along with accompanying peptide spectrum
matches (PSMs) and tandem mass spectra. The data presented may be filtered according
to confidence, type of peptide, and which modifications are present on the peptide. Note,
this document covers the peptide view page for a single search. For the view page seen
when merging multiple searches, see :doc:`/using/merged-peptide`.

Search Information
=========================
The name of the search (and internal search ID reference number) from which these
data were obtained is shown first. The red [+] icon may be clicked to reveal details
about the search.

Search Details
---------------------------
The "Path" is the location on disk from which the data were imported. The "Linker" is the
name of the crossinker used in the experiment. "Search Program(s)" is the name and
version number of the PSM search software used. "Upload date" is the date the data were
uploaded into ProXL. "FASTA file" is the name of the FASTA file used to perform the
PSM search.

Filter Data
=========================
The data presented may be filtered according to the following criteria. Note: Only peptides
that meet ALL of the specified criteria are returned.

PSM Filters
--------------------
The filters to apply at the PSM level. Only results which have at least one PSM that meets all of the selected
critiera will be listed. When listing PSMs associated with peptides, only PSMs that meet all of the selected
critiera will be listed.

To change the PSM-level filters, first click the pencil icon next to "PSM Filters":

.. image:: /images/filter-change-psm-filter1.png

This opens an overlay with the containing the possible score types to use as PSM filters for this search. To change
the cutoff values to be used for any of these score types, enter the value next to the score type. ProXL will correctly
handle scores for which larger values are more significant or scores for which smaller values are more signiciant.

.. image:: /images/filter-change-psm-filter2.png

To save the new values to the page, click the "Save" button. To cancel, click "Cancel".

The "Reset to Defaults" button will reset the cutoff values to the defaults specified by the ProXL XML file uploaded
to the database. This typically represents the suggested cutoffs by the author of the respective search program.

*Important*: It is necessary to update the data on the page after changing filter cutoff values. After clicking
the "Save" button, you must click the "Update From Database" button on the page to apply any new PSM- or peptide-level
filters.

.. image:: /images/filter-update-from-database.png


Peptide Filters
-----------------------
The filters to apply at the peptide level. Only results which have at least one peptide that meets all of the selected
critiera will be listed.

To change the peptide-level filters, first click the pencil icon next to "Peptide Filters":

.. image:: /images/filter-change-peptide-filter1.png

This opens an overlay with the containing the possible score types to use as peptide-level filters for this search. To change
the cutoff values to be used for any of these score types, enter the value next to the score type. ProXL will correctly
handle scores for which larger values are more significant or scores for which smaller values are more signiciant.

.. image:: /images/filter-change-peptide-filter2.png

To save the new values to the page, click the "Save" button. To cancel, click "Cancel".

The "Reset to Defaults" button will reset the cutoff values to the defaults specified by the ProXL XML file uploaded
to the database. This typically represents the suggested cutoffs by the author of the respective search program.

*Important*: It is necessary to update the data on the page after changing filter cutoff values. After clicking
the "Save" button, you must click the "Update From Database" button on the page to apply any new PSM- or peptide-level
filters.

.. image:: /images/filter-update-from-database.png


Type filter
-------------------------
Only peptides of the checked type(s) will be returned. Proxl defines the types as:

	* crosslink - A pair of peptides linked by a crosslinker.
	* looplink - A single peptide with two residues linked by a crosslinker.
	* monolink - A peptide containing at least one linked residue, where the other end of the linker is unlinked.
	* unlinked - The peptide without a crosslinker on any residue.

Checking multiple boxes will include any peptide that has at least one of the checked types.
I.e., checking 'crosslinks' and 'looplinks' will only include peptides that are either
crosslinks or looplinks. Only checking 'crosslinks' will only return crosslinked peptides.
The 'monolinks' type will include any peptides that contain a monolink.


Modification filter
-------------------------
Only peptides with at least one of the checked modifications will be included. Note that monolinks
are considered modifications of residues in ProXL, so the mass of the crosslinker when found
on monolinks is included here.

Update
-------------------------
In order to apply new filter parameters to the shown data, the "Update" button must be clicked. This will
fetch filtered data from the ProXL server and display the data on the web page.

Save As Default
--------------------------
Project owners may save the current filter parameters as the default view of the data on this page by
clicking this button. This default view will be shown when users follow links to the "Peptide View" for
this search.

Table Description
=========================
Above the table is the text, "Peptides (#)", where # is the number of distinct reported peptides were found
for this search. A distinct peptide is the combination of peptide sequence(s), linked positions in those
peptides, and the location and type of post-translational modifications.

Columns
-------------------------
The columns are described below. Note that all column headers may be clicked to toggle between ascending and
descending sorting of that column. Holding the shift key while clicking column headers allow sorting on
multiple columns.

Type
^^^^^^^^^^^^^^^^^^^^^^^^^
The type of peptide (crosslink, looplink, or unlinked).

Reported peptide
^^^^^^^^^^^^^^^^^^^^^^^^^
The peptide as it was reported by the search program used.

Peptide 1
^^^^^^^^^^^^^^^^^^^^^^^^^
The parsed sequence of the peptide (or the first peptide in the case of crosslinks).

Pos
^^^^^^^^^^^^^^^^^^^^^^^^^
The position in that peptide containing the linker.

Peptide 2
^^^^^^^^^^^^^^^^^^^^^^^^^
The parse sequence of the second peptide in the crosslink.

Pos
^^^^^^^^^^^^^^^^^^^^^^^^^
The position in that peptide containing the linker.

Protein 1
^^^^^^^^^^^^^^^^^^^^^^^^^
The protein(s) to which the first peptide matches, and the position in that
protein to which the linker position in that peptide matched. Mouse-over
the protein name to get a description.

Protein 2
^^^^^^^^^^^^^^^^^^^^^^^^^
The protein(s) to which the second peptide matches, and the position in that
protein to which the linker position in that peptide matched. Mouse-over
the protein name to get a description.

Q-value
^^^^^^^^^^^^^^^^^^^^^^^^^
The peptide-level q-value, if available.

# PSMs
^^^^^^^^^^^^^^^^^^^^^^^^^
The number of PSMs that have a q-value <= the specified PSM-level cutoff that identified this peptide. Note: click
the table row containing the peptide to see all the PSMs.

Best PSM Q-value
^^^^^^^^^^^^^^^^^^^^^^^^^
The best q-value among the PSMs with a q-value <= the specified PSM-level cutoff that identified this peptide.

View PSMs
=========================
All PSMs with a q-value <= the specified PSM-level cutoff may be viewed for a peptide by clicking on the table row
containing that peptide.

.. image:: /images/peptide-page-view-psms.png

Columns
-------------------------
The PSMs appear in a table with the following columns:

Scan Num.
^^^^^^^^^^^^^^^^^^^^^^^^^
The scan number from the spectral file (e.g., mzML file)

Charge
^^^^^^^^^^^^^^^^^^^^^^^^^
The predicted charge state of the precursor ion.

Obs. m/z
^^^^^^^^^^^^^^^^^^^^^^^^^
The observed m/z of the precursor ion.

RT (min)
^^^^^^^^^^^^^^^^^^^^^^^^^
The retention time in minutes.

Scan Filename
^^^^^^^^^^^^^^^^^^^^^^^^^
The filename of the scan file.

Q-value
^^^^^^^^^^^^^^^^^^^^^^^^^
The q-value for the PSM.

PEP
^^^^^^^^^^^^^^^^^^^^^^^^^
The posterior error probabiliy for this PSM, if available.

SVM Score
^^^^^^^^^^^^^^^^^^^^^^^^^
The support vector machine score for this PSM, if available.

View Spectra
-------------------------
The annotated mass spectrum may be viewed for any PSM by clicking the "View Spectrum" link. For help on our
spectrum viewer, see the :doc:`/using/spectrum-viewer` page.

Sort Data
=========================
All column headers may be clicked to toggle between ascending and
descending sorting of that column. Holding the shift key while clicking column headers allow sorting on
multiple columns.

Download Data
=========================
Clicking the [Download Data] link will download the shown data as a tab-delimited text file.
