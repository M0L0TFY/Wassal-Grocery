package com.Wassal.model;

import jakarta.persistence.Embeddable;
import lombok.*;

/*
* Snapshot of UserAddress to link with an order
* Since if we linked with UserAddress_id it would change old orders when updating an Address
*/

@Embeddable
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderAddress {
    private String phoneNumber;

    private String city;
    private String street;

    private EBuilding buildingType;
    private String buildingName;
    private String floorNumber;
    private String apartmentNumber;
}
