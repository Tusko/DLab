/*
 * Copyright (c) 2018, EPAM SYSTEMS INC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.dlab.backendapi.resources;

import com.epam.dlab.auth.UserInfo;
import com.epam.dlab.backendapi.dao.ExploratoryDAO;
import com.epam.dlab.backendapi.domain.RequestId;
import com.epam.dlab.backendapi.resources.dto.*;
import com.epam.dlab.backendapi.service.ExternalLibraryService;
import com.epam.dlab.backendapi.service.LibraryService;
import com.epam.dlab.backendapi.resources.dto.LibraryDTO;
import com.epam.dlab.dto.UserInstanceDTO;
import com.epam.dlab.dto.computational.UserComputationalResource;
import com.epam.dlab.dto.exploratory.LibraryInstallDTO;
import com.epam.dlab.exceptions.DlabException;
import com.epam.dlab.rest.client.RESTService;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.http.HttpStatus;
import org.bson.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class LibExploratoryResourceTest extends TestBase {

	private ExploratoryDAO exploratoryDAO = mock(ExploratoryDAO.class);
	private LibraryService libraryService = mock(LibraryService.class);
	private RESTService provisioningService = mock(RESTService.class);
	private ExternalLibraryService externalLibraryService = mock(ExternalLibraryService.class);
	private RequestId requestId = mock(RequestId.class);

	@Rule
	public final ResourceTestRule resources = getResourceTestRuleInstance(
			new LibExploratoryResource(exploratoryDAO, libraryService, provisioningService, requestId,
					externalLibraryService));

	@Before
	public void setup() throws AuthenticationException {
		authSetup();
	}

	@Test
	public void getLibGroupListWithFailedAuth() throws AuthenticationException {
		authFailSetup();
		when(exploratoryDAO.fetchExploratoryFields(anyString(), anyString(), anyString())).thenReturn
				(getUserInstanceDto());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_groups")
				.queryParam("exploratory_name", "explName")
				.queryParam("computational_name", "compName")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryDAO).fetchExploratoryFields(USER.toLowerCase(), "explName", "compName");
		verifyNoMoreInteractions(exploratoryDAO);
	}

	@Test
	public void getLibGroupListWithException() {
		when(exploratoryDAO.fetchExploratoryFields(anyString(), anyString(), anyString())).thenReturn
				(getUserInstanceDto());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_groups")
				.queryParam("exploratory_name", "explName")
				.queryParam("computational_name", "compName")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryDAO).fetchExploratoryFields(USER.toLowerCase(), "explName", "compName");
		verifyNoMoreInteractions(exploratoryDAO);
	}

	@Test
	public void getLibGroupListWithoutComputationalWithFailedAuth() throws AuthenticationException {
		authFailSetup();
		when(exploratoryDAO.fetchExploratoryFields(anyString(), anyString())).thenReturn(getUserInstanceDto());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_groups")
				.queryParam("exploratory_name", "explName")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryDAO).fetchExploratoryFields(USER.toLowerCase(), "explName");
		verifyNoMoreInteractions(exploratoryDAO);
	}

	@Test
	public void getLibGroupListWithoutComputationalWithException() {
		when(exploratoryDAO.fetchExploratoryFields(anyString(), anyString())).thenReturn(getUserInstanceDto());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_groups")
				.queryParam("exploratory_name", "explName")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryDAO).fetchExploratoryFields(USER.toLowerCase(), "explName");
		verifyNoMoreInteractions(exploratoryDAO);
	}

	@Test
	public void getLibList() {
		when(libraryService.getLibs(anyString(), anyString(), anyString())).thenReturn(getDocuments());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_list")
				.queryParam("exploratory_name", "explName")
				.queryParam("computational_name", "compName")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(getDocuments(), response.readEntity(new GenericType<List<Document>>() {
		}));
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(libraryService).getLibs(USER.toLowerCase(), "explName", "compName");
		verifyNoMoreInteractions(libraryService);
	}

	@Test
	public void getLibListWithFailedAuth() throws AuthenticationException {
		authFailSetup();
		when(libraryService.getLibs(anyString(), anyString(), anyString())).thenReturn(getDocuments());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_list")
				.queryParam("exploratory_name", "explName")
				.queryParam("computational_name", "compName")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(getDocuments(), response.readEntity(new GenericType<List<Document>>() {
		}));
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(libraryService).getLibs(USER.toLowerCase(), "explName", "compName");
		verifyNoMoreInteractions(libraryService);
	}

	@Test
	public void getLibListWithException() {
		doThrow(new DlabException("Cannot load installed libraries"))
				.when(libraryService).getLibs(anyString(), anyString(), anyString());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_list")
				.queryParam("exploratory_name", "explName")
				.queryParam("computational_name", "compName")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(libraryService).getLibs(USER.toLowerCase(), "explName", "compName");
		verifyNoMoreInteractions(libraryService);
	}

	@Test
	public void getLibListFormatted() {
		when(libraryService.getLibInfo(anyString(), anyString())).thenReturn(getLibInfoRecords());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_list/formatted")
				.queryParam("exploratory_name", "explName")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(libraryService).getLibInfo(USER.toLowerCase(), "explName");
		verifyNoMoreInteractions(libraryService);
	}

	@Test
	public void getLibListFormattedWithFailedAuth() throws AuthenticationException {
		authFailSetup();
		when(libraryService.getLibInfo(anyString(), anyString())).thenReturn(getLibInfoRecords());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_list/formatted")
				.queryParam("exploratory_name", "explName")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(libraryService).getLibInfo(USER.toLowerCase(), "explName");
		verifyNoMoreInteractions(libraryService);
	}

	@Test
	public void getLibListFormattedWithException() {
		doThrow(new DlabException("Cannot load  formatted list of installed libraries"))
				.when(libraryService).getLibInfo(anyString(), anyString());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_list/formatted")
				.queryParam("exploratory_name", "explName")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(libraryService).getLibInfo(USER.toLowerCase(), "explName");
		verifyNoMoreInteractions(libraryService);
	}

	@Test
	public void libInstall() {
		when(libraryService.generateLibraryInstallDTO(any(UserInfo.class), any(LibInstallFormDTO.class)))
				.thenReturn(getLibraryInstallDTO());
		when(libraryService.prepareComputationalLibInstallation(anyString(), any(LibInstallFormDTO.class),
				any(LibraryInstallDTO.class))).thenReturn(getLibraryInstallDTO());
		when(provisioningService.post(anyString(), anyString(), any(LibraryInstallDTO.class), any()))
				.thenReturn("someUuid");
		when(requestId.put(anyString(), anyString())).thenReturn("someUuid");
		LibInstallFormDTO libInstallFormDTO = new LibInstallFormDTO();
		libInstallFormDTO.setComputationalName("compName");
		libInstallFormDTO.setNotebookName("explName");
		libInstallFormDTO.setLibs(Collections.emptyList());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_install")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.post(Entity.json(libInstallFormDTO));

		assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verifyZeroInteractions(libraryService, provisioningService, requestId);
	}

	@Test
	public void libInstallWithFailedAuth() throws AuthenticationException {
		authFailSetup();
		when(libraryService.generateLibraryInstallDTO(any(UserInfo.class), any(LibInstallFormDTO.class)))
				.thenReturn(getLibraryInstallDTO());
		when(libraryService.prepareComputationalLibInstallation(anyString(), any(LibInstallFormDTO.class),
				any(LibraryInstallDTO.class))).thenReturn(getLibraryInstallDTO());
		when(provisioningService.post(anyString(), anyString(), any(LibraryInstallDTO.class), any()))
				.thenReturn("someUuid");
		when(requestId.put(anyString(), anyString())).thenReturn("someUuid");
		LibInstallFormDTO libInstallFormDTO = new LibInstallFormDTO();
		libInstallFormDTO.setComputationalName("compName");
		libInstallFormDTO.setNotebookName("explName");
		libInstallFormDTO.setLibs(Collections.emptyList());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_install")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.post(Entity.json(libInstallFormDTO));

		assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verifyZeroInteractions(libraryService, provisioningService, requestId);
	}


	@Test
	public void libInstallWithoutComputational() {
		when(libraryService.generateLibraryInstallDTO(any(UserInfo.class), any(LibInstallFormDTO.class)))
				.thenReturn(getLibraryInstallDTO());
		when(libraryService.prepareExploratoryLibInstallation(anyString(), any(LibInstallFormDTO.class),
				any(LibraryInstallDTO.class))).thenReturn(getLibraryInstallDTO());
		when(provisioningService.post(anyString(), anyString(), any(LibraryInstallDTO.class), any()))
				.thenReturn("someUuid");
		when(requestId.put(anyString(), anyString())).thenReturn("someUuid");
		LibInstallFormDTO libInstallFormDTO = new LibInstallFormDTO();
		libInstallFormDTO.setComputationalName("");
		libInstallFormDTO.setNotebookName("explName");
		libInstallFormDTO.setLibs(Collections.emptyList());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/lib_install")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.post(Entity.json(libInstallFormDTO));

		assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verifyZeroInteractions(libraryService, provisioningService, requestId);
	}

	@Test
	public void getLibraryListWithFailedAuth() throws AuthenticationException {
		authFailSetup();
		when(exploratoryDAO.fetchExploratoryFields(anyString(), anyString(), anyString()))
				.thenReturn(getUserInstanceDto());
		SearchLibsFormDTO searchLibsFormDTO = new SearchLibsFormDTO();
		searchLibsFormDTO.setComputationalName("compName");
		searchLibsFormDTO.setNotebookName("explName");
		searchLibsFormDTO.setGroup("someGroup");
		searchLibsFormDTO.setStartWith("someText");
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/search/lib_list")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.post(Entity.json(searchLibsFormDTO));

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryDAO).fetchExploratoryFields(USER.toLowerCase(), "explName", "compName");
		verifyNoMoreInteractions(exploratoryDAO);
	}

	@Test
	public void getLibraryListWithException() {
		when(exploratoryDAO.fetchExploratoryFields(anyString(), anyString(), anyString()))
				.thenReturn(getUserInstanceDto());
		SearchLibsFormDTO searchLibsFormDTO = new SearchLibsFormDTO();
		searchLibsFormDTO.setComputationalName("compName");
		searchLibsFormDTO.setNotebookName("explName");
		searchLibsFormDTO.setGroup("someGroup");
		searchLibsFormDTO.setStartWith("someText");
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/search/lib_list")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.post(Entity.json(searchLibsFormDTO));

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryDAO).fetchExploratoryFields(USER.toLowerCase(), "explName", "compName");
		verifyNoMoreInteractions(exploratoryDAO);
	}

	@Test
	public void getLibraryListWithoutComputationalWithException() {
		when(exploratoryDAO.fetchExploratoryFields(anyString(), anyString()))
				.thenReturn(getUserInstanceDto());
		SearchLibsFormDTO searchLibsFormDTO = new SearchLibsFormDTO();
		searchLibsFormDTO.setComputationalName("");
		searchLibsFormDTO.setNotebookName("explName");
		searchLibsFormDTO.setGroup("someGroup");
		searchLibsFormDTO.setStartWith("someText");
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/search/lib_list")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.post(Entity.json(searchLibsFormDTO));

		assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		verify(exploratoryDAO).fetchExploratoryFields(USER.toLowerCase(), "explName");
		verifyNoMoreInteractions(exploratoryDAO);
	}

	@Test
	public void getMavenArtifact() {
		when(externalLibraryService.getLibrary(anyString(), anyString(), anyString())).thenReturn(libraryDto());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/search/lib_list/maven")
				.queryParam("artifact", "group:artifact:version")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
		final LibraryDTO libraryDTO = response.readEntity(LibraryDTO.class);
		assertEquals("test", libraryDTO.getName());
		assertEquals("1.0", libraryDTO.getVersion());

		verify(externalLibraryService).getLibrary("group", "artifact", "version");
		verifyNoMoreInteractions(externalLibraryService);
	}

	@Test
	public void getMavenArtifactWithValidationException() {
		when(externalLibraryService.getLibrary(anyString(), anyString(), anyString())).thenReturn(libraryDto());
		final Response response = resources.getJerseyTest()
				.target("/infrastructure_provision/exploratory_environment/search/lib_list/maven")
				.queryParam("artifact", "group:artifact")
				.request()
				.header("Authorization", "Bearer " + TOKEN)
				.get();

		assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

		assertEquals("{\"errors\":[\"query param artifact Wrong library name format. Should be " +
						"<groupId>:<artifactId>:<versionId>. E.g. io.dropwizard:dropwizard-core:1.3.5\"]}",
				response.readEntity(String.class));

		verifyZeroInteractions(externalLibraryService);
	}

	private LibraryDTO libraryDto() {
		return new LibraryDTO(
				"test", "1.0");
	}

	private UserInstanceDTO getUserInstanceDto() {
		UserComputationalResource ucResource = new UserComputationalResource();
		ucResource.setComputationalName("compName");
		return new UserInstanceDTO().withUser(USER).withExploratoryName("explName")
				.withResources(Collections.singletonList(ucResource));
	}

	private List<Document> getDocuments() {
		return Collections.singletonList(new Document());
	}

	private List<LibInfoRecord> getLibInfoRecords() {
		return Collections.singletonList(new LibInfoRecord(
				new LibKey(), Collections.singletonList(new LibraryStatus())));
	}

	private LibraryInstallDTO getLibraryInstallDTO() {
		return new LibraryInstallDTO().withComputationalName("compName");
	}
}
