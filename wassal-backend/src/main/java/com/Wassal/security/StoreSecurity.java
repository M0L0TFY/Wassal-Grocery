package com.Wassal.security;

import com.Wassal.repository.ProductRepository;
import com.Wassal.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("storeSecurity")
@RequiredArgsConstructor
public class StoreSecurity {
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public boolean isManagerOfStore(Long storeId) {
        //Get Authentication object from SecurityContextHolder
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        //Check if user is logged in
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl user)) return false;
        //Return true if the store object is assigned to the manager, otherwise return false
        return storeRepository.findByIdWithManager(storeId)
                .map(store -> store.getManager() != null && store.getManager().getId().equals(user.id()))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isManagerOfProduct(String productId) {
        return productRepository.findById(productId) //Fetch product by its id
                .map(product -> isManagerOfStore(product.getStoreId())) //Check the manager
                .orElse(false); //Return false if product doesn't exist
    }

}
