===================
Structure View Page
===================

.. image:: /images/structure-page.png

The structure viewer allows users to upload PDB files (including
multi-protein complexes) from any source, align protein sequences
from their experiment to sequences in those PDB files, and then
visualize and interact with the crosslinking results on fully interactive
3D structures. The structure viewer includes multiple filtering and
display options, distance reporting, and users may download the data for use in
other structure visualization software or as high-quality raster
images.

Viewer Basics
============================

URL Captures State
--------------------
The URL of the page is dynamically updated at all times to reflect the state of
the viewer--including filter parameters, selected PDB alignments, and all viewer options.
As such, the URL may be bookmarked or shared to link to a viewer with the same content
and appearance as the current viewer--just copy and paste from the address bar. (The current
rotation and zoom-level of the structure is not currently captured by the URL.)
For complicated visualizations, this is a convenient way to save and share you work. Note,
this link only works for other users who are listed on this project unless public access is enabled.

Save as Default
--------------------
Project owners may click "Save as Default" to save the current URL (see above) as the default
view of the "Structure View" for this project. This default view will be populated with the same
proteins and viewer options as when the button is clicked. This is a convenient
way to share data with collaborators or the public that does not require that they
manipulate the image viewer to see the data.

Viewer Display
------------------
The viewer is separated into two side-by-side panels. The left panel contains an
interactive, 3D rendering of the PDB structure file, link locations from the
experiment, and other sequence annotations for those proteins. The right panel
contains either a report of the distances of the currently-rendered links (see :ref:`structure-distance-report-label`) or a
form for choosing which protein-PDB chain alignments are currently being
drawn (see :ref:`structure-pdb-chain-to-protein-map-label`).

.. image:: /images/structure-panels-overview.png

Crosslinks (and looplinks, if enabled) are displayed as rods connecting two points in the protein structure. By
default, the color of these links is determined by the distance between the two points they connect. (This coloring
scheme may be changed, see :ref:`structure-color-option-label`.) Monolinks are depicted as short rods connected to
the structure on only one end. By default, "linkable" positions are shown as black spheres placed on the alpha carbons
of residues in proteins that may react with the crosslinker used in the experiment.

.. image:: /images/structure-viewer-diagram.png

.. _structure-mapping-residue-position-to-3D-label:

Mapping Residue Position to 3D Space
-------------------------------------
After a PDB file is uploaded, proteins found in the experiment may be mapped to chains from the PDB file
based on sequence (see :ref:`structure-map-pdb-to-proteins-label`). This process creates a mapping of
residue positions in proteins found in the experiment to residue positions in the PDB structure. This mapping
is very rarely 1 to 1, that is, the sequences of the protein and the PDB chain may be different--containing
variation, insertions, or deletions. For example, view the following alignment between Skp1dd and
chain C from 4I6J.pdb:

.. image:: /images/structure-show-alignment.png

Note that position 1 in Skp1dd does not map to any position in chain C of the PDB file. Position 3 in
Skp1dd maps to position 1 in chain C. Position 4 in Skip1dd maps to position 2, Position 5 maps to position
3, and so on. All insertions and deletions are taken into account to create a mapping for every position between
the two sequences.

So, then, when drawing (or measuring distances between) positions that correspond to specific residues in
experimental proteins, this mapping is used to lookup the corresponding
positions in the PDB chain sequences. If the positions map to the PDB, the 3D coordinate position of the alpha carbon
in the corresponding PDB residue is used as the location of the residue in the rendered structure. If
the positions do not map to the PDB, they are never drawn or measured.

.. image:: /images/structure-alignment-figure.png

In ProXL, links that map to the
structure on both ends are said to be "mappable". The distance report displays the number of mappable links
from the experiment out of the total number of observed links. See :ref:`structure-distance-report-label` for
more information.


Uploading PDB Files and Mapping Proteins
====================================

.. _structure-upload-pdb-label:

Upload PDB File
--------------------------------------
A PDB file in ProXL is any file adhering to the `PDB file format <http://www.wwpdb.org/documentation/file-format>`_. These
include public PDB files downloaded from the PDB database, or structures you have generated yourself using any number of programs--
as long as they adhere to the PDB file format. The PDB file may contain structures for multiple proteins (such as for a protein complex).
PDB files you upload to ProXL are only visible to members of the project with
which the data are associated. (If you enable public access on the project, the PDB file will be visible to public access users as well.)

To upload a PDB file, click the "+Upload PDB File" link next to the PDB file pull-down menu above the viewer:

.. image:: /images/structure-upload-pdb1.png

This will open a dialog for uploading a PDB file. Click the button next to "Select PDB File" to select a PDB file on your computer:

.. image:: /images/structure-upload-pdb2.png

Enter a brief description for your PDB file and click "Upload PDB File." The PDB file will then be visible in the PDB File pull-down menu.

.. _structure-map-pdb-to-proteins-label:

Map PDB Chains to Proteins
----------------------------------------
In order to calculate distances or view crosslinking data on a 3D structure, it is necessary to first map proteins from the experiment to sequences present in a PDB file.
(To learn more about how ProXL uses this mapping to find 3D positions, see :ref:`structure-mapping-residue-position-to-3D-label`.) The sequences in the PDB file do not need
to be 100% matches to the protein sequences in your experiment, and may contain insertions or deletions. However, the quality of the reported distances and visualized
links depends on the matches being close. It is also not necessary to map proteins to all chains in the PDB file.

To perform this mapping, first select a PDB file in the PDB file pull-down menu. This will display the 3D structure from the PDB file in the
left panel and, for proteins with no mapping, a "PDB Chain to Protein Map" in the right panel with no proteins listed for any of the chains.

.. image:: /images/structure-map-proteins-to-chains1.png

To begin, click the "[Map Protein]" link next to the desired PDB chain. This will open the following window:

.. image:: /images/structure-map-proteins-to-chains2.png

The panel to the left shows the structure from the PDB file, with the currently-selected chain highlighted in red. This rendering may be rotated and zoomed just as
the main visualization, see :ref:`structure-manipulation-label`. To the right is a pull-down list of all proteins found in the experiment. Click the one to be
mapped to the currently-selected chain and click the "Map Protein to Structure" button. This will perform a local pairwise sequence alignment on the experimental
protein's sequence and the sequence for the selected chain from the PDB file and present the results in the window:

.. image:: /images/structure-map-proteins-to-chains3.png

**This is an example of a bad match.** The pairwise sequence alignment will always be successful, even in the case of poor matches. In the example above, "Fbxl3-human" was chosen and a sequence
alignment was performed against chain A from 4I6J.pdb. Note that very few residues match between the alignments, and there are many insertions and deletions
present. The horizontal scroll bar present beneath the alignment may be used to view the entire alignment. To reject
this alignment, click "Cancel" to map a different protein.

.. image:: /images/structure-map-proteins-to-chains4.png

**This is an example of a good match.** In the example above, "mCRY2-1-544-mouse" was chosen as the experimental protein. All the matched residues are identical. There is a segment of sequence at
the N-terminus of the experimental protein that is not present in the PDB file. To accept this match, click "Save." The
mapped protein will now appear as associated with Chain A in the "PDB Chain to Protein Map":

.. image:: /images/structure-map-proteins-to-chains5.png

This process can be repeated for as many other chains as desired. Additionally, each chain may be associated with multiple proteins from the experiment--such as
in the case that homologs or proteoforms are present in the search results. However, only up to one protein listed under a given chain may be checked at any given time.

Then, to view the crosslinking results on the structure, check the desired protein chain alignments (check box next to a given protein listed under a given chain). To learn
more about the effects and implications of checking the alignments, see :ref:`structure-checking-and-unchecking-proteins-label`.

.. image:: /images/structure-map-proteins-to-chains6.png

Edit PDB Chain to Protein Mapping
------------------------------------------
Although not generally advised, there are two methods for editing the alignment between an experimental protein and a PDB chain. First, during the initial mapping process described above, instead
of clicking "Save" or "Cancel" on the final step, click "Edit." Or, after the mapping is complete, click the pencil icon next to the protein name associated with
a chain in the PDB and click "Edit" in the resulting window. Both methods will open the alignment edit window:

.. image:: /images/structure-edit-mapping.png

This interface consists of a simple text field containing the current pairwise sequence alignment, with the experimental protein on the top and the PDB sequence on the bottom.
This alignment may be manually edited, either in this window or in an external text editor and pasted back into this window. The length of both aligned sequences, including
the dashes (-) must be identical, and the sequences present for the experimental protein and PDB chain (without the dashes) must match the sequences on record. To cancel the process,
click "Cancel". To save the manual alignment, click "Save."

Viewer Interaction
============================

.. _structure-checking-and-unchecking-proteins-label:

PDB Chain to Protein Map
--------------------------------

.. _structure-distance-report-label:


.. _structure-interactive-structure-panel-label:

Interactive Structure Panel
--------------------------------

.. _structure-pdb-chain-to-protein-map-label:


Distance Report
--------------------------------

.. _structure-manipulation-label:

Structure Manipulation
--------------------------------



Viewer Options
============================

.. _structure-color-option-label:

Color links by
-------------------------


Filter Data
============================