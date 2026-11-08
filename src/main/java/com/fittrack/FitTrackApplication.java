package com.fittrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree de FitTrack, application de suivi d'entrainement.
 */
@SpringBootApplication
public class FitTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitTrackApplication.class, args);
    }
}
