package com.Wassal.dto;

import com.Wassal.model.EBuilding;

public record OrderAddressResponse(
        String phoneNumber,
        String city,
        String street,
        EBuilding buildingType,
        String buildingName,
        String floorNumber,
        String apartmentNumber
) {
}
