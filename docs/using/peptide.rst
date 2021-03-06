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
uploaded into proxl. "FASTA file" is the name of the FASTA file used to perform the
PSM search.

General Options
============================

Change Searches
---------------------
.. image:: /images/peptide-page-change-searches-link.png

The "Change searches" link allows the user to change which searches are currently being displayed. Clicking the link causes the following overlay to be displayed:

.. image:: /images/change-searches-overlay.png

Select or de-select searches by clicking on them in the list. Once done, click "Change" to update the page with the new data or "Cancel" to close the overlay.


Update From Database
---------------------
.. image:: /images/peptide-page-button-update.png

If the user changes any filter parameters--such as PSM/peptide score cutoffs--this button must be clicked to reflect the new filter choices.

Save as Default
--------------------
.. image:: /images/peptide-page-button-save-as-default.png

Project owners may click "Save as Default" to save the current URL as the default
view of the "Peptide View" for this search. This default view will be populated with the same
options as when the button is clicked. This is a convenient
way to share data with collaborators or the public that does not require that they
manipulate the image viewer to see the data.


Share Page
--------------------
.. image:: /images/peptide-page-button-share-page.png

Clicking the "Share Page" button will generate a shortcut URL for viewing the current page. The shortened URL will appear in an overlay as:

.. image:: /images/share-page-overlay.png

Copying and sharing the highlighted URL will direct users to the view of the page when the URL was generated. Note that this
URL does not grant access to the page to any user that would not otherwise have access.

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
the cutoff values to be used for any of these score types, enter the value next to the score type. proxl will correctly
handle scores for which larger values are more significant or scores for which smaller values are more signiciant.

.. image:: /images/filter-change-psm-filter2.png

To save the new values to the page, click the "Save" button. To cancel, click "Cancel".

The "Reset to Defaults" button will reset the cutoff values to the defaults specified by the proxl XML file uploaded
to the database. This typically represents the suggested cutoffs by the author of the respective search program.

*Important*: It is necessary to update the data on the page after changing filter cutoff values. After clicking
the "Save" button, you must click the "Update" button on the page to apply any new PSM- or peptide-level
filters.

.. image:: /images/filter-update-from-database.png


Peptide Filters
-----------------------
The filters to apply at the peptide level. Only results which have at least one peptide that meets all of the selected
critiera will be listed.

To change the peptide-level filters, first click the pencil icon next to "Peptide Filters":

.. image:: /images/filter-change-peptide-filter1.png

This opens an overlay with the containing the possible score types to use as peptide-level filters for this search. To change
the cutoff values to be used for any of these score types, enter the value next to the score type. proxl will correctly
handle scores for which larger values are more significant or scores for which smaller values are more signiciant.

.. image:: /images/filter-change-peptide-filter2.png

To save the new values to the page, click the "Save" button. To cancel, click "Cancel".

The "Reset to Defaults" button will reset the cutoff values to the defaults specified by the proxl XML file uploaded
to the database. This typically represents the suggested cutoffs by the author of the respective search program.

*Important*: It is necessary to update the data on the page after changing filter cutoff values. After clicking
the "Save" button, you must click the "Update" button on the page to apply any new PSM- or peptide-level
filters.

.. image:: /images/filter-update-from-database.png


Type filter
-------------------------
Only peptides of the checked type(s) will be returned. Proxl defines the types as:

	* crosslink - A pair of peptides linked by a crosslinker.
	* looplink - A single peptide with two residues linked by a crosslinker.
	* unlinked - The peptide without a crosslinker on any residue.

Checking multiple boxes will include any peptide that has at least one of the checked types.
I.e., checking 'crosslinks' and 'looplinks' will only include peptides that are either
crosslinks or looplinks. Only checking 'crosslinks' will only return crosslinked peptides.

Modification filter
-------------------------
Only peptides with at least one of the checked modifications will be included. Note that monolinks
are considered modifications of residues in proxl, so the mass of the crosslinker when found
on monolinks is included here.

Update
-------------------------
In order to apply new filter parameters to the shown data, the "Update" button must be clicked. This will
fetch filtered data from the proxl server and display the data on the web page.

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

Peptide-level Scores
^^^^^^^^^^^^^^^^^^^^^^^^^
If peptide-level scores are available for this search, the scores will appear as separate columns.

Best PSM-level Scores
^^^^^^^^^^^^^^^^^^^^^^^^^
Columns will appear for each PSM-level score on which the results are currently being filtered. Each of these columns will
show the best PSM-level score for each respective PSM-level filters. E.g., if p-value is being used as a PSM-level score,
the best PSM p-value will be displayed for each peptide.

View PSMs
=========================
All PSMs meeting the current filtering criteria that map to a given peptide can by shown by
clicking on the table row containing that peptide.

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

PSM-level scores
^^^^^^^^^^^^^^^^^^^^^^^^^
Each PSM-level score will appear as a separate column.

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
