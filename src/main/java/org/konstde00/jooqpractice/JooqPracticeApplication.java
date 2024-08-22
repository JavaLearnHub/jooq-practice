package org.konstde00.jooqpractice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class JooqPracticeApplication implements CommandLineRunner {

    public JooqPracticeApplication() {}

    public static void main(String[] args) {
        log.info("Starting the JooqPracticeApplication Service");
        SpringApplication.run(JooqPracticeApplication.class, args);
        log.info("JooqPracticeApplication Service started");
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Application has started!!!");
    }
}
