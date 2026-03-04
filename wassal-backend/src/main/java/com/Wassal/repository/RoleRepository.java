package com.Wassal.repository;

import com.Wassal.model.ERole;
import com.Wassal.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    //Finds role by its roleName
    Optional<Role> findByRole(ERole role);
}
