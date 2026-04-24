package com.Wassal.dto;

import com.Wassal.model.EBuilding;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserAddressRequest(
        @Schema(example = "myAddressLabel")
        String label,
        @Schema(example = "+201012345678")
        @NotBlank(message = "Phone number required.")
        @Pattern(regexp = "^(\\+201[0-9]{9})$", message = "Phone number must start with +20")
        String phoneNumber,
        @Schema(example = "Cairo")
        @NotBlank(message = "City required.")
        String city,
        @Schema(example = "myStreet")
        @NotBlank(message = "Street name required.")
        String street,
        @Schema(example = "APARTMENT")
        @NotNull(message = "Building type required.")
        EBuilding buildingType,
        @Schema(example = "myBuildingName")
        @NotBlank(message = "Building name required.")
        String buildingName,
        @Schema(example = "myFloorNumber")
        @NotBlank(message = "Floor number required.")
        String floorNumber,
        @Schema(example = "myApartmentNumber")
        @NotBlank(message = "Apartment number required.")
        String apartmentNumber
) {
}
