## How to build the importer docker image

1. Build Limelight

   ```
   # go to root directory of this repo
   cd ../..
   
   # use docker to build the project
   sudo docker run --rm -it --user $(id -u):$(id -g) -v `pwd`:`pwd` -w `pwd` --env HOME=. --entrypoint ant mriffle/build-limelight -f ant__build_all_limelight.xml
   ```

   See https://github.com/yeastrc/limelight-build-docker for more information.

2. Copy war file into this directory

   ```
   cd docker/webapp
   cp ../../deploy/limelight.war ./
   ```

3. Build the Docker image

   ```
   sudo docker image build -t mriffle/limelight-webapp ./
   ```
   
   Tag as desired.