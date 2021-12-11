===========================
Proxl SMTP Setup Guide
===========================

This guide assumes you have completed all the steps in the :doc:`intro` tutorial.

Setting up SMTP allows proxl to send emails to users. This enables you to invite researchers
to projects using their email, users to reset forgotten passwords and to receive notifications
when their file uploads are complete. Setting up SMTP is not required to run proxl, but these
features will not be available.

1. Acquire SMTP Relay Server Information
===========================================
This is, potentially the most complex part of enabling SMTP in proxl. Acquiring SMTP relay server information
means finding the **host name**, **port**, **username**, and **password** to use to send email through an email server.
Below we present some options on how to locate or set up SMTP relay server information.

Check With Your Organization
----------------------------
The best place to begin this process is checking with your organization's IT department or your internet service
provider to see if this service is available to you. If it is, it would potentially look something like:

.. list-table:: Hypothetical SMTP Relay Server Information
   :widths: 25 25
   :header-rows: 0

   * - Host Name
     - smtp.organization.com
   * - Port
     - 587
   * - Username
     - my_username
   * - Password
     - my_password

SendGrid
---------
SendGrid is a free service that allows applications like proxl to send emails through their servers. The free
tier allows you to send up to 100 emails per day. Here are the rough steps for setting up email sending
capabilities:

  1. `Go to https://sendgrid.com/ <https://sendgrid.com/>`_.
  2. Create a free account
  3. Establish a single sender, which is an email address from which you will be allowed to send emails through
     SendGrid. SendGrid will require that you verify you can receive emails at this address.  **This does not
     give SendGrid access to your email.** It only allows you to send email **from this address** using
     SendGrid's servers. `See SendGrid's Guide for Single Sender Verification <https://sendgrid.com/docs/ui/sending-email/sender-verification/>`_.
  4. Click on the ``Email API`` navigation option on the left and choose ``Integration Guide``. Click on the ``SMTP Relay``
     option that appears on the page.
  5. Follow the directions to acquire your SMTP relay server information.

Your SendGrid SMTP relay server information will look something like:

.. list-table:: SendGrid SMTP Relay Server Information
   :widths: 25 25
   :header-rows: 0

   * - Host Name
     - smtp.sendgrid.net
   * - Port
     - 587
   * - Username
     - apikey
   * - Password
     - YOUR_API_KEY

Google SMTP Relay
-----------------
Google allows your Google Workspace account to use their servers as a SMTP relay. To enable this,
`follow Google's Guide for setting up SMTP relay <https://support.google.com/a/answer/2956491>`_.
In the ``Authentication`` section, you want to enable ``Require SMTP Authentication``.

Your Google SMTP relay server information will look something like:

.. list-table:: Google SMTP Relay Server Information
   :widths: 25 25
   :header-rows: 0

   * - Host Name
     - smtp-relay.gmail.com
   * - Port
     - 587
   * - Username
     - Google username
   * - Password
     - Google password

Other Options
-----------------
There are other service on the internet that provide SMTP relay server information. Any of them should work, so
long as you have a **host name**, **port**, **username**, and **password**.

2. Update Your ``.env`` File
===========================================
The ``.env`` file you set up during the :doc:`intro` tutorial should contain the following lines
(among others):

    .. code-block:: none

        # Settings for setting up sending of emails by proxl
        SMTP_HOST=smtp.example.com
        SMTP_PORT=587
        SMTP_USERNAME=smtp_username
        SMTP_PASSWORD=smtp_password

Open this file using your favorite text editor. On Linux (including Docker on Windows), we'll assume
that is ``nano``. To edit the file, type:

    .. code-block:: bash

       # ensure you are in correct directory. if you followed tutorial type:
       cd ~/proxl

       # edit the file
       nano .env

Update these lines to reflect the SMTP relay server information from part 1. If you used SendGrid for your SMTP
relay server, your information would be something close to:

    .. code-block:: none

        SMTP_HOST=smtp.sendgrid.net
        SMTP_PORT=587
        SMTP_USERNAME=apikey
        SMTP_PASSWORD=your API KEY goes here


Type ``Control-o``, ``<ENTER>``, and ``Control-x`` to save and exit ``nano``.



3. Update Email Address for Sender in proxl
================================================
    1. Log into proxl and click the ``Manage Proxl Configuration`` icon in the top right (shaped like a gear). You must be logged in as an administrator user, such as the initial user created when you followed the :doc:`intro` tutorial.

    2. Edit the field for ``From Address for emails sent``. This is the email address from which emails sent by proxl will appear to come. You may be restricted by what email address you can use here by the SMTP server you are using. For example, if you set up SMTP relay service with SendGrid, this email must match the verified sender you set up.

    4. Click the ``Save`` button to save the changes.


4. Restart proxl
=====================
Proxl must be restarted to use the new configuration settings in the ``.env`` file. Type the following into your terminal to restart proxl:

    .. code-block:: bash

       # ensure you are in correct directory. if you followed tutorial type:
       cd ~/proxl

       # shutdown proxl
       sudo docker-compose down

       # startup proxl
       sudo docker-compose up --detach

5. Investigating Problems
==========================
If after following this guide, emails are not being sent, you can view the logs of the SMTP server by typing
the following into a terminal:

    .. code-block:: bash

       sudo docker logs proxl-smtp

Carefully read this log and look for error messages, such as an authentication failure or other reasons
the message may have been rejected.
