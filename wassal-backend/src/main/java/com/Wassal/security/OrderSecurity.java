package com.Wassal.security;

import com.Wassal.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public boolean isOwnerOfOrder(Long orderId) {
        //Get Authentication object from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Check if user is logged in
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl user)) return false;
        //Return true if logged in userId is equal to order userId, otherwise return false
        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getId().equals(user.id()))
                .orElse(false);
    }

}
