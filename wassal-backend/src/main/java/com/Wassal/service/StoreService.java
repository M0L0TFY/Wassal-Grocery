package com.Wassal.service;

import com.Wassal.dto.DetailedStoreResponse;
import com.Wassal.dto.StoreRequest;
import com.Wassal.dto.StoreResponse;
import com.Wassal.exception.AlreadyExistsException;
import com.Wassal.exception.ResourceNotFoundException;
import com.Wassal.mapper.StoreMapper;
import com.Wassal.model.ERole;
import com.Wassal.model.Store;
import com.Wassal.model.User;
import com.Wassal.repository.ProductRepository;
import com.Wassal.repository.StoreRepository;
import com.Wassal.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<StoreResponse> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable).map(storeMapper::toDTO);
    }
    @Transactional(readOnly = true)
    public StoreResponse getStoreById(Long storeId) {
        return storeRepository.findById(storeId).map(storeMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Store with this id doesn't exist."));
    }
    @Transactional(readOnly = true)
    public Store findStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store with this id doesn't exist."));
    }
    @Transactional(readOnly = true)
    public StoreResponse getStoreByName(String storeName) {
        return storeRepository.findByName(storeName).map(storeMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Store with this name doesn't exist."));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public StoreResponse createStore(@Valid StoreRequest request) {
        //Check if store with this name already exists
        if (storeRepository.existsByName(request.name())) throw new AlreadyExistsException("Store with this name already exists.");
        //Map request to store object then save to postgresDB
        Store store = storeMapper.toEntity(request);
        return storeMapper.toDTO(storeRepository.save(store));
    }

    @Transactional
    @PreAuthorize("@storeSecurity.isManagerOfStore(#storeId)")
    public StoreResponse updateStore(Long storeId, @Valid StoreRequest request) {
        //Fetch store object from storeId then save it to postgresDB
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store with this id doesn't exist."));
        storeMapper.toUpdatedEntity(store, request);
        return storeMapper.toDTO(storeRepository.save(store));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public DetailedStoreResponse getStoreWithManager(Long storeId) {
        return storeRepository.findByIdWithManager(storeId).map(storeMapper::toDetailedDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Store with this id doesn't exist."));
    }
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public DetailedStoreResponse assignManagerToStore(Long storeId, Long userId) {
        //Extract user object from userId and check if user has ROLE_STORE_MANAGER
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with this id doesn't exist."));
        boolean isManager = user.getRoles().stream()
                .anyMatch(role -> role.getRole().equals(ERole.ROLE_STORE_MANAGER));
        if (!isManager) throw new AccessDeniedException("User doesn't have ROLE_STORE_MANAGER.");
        //Extract store object from storeId
        Store store = storeRepository.findByIdWithManager(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found."));
        store.setManager(user);
        return storeMapper.toDetailedDTO(storeRepository.save(store));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @storeSecurity.isManagerOfStore(#storeId)")
    public void deleteStore(Long storeId) {
        //Check if a store exists by its id (for admin request since it doesn't hit storeSecurity)
        if (!storeRepository.existsById(storeId)) throw new ResourceNotFoundException("Store with this id doesn't exist.");
        //Delete all the products (in Mongo) linked to the store (in Postgres)
        productRepository.deleteAllByStoreId(storeId);
        storeRepository.deleteById(storeId);
    }

}
