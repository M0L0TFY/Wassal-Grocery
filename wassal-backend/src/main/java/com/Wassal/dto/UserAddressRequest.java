package com.Wassal.dto;

import com.Wassal.model.EBuilding;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserAddressRequest(
        String label,
        @NotBlank(message = "Phone number required.")
        @Pattern(regexp = "^(\\+201[0-9]{9})$", message = "Phone number must start with +20")
        String phoneNumber,
        @NotBlank(message = "City required.")
        String city,
        @NotBlank(message = "Street name required.")
        String street,
        @NotNull(message = "Building type required.")
        EBuilding buildingType,
        @NotBlank(message = "Building name required.")
        String buildingName,
        @NotBlank(message = "Floor number required.")
        String floorNumber,
        @NotBlank(message = "Apartment number required.")
        String apartmentNumber
) {
}
