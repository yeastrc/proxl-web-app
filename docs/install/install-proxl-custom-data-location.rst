===========================
Customize Data Locations
===========================

.. note::
    This tutorial assumes you have completed our :doc:`intro` tutorial through step 4.

By default, our installation tutorial will allow Docker to manage where proxl stores its data. This
includes things like where MySQL stores its data files, where uploaded scans are stored, and working
directories for processing uploaded data. On Linux (including Windows running Ubuntu), these data will
mostly likely be kept under ``/var/lib/docker/``.

It is recommended that you let Docker manage the data directories if you can. However, if you would like to
customize where the data are stored for proxl, follow the steps below.

1. Create data directories
================================================
You will need to create five directories for proxl to store its data.

    1. MySQL data directory. This is the directory used to store the database.
    2. Spectr upload directory. This the directory used for spectra processing.
    3. Spectr storage directory. This the directory used to store spectra.
    4. Proxl upload directory. This is the directory where uploads are temporarily stored.
    5. Proxl cache directory. Cache some results on disk to speed up the web site.

.. important::
    If you are using WSL2 on Windows, specifying a Windows filesystem drive (e.g., ``/mnt/d/``) for your
    data directories is not supported.

For example, if you would like store store all data in the ``/data/proxl-data`` directory, you would type
the following:

    .. code-block:: bash

        # make a parent directory for proxl data
        sudo mkdir -p /data/proxl-data

        # make the five directories for storing data
        sudo mkdir /data/proxl-data/mysql
        sudo mkdir /data/proxl-data/spectr-upload
        sudo mkdir /data/proxl-data/spectr-storage
        sudo mkdir /data/proxl-data/proxl-upload
        sudo mkdir /data/proxl-data/proxl-cache


2. Update ``.env`` with data storage locations
================================================
The ``.env`` configuration file will need to be updated to include the locations of the data directories.
Open this file using your favorite text editor. On Linux (including Docker on Windows), we'll assume
that is ``nano``. To edit the file, type:

    .. code-block:: bash

       # ensure you are in correct directory. if you followed tutorial type:
       cd ~/proxl

       # edit the file
       nano .env

Add the following lines to the end of the file. Substitute the actual directories with directories
you chose above. This example uses the example directory names:

    .. code-block:: none

       MYSQL_DATA_DIRECTORY=/data/proxl-data/mysql
       SPECTR_UPLOAD_DIRECTORY=/data/proxl-data/spectr-upload
       SPECTR_STORAGE_DIRECTORY=/data/proxl-data/spectr-storage
       PROXL_UPLOAD_DIRECTORY=/data/proxl-data/proxl-upload
       PROXL_CACHE_DIRECTORY=/data/proxl-data/proxl-cache

Type ``Control-o``, ``<ENTER>``, and ``Control-x`` to save and exit ``nano``.


3. Starting and Stopping Proxl
===================================

.. important::
    The commands below are different than the commands for starting and stopping Proxl on our
    :doc:`intro` tutorial! You must always use these commands if you have customized the
    data locations.

At this point, starting and stopping proxl should be straight forward.

To start proxl:

    .. code-block:: bash

       sudo docker-compose -f docker-compose-custom-data.yml up --detach

To stop proxl:

    .. code-block:: bash

       sudo docker-compose -f docker-compose-custom-data.yml down

.. note::
   If you are using **Windows**, ensure Docker is running by typing:

   .. code-block:: bash

      sudo service docker start

   You should now be able to start Proxl.

.. note::
   The first time you start proxl, all of the components will download and the database will
   initialize. This may take a few minutes, depending on your download speed. Subsequent startups
   of proxl will not require these steps and will be faster.

.. note::
   These commands must be typed while you are in the project code directory. If you followed these
   instructions, you can ensure you are in this directory by typing:

   .. code-block:: bash

       cd ~/proxl


4. Proceed with installation
================================================
You should now proceed to step 6 in our :doc:`intro` tutorial.
However, recall that your command for stopping and starting is different than that listed in the tutorial. (See above.)
