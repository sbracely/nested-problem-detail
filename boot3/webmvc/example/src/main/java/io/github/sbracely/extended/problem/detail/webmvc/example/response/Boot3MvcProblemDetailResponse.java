package io.github.sbracely.extended.problem.detail.webmvc.example.response;

import io.github.sbracely.extended.problem.detail.webmvc.example.response.serializer.Boot3MvcProblemDetailResponseSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = Boot3MvcProblemDetailResponseSerializer.class)
public class Boot3MvcProblemDetailResponse {

}
