package com.example.tsp_rest_user;

import com.example.databaseCore.Entities.User.User;
import com.example.databaseCore.Pojos.User.Req.PasswordChanged;
import com.example.databaseCore.Pojos.User.Req.UserRegister;
import com.example.databaseCore.Pojos.User.Res.UserRes;
import com.example.databaseCore.Repositories.User.UserRepository;
import com.example.tsp_rest_user.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstname("John");
        testUser.setLastname("Smith");
        testUser.setEmail("john@email.com");
        testUser.setPassword("encodedPassword");
        testUser.setLoggedIn(true);
        testUser.setPasswordChanged(false);
    }


    //    Sign up

    @Test
    void shouldRegisterUserSuccessfully() {
        UserRegister userRegister = new UserRegister("John", "Smith", "john@example.com", "pass", "pass");

        when(userRepository.findUserByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encodedPassword123");

        ResponseEntity<String> response = userService.register(userRegister);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldReturnConflictWhenEmailExists() {
        UserRegister userRegister = new UserRegister("John", "Smith", "john@example.com", "pass", "pass");

        when(userRepository.findUserByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        ResponseEntity<String> response = userService.register(userRegister);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void shouldReturnNotAcceptableWhenPasswordsDoNotMatch() {
        UserRegister userRegister = new UserRegister("John", "Smith", "john@example.com", "pass", "pass1");

        ResponseEntity<String> response = userService.register(userRegister);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
    }

    //    Get User

    @Test
    void shouldReturnUserByIdSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<UserRes> response = userService.getUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstname());
        assertEquals("Smith", response.getBody().getLastname());
    }

    @Test
    void shouldReturnNotFoundWhenUserNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<UserRes> response = userService.getUser(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    //    Logout

    @Test
    void shouldLogoutUserSuccessfully() {
        testUser.setLoggedIn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<Void> response = userService.logout(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository).save(testUser);
    }

    //    Password

    @Test
    void shouldVerifyPasswordSuccessfully() {
        PasswordChanged pwd = new PasswordChanged("pass", "pass");

        testUser.setPasswordChanged(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("pass", "encodedPassword")).thenReturn(true);

        ResponseEntity<Void> response = userService.verify_password(1L, pwd);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(testUser.getPasswordChanged());
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        PasswordChanged pwd = new PasswordChanged("pass", "pass");

        testUser.setPasswordChanged(true);  // 🔴 konieczne!
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("pass")).thenReturn("newEncoded");

        ResponseEntity<Void> response = userService.password_change(1L, pwd);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("newEncoded", testUser.getPassword());
        assertFalse(testUser.getPasswordChanged());
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldReturnNotAcceptableWhenPasswordChangedIsFalse() {
        PasswordChanged pwd = new PasswordChanged("pass", "pass");

        testUser.setPasswordChanged(false); // 🔴 Niezweryfikowany użytkownik
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<Void> response = userService.password_change(1L, pwd);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        verify(userRepository, never()).save(any());
    }


    //    Delete

    @Test
    void shouldDeleteAccountSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<Void> response = userService.account_delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository).delete(testUser);
    }


}
