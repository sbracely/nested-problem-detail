package org.example.exceptionhandlerexample.test.file;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptionhandlerexample.response.NestedProblemDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONTENT_TOO_LARGE;

@Slf4j
@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.servlet.multipart.max-file-size=1")
class FileTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void maxUploadSizeExceededException() {
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

        String uri = "/mvc-problem-detail/file-max-size";
        ResponseEntity<NestedProblemDetail> response = restTemplate.postForEntity(
                "http://localhost:" + port + uri,
                new HttpEntity<>(body, headers),
                NestedProblemDetail.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONTENT_TOO_LARGE);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
        NestedProblemDetail nestedProblemDetail = response.getBody();
        assertThat(nestedProblemDetail).isNotNull();
        assertThat(nestedProblemDetail.getDetail()).isEqualTo("Maximum upload size exceeded");
        assertThat(nestedProblemDetail.getInstance()).isEqualTo(URI.create(uri));
        assertThat(nestedProblemDetail.getStatus()).isEqualTo(CONTENT_TOO_LARGE.value());
        assertThat(nestedProblemDetail.getTitle()).isEqualTo(CONTENT_TOO_LARGE.getReasonPhrase());
        assertThat(nestedProblemDetail.getErrors()).isNull();
    }
}
