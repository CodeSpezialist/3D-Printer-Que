package com.printer.fileque.repos;

import com.printer.fileque.entities.AccessToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {

    void deleteByAccessToken(String accessToken);

    void deleteByEmail(String email);

    Optional<AccessToken> findByAccessToken(String accessToken);
}
