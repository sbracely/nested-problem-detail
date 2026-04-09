package io.github.sbracely.extended.problem.detail.webmvc.example.response.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.sbracely.extended.problem.detail.webmvc.example.response.MvcProblemDetailResponse;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

public class MvcProblemDetailResponseSerializer extends JsonSerializer<MvcProblemDetailResponse> {

    @Override
    public void serialize(MvcProblemDetailResponse value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        throw new HttpMessageNotWritableException("FaultyConverter");
    }
}
