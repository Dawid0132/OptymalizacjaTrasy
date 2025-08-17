package com.example.tsp_rest_user;

import com.example.databaseCore.Entities.User.User;
import com.example.databaseCore.Pojos.User.Req.PasswordChanged;
import com.example.databaseCore.Pojos.User.Req.UserRegister;
import com.example.databaseCore.Repositories.User.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class TspRestUserApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        userRepository.deleteAll();

        if (!testInfo.getTestMethod().get().getName().equals("shouldRegisterUserSuccessfully") &&
                !testInfo.getTestMethod().get().getName().equals("shouldNotRegisterUserSuccessfully")) {

            testUser = new User();
            testUser.setFirstname("John");
            testUser.setLastname("Smith");
            testUser.setEmail("john@email.com");
            testUser.setPassword(passwordEncoder.encode("ASdfghjkl123!"));

            userRepository.save(testUser);
        }
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRegister user = new UserRegister("John", "Smith", "john@email.com", "ASdfghjkl123!",
                "ASdfghjkl123!");

        mockMvc.perform(post("/rest/user/v2/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotRegisterUserSuccessfully() throws Exception {
        UserRegister user = new UserRegister("John", "Smith", "john@gmail.com", "abc123",
                "abc123");

        mockMvc.perform(post("/rest/user/v2/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUserByIdSuccessfully() throws Exception {

        mockMvc.perform(get("/rest/user/v2/user/get/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").exists())
                .andExpect(jsonPath("$.lastname").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.lastLogin").exists());
    }

    @Test
    void shouldNotReturnUserByIdSuccessfully() throws Exception {

        mockMvc.perform(get("/rest/user/v2/user/get/" + (testUser.getId() + 1L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldLogoutUserAndSetLoggedInFalse() throws Exception {

        User user = userRepository.findById(testUser.getId()).get();
        user.setLoggedIn(true);
        userRepository.save(user);

        mockMvc.perform(put("/rest/user/v2/logout/" + user.getId()))
                .andExpect(status().isNoContent());


        User updatedUser = userRepository.findById(user.getId()).orElseThrow();

        assertFalse(updatedUser.getLoggedIn(), "User should be logged out (loggedIn=false)");
    }

    @Test
    void shouldVerifyPasswordAndSetPasswordChangedTrue() throws Exception {

        PasswordChanged passwordChanged = new PasswordChanged();
        passwordChanged.setPassword("ASdfghjkl123!");
        passwordChanged.setNew_password("ASdfghjkl123!");

        String jsonContent = new ObjectMapper().writeValueAsString(passwordChanged);


        mockMvc.perform(post("/rest/user/v2/password/verify/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk());


        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(updatedUser.getPasswordChanged(), "PasswordChanged flag should be true after verification");
    }

    @Test
    void shouldChangePasswordAndReturnNoContent() throws Exception {

        User user = userRepository.findById(testUser.getId()).get();
        user.setPasswordChanged(Boolean.TRUE);
        userRepository.save(user);

        PasswordChanged passwordChanged = new PasswordChanged();
        passwordChanged.setPassword("NEwpass123!");
        passwordChanged.setNew_password("NEwpass123!");

        String jsonContent = new ObjectMapper().writeValueAsString(passwordChanged);

        mockMvc.perform(put("/rest/user/v2/password/change/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isNoContent());

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();

        assertTrue(passwordEncoder.matches(passwordChanged.getNew_password(), updatedUser.getPassword()));
        assertFalse(updatedUser.getPasswordChanged());
    }

    @Test
    void shouldDeleteUserAccountAndReturnNoContent() throws Exception {

        Long userId = testUser.getId();
        assertTrue(userRepository.existsById(userId), "Użytkownik powinien istnieć przed usunięciem");

        mockMvc.perform(delete("/rest/user/v2/account/delete/" + userId))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(userId), "Użytkownik powinien zostać usunięty z bazy danych");
    }

}

