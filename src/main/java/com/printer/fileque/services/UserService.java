package com.printer.fileque.services;

import com.printer.fileque.entities.User;
import com.printer.fileque.repos.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Lock lockSyncUser = new ReentrantLock();

    private final UserRepo userRepo;

    @Transactional
    public User syncUser(User user) {
        try {
            lockSyncUser.lock();

            if (user == null) {
                throw new EntityNotFoundException("Error while user sync");
            }
            User saveUser = user;
            Optional<User> optionalUser = userRepo.findByEmail(user.getEmail());

            if (optionalUser.isPresent()) {
                saveUser = optionalUser.get();
                saveUser.setFirstName(user.getFirstName());
                saveUser.setLastName(user.getLastName());
                saveUser.setKeycloakId(user.getKeycloakId());
            }

            return userRepo.save(saveUser);
        } finally {
            lockSyncUser.unlock();
        }
    }

    public User getLoggedUser() {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String email = String.valueOf(token.getTokenAttributes().get("email"));
        return userRepo.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Error while get logged in user"));
    }

    public User getUserByMail(String userMail) {
        Optional<User> optionalUser = userRepo.findByEmail(userMail);
        if(optionalUser.isEmpty()){
            throw new EntityNotFoundException("User not Found");
        }
        return optionalUser.get();
    }

    public Optional<User> getOptionalUserByMail(String orgaAdminMail) {
        return userRepo.findByEmail(orgaAdminMail);
    }
}
