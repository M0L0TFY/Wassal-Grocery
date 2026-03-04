package com.Wassal.security;

import com.Wassal.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userAddressSecurity")
@RequiredArgsConstructor
public class UserAddressSecurity {
    private final UserAddressRepository userAddressRepository;

    @Transactional(readOnly = true)
    public boolean isOwnerOfAddress(Long userAddressId) {
        //Get Authentication object from SecurityContextHolder
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        //Check if user is logged-in
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl user)) return false;
        //Return true if the User in UserAddress is equal to the logged-in user id, else return false
        return userAddressRepository.findById(userAddressId)
                .map(userAddress -> userAddress.getUser().getId().equals(user.id()))
                .orElse(false);
    }
}
