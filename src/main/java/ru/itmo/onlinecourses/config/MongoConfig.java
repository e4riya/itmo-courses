package ru.itmo.onlinecourses.config;

import com.mongodb.MongoClientSettings;
import org.bson.UuidRepresentation;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {
    @Bean
    MongoClientSettingsBuilderCustomizer mongoUuidRepresentationCustomizer() {
        return (MongoClientSettings.Builder builder) -> builder.uuidRepresentation(UuidRepresentation.STANDARD);
    }
}
