package com.Wassal.service;

import com.Wassal.dto.*;
import com.Wassal.exception.AlreadyExistsException;
import com.Wassal.mapper.AuthMapper;
import com.Wassal.mapper.UserMapper;
import com.Wassal.model.ERole;
import com.Wassal.model.Role;
import com.Wassal.model.User;
import com.Wassal.repository.RoleRepository;
import com.Wassal.repository.UserRepository;
import com.Wassal.security.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthMapper authMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthService authService;

    @AfterEach
    void clearContext(){
        SecurityContextHolder.clearContext();
    }

    @Nested
    class Register{
        @Test
        void registerSuccess(){
            RegisterRequest request = registerRequestBuilder();
            User userEntity = userBuilder(null, null);
            Role defaultRole = roleBuilder();
            User savedUser = userBuilder(1L, defaultRole);
            UserResponse expectedResponse = userResponseBuilder(defaultRole);

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(userEntity);
            when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");
            when(roleRepository.findByRole(ERole.ROLE_USER)).thenReturn(Optional.of(defaultRole));
            when(userRepository.save(userEntity)).thenReturn(savedUser);
            when(userMapper.toDTO(savedUser)).thenReturn(expectedResponse);

            UserResponse actualResponse = authService.register(request);
            assertNotNull(actualResponse);
            assertEquals(expectedResponse, actualResponse);

            verify(passwordEncoder, times(1)).encode(request.password());
            verify(userRepository, times(1)).save(userEntity);
        }

        @Test
        void userAlreadyExists(){
            RegisterRequest request = registerRequestBuilder();
            when(userRepository.existsByEmail(request.email())).thenReturn(true);
            assertThrows(AlreadyExistsException.class, ()->{
                authService.register(request);
            });
            verify(userRepository, never()).save(any());
            verify(passwordEncoder, never()).encode(any());
        }
    }

    @Nested
    class Login{
        @Test
        void loginSuccess(){
            LoginRequest request = loginRequestBuilder();
            //Actual user details saved in database
            UserDetailsImpl userDetails = new UserDetailsImpl(1L, request.email(), "hashedPassword", List.of());
            Authentication auth = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
            when(auth.getPrincipal()).thenReturn(userDetails);

            when(jwtService.generateToken(auth)).thenReturn("jwt-string");
            when(refreshTokenService.createRefreshToken(userDetails.id())).thenReturn("refreshToken-uuid");

            JwtResponse expectedJwtResponse = jwtResponseBuilder();
            when(authMapper.toJwtResponse("jwt-string", userDetails)).thenReturn(expectedJwtResponse);

            ResponseCookie responseCookie = ResponseCookie.from("refreshToken", "refreshToken-uuid").build();
            when(refreshTokenService.generateRefreshTokenCookie("refreshToken-uuid")).thenReturn(responseCookie);

            AuthResponse response = authService.login(request);
            assertNotNull(response);
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertEquals("jwt-string", response.jwtResponse().accessToken());
            assertEquals("refreshToken-uuid", response.refreshCookie().getValue());
        }

        @Test
        void badCredentialsLoginFail(){
            LoginRequest request = loginRequestBuilder();
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid email or password"));
            assertThrows(BadCredentialsException.class, ()->{
                authService.login(request);
            });
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(jwtService, never()).generateToken(any(Authentication.class));
            verify(refreshTokenService, never()).createRefreshToken(any());
            verify(authMapper, never()).toJwtResponse(any(), any());
            verify(refreshTokenService, never()).generateRefreshTokenCookie(any());
        }
    }

    @Test
    void logoutSuccess(){
        doNothing().when(refreshTokenService).deleteByToken("refreshToken-uuid");
        authService.logout("refreshToken-uuid");
        verify(refreshTokenService, times(1)).deleteByToken("refreshToken-uuid");
    }

    private RegisterRequest registerRequestBuilder(){
        return new RegisterRequest(
                "firstName",
                "lastName",
                "email@email.com",
                "password"
        );
    }

    private LoginRequest loginRequestBuilder(){
        return new LoginRequest(
                "email@email.com",
                "password"
        );
    }

    private User userBuilder(Long id, Role role){
        Set<Role> rolesSet = (role == null) ? new HashSet<>() : Set.of(role);
        return User.builder()
                .id(id)
                .firstName("firstName")
                .lastName("lastName")
                .email("email@email.com")
                .password("password")
                .roles(rolesSet)
                .build();
    }

    private UserResponse userResponseBuilder(Role role){
        return new UserResponse(
                1L,
                "firstName",
                "lastName",
                "email@email.com",
                Set.of(role.getRole().name())
        );
    }

    private Role roleBuilder(){
        return Role.builder()
                .id(1)
                .role(ERole.ROLE_USER)
                .build();
    }

    private JwtResponse jwtResponseBuilder(){
        return new JwtResponse(
                "jwt-string",
                "Bearer",
                1L,
                "email@email.com",
                Set.of()
        );
    }

}