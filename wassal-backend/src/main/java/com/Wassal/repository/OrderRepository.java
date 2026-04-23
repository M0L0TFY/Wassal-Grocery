package com.Wassal.repository;

import com.Wassal.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    //Extract all orders linked to user
    Page<Order> findAllByUserId(Long userId, Pageable pageable);
    //Extract all orders linked to store
    Page<Order> findAllByStoreId(Long storeId, Pageable pageable);

    @EntityGraph(attributePaths = {"orderItems"}) //Prevents N+1 problem by using a Left Join Fetch
    Optional<Order> findOrderById(Long orderId);
}
