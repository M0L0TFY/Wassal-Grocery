package com.Wassal.repository;

import com.Wassal.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    //Fetch all addresses linked to a user
    List<UserAddress> findAllByUserId(Long userId);
}
