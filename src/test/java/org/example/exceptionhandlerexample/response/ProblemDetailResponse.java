package org.example.exceptionhandlerexample.response;

import org.example.exceptionhandlerexample.serializer.ProblemDetailResponseSerializer;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ProblemDetailResponseSerializer.class)
public class ProblemDetailResponse {

}
