package com.github.sbracely.extended.problem.detail.test.mvc.response;

import com.github.sbracely.extended.problem.detail.test.mvc.response.serializer.ProblemDetailResponseSerializer;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ProblemDetailResponseSerializer.class)
public class ProblemDetailResponse {

}
