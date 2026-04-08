package io.github.sbracely.extended.problem.detail.webmvc.example.response;

import io.github.sbracely.extended.problem.detail.webmvc.example.response.serializer.Boot4MvcProblemDetailResponseSerializer;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = Boot4MvcProblemDetailResponseSerializer.class)
public class Boot4MvcProblemDetailResponse {

}
