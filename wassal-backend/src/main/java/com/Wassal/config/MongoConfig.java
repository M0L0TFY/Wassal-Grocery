package com.Wassal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing //Enables @CreatedDate
public class MongoConfig {
}
