==================
Proteins View Page
==================

.. image:: /images/protein-page.png

The protein view page provides a table view of crosslinks or looplinks at the protein level.
Each row in the table corresponds to a unique crosslink ( specific position in protein A linked
to a specific position in protein B) or a unique looplink (specific  pair of positions
in a protein). The data may be filtered according to confidence, taxonomy, or individually
by protein. For the view page seen when merging multiple searches, see :doc:`/using/merged-protein`.

**Note**: If any identified peptides map to multiple proteins, those proteins are listed here
as separate rows. For example if peptide 1 is linked to peptide 2, and peptide 1 maps to
protein A and peptide 2 maps to proteins B and C, rows will be present for A-B and A-C.
This may dramatically increase the number of reported crosslinks if your
protein database is redundant in terms of homologous proteins or proteoforms or if small
peptides are mapping to many proteins. The filtering options described below are meant to
help eliminate this redundancy in reported proteins.

View Looplinks
=========================
By default, the table shows crosslinks. To switch to looplinks, click the [View Looplinks]
link at the top of the table. To view crosslinks again, click the [View Crosslinks] link
that appears at the top of the table.

.. _download-udrs-label:

Download Data
=========================
All crosslinks and looplinks that meet the current filtering criteria may be downloaded
as tab-delimited text by cliking the [Download Data (#)] link above the table. # indicates
the number of rows in the table.

.. _download-data-label:

Download UDRs
=========================
UDR stands for "unique distance restraint", which takes its name from 3D modelling
terminology. A UDR, in ProXL, is any specific position in a protein linked to a
specific position in another protein, whether it is a crosslink or a looplink. The
[Download UDRs (#)] link downloads a non-redundant tab-delimited text table of these UDRs consolidated
from the crosslinks and looplinks. The # is the number of UDRs.

Search Information
=========================
The name of the search (and internal search ID reference number) from which these
data were obtained is shown first. The red [+] icon may be clicked to reveal more
information about the search, including the path the data were imported from,
the linker that was used, the upload date, and the FASTA file that was searched.

Filter Data
=========================
The data presented may be filtered according to the following criteria. Note: Only crosslinks
or looplinks that meet ALL the filter criteria are shown.

PSM q-value cutoff
-------------------------
Only proteins that contain peptides that were identified by at least one PSM with a q-value <= to the value
specified will be shown.

Peptide q-value cutoff
-------------------------
Only proteins that contain peptides that have a peptide-level q-value <= the value specified will be shown.
As not all software produces peptide-level q-values, this field will have no effect
on those data.

Exclude xlinks with
-------------------------
Crosslinks or looplinks that have any of the checked attributes will be excluded. The attributes are:

	* no unique peptides - If all peptides that ID either one of the crosslinked proteins also map to another protein
	* only one PSM - If a given crosslink or looplink was identified by a single PSM
	* only one peptide - If a given crosslink or looplink was identifed by a single peptide, where a peptide is the combination of sequence, linked positions, and modifications

Exclude organisms
------------------------
Any links containing a protein that maps to any of the checked organisms will be excluded. The list of
organisms presented was gathered by the proteins found in the search. Useful for filtering out
groups of contaminant proteins.

Exclude protein(s)
------------------------
Any links containing a any of the selected proteins will be excluded. Multiple proteins may be selected
or unselected using control-click (command-click on the mac) or shift-click. Useful for filtering
out individual contaminant proteins.

Update
-------------------------
In order to apply new filter parameters to the shown data, the "Update" button must be clicked. This will
fetch filtered data from the ProXL server and display the data on the web page.

Save As Default
--------------------------
Project owners may save the current filter parameters as the default view of the data on this page by
clicking this button. This default view will be shown when users follow links to the "Protein View" for
this search.

Table Description
=========================
Above the table is the text "Crosslinks (#)" or "Looplinks (#)". # is the number of rows in the table.
The [View Looplinks (#)] links will change to viewing looplinks, where # is the number of looplinks
that will be shown. The [Download Data (#)] link downloads the data as tab delimited text (see 
:ref:`download-data-label`) and [Download UDRs (#)] downloads the UDRs as tab delimited text (see :ref:`download-udrs-label`).


Columns
-------------------------
The columns are described below. Note that all column headers may be clicked to toggle between ascending and
descending sorting of that column. Holding the shift key while clicking column headers allow sorting on
multiple columns.

Protein 1 and 2 (Crosslink-only)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
In the case of crosslinks, these are the crosslinked proteins

Position (Crosslink-only)
^^^^^^^^^^^^^^^^^^^^^^^^^^
This is the crosslinked position in the respective proteins, where the
first residue is counted as position 1.

Protein (Looplink-only)
^^^^^^^^^^^^^^^^^^^^^^^^^
In the case of looplinks, this is the looplinked protein

Position 1 and 2 (Looplink-only)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
In looplinked proteins, these are the positions in the protein that are linked.

PSMs
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The total number of PSMs (peptide spectrum matches) meeting the cutoff that identified either crosslinked (crosslink view) or looplinked
(looplink view) peptides that mapped to the reported proteins and positions.

# Peptides
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The total number of identified crosslinked (crosslink view) or looplinked (looplink view) peptides
that mapped to the reported proteins and positions. Only peptides with a peptide-level
q-value <= the requested cutoff (if applicable) AND having at least one PSM having a
psm-level cutoff <= the requested cutoff are counted.

**Note**: The individual peptides may be viewed by clicking a row in the table to view a
table of peptides. Rows in that peptide table may also be viewed to view the underlying
PSMs and view spectra. See :ref:`protein-view-peptides-label`.

# Unique Peptides
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Of the # of peptides, the total number that uniquely mapped to this protein pair (crosslink view) or
protein (looplink view).

Best Peptide Q-value
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Of the peptides describe above, the best peptide-level q-value found for those peptides (if available).

Best PSM Q-value
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The best PSM-level q-value among the PSMs described above.

.. _protein-view-peptides-label:

View Peptides
=========================
All peptides that meet the q-value cutoffs that were mapped to a protein-level crosslink
or looplink may be seen by clicking on the respective row in the table. Additionally, all rows
of this peptide table may clicked to view all PSMs associated with that peptide identification. (See :ref:`protein-view-psms-label`.)

.. image:: /images/protein-page-view-peptides.png

Columns
-------------------------
The peptides appear in a table with the following columns:

Reported peptide
^^^^^^^^^^^^^^^^^^^^^^^^^
The peptide identificaton as it was reported by the respective search program.

Peptide 1 and 2 (Crosslink-only)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The sequences of the two crosslinked peptides.

Pos (Crosslink-only)
^^^^^^^^^^^^^^^^^^^^^^^^^
The positions in the respective peptides that were crosslinked (starting at 1).

Peptide (Looplink-only)
^^^^^^^^^^^^^^^^^^^^^^^^^
The sequence of the looplinked peptide.

Pos 1 and 2 (Looplink-only)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The positions in the peptide that were looplinked.

Q-value
^^^^^^^^^^^^^^^^^^^^^^^^^
The peptide-level q-value for this peptide identification (if available)

# PSMs
^^^^^^^^^^^^^^^^^^^^^^^^^
The number of PSMs that meet the cutoff criteria that identified this peptide.

.. _protein-view-psms-label:

View PSMs
=========================
All PSMs with a q-value <= the specified PSM-level cutoff may be viewed for a peptide by clicking on a row
in the peptide table that is shown when clicking a row in the main protein table.

.. image:: /images/protein-page-view-psms.png

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