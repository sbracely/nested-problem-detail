package org.example.exceptionhandlerexample.response.serializer;

import org.example.exceptionhandlerexample.response.ProblemDetailResponse;
import org.springframework.http.converter.HttpMessageNotWritableException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class ProblemDetailResponseSerializer extends ValueSerializer<ProblemDetailResponse> {

    @Override
    public void serialize(ProblemDetailResponse value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        throw new HttpMessageNotWritableException("FaultyConverter");
    }
}
