package com.Wassal.service;

import com.Wassal.security.UserDetailsImpl;
import com.Wassal.exception.ResourceNotFoundException;
import com.Wassal.model.User;
import com.Wassal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/*
* Loads the user data from database via userRepository
* Build UserDetailsImpl object with email, password and roles
*/

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with this email doesn't exist."));
        return UserDetailsImpl.build(user);
    }

    @Transactional(readOnly = true)
    public UserDetailsImpl loadUserById(Long id) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with this id doesn't exist."));
        return UserDetailsImpl.build(user);
    }

}
