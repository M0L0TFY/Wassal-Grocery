package com.Wassal.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "order_items")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "product_id", nullable = false, updatable = false)
    private String productId;
    @Column(name = "product_name", nullable = false, updatable = false)
    private String productName;
    @Column(name = "product_price", nullable = false, updatable = false)
    private BigDecimal productPrice;
    @Column(name = "quantity", nullable = false, updatable = false)
    private Integer quantity;

    @Column(name = "sub_total", nullable = false, updatable = false)
    private BigDecimal subTotal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Order order;
}
