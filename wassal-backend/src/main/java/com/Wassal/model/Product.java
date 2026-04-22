package com.Wassal.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "products")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
//Index for products to be able to search a product across multiple stores
@CompoundIndex(def = "{'storeId': 1, 'name': 1, 'brand': 1}", unique = true)
public class Product {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    private String name;
    private String brand;
    @Field("image_url")
    private String imageURL;
    private String description;
    private ECategory category;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;
    private Integer quantity;

    @Builder.Default //Returns an empty hashmap instead of null
    private Map<String, Object> attributes = new HashMap<>(); //for dynamic data between different products

    @CreatedDate
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @Setter(AccessLevel.NONE)
    private Long storeId; //Surrogate ID reference to the postgres stores table
}
