package io.github.sbracely.extended.problem.detail.webflux.example.service;

import io.github.sbracely.extended.problem.detail.webflux.example.request.FluxProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.FluxCheckName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class FluxProblemDetailService {

    private static final Logger logger = LoggerFactory.getLogger(FluxProblemDetailService.class);

    @FluxCheckName(nameIndex = 0, problemDetailRequestIndex = 1)
    public void createProblemDetail(@NotBlank(message = "{flux.example.service.name.blank}")
                                    @NotNull(message = "{flux.example.service.name.missing}") String name,
                                    @Valid FluxProblemDetailRequest problemDetailRequest) {
        logger.info("createProblemDetail, name: {}, problemDetailRequest: {}", name, problemDetailRequest);
    }
}
