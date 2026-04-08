package io.github.sbracely.extended.problem.detail.webmvc.example.response.serializer;

import io.github.sbracely.extended.problem.detail.webmvc.example.response.Boot4MvcProblemDetailResponse;
import org.springframework.http.converter.HttpMessageNotWritableException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class Boot4MvcProblemDetailResponseSerializer extends ValueSerializer<Boot4MvcProblemDetailResponse> {

    @Override
    public void serialize(Boot4MvcProblemDetailResponse value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        throw new HttpMessageNotWritableException("FaultyConverter");
    }
}
