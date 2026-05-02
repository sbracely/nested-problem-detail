package io.github.sbracely.extended.problem.detail.webflux.example.valid.validator;

import io.github.sbracely.extended.problem.detail.webflux.example.request.FluxProblemDetailRequest.Address;
import io.github.sbracely.extended.problem.detail.webflux.example.valid.annotation.FluxValidAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FluxValidAddressValidator implements ConstraintValidator<FluxValidAddress, Address> {

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
