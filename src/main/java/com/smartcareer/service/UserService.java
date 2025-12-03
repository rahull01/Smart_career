package com.smartcareer.service;

import com.smartcareer.model.User;
import com.smartcareer.repository.UserRepository;
import com.smartcareer.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ✅ LOGIN
    public String login(String email, String password) throws Exception {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Invalid password");
        }

        // ✅ JWT Token return
        return jwtUtil.generateToken(email);
    }

    // ✅ REGISTER
    public User register(User user) throws Exception {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Email already registered!");
        }

        // ✅ Hash password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
}
