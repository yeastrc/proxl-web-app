===========================================
ProXL Configuration
===========================================

To change ProXL configuration, log in and click the gear-shaped icon in the top-right of the site:

.. image:: /images/gear-icon-pointer.png


Configuration options:
==========================================================


Allow Account Registration WITHOUT Invite
---------------------------------------------------------
By default, only users invited to projects by existing users of ProXL
may create accounts. If this check box is checked, any user visiting
your ProXL server will be able to register and create accounts.

Google Recaptcha (Not used if either not configured):
----------------------------------------------------------
Google reCAPTCHA is a free service that verifies that a human is interacting with
your site instead of a robot. More information here: `<https://www.google.com/recaptcha/>`_ If you
allow account registration without an invite, and if both values
below are set, a reCAPTCHA block will be presented to users who register.

Site key
^^^^^^^^^^^^
Upon registering your site with reCAPTCH, enter your site key here.

Secret key
^^^^^^^^^^^^^^^^
Upon registering your site with reCAPTCH, enter your secret key here. Do not share this.

    
HTML to put at center of bottom of web page
----------------------------------------------------
This line of HTML will appear in the bottom center of the footer throughout ProXL. For example:
``Managed by Joe McDowadle (<a href="mailto:jmcdow@university.edu" target="_top">jmcdow@university.edu</a>)``


From Address for emails sent
-----------------------------
All emails sent from ProXL will be sent from this email address. For example: ``do_not_reply@university.edu``.

SMTP Server URL for emails sent
--------------------------------
The SMTP server to use for sending emails. This is typically ``localhost`` on a Linux server. A SMTP server is
also typically provided by your institution, possibly as ``smtp.university.edu``--please consult your local IT
support resources if you have questions. **Note:** Functions in ProXL, such as inviting users to projects or
resetting forgotten passwords, will not work unless this configuration option is set correctly.

Google Analytics Tracking Code
-------------------------------
Google Analytics provides statistics about visitors to your web site. For more information visit: `<http://analytics.google.com/>`_.
Once you have signed up and registered your website with Google Analytics, enter your tracking code here to track visits to your
installation of ProXL.

Protein Annotation Service URL
--------------------------------
This is the URL to the PAWS service that provides protein sequence annotations (such as disordered region
predictions or secondary structure predictions) for supplied protein sequences. This service is
used on the protein image view to retrieve the displayed annotation information. Currently, the
YRC provides this service and it is not recommended that you change this setting.

Protein Listing Service URL
-------------------------------
This is the URL used to try to find more widely-recognized names for proteins found in experiments, regardless
of what naming database was used to generate the respective FASTA file used in a search. This name appears when
mousing over protein names in ProXL. By default, this is set
to the YRC PDR's web service designed for this purpose. It is not recommended that you change this
setting. Making this field blank will remove the tooltip from appearing when mousing over protein names in
ProXL.
