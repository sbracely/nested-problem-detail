package com.github.sbracely.extended.problem.detail.test.flux.request;

import com.github.sbracely.extended.problem.detail.test.flux.request.valid.annocation.ConfirmPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@ConfirmPassword(message = "Password and confirm password do not match", fields = {"password", "confirmPassword"})
public class ProblemDetailRequest {

    @NotBlank(message = "Name cannot be blank")
    @NotNull(message = "Name cannot be null")
    @Length(min = 6, max = 10, message = "Name length must be between 6-10")
    private String name;

    @NotNull(message = "Age cannot be null")
    @Range(min = 0, max = 150, message = "Age range is 0-150")
    private Integer age;

    private String password;
    private String confirmPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public String toString() {
        return "ProblemDetailRequest{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
