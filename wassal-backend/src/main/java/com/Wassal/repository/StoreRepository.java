package com.Wassal.repository;

import com.Wassal.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    //Finds a store by its id and returns the manager with it (since roles are LAZY fetched)
    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.manager WHERE s.id = :id") //Ensures EAGER fetch for manager
    Optional<Store> findByIdWithManager(@Param("id") Long id);

    //Find store by its name
    Optional<Store> findByName(String storeName);
    //Checks if a store with this name already exists
    boolean existsByName(String name);
}
