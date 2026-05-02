package io.github.sbracely.extended.problem.detail.webmvc.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webmvc.example.request.MvcProblemDetailRequest.Address;
import io.github.sbracely.extended.problem.detail.webmvc.example.valid.annotation.MvcValidAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MvcValidAddressValidator implements ConstraintValidator<MvcValidAddress, Address> {

    @Override
    public boolean isValid(Address address, ConstraintValidatorContext context) {
        if (address == null || address.getStreet() == null || address.getGeo() == null || address.getGeo().getLocation() == null) {
            return true;
        }
        String street = address.getStreet().trim();
        String code = address.getGeo().getLocation().getCode();
        if (street.isEmpty() || code == null || code.trim().isEmpty()) {
            return true;
        }
        return !street.equalsIgnoreCase(code.trim());
    }
}
