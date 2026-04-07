package io.github.sbracely.extended.problem.detail.webflux.example.service;

import io.github.sbracely.extended.problem.detail.webflux.example.request.ProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.CheckName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ProblemDetailService {

    private static final Logger logger = LoggerFactory.getLogger(ProblemDetailService.class);

    @CheckName(nameIndex = 0, problemDetailRequestIndex = 1)
    public void createProblemDetail(@NotBlank(message = "name must not be blank")
                                    @NotNull(message = "name must not be null") String name,
                                    @Valid ProblemDetailRequest problemDetailRequest) {
        logger.info("createProblemDetail, name: {}, problemDetailRequest: {}", name, problemDetailRequest);
    }
}
