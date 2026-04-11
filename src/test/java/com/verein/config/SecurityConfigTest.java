package com.verein.config;

import com.verein.entity.User;
import com.verein.repository.UserRepository;
import com.verein.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testPublicEndpoints() throws Exception {
        mockMvc.perform(get("/api/clubs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegister() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newuser\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void testLogin() throws Exception {
        User user = new User();
        user.setUsername("loginuser");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole("USER");
        user.setEnabled(true);
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"loginuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testAccessWithValidToken() throws Exception {
        User user = new User();
        user.setUsername("tokenuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole("ADMIN");
        user.setEnabled(true);
        userRepository.save(user);

        String token = jwtUtil.generateToken("tokenuser", "ADMIN");

        mockMvc.perform(get("/api/clubs")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testAccessWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/clubs")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}