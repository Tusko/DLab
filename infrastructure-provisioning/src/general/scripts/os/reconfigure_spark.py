#!/usr/bin/python

# *****************************************************************************
#
# Copyright (c) 2016, EPAM SYSTEMS INC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# ******************************************************************************

import sys
import argparse
from dlab.notebook_lib import *
from dlab.fab import *
from dlab.actions_lib import *
from fabric.api import *
import json
import os


parser = argparse.ArgumentParser()
parser.add_argument('--os_user', type=str, default='')
parser.add_argument('--instance_ip', type=str, default='')
parser.add_argument('--keyfile', type=str, default='')
parser.add_argument('--resource_type', type=str, default='')
parser.add_argument('--spark_type', type=str, default='local')
parser.add_argument('--cluster_name', type=str, default='local')
args = parser.parse_args()


if __name__ == "__main__":
    env['connection_attempts'] = 100
    env.key_filename = [args.keyfile]
    env.host_string = '{}@{}'.format(args.os_user, args.instance_ip)

    jars_dir = '/opt/jars/'
    templates_dir = '/root/templates/'
    if args.resource_type == 'dataengine':
        memory_type = ''
    else:
        memory_type = 'driver'
    if args.spark_type == 'local':
        configure_local_spark(jars_dir, templates_dir, memory_type)
    elif args.spark_type == 'dataengine':
        cluster_dir = '/opt/' + args.cluster_name + '/'
        if 'azure_datalake_enable' in os.environ:
            datalake_enabled = os.environ['azure_datalake_enable']
        else:
            datalake_enabled = 'false'
        configure_dataengine_spark(args.cluster_name, jars_dir, cluster_dir, datalake_enabled)
