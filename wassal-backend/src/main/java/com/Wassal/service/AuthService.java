package com.Wassal.service;

import com.Wassal.security.UserDetailsImpl;
import com.Wassal.dto.*;
import com.Wassal.exception.AlreadyExistsException;
import com.Wassal.exception.ResourceNotFoundException;
import com.Wassal.mapper.AuthMapper;
import com.Wassal.mapper.UserMapper;
import com.Wassal.model.ERole;
import com.Wassal.model.User;
import com.Wassal.repository.RoleRepository;
import com.Wassal.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

/*
 * Handle user registration, login, refreshToken
 * Delegates logout and emptyRefreshTokenCookie to refreshTokenService to simplify AuthController
*/

@Service
@RequiredArgsConstructor
@Validated
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final UserMapper userMapper;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Transactional
    public UserResponse register(@Valid RegisterRequest request) {
        //Check if email exists in the DB
        if (userRepository.existsByEmail(request.email())) throw new AlreadyExistsException(request.email());
        //Build user object from DTO to Entity
        User user = userMapper.toEntity(request);
        //Manually set password since mapstruct cant encode it
        user.setPassword(passwordEncoder.encode(request.password()));
        //Assign default ROLE_USER upon registry
        var defaultRole = roleRepository.findByRole(ERole.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("ROLE_USER not found."));
        user.setRoles(Set.of(defaultRole));
        //Save user object to database
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public AuthResponse login(@Valid LoginRequest request) {
        //Verify email and password then calls DaoAuthenticationProvider to get user object
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        //Populate SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //Generate stateless accessToken, redis refreshToken
        String jwt = jwtService.generateToken(authentication);
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.id());
        //Build JwtDTO and RefreshCookie
        JwtResponse jwtResponse = authMapper.toJwtResponse(jwt, userDetails);
        ResponseCookie cookie = refreshTokenService.generateRefreshTokenCookie(refreshToken);
        return new AuthResponse(jwtResponse, cookie);
    }

    public AuthResponse refreshToken(String refreshTokenFromCookie) {
        //Extract email from expired cookie
        return refreshTokenService.findUserIdByToken(refreshTokenFromCookie)
                .map(userId -> {
                    //Get the current userDetails object (checks postgresDB for new roles also)
                    UserDetailsImpl userDetails = userDetailsServiceImpl.loadUserById(userId);
                    //Delete old token from redis and create a new one
                    refreshTokenService.deleteByToken(refreshTokenFromCookie);
                    String newRefreshToken = refreshTokenService.createRefreshToken(userDetails.id());
                    //Create new JWT Access Token and a new cookie
                    String newJwt = jwtService.generateToken(userDetails);
                    //Build JwtDTO and RefreshCookie
                    JwtResponse jwtResponse = authMapper.toJwtResponse(newJwt, userDetails);
                    ResponseCookie newCookie = refreshTokenService.generateRefreshTokenCookie(newRefreshToken);
                    return new AuthResponse(jwtResponse, newCookie);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found."));
    }

    public void logout(String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
    }
    public ResponseCookie getEmptyRefreshTokenCookie() {
        return refreshTokenService.getEmptyRefreshTokenCookie();
    }

}
