
package com.smartretail.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartretail.entity.UserAccount;
import com.smartretail.repository.UserAccountRepository;

@Service
public class UserAccountService {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(
            UserAccountRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount register(
            String fullName,
            String username,
            String email,
            String password) {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username already exists. Choose another username.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email already registered. Use another email.");
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters.");
        }

        UserAccount account = new UserAccount();
        account.setFullName(fullName.trim());
        account.setUsername(username.trim());
        account.setEmail(email.trim());
        account.setPasswordHash(passwordEncoder.encode(password));
        account.setEnabled(true);

        return userRepository.save(account);
    }

    public UserAccount getAccount(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User account not found."));
    }

    @Transactional
    public UserAccount updateProfile(
            String username,
            String fullName,
            String email) {

        UserAccount account = getAccount(username);
        String cleanEmail = email.trim();

        userRepository.findByEmail(cleanEmail)
                .filter(existing -> !existing.getId().equals(account.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Email already used by another account.");
                });

        account.setFullName(fullName.trim());
        account.setEmail(cleanEmail);

        return userRepository.save(account);
    }

    @Transactional
    public void changePassword(
            String username,
            String currentPassword,
            String newPassword) {

        UserAccount account = getAccount(username);

        if (!passwordEncoder.matches(
                currentPassword, account.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "Current password is incorrect.");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "New password must contain at least 8 characters.");
        }

        account.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(account);
    }
}
