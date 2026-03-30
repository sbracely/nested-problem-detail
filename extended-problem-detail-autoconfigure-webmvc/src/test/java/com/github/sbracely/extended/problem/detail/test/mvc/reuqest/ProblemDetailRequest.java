package com.github.sbracely.extended.problem.detail.test.mvc.reuqest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.github.sbracely.extended.problem.detail.test.mvc.reuqest.valid.annocation.ConfirmPassword;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@ToString
@ConfirmPassword(message = "密码与确认密码不一致", fields = {"password", "confirmPassword"})
public class ProblemDetailRequest {

    @NotBlank(message = "姓名不可为空")
    @NotNull(message = "姓名不能为null")
    @Length(min = 6, max = 10, message = "姓名长度范围 6-10")
    private String name;

    @NotNull(message = "年龄不可为空")
    @Range(min = 0, max = 150, message = "年龄范围 0-150")
    private Integer age;

    private String password;
    private String confirmPassword;
}
