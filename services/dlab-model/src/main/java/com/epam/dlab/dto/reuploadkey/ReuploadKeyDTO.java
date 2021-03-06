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

package com.epam.dlab.dto.reuploadkey;

import com.epam.dlab.dto.ResourceSysBaseDTO;
import com.epam.dlab.model.ResourceData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class ReuploadKeyDTO extends ResourceSysBaseDTO<ReuploadKeyDTO> {

	@JsonProperty
	private String content;

	@JsonProperty
	private List<ResourceData> resources;

	@JsonProperty
	private String id;


	public ReuploadKeyDTO withContent(String content){
		this.content = content;
		return this;
	}

	public ReuploadKeyDTO withResources(List<ResourceData> resources) {
		this.resources = resources;
		return this;
	}

	public ReuploadKeyDTO withId(String id){
		this.id = id;
		return this;
	}
}
