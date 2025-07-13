package com.doubleo.didagent;

import java.security.Security;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DidAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DidAgentApplication.class, args);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
}
