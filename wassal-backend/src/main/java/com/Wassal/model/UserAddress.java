package com.Wassal.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "user_addresses")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "street", nullable = false)
    private String street;

    @Enumerated(EnumType.STRING)
    @Column(name = "building_type", nullable = false)
    private EBuilding buildingType;
    @Column(name = "building_name", nullable = false)
    private String buildingName;
    @Column(name = "floor_number", nullable = false)
    private String floorNumber;
    @Column(name = "apartment_number", nullable = false)
    private String apartmentNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
