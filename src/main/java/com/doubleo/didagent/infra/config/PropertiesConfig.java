package com.doubleo.didagent.infra.config;

import com.doubleo.didagent.infra.config.acapy.AcapyProperties;
import com.doubleo.didagent.infra.config.hospital.HospitalProperties;
import com.doubleo.didagent.infra.config.mediator.MediatorProperties;
import com.doubleo.didagent.infra.config.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
    MediatorProperties.class,
    HospitalProperties.class,
    RedisProperties.class,
    AcapyProperties.class
})
@Configuration
public class PropertiesConfig {}
