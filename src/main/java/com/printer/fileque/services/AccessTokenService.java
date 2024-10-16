package com.printer.fileque.services;

import com.printer.fileque.entities.AccessToken;
import com.printer.fileque.repos.AccessTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AccessTokenService {

    private final AccessTokenRepo accessTokenRepo;

    @Autowired
    public AccessTokenService(AccessTokenRepo accessTokenRepo) {
        this.accessTokenRepo = accessTokenRepo;
    }

    public AccessToken createNewAccessToken(String email) {
        accessTokenRepo.deleteByEmail(email);
        return accessTokenRepo.save(new AccessToken(email));
    }

    public boolean checkIfTokenIsValid(String accessToken) {
        Optional<AccessToken> optionalAccessToken = accessTokenRepo.findByAccessToken(accessToken);
        if (optionalAccessToken.isPresent()) {
            if (optionalAccessToken.get().getExpirationDate().getTime() > new Date().getTime()) {
                return true;
            } else {
                accessTokenRepo.deleteByAccessToken(accessToken);
            }
        }
        return false;
    }
}
