package com.profecarlos.tallerapirest.restapi.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class PasswordPolicyService {

    private static final Pattern STRONG_PASSWORD = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    public void validate(String password) {
        if (password == null || !STRONG_PASSWORD.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener mínimo 8 caracteres, incluir mayúscula, minúscula, número y símbolo.");
        }
    }
}
