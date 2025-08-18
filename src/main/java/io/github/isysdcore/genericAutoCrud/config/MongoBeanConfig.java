package io.github.isysdcore.genericAutoCrud.config;

import io.github.isysdcore.genericAutoCrud.query.mongo.MongoPropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/// @author domingos.fernando
/// This configuration class defines a bean for MongoPropertyResolver,
/// which is used to resolve property types in MongoDB entities.
/// The MongoPropertyResolver relies on the MongoMappingContext to access the metadata of MongoDB entities
/// and their properties.
@Configuration
public class MongoBeanConfig {

    @Bean
    public MongoPropertyResolver mongoPropertyResolver(MongoMappingContext context) {
        return new MongoPropertyResolver(context);
    }
}