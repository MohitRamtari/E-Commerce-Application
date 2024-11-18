package com.ttn.mohitramtari.bootcampproject.ecommerce;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.AppAuditAware;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.imagestorage.dto.ImageStorageDto;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({ImageStorageDto.class})
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AppAuditAware();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
