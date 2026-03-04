package com.Wassal.repository;

import com.Wassal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //Finds a user by their id and returns the roles with it (since roles are LAZY fetched)
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id") //Ensures EAGER fetch for roles
    Optional<User> findByIdWithRoles(@Param("id") Long id);
    //Finds a user by their email and returns the roles with it (since roles are LAZY fetched)
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email") //Ensures EAGER fetch for roles
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    //Checks if a user with this email already exists
    boolean existsByEmail(String email);
}
