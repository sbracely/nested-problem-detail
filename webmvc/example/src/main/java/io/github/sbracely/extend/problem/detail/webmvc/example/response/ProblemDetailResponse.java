package io.github.sbracely.extend.problem.detail.webmvc.example.response;

import io.github.sbracely.extend.problem.detail.webmvc.example.response.serializer.ProblemDetailResponseSerializer;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ProblemDetailResponseSerializer.class)
public class ProblemDetailResponse {

}
