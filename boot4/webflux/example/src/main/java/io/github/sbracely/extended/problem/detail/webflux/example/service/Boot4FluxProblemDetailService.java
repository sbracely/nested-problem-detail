package io.github.sbracely.extended.problem.detail.webflux.example.service;

import io.github.sbracely.extended.problem.detail.webflux.example.request.Boot4FluxProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.Boot4FluxCheckName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class Boot4FluxProblemDetailService {

    private static final Logger logger = LoggerFactory.getLogger(Boot4FluxProblemDetailService.class);

    @Boot4FluxCheckName(nameIndex = 0, problemDetailRequestIndex = 1)
    public void createProblemDetail(@NotBlank(message = "name must not be blank")
                                    @NotNull(message = "name must not be null") String name,
                                    @Valid Boot4FluxProblemDetailRequest problemDetailRequest) {
        logger.info("createProblemDetail, name: {}, problemDetailRequest: {}", name, problemDetailRequest);
    }
}
