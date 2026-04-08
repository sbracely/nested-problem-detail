package io.github.sbracely.extended.problem.detail.webmvc.example.service;

import io.github.sbracely.extended.problem.detail.webmvc.example.request.Boot4MvcProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation.Boot4MvcCheckName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class Boot4MvcProblemDetailService {

    private static final Logger logger = LoggerFactory.getLogger(Boot4MvcProblemDetailService.class);

    @Boot4MvcCheckName(nameIndex = 0, problemDetailRequestIndex = 1)
    public void createProblemDetail(@NotBlank(message = "name must not be blank")
                                    @NotNull(message = "name must not be null") String name,
                                    @Valid Boot4MvcProblemDetailRequest problemDetailRequest) {
        logger.info("createProblemDetail, name: {}, problemDetailRequest: {}", name, problemDetailRequest);
    }
}
