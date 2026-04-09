package io.github.sbracely.extended.problem.detail.webmvc.example.response;

import io.github.sbracely.extended.problem.detail.webmvc.example.response.serializer.MvcProblemDetailResponseSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = MvcProblemDetailResponseSerializer.class)
public class MvcProblemDetailResponse {

}
