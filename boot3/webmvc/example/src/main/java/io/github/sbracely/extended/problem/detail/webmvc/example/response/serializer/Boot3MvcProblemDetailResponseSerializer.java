package io.github.sbracely.extended.problem.detail.webmvc.example.response.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.sbracely.extended.problem.detail.webmvc.example.response.Boot3MvcProblemDetailResponse;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

public class Boot3MvcProblemDetailResponseSerializer extends JsonSerializer<Boot3MvcProblemDetailResponse> {

    @Override
    public void serialize(Boot3MvcProblemDetailResponse value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        throw new HttpMessageNotWritableException("FaultyConverter");
    }
}
