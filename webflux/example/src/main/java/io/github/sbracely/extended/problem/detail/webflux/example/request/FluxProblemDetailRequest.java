package io.github.sbracely.extended.problem.detail.webflux.example.request;

import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.FluxConfirmPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@FluxConfirmPassword(message = "Password and confirm password do not match", fields = {"password", "confirmPassword"})
@Schema(name = "FluxProblemDetailRequest", description = "Sample request payload used by the example endpoints.")
public class FluxProblemDetailRequest {

    @NotBlank(message = "Name cannot be blank")
    @NotNull(message = "Name cannot be null")
    @Length(min = 6, max = 10, message = "Name length must be between 6-10")
    @Schema(description = "User name used by validation examples.", example = "springdoc")
    private String name;

    @NotNull(message = "Age cannot be null")
    @Range(min = 0, max = 150, message = "Age range is 0-150")
    @Schema(description = "User age used by validation examples.", example = "28", minimum = "0", maximum = "150")
    private Integer age;

    @Schema(description = "Password used by custom validation examples.", example = "password123", format = "password")
    private String password;
    @Schema(description = "Repeated password used by custom validation examples.", example = "password123", format = "password")
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
        return "FluxProblemDetailRequest{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
