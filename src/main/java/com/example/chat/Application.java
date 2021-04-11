package com.example.chat;

import ch.qos.logback.classic.Logger;
import com.toornament.ToornamentClient;
import com.toornament.model.enums.Scope;
import java.util.HashSet;
import org.goldrenard.jb.configuration.BotConfiguration;
import org.goldrenard.jb.core.Bot;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Bot alice() {
        return new Bot(BotConfiguration.builder()
                .name("alice")
                .path("src/main/resources")
                .build()
        );
    }
    @Bean
    public Logger logger(){
        return  (Logger) LoggerFactory.getLogger(this.getClass());
    }
    
    @Bean
    public ToornamentClient client() {
        HashSet<Scope> scopes = new HashSet<>();
        scopes.add(Scope.ORGANIZER_VIEW);
        
        return new ToornamentClient("yEF4GKOHO6MDYWh4q_6u0mHO5KfEVu1gAN20Dr76GtI",
            "58ff4401140ba08e7f8b4567269ltppwn480ggw08gc4ggkcccwsgsog4cssc80c8swgkwg0so",
            "nyfrwu8nsfko8cwckgwco840csc0k4wcog0gw84gwo440gggg",scopes);
    }

    @Bean
    public ScheduledExecutorService executorService() {
        return Executors.newScheduledThreadPool(2);
    }

}
