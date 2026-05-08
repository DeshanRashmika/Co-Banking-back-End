package edu.icet.banking.auth.infrastructure.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${google.oauth.client-id}") String googleClientId)
            throws GeneralSecurityException, IOException {
        this.verifier = new GoogleIdTokenVerifier.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance()
        ).setAudience(Collections.singletonList(googleClientId)).build();
    }

    public GoogleIdToken verify(String idTokenString) {
        try {
            return verifier.verify(idTokenString);
        } catch (Exception ex) {
            log.warn("Google token verification failed: {}", ex.getMessage());
            return null;
        }
    }

    public boolean verifyToken(String idTokenString) {
        return verify(idTokenString) != null;
    }

    public String getEmailFromGoogleToken(String idTokenString) {
        GoogleIdToken idToken = verify(idTokenString);
        return idToken == null ? null : idToken.getPayload().getEmail();
    }

    public String getGoogleIdFromToken(String idTokenString) {
        GoogleIdToken idToken = verify(idTokenString);
        return idToken == null ? null : idToken.getPayload().getSubject();
    }
}
