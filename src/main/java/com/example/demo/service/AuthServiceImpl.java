package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.RegisterResponse;
import com.example.demo.entity.User;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return new RegisterResponse(false, "Email already exists");
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        // Encrypt password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return new RegisterResponse(true, "User Registered Successfully");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        System.out.println("Request Email: " + request.getEmail());
        System.out.println("Request Password: " + request.getPassword());


        Optional<User> optionalUser =
                userRepository.findByEmail(request.getEmail());
        System.out.println("User Found: " + optionalUser.isPresent());
        if (optionalUser.isEmpty()) {
            return new LoginResponse(false,
                    "Invalid Email or Password",
                    null);
        }

        User user = optionalUser.get();
        System.out.println("DB Email: " + user.getEmail());
        System.out.println("DB Password: " + user.getPassword());

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return new LoginResponse(false,
                    "Invalid Email or Password",
                    null);
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                true,
                "Login Successful",
                token
        );
    }
}
