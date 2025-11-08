package com.emotionalai.service;

import com.emotionalai.model.User;
import com.emotionalai.repository.AuthRepository;

public class AuthService {

    private final AuthRepository authRepository;

    public AuthService() {
        this.authRepository = new AuthRepository();
    }

    public boolean register(User user) {
        if(user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            return false;
        }
        return authRepository.register(user);
    }

    public User login(String email, String password) {
        if(email == null || password == null) {
            return null;
        }
        return authRepository.login(email, password);
    }
}
