package com.Wassal.dto;

import com.Wassal.model.EBuilding;

public record UserAddressResponse(
        Long id,
        String label,
        String phoneNumber,
        String city,
        String street,
        EBuilding buildingType,
        String buildingName,
        String floorNumber,
        String apartmentNumber
) {
}
