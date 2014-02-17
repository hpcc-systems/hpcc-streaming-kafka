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
# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"
BASE_BOX_NAME="raring64"
BASE_BOX_URL="http://goo.gl/Y4aRr"

#
# Provision, configure and start the following 2 VMs:
# - HPCC Platform
# - Apache Kafka message broker
#
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  
  # VM provisioning for the HPCC Platform (single node for now)
  config.vm.define "hpcc4_2" do |hpcc4_2|
    hpcc4_2.vm.box = BASE_BOX_NAME

    # The url from where the 'hpcc4_2.vm.box' box will be fetched if it
    # doesn't already exist on the user's system.
    hpcc4_2.vm.box_url = BASE_BOX_URL

    hpcc4_2.vm.network :private_network, ip: "192.168.22.10"

    hpcc4_2.vm.provision "shell", path: "vagrant/hpcc.sh"
  end
  
  # VM provisioning for the Apache Kafka node
  config.vm.define "kafka" do |kafka|
    kafka.vm.box = BASE_BOX_NAME

    # The url from where the 'kafka.vm.box' box will be fetched if it
    # doesn't already exist on the user's system.
    kafka.vm.box_url = BASE_BOX_URL

    kafka.vm.network :private_network, ip: "192.168.22.20"
    
    kafka.vm.provider :virtualbox do |vb|
      vb.customize ["modifyvm", :id, "--memory", "1024"]
    end
    
    kafka.vm.provision "shell", path: "vagrant/kafka.sh"
  end

end
