package com.github.sbracely.extended.problem.detail.test.mvc.service;

import com.github.sbracely.extended.problem.detail.test.mvc.reuqest.ProblemDetailRequest;
import com.github.sbracely.extended.problem.detail.test.mvc.reuqest.valid.annocation.CheckName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class ProblemDetailService {


    @CheckName(nameIndex = 0, problemDetailRequestIndex = 1)
    public Void createProblemDetail(@NotBlank(message = "name must not be blank")
                                    @NotNull(message = "name must not be null") String name,
                                    @Valid ProblemDetailRequest problemDetailRequest) {
        log.info("createProblemDetail, name: {}, problemDetailRequest: {}", name, problemDetailRequest);
        return null;
    }
}
