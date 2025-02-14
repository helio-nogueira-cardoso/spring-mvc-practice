package br.com.helio.springmvc.testUtils;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public class RequestPostProcessors {
    public static RequestPostProcessor mockJwt(String subject, List<String> scopes) {
        return jwt().jwt(jwt -> {
            jwt.subject(subject);
            jwt.claims(claims -> claims.put("scope", String.join(" ", scopes)));
            jwt.notBefore(Instant.now().minusSeconds(5));
        });
    }
}
