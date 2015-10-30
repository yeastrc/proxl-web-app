Getting started using ProXL DB
===========================================

ProXL is a database and web application for viewing, analyzing, and sharing bottom-up
proteomics data resulting from chemical crosslinking and mass spectrometry
analysis. It includes tools for viewing, downloading, and visualizing data; including tools
for comparing data between different searches and pipelines.

Projects and Access Control
----------------------------------
Projects in ProXL are the core means of organizing data and controlling access to those
data. While projects serve to logically organize data by theme or aim,
they also serve to organize the data by who you would like to have access
to those data. All data are associated with projects and, by default, only the researchers
associated with a project may view data associated with that project. (These permissions
by be changed, see :ref:`public-access-label`.)


Gaining Access
---------------------------------
The only way to gain access to a ProXL installation is to be invited to a project by an
existing user. Only users associated with a project may invite users to that project.
(To learn how to invite a user to a project, see :ref:`invite-researchers-label`.)
Invitations will appear in your email with a link to register as a user of ProXL.
**Important**: Be sure to check your SPAM folder for ProXL invitations if they do not appear.

Unless you are already logged into ProXL, following the link in the invitation email
brings you to the following screen. (If you are already logged in, you will be taken to a list of
you projects, which now includes the one to which you were invited.)

.. image:: /images/register-screen1.png

Choosing "Sign In" will sign you into ProXL, and the new project will be listed in your
project list. Choosing "Create Account" will present you with the following registration form:

.. image:: /images/register-screen2.png

Filling out this form will create a new account, log you in, and display the project
to which you were invited in your project list.

Sign In
------------------------------------
Accessing the URL for any non-public data or the URL for the home page of ProXL will
produce the password prompt below. Provide your username and password to proceed.

.. image:: /images/signin.png

Forgot Password
-------------------------------------
If you are unable to remember your password, click the "Reset Password" button at the
bottom-right of the sign-in form. Entering either your username or email address on file
for your account will send a link to your email address that may be used to reset
your password. For security reasons, this link is only valid for 24 hours.

Accessing Projects and Data
------------------------------------
Once logged in, you will be presented with a list of projects that you may currently access.
For example:

.. image:: /images/project-list1.png

In this example, the user has access to three projects. Clicking on the titles of any of
these projects will navigate to that project and its data. From any page, the user may
also mouse over (or tap on mobile devices) the "Projects" text at the top page to
see a drop-down list of  project titles. Clicking on any of these will navigate to that
project's page and data:

.. image:: /images/project-list2.png

Return to Project List
------------------------------------
To return to the project list (required for adding or deleting projects), click the
"ProXL DB" icon or the "Projects" text at the top-left of any page in ProXL

Adding New Projects
------------------------------------
Click on "(+) New Project" above the project list to add a new project by
supplying a title and (optionally) an abstract for your project. Once the project
is added, it will appear in the project list with the supplied title. Click on the
project title to navigate to that project's overview page to manage access and
add researchers to the project. (See ":ref:`invite-researchers-label`.)


Deleting Projects
------------------------------------
Click the red (X) icon to the left of the project title on the project list to delete
that project. Only project owners may delete projects. Once a project is deleted,
the project and all associated data will be removed.

Get Help
---------------------------------
To view documentation for ProXL, click the (?) icon on the top-right nagivation
bar present on all pages.

.. image:: /images/get-help.png

Manage Account
---------------------------------
You may change your name, email address, organization, username or password by clicking
the manage account icon (gear shape) at the top-right of the page:

.. image:: /images/manage-account.png


Sign Out
----------------------------------
To securely sign out, click the sign-out icon at the top-right of the page and close
your web browser.

.. image:: /images/signout.png

Manage Users (Admin Only)
---------------------------------
If you are an administrator, you may manage or invite users by clicking the manage
user icon at the top-right of the page:

.. image:: /images/manage-users.png
