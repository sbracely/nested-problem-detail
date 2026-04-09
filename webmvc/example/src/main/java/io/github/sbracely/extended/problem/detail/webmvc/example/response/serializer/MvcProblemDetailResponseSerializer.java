package io.github.sbracely.extended.problem.detail.webmvc.example.response.serializer;

import io.github.sbracely.extended.problem.detail.webmvc.example.response.MvcProblemDetailResponse;
import org.springframework.http.converter.HttpMessageNotWritableException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class MvcProblemDetailResponseSerializer extends ValueSerializer<MvcProblemDetailResponse> {

    @Override
    public void serialize(MvcProblemDetailResponse value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        throw new HttpMessageNotWritableException("FaultyConverter");
    }
}
