package com.Wassal.service;

import com.Wassal.dto.UserAddressRequest;
import com.Wassal.dto.UserAddressResponse;
import com.Wassal.exception.ResourceNotFoundException;
import com.Wassal.mapper.UserAddressMapper;
import com.Wassal.model.UserAddress;
import com.Wassal.repository.UserAddressRepository;
import com.Wassal.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class UserAddressService {
    private final UserAddressMapper userAddressMapper;
    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("#userId == authentication.principal.id")
    public List<UserAddressResponse> getAllUserAddresses(Long userId) {
        return userAddressMapper.toDTOList(userAddressRepository.findAllByUserId(userId));
    }
    @Transactional(readOnly = true)
    public UserAddress findUserAddressById(Long addressId) {
        return userAddressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("User Address with this id doesn't exist."));
    }

    @Transactional
    @PreAuthorize("#userId == authentication.principal.id")
    public UserAddressResponse addAddress(Long userId, @Valid UserAddressRequest request) {
        //Build UserAddress object from UserAddressRequest
        UserAddress address = userAddressMapper.toEntity(request);
        //Set the user object property in UserAddress since it's ignored in UserAddressMapper
        address.setUser(userRepository.getReferenceById(userId)); //getReference since we don't need other user data
        return userAddressMapper.toDTO(userAddressRepository.save(address));
    }

    @Transactional
    @PreAuthorize("@userAddressSecurity.isOwnerOfAddress(#userAddressId)")
    public UserAddressResponse updateAddress(Long userAddressId, @Valid UserAddressRequest request) {
        //Fetch the UserAddress we want to update by its id
        UserAddress address = userAddressRepository.findById(userAddressId)
                .orElseThrow(() -> new ResourceNotFoundException("User address with this id doesn't exist."));
        //Build UserAddress object from the updated UserAddressRequest
        userAddressMapper.toUpdatedEntity(address, request);
        return userAddressMapper.toDTO(userAddressRepository.save(address));
    }

    @Transactional
    @PreAuthorize("@userAddressSecurity.isOwnerOfAddress(#userAddressId)")
    public void deleteAddress(Long userAddressId) {
        userAddressRepository.deleteById(userAddressId);
    }

}
