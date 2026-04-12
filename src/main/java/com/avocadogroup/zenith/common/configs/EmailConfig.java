package com.avocadogroup.zenith.common.configs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class EmailConfig {
    @Value("${spring.mail.username}")
    private String from;
}
