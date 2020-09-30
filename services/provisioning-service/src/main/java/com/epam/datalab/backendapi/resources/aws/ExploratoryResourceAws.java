/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.epam.datalab.backendapi.resources.aws;

import com.epam.datalab.auth.UserInfo;
import com.epam.datalab.backendapi.core.commands.DockerAction;
import com.epam.datalab.backendapi.resources.base.ExploratoryService;
import com.epam.datalab.dto.aws.exploratory.ExploratoryCreateAws;
import com.epam.datalab.dto.exploratory.ExploratoryActionDTO;
import com.epam.datalab.dto.exploratory.ExploratoryGitCredsUpdateDTO;
import com.epam.datalab.dto.exploratory.ExploratoryReconfigureSparkClusterActionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import io.dropwizard.auth.Auth;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/exploratory")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExploratoryResourceAws {

    @Inject
    private ExploratoryService exploratoryService;


    @Path("/create")
    @POST
    public String create(@Auth UserInfo ui, ExploratoryCreateAws dto) throws JsonProcessingException {
        return exploratoryService.action(ui.getName(), dto, DockerAction.CREATE);
    }

    @Path("/start")
    @POST
    public String start(@Auth UserInfo ui, ExploratoryGitCredsUpdateDTO dto) throws JsonProcessingException {
        return exploratoryService.action(ui.getName(), dto, DockerAction.START);
    }

    @Path("/terminate")
    @POST
    public String terminate(@Auth UserInfo ui, ExploratoryActionDTO<?> dto) throws JsonProcessingException {
        return exploratoryService.action(ui.getName(), dto, DockerAction.TERMINATE);
    }

    @Path("/stop")
    @POST
    public String stop(@Auth UserInfo ui, ExploratoryActionDTO<?> dto) throws JsonProcessingException {
        return exploratoryService.action(ui.getName(), dto, DockerAction.STOP);
    }

    @Path("/reconfigure_spark")
    @POST
    public String reconfigureSpark(@Auth UserInfo ui, ExploratoryReconfigureSparkClusterActionDTO dto) throws JsonProcessingException {
        return exploratoryService.action(ui.getName(), dto, DockerAction.RECONFIGURE_SPARK);
    }
}
