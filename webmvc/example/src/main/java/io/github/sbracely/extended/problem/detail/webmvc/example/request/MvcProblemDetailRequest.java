package io.github.sbracely.extended.problem.detail.webmvc.example.request;

import io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation.MvcConfirmPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@MvcConfirmPassword(message = "{mvc.example.request.password.confirmation-mismatch}", fields = {"password", "confirmPassword"})
@Schema(name = "MvcProblemDetailRequest", description = "Sample request payload used by the example endpoints.")
public class MvcProblemDetailRequest {

    @NotBlank(message = "{mvc.example.request.name.blank}")
    @NotNull(message = "{mvc.example.request.name.missing}")
    @Length(min = 6, max = 10, message = "{mvc.example.request.name.length}")
    @Schema(description = "User name used by validation examples.", example = "springdoc")
    private String name;

    @NotNull(message = "{mvc.example.request.age.missing}")
    @Range(min = 0, max = 150, message = "{mvc.example.request.age.range}")
    @Schema(description = "User age used by validation examples.", example = "28", minimum = "0", maximum = "150")
    private Integer age;

    @Schema(description = "Password used by custom validation examples.", example = "password123", format = "password")
    private String password;
    @Schema(description = "Repeated password used by custom validation examples.", example = "password123", format = "password")
    private String confirmPassword;

    @Valid
    @Schema(description = "Nested payload used by validation examples.")
    private Address address;

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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "MvcProblemDetailRequest{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", address=" + address +
                '}';
    }

    @Schema(name = "MvcProblemDetailAddress", description = "Nested request payload used by validation examples.")
    public static class Address {

        @NotBlank(message = "{mvc.example.request.address.street.blank}")
        @Schema(description = "Street name used by nested validation examples.", example = "Main St")
        private String street;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "street='" + street + '\'' +
                    '}';
        }
    }
}
