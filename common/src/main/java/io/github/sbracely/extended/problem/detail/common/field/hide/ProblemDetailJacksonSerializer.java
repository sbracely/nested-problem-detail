package io.github.sbracely.extended.problem.detail.common.field.hide;

import org.springframework.http.ProblemDetail;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * Jackson serializer for ProblemDetail that applies field visibility rules.
 *
 * @since 1.1.0
 */
public class ProblemDetailJacksonSerializer extends StdSerializer<ProblemDetail> {

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
    public void serialize(ProblemDetail value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
        ProblemDetailJacksonSerializerSupport.writeProblemDetail(value, gen, provider, fieldVisibility);
    }
}
