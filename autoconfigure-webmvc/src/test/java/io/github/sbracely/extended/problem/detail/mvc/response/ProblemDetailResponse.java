package io.github.sbracely.extended.problem.detail.mvc.response;

import io.github.sbracely.extended.problem.detail.mvc.response.serializer.ProblemDetailResponseSerializer;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ProblemDetailResponseSerializer.class)
public class ProblemDetailResponse {

}
