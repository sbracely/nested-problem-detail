package io.github.sbracely.extended.problem.detail.webmvc.example.contract;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.sbracely.extended.problem.detail.common.response.ExtendedProblemDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Strict OpenAPI contract tests for the <b>multipart-limit</b> configuration scenario (WebMVC).
 * <p>
 * Starts a real HTTP server with {@code spring.servlet.multipart.max-file-size=1} to verify
 * that {@code maxUploadSizeExceededException} matches its documented OpenAPI example.
 */
@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.servlet.multipart.max-file-size=1")
class MvcOpenApiMultipartContractTests {

    private static final String SCENARIO = "multipart-limit";
    private static final String BASE = "/mvc-extended-problem-detail";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void maxUploadSizeExceededExceptionContractMatches() throws Exception {
        JsonNode apiDocs = MvcOpenApiContractTestSupport.fetchApiDocs(mockMvcTester);
        JsonNode docExample = MvcOpenApiContractTestSupport.extractDocumentedExample(
                apiDocs, BASE + "/max-upload-size-exceeded-exception", "post");
        assertThat(docExample)
                .as("documented example for maxUploadSizeExceededException should be present").isNotNull();

        byte[] largeContent = new byte[2];
        ByteArrayResource resource = new ByteArrayResource(largeContent) {
            @Override
            public String getFilename() {
                return "large-test-file.txt";
            }
        };
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        String uri = BASE + "/max-upload-size-exceeded-exception";
        ResponseEntity<ExtendedProblemDetail> response = testRestTemplate.postForEntity(
                "http://localhost:" + port + uri,
                new HttpEntity<>(body, headers),
                ExtendedProblemDetail.class);

        assertThat(response.getStatusCode().value()).isEqualTo(413);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);

        ExtendedProblemDetail actual = response.getBody();
        MvcOpenApiContractTestSupport.assertContractMatches(actual, docExample);
    }
}
