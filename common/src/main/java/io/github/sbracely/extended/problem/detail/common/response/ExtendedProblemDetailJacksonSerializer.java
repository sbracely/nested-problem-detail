package io.github.sbracely.extended.problem.detail.common.response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Jackson serializer for ExtendedProblemDetail that applies field visibility rules.
 *
 * @since 1.1.0
 */
public class ExtendedProblemDetailJacksonSerializer extends StdSerializer<ExtendedProblemDetail> {

    private final ProblemDetailFieldVisibility fieldVisibility;

    /**
     * Creates a serializer with the given field visibility.
     *
     * @param fieldVisibility the effective field visibility
     */
    public ExtendedProblemDetailJacksonSerializer(ProblemDetailFieldVisibility fieldVisibility) {
        super(ExtendedProblemDetail.class);
        this.fieldVisibility = fieldVisibility;
    }

    @Override
    public void serialize(ExtendedProblemDetail value, JsonGenerator gen, SerializerProvider provider) throws java.io.IOException {
        ProblemDetailJacksonSerializerSupport.writeProblemDetail(value, gen, provider, fieldVisibility);
    }
}
