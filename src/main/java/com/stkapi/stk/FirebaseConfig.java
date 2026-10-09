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
import java.util.logging.Logger;

@Configuration
public class FirebaseConfig {

    private static final Logger log = Logger.getLogger(FirebaseConfig.class.getName());

    @Value("${app.firebase.config-path:firebase-service-account (2).json}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        InputStream serviceAccount;

        String rawEnvConfig = System.getenv("FIREBASE_CONFIG_JSON");

        if (rawEnvConfig != null && !rawEnvConfig.trim().isEmpty()) {
            log.info("Loading Firebase credentials from FIREBASE_CONFIG_JSON environment variable.");

            String trimmed = rawEnvConfig.trim();
            byte[] jsonBytes;

            // Automatically decode Base64 if it's not a raw JSON bracket
            if (!trimmed.startsWith("{")) {
                log.info("Decoding Base64 FIREBASE_CONFIG_JSON string.");
                jsonBytes = Base64.getDecoder().decode(trimmed);
            } else {
                log.info("Reading raw JSON FIREBASE_CONFIG_JSON string.");
                jsonBytes = trimmed.replace("\\n", "\n").getBytes(StandardCharsets.UTF_8);
            }

            serviceAccount = new ByteArrayInputStream(jsonBytes);
        } else {
            log.info("Loading Firebase credentials from local classpath resource: " + firebaseConfigPath);
            serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();
        }

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