/**
 * Copyright (C) 2011-2018 Red Hat, Inc. (https://github.com/Commonjava/indy)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.indy.koji.bind.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.commonjava.indy.bind.jaxrs.IndyResources;
import org.commonjava.indy.bind.jaxrs.SecurityManager;
import org.commonjava.indy.koji.data.KojiRepairException;
import org.commonjava.indy.koji.data.KojiRepairManager;
import org.commonjava.indy.koji.model.KojiRepairRequest;
import org.commonjava.indy.koji.model.KojiRepairResult;
import org.commonjava.indy.model.core.PackageTypeDescriptor;
import org.commonjava.indy.model.core.PackageTypes;
import org.commonjava.indy.util.ApplicationContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import static org.commonjava.indy.bind.jaxrs.util.ResponseUtils.throwError;
import static org.commonjava.indy.koji.model.IndyKojiConstants.REPAIR_KOJI;
import static org.commonjava.indy.koji.model.IndyKojiConstants.VOL;
import static org.commonjava.indy.util.ApplicationContent.application_json;

@Api( value = "Koji repairVolume operation", description = "Repair Koji remote repositories." )
@Path( "/api/" + REPAIR_KOJI )
@Produces( { application_json } )
public class KojiRepairResource
                implements IndyResources
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    private ObjectMapper mapper;

    @Inject
    private SecurityManager securityManager;

    @Inject
    private KojiRepairManager repairManager;

    @ApiOperation( "Repair koji repository remote url /vol." )
    @ApiResponse( code = 200, message = "Operation finished (consult response content for success/failure).",
                    response = KojiRepairResult.class )
    @ApiImplicitParam( name = "body", paramType = "body",
                    value = "JSON request specifying source and other configuration options",
                    required = true, dataType = "org.commonjava.indy.koji.model.KojiRepairRequest" )
    @POST
    @Path( "/" + VOL )
    @Consumes( ApplicationContent.application_json )
    public KojiRepairResult repair( final KojiRepairRequest request, final @Context HttpServletRequest servletRequest,
                                    final @Context SecurityContext securityContext, final @Context UriInfo uriInfo )
    {
        try
        {
            PackageTypeDescriptor packageTypeDescriptor =
                            PackageTypes.getPackageTypeDescriptor( request.getSource().getPackageType() );

            String user = securityManager.getUser( securityContext, servletRequest );
            final String baseUrl = uriInfo.getBaseUriBuilder()
                                          .path( packageTypeDescriptor.getContentRestBasePath() )
                                          .build( request.getSource().getType().singularEndpointName(),
                                                  request.getSource().getName() )
                                          .toString();
            return repairManager.repairVol( request, user, baseUrl );
        }
        catch ( KojiRepairException e )
        {
            logger.error( e.getMessage(), e );
            throwError( e );
        }

        return null;
    }

}
