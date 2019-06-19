#!/usr/bin/python

# *****************************************************************************
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# ******************************************************************************

import json
from dlab.fab import *
from dlab.meta_lib import *
import sys, time, os
from dlab.actions_lib import *
import traceback

if __name__ == "__main__":
    local_log_filename = "{}_{}_{}.log".format(os.environ['conf_resource'], os.environ['project_tag'], os.environ['request_id'])
    local_log_filepath = "/logs/project/" + local_log_filename
    logging.basicConfig(format='%(levelname)-8s [%(asctime)s]  %(message)s',
                        level=logging.DEBUG,
                        filename=local_log_filepath)

    create_aws_config_files()
    print('Generating infrastructure names and tags')
    project_conf = dict()
    project_conf['service_base_name'] = os.environ['conf_service_base_name']
    project_conf['project_tag_value'] = os.environ['project_tag']
    project_conf['vpc_id'] = os.environ['aws_vpc_id']
    project_conf['region'] = os.environ['aws_region']
    project_conf['zone'] = os.environ['aws_region'] + os.environ['aws_zone']
    project_conf['tag_name'] = '{}-Tag'.format(project_conf['service_base_name'])
    project_conf['tag_name_value'] = '{0}-{1}-subnet'.format(project_conf['service_base_name'], project_conf['project_tag_value'])
    project_conf['private_subnet_prefix'] = os.environ['aws_private_subnet_prefix']
    project_conf['private_subnet_name'] = '{0}-{1}-subnet'.format(project_conf['service_base_name'], project_conf['project_tag_value'])

    project_conf['vpc2_cidr'] = os.environ['conf_vpc2_cidr']
    project_conf['tag2_name'] = project_conf['service_base_name'] + '-secondary-Tag'
    project_conf['vpc2_name'] = '{}-secondary-VPC'.format(project_conf['service_base_name'])

    try:
        if os.environ['conf_duo_vpc_enable'] == 'true' and not os.environ['aws_vpc2_id']:
            try:
                pre_defined_vpc2 = True
                logging.info('[CREATE SECONDARY VPC AND ROUTE TABLE]')
                print('[CREATE SECONDARY VPC AND ROUTE TABLE]')
                params = "--vpc {} --region {} --infra_tag_name {} --infra_tag_value {} --secondary " \
                         "--vpc_name {}".format(project_conf['vpc2_cidr'], project_conf['region'], project_conf['tag2_name'], project_conf['service_base_name'], project_conf['vpc2_name'])
                try:
                    local("~/scripts/{}.py {}".format('ssn_create_vpc', params))
                except:
                    traceback.print_exc()
                    raise Exception
                os.environ['aws_vpc2_id'] = get_vpc_by_tag(tag2_name, service_base_name)
            except Exception as err:
                print('Error: {0}'.format(err))
                append_result("Failed to create secondary VPC. Exception:" + str(err))
                if pre_defined_vpc:
                    remove_internet_gateways(os.environ['aws_vpc_id'], tag_name, service_base_name)
                    remove_route_tables(tag_name, True)
                    remove_vpc(os.environ['aws_vpc_id'])
                sys.exit(1)
        else:
            try:
                 project_conf['vpc2_id'] = os.environ['aws_notebook_vpc_id']
            except KeyError:
                project_conf['vpc2_id'] = project_conf['vpc_id']
    except:
        traceback.print_exc()
        sys.exit(1)

    try:
        if os.environ['conf_user_subnets_range'] == '':
            raise KeyError
    except KeyError:
        os.environ['conf_user_subnets_range'] = ''


    print("Will create exploratory environment as following: {}".
          format(json.dumps(project_conf, sort_keys=True, indent=4, separators=(',', ': '))))
    logging.info(json.dumps(project_conf))

    try:
        logging.info('[CREATE SUBNET]')
        print('[CREATE SUBNET]')
        params = "--vpc_id '{}' --infra_tag_name {} --infra_tag_value {} --prefix {} " \
                 "--user_subnets_range '{}' --subnet_name {} --zone {}".format(
            project_conf['vpc2_id'], project_conf['tag_name'], project_conf['service_base_name'],
            project_conf['private_subnet_prefix'], os.environ['conf_user_subnets_range'], project_conf['private_subnet_name'],
            project_conf['zone'])
        try:
            local("~/scripts/{}.py {}".format('common_create_subnet', params))
        except:
            traceback.print_exc()
            raise Exception
    except Exception as err:
        print('Error: {0}'.format(err))
        append_result("Failed to create subnet.", str(err))
        sys.exit(1)

    tag = {"Key": project_conf['tag_name'], "Value": "{0}-{1}-subnet".format(project_conf['service_base_name'], project_conf['project_tag_value'])}
    project_conf['private_subnet_cidr'] = get_subnet_by_tag(tag)
    project_tag = {"Key": 'project_tag', "Value": project_conf['project_tag_value']}
    subnet_id = get_subnet_by_cidr(project_conf['private_subnet_cidr'])
    print('subnet id: {}'.format(subnet_id))
    create_tag(subnet_id, project_tag)
    print('NEW SUBNET CIDR CREATED: {}'.format(project_conf['private_subnet_cidr']))