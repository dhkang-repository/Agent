package org.example.agent.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.agent.global.constrant.LogMarker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"local", "default"})
@Component
@RequiredArgsConstructor
class JpaBootCheck implements CommandLineRunner {
    private final EntityManagerFactory emf;

    @Override
    public void run(String... args) {
        log.info(LogMarker.SERVICE.getMarker(), "=== Managed types ===");
        emf.getMetamodel().getEntities()
                .forEach(e -> log.info(LogMarker.SERVICE.getMarker(), e.getJavaType().getName()));
    }
}

