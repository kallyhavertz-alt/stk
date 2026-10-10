
package com.stkapi.stk;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class FirebaseConfig {

    private static final Logger log =
            Logger.getLogger(FirebaseConfig.class.getName());

    @Value("${app.firebase.config-path:firebase-service-account (2).json}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {

        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("Firebase app already initialized.");
            return FirebaseApp.getInstance();
        }

        String envConfig = System.getenv("FIREBASE_CONFIG_JSON");

        try (InputStream serviceAccount = getCredentialsStream(envConfig)) {

            GoogleCredentials credentials =
                    GoogleCredentials.fromStream(serviceAccount);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);

            log.info("Firebase Admin SDK initialized successfully.");

            return app;

        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "Firebase initialization failed: "
                            + e.getClass().getSimpleName()
                            + ": " + e.getMessage());

            throw new IllegalStateException(
                    "Unable to initialize Firebase Admin SDK", e);
        }
    }

    private InputStream getCredentialsStream(String envConfig)
            throws IOException {

        if (envConfig == null || envConfig.isBlank()) {
            log.info("Loading Firebase credentials from classpath.");

            return new ClassPathResource(firebaseConfigPath)
                    .getInputStream();
        }

        String trimmed = envConfig.trim();

        if (trimmed.startsWith("{")) {
            log.info("Loading raw JSON from environment variable.");

            return new ByteArrayInputStream(
                    trimmed.getBytes(StandardCharsets.UTF_8));
        }

        log.info("Decoding Base64 Firebase credentials.");

        byte[] decoded = Base64.getDecoder().decode(trimmed);

        return new ByteArrayInputStream(decoded);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}

/*package com.stkapi.stk;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

@Configuration
public class FirebaseConfig {

    private static final Logger log = Logger.getLogger(FirebaseConfig.class.getName());

    @Value("${app.firebase.config-path:firebase-service-account (2).json}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("Firebase Application initialized successfully.");
            return app;
        } else {
            return FirebaseApp.getInstance();
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}

 */