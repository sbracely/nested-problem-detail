package io.github.sbracely.extended.problem.detail.common.field.hide;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.http.ProblemDetail;

/**
 * Jackson serializer for ProblemDetail that applies field visibility rules.
 *
 * @since 1.1.0
 */
public class ProblemDetailJacksonSerializer extends StdSerializer<ProblemDetail> {

    /**
     * Effective field visibility used while writing the serialized response.
     */
    private final ProblemDetailFieldVisibility fieldVisibility;

    /**
     * Creates a serializer with the given field visibility.
     *
     * @param fieldVisibility the effective field visibility
     */
    public ProblemDetailJacksonSerializer(ProblemDetailFieldVisibility fieldVisibility) {
        super(ProblemDetail.class);
        this.fieldVisibility = fieldVisibility;
    }

    @Override
    public void serialize(ProblemDetail value, JsonGenerator gen, SerializerProvider provider) throws java.io.IOException {
        ProblemDetailJacksonSerializerSupport.writeProblemDetail(value, gen, provider, fieldVisibility);
    }
}
