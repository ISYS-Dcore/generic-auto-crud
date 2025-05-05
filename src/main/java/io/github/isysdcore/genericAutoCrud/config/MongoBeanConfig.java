package io.github.isysdcore.genericAutoCrud.config;

import io.github.isysdcore.genericAutoCrud.query.mongo.MongoPropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * @author domingos.fernando

 */
@Configuration
public class MongoBeanConfig {

    @Bean
    public MongoPropertyResolver mongoPropertyResolver(MongoMappingContext context) {
        return new MongoPropertyResolver(context);
    }
}