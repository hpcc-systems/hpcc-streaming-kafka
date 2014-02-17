# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
# 
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#!/bin/sh -Eux

#  Trap non-normal exit signals: 1/HUP, 2/INT, 3/QUIT, 15/TERM, ERR
trap founderror 1 2 3 15 ERR

founderror()
{
        exit 1
}

exitscript()
{
        exit 0
}


# Download libxerces-c3.1 and libxalan-c111 (unavailable via apt-get)
wget http://security.ubuntu.com/ubuntu/pool/universe/x/xerces-c/libxerces-c3.1_3.1.1-5_amd64.deb
wget http://security.ubuntu.com/ubuntu/pool/universe/x/xalan/libxalan-c111_1.11-3_amd64.deb

# Install libxerces-c3.1 and libxalan-c111
sudo dpkg -i libxerces-c3.1_3.1.1-5_amd64.deb
sudo dpkg -i libxalan-c111_1.11-3_amd64.deb

# Install other dependencies
sudo apt-get -f install -y libboost-regex1.53.0 libicu48 expect libarchive13

# Install the HPCC Platform
sudo dpkg -i /vagrant/vagrant/hpccsystems-platform_community-4.2.0-4saucy_amd64.deb

# Create symbolic link needed by the HPCC Platform
sudo ln -s /usr/lib/libbfd-2.23.2-system.so /usr/lib/libbfd-2.23.52-system.20130913.so

# Install Java
/vagrant/vagrant/java-install.sh

# Start the HPCC Platform services
sudo service hpcc-init start

exitscript