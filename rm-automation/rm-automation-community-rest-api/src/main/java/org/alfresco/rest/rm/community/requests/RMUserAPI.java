/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.rest.rm.community.requests;

import static com.jayway.restassured.RestAssured.given;

import static org.alfresco.rest.core.RestRequest.requestWithBody;
import static org.alfresco.rest.core.RestRequest.simpleRequest;
import static org.alfresco.rest.rm.community.util.ParameterCheck.mandatoryObject;
import static org.alfresco.rest.rm.community.util.PojoUtility.toJson;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;

import java.text.MessageFormat;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import org.alfresco.dataprep.AlfrescoHttpClient;
import org.alfresco.dataprep.AlfrescoHttpClientFactory;
import org.alfresco.dataprep.UserService;
import org.alfresco.rest.core.RestAPI;
import org.alfresco.rest.rm.community.model.site.RMSite;
import org.alfresco.utility.data.DataUser;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * RM user management API
 * 
 * @author Kristijan Conkas
 * @since 2.6
 */
// FIXME: As of December 2016 there is no v1-style API for managing RM users and users' 
// roles. Until such APIs have become available, methods in this class are just proxies to 
// "old-style" API calls.
@Component
@Scope (value = "prototype")
public class RMUserAPI extends RestAPI<RMUserAPI>
{
    @Autowired
    private RMSiteAPI rmSiteAPI;

    @Autowired
    private UserService userService;
    
    @Autowired
    private DataUser dataUser;
    
    @Autowired
    private AlfrescoHttpClientFactory alfrescoHttpClientFactory;
        
    public void assignRoleToUser(String userName, String userRole) throws Exception
    {
        // get an "old-style" REST API client
        AlfrescoHttpClient client = alfrescoHttpClientFactory.getObject();
        
        // override v1 baseURI and basePath
        RequestSpecification spec = new RequestSpecBuilder()
            .setBaseUri(client.getApiUrl())
            .setBasePath("/")
            .build();
        
        Response response = given()
            .spec(spec)
            .log().all()
            .pathParam("role", userRole)
            .pathParam("authority", userName)
            .param("alf_ticket", client.getAlfTicket(
                dataUser.getAdminUser().getUsername(), dataUser.getAdminUser().getPassword()))
        .when()
            .post("/rm/roles/{role}/authorities/{authority}")
            .prettyPeek()
            .andReturn();
        usingRestWrapper().setStatusCode(Integer.toString(response.getStatusCode()));
    }
}
