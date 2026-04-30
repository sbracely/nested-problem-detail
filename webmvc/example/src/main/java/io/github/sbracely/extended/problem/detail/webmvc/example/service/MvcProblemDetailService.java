package io.github.sbracely.extended.problem.detail.webmvc.example.service;

import io.github.sbracely.extended.problem.detail.webmvc.example.request.MvcProblemDetailRequest;
import io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation.MvcCheckName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class MvcProblemDetailService {

    private static final Logger logger = LoggerFactory.getLogger(MvcProblemDetailService.class);

    @MvcCheckName(nameIndex = 0, problemDetailRequestIndex = 1)
    public void createProblemDetail(@NotBlank(message = "{mvc.example.validation.service.name.not-blank}")
                                    @NotNull(message = "{mvc.example.validation.service.name.not-null}") String name,
                                    @Valid MvcProblemDetailRequest problemDetailRequest) {
        logger.info("createProblemDetail, name: {}, problemDetailRequest: {}", name, problemDetailRequest);
    }
}
