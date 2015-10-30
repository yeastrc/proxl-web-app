====================================
Merged Peptides View Page
====================================

.. image:: /images/merged-peptide-page.png

To reach this page, select multiple searches on the project page and click
"View Merged Peptides". (See :doc:`/using/project`.) This page combines and collates
the data from multiple searches and presents the results as an interactive table.
The searches do not need to be from the same software pipeline. For example,
different versions of the same program may be compared, or the results from
entirely different programs (e.g., Kojak and XQuest) may be compared. Currently,
the total number of merged runs must be 2 or 3 and must be from the same
project. Note, for the peptide view page seen when viewing a single search,
see :doc:`/using/peptide`.

Search List
=========================
The list of merged searches is presented below the top navigation. Each search
is shown next to its assigned color for the page, and the color referencing
this search is retained in the Euler diagram and in the peptide table. Clicking the
[+] icon will expand that search to view details:

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

PSM q-value cutoff
-------------------------
Only peptides that were identified by at least one PSM with a q-value <= to the value
specified will be shown.

Peptide q-value cutoff
-------------------------
Only peptides that have a peptide-level q-value <= the value specified will be shown.
As not all software produces peptide-level q-values, this field will have no effect
on those data.

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

Euler diagram
======================================
.. image:: /images/merged-peptide-euler-diagram.png

The Euler diagram (similar to a Venn diagram) provides a graphical depiction of the overlap
between the peptides found in the merged searches. The colors in the diagram match
the colors used for the search list above. The search list is provided  to the
left of the diagram with their associated colors as a legend. The labels for each
color include the search ID number and the number of distinct peptides found in each
of the merged searches. The total number of peptides resulting from the merge is presented
in the header above the legend next to "Peptides".

The "[Download Data]" link in the legend header will download the data in the table as a
tab-delimited text file.

Table Description
=========================
The table presents columns describing the peptides and indicates in which of the merged searches
the peptides were found. There is one row per peptide. A peptide on this page is defined as the
unique combination of peptide sequence(s), link positions in those peptides, and dynamic modifications
present on the peptides. So an unmodified peptide and a modified peptide with the same sequence will
appear as separate rows in the table. Each row in the table may be clicked on to expand and view
the peptide-level statistics for the given peptide from each search. Each of these searches may
then be clicked on to view PSMs and spectra from those searches.

Columns
-------------------------
The columns are described below. Note that all column headers may be clicked to toggle between ascending and
descending sorting of that column. Holding the shift key while clicking column headers allow sorting on
multiple columns.

Search Columns
^^^^^^^^^^^^^^^^^^^^^^^^^
The first 1-3 columns will be labeled with search ID numbers as headers, and provide an indication for
whether or not the peptide in that row was found in that search. If found in that search, the cell for
this search in this row will be shaded the same color associated with that search in the Euler diagram
and search list at the top of the page. The column will also contain an asterisk. If not found, this
cell is empty.

Searches
^^^^^^^^^^^^^^^^^^^^^^^^^
The number of the merged searches that contain this peptide. The [+] icon indicates that the row may be clicked on to
be expanded to show underlying searches in which this peptide as found, the stats for this peptide from each
search, and the ability to view PSMs and associated spectra.

Type
^^^^^^^^^^^^^^^^^^^^^^^^^
The type of peptide (crosslink, looplink, or unlinked).

Peptide 1
^^^^^^^^^^^^^^^^^^^^^^^^^
The parsed sequence of the peptide (or the first peptide in the case of crosslinks).

Pos
^^^^^^^^^^^^^^^^^^^^^^^^^
The position in that peptide containing the linker.

Mods
^^^^^^^^^^^^^^^^^^^^^^^^^
A comma-delimited list of dynamic modifications found for peptide 1 in the form of position(mass).
E.g., 17(15.99), 20(14.02)

Peptide 2
^^^^^^^^^^^^^^^^^^^^^^^^^
The parse sequence of the second peptide in the crosslink.

Pos
^^^^^^^^^^^^^^^^^^^^^^^^^
The position in that peptide containing the linker.

Mods
^^^^^^^^^^^^^^^^^^^^^^^^^
A comma-delimited list of dynamic modifications found for peptide 2 in the form of position(mass).
E.g., 17(15.99), 20(14.02)

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

Best Q-value
^^^^^^^^^^^^^^^^^^^^^^^^^
The best peptide-level q-value from the searches, if available.

# PSMs
^^^^^^^^^^^^^^^^^^^^^^^^^
The total number of PSMs from the searches for this peptide that have a q-value <= the specified PSM-level cutoff that identified this peptide. Note: click
the table row containing the peptide to see all the PSMs.

View PSMs
=========================
To view PSMs for a given peptide, first click on a row in the table to expand and view the peptide-level statistics for a given
peptide from each search in which it was found (at the given cutoffs). Each of these searches may be clicked to expand and view all
PSMs with a q-value <= the specified PSM-level cutoff. 

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
Clicking the [Download Data] link in the header of the Euler diagram will download the shown data as a tab-delimited text file.