package com.Wassal.repository;

import com.Wassal.model.ECategory;
import com.Wassal.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    //Extracts all products in a store
    Page<Product> findAllByStoreId(Long storeId, Pageable pageable);
    //Extracts all products in a store by its category
    Page<Product> findAllByStoreIdAndCategory(Long storeId, ECategory category, Pageable pageable);
    //Checks for existing product
    boolean existsByStoreIdAndNameAndBrand(Long storeId, String name, String brand);
    Optional<Product> findByIdAndStoreId(String productId, Long storeId);
    void deleteAllByStoreId(Long storeId);
    //Decrease stock quantity of a product
    @Query("{ '_id': ?0, 'quantity': { $gte: ?1 } }") //Checks if quantity is >= quantity
    @Update("{ '$inc': { 'quantity': ?2 } }") //Decrements the quantity by decrementAmount
    long decreaseStock(String productId, Integer quantity, Integer decrementAmount);
}
