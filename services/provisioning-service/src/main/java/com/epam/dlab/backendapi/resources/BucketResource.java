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

package com.epam.dlab.backendapi.resources;

import com.epam.dlab.auth.UserInfo;
import com.epam.dlab.backendapi.service.BucketService;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.inject.Inject;
import io.dropwizard.auth.Auth;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Slf4j
@Path("/bucket")
public class BucketResource {
    private final BucketService bucketService;

    @Inject
    public BucketResource(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @GET
    @Path("/{bucket}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListOfObjects(@Auth UserInfo userInfo,
                                     @PathParam("bucket") String bucket) {
        return Response.ok(bucketService.getObjects(bucket)).build();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadObject(@Auth UserInfo userInfo,
                                 @FormDataParam("object") String object,
                                 @FormDataParam("bucket") String bucket,
                                 @FormDataParam("file") InputStream inputStream,
                                 @FormDataParam("file") FormDataContentDisposition fileMetaData) {
        bucketService.uploadObject(bucket, object, inputStream);
        return Response.ok().build();
    }

    @GET
    @Path("/{bucket}/object/{object}/download")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadObject(@Auth UserInfo userInfo,
                                   @PathParam("object") String object,
                                   @PathParam("bucket") String bucket) {
        return Response.ok(bucketService.downloadObject(bucket, object)).build();
    }

    @DELETE
    @Path("/{bucket}/object/{object}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadObject(@Auth UserInfo userInfo,
                                 @PathParam("bucket") String bucket,
                                 @PathParam("object") String object) {
        bucketService.deleteObject(bucket, object);
        return Response.ok().build();
    }

    public static void main(String[] args) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String bucketName = "ofuks-1304-prj1-local-bucket";
        storage.delete(bucketName, "1.txt");
    }
}
