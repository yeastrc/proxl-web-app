=====================
Project Overview Page
=====================

Clicking on the title of a project on the project list or project pull-down list at the
top of the page will bring you to that project's overview page. **Important**: Only users
listed as researchers on the project may access this page. Example project overview page:

.. image:: /images/project-overview.png

From here, you may edit the project information, add or remove researchers, toggle
public access, explore or manage the data, and lock the project. Click the [+] icon
next to each section to expand and view that section. Please see below for
help on all of the sections.

Project Information
=========================
Project members may edit the title or abstract by clicking the pencil icon next to the
respective item. Project members may add notes to the project by clicking "[+Note]".
Notes may be edited or deleted by clicking on the associated pencil or delete (X) icons.
The title, abstract, and notes are visible to public users (if public access is enabled,
see below).

Lock Project
--------------------
The project may be locked by clicking the lock icon next to the "Project Information" section
header:

.. image:: /images/project-overview-lock-project1.png

Clicking the icon changes the icon to a locked state:

.. image:: /images/project-overview-lock-project2.png

Projects that are locked may not be changed in any way until they are unlocked. This includes
associated users, public access, uploading or annotating data, and so on. This is meant
to accompany projects that are supporting publications, and so should not change. To unlock a
project, the project owner may click on the lock icon next to the "Project Information" section
header.

.. _invite-researchers-label:

Researchers
=========================

.. image:: /images/project-overview-researchers1.png

This section defaults to not being expanded, click the [+] icon next to this section title
to expand it. This section lists the users associated with this project. Some important notes
about the researchers section:

	* Only users listed here may access the project overview page or the project's data. (Except if public access is enabled, see below.)
	* The "Researchers" section of this page is not visible to public users.
	* Any user may invite other users to the project.

Invite User
--------------------------
To invite a user to the project, click the "Invite User" text or associated icon at
the top of the user list. This will open the following dialogue.

.. image:: /images/project-overview-invite-user.png

For Existing Users
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
To invite an existing user to this project, type their last name or email address
into the respective text box. If they are found in the database, their name will
appear as you type:

.. image:: /images/project-overview-invite-user2.png

Clicking on the name produces:

.. image:: /images/project-overview-invite-user3.png

Here you may designate their level of access (if you are a project owner) and click
"Invite User" to invite that user to the project. The user will immediately have
access to the project, and this project will appear in their list of projects. 
Alternatively, click "Cancel" to cancel the process.

For New Users
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
To invite a new user to ProXL and provide them access to this project, type in their
email address and click "Invite User". They will receive an email with a link for
creating an account in ProXL and this project will appear in their project list. New
users invited to the project will appear as:

.. image:: /images/project-overview-invite-user4.png

Once they have created an account, their name will appear in the user list instead
of an email address, and the "Invited on..." text will no longer be present.

Remove User
--------------------------
Click the red (X) icon to the left of a listed user to remove that user from this
project. That user will immediately lose access to the project and its associated
data. This will not remove existing users, only revoke their access to this project.
For invited email addresses, this will invalidate the access code included in the
invitation email and they will not be able to use it to create a ProXL account.

Promote or Demote User
--------------------------
Clicking the up or down arrow next to the access level of a user will either promote
that user to an owner or demote that user to a researcher. Owners have complete
complete access to a project, including the ability to lock or unlock it, enable or
disable public access, promote or demote other users, define default views of data,
or delete data.


.. _public-access-label:

Public Access
=========================

.. image:: /images/project-overview-public-access.png

This section defaults to not being expanded, click the [+] icon next to this section title
to expand it. This section controls whether or not public access is enabled and, if it is,
whether or not a public access code is required to access the project and its data. 
Some important notes about the public access section:

	* Only project owners may change public access settings.
	* The "Public Access" section of this page is not visible to public users.

Enable or Disable Public Access
--------------------------------
Enabling public access allows access to the data without requiring that users
have ProXL accounts. Clicking "Enable Public Access" enables public access and changes
the display of this section to indicate that public access is enabled.

.. image:: /images/project-overview-public-access2.png

Public Access Code
^^^^^^^^^^^^^^^^^^^^^^^^^^
By default, public access is enabled in a way that requires a specially-formatted URL that contains
an unguessable public access code. This URL is listed here as "Project public access URL."
This exact URL must be used to access the project before the user may access any of the data. This
is useful for semi-private sharing of data with select users (or reviewers) without making the
data completely publicly accessible.

The requirement for the public access code may be removed by clicking "No" next to "Require public
access code." If "No" is selected, URLs for the project or any of the data pages may be directly
shared without the need of the user to first use the public access code. This is useful for truly
public sharing of the data, such as in the case of publication.

The "Generate New Public Access Code" button will generate and replace the current unguessable
public access code with a new code. This will revoke access to users that have used the
previous code.

Lock Public Access
^^^^^^^^^^^^^^^^^^^^^^^^^^
Clicking the "Lock Public Access" button makes it impossible to change public access code settings
without first clicking "Unlock Public Access." This is meant to prevent accidental disabling of
public access or generation of new public access codes, which would revoke previously-granted
access to the public, colleagues or reviewers.

Explore Data
=========================

.. image:: /images/project-overview-explore-data.png

This section lists each of the searches associated with this project. A "search" in this context
is a run of Kojak, XQuest or some other peptide-spectrum match software pipeline run against spectral
data. Project owners may change the name of these searches by clicking the pencil icon to the
right of the current search name. To the right of the search name the search ID number is listed
in parentheses as a standard way to refer to specific searches.

View Search Information
--------------------------------
To view information about a search, click the [+] icon to the left of the search name. This will
display the following information. (Alternatively, click the "Expand All" button at the top of the
to see all information about all searches.)

.. image:: /images/project-overview-explore-data2.png

Search information includes the following information for each search:

	* Path - The path the data were in when imported into ProXL.
	* Linker - the crosslinker(s) used in this experiment
	* Upload - the date the data were uploaded to ProXL
	* QC Plots - links to a retention time QC plot. See below.
	* Raw MS data files - Links to RAW files that contain the raw machine output for this experiment. Project owners may add URL links to RAW files by clicking [+Link to Raw file]. 
	* Additional files - Links to additional files associated with this search, such as the configuration or parameters files for the respective search program.
	* Comments - Lists the comments that have been added to the search. Comments may be deleted by clicking the red (X) to the left of the comment, or edited by clicking the pencil icon to the right.

Retention Time QC Plot
^^^^^^^^^^^^^^^^^^^^^^^^^^^
Clicking the "[Retention Time]" link next to "QC Plots:" produces the retention time QC plot:

.. image:: /images/project-overview-rt.png

This is a histogram showing the number of MS2 scans taken versus retention time. The pink bars
show all scans, and the dark red bars show the number of those scans that meet our filtering
criteria at the top of the plot--or, "Filtered PSMs". To close the chart, click the "X"
in the top right of the window, or anywhere in the browser outside the chart window.

The chart options are:
	* Scan File - If multiple spectral files were searched, each will be listed here. The data in the chart reflect the selected spectral file.
	* PSM Q-value cutoff - Counts for "Filtered PSMs" will only include scans that resulted in a PSM with this q-value or lower.
	* Scans with - Counts for "Filtered PSMs" will only include scans that resulted in a PSM where the peptide was of a type that is checked here. E.g., if only "crosslinks" is checked, only scans that resulted in crosslinked peptides will be used to for "Fitlered PSMs" counts. If "looplinks" and "crosslinks" are checked, only PSMs resulting in crosslinked or looplinked peptides will be counted.
	* Max - Values entered here will be the maximum value on the X or Y axis--used for rescaling the chart.

Cumulative PSM Count vs/ Q-value QC Plot
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Clicking the "[PSM Q Values]" link next to "QC Plots:" produces a plot of cumulative PSM count vs/ q-value cutoff:

.. image:: /images/project-overview-psm-count-over-qvalue-qc.png

Q-value cutoff is presented on the x-axis, and y-values are the total number of PSMs found in this search with that q-value or lower. Each class
of PSM is presented as a separate line: crosslinks, looplinks, and unlinked. A line for all PSM types combined may optionally be displayed by checking "all"
in the "PSMs with:" options. 

The "View as:" option allows switching between raw counts (default) and percentage. The "raw counts" option uses the raw PSM counts for the respective types.
The percentage option displays the PSM counts as the percentage the total PSMs of the respective type, and so applies the same scale to all lines, which will
always move from 0 to 100 in the plot.

The "PSMs with:" options toggle the visibility of the lines corresponding to the respective class of PSM.

The "Max:" options allow for a user-supplied maximum for the X- and Y-axes. "Reset" clears the user-supplied maximum values.


View Data
--------------------------------
.. image:: /images/project-overview-explore-data3.png

Use the links to the right of the search names (in red box above) to view the data. There are four views currently available:

	* **Peptides** - Provides a table view of the identified peptides and associated data. See :doc:`peptide` for more details.
	* **Proteins** - Provides a table view of the crosslinks and looplinks at the protein level. See :doc:`protein` for more details.
	* **Image** - Provides a graphical view of the data where proteins are represented as proportionately-sized bars that are annotated with link locations within and between proteins in the context of sequence annotation data. See :doc:`image` for more details.
	* **Structure** - Provides a view of crosslinking data on 3D protein structures, including an interface for uploading PDB files and mapping sequences from the FASTA file onto those PDB files using pairwise sequence alignment. See :doc:`structure` for more details. 


View Merged Data
--------------------------------
To compare and contrast data between different searches


Move Data
--------------------------------
Project owners may move searches to a different project (where they must also be an owner). To move data,
check the checkbox to the left of the search name(s) you wish to move and click the "Move Searches"
button above the search list to see the following dialogue:

.. image:: /images/project-overview-move-search.png

Projects to which you have permission to move data are listed. Selecting one will move the selected
searches to that project. The searches will no longer be visible for the previous project. Selecting
"Cancel" will cancel the move with no changes.

Delete Data
--------------------------------
Project owners may delete searches by clicking the red (X) icon to the right of the view data
links. This will remove all data associated with that search from the database--use with care.


