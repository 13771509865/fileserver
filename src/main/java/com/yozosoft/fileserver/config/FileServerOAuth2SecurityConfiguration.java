package com.yozosoft.fileserver.config;

import com.yozosoft.api.auth.AuthClient;
import com.yozosoft.config.oauth2.OAuth2AuthenticationService;
import com.yozosoft.config.oauth2.OAuth2CookieHelper;
import com.yozosoft.config.oauth2.OAuth2RefreshTokenFilterConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableResourceServer
public class FileServerOAuth2SecurityConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private TokenExtractor tokenExtractor;

    @Autowired
    private OAuth2AuthenticationService authenticationService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private OAuth2CookieHelper cookieHelper;

    @Autowired
    private AuthClient authClient;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .headers()
                .frameOptions()
                .disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().migrateSession()
                .and()
                .authorizeRequests()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/error").permitAll()
                .antMatchers("/swagger-resources**").permitAll()
                .antMatchers("/actuator**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .anyRequest().permitAll()
                .and().apply(refreshTokenSecurityConfigurerAdapter());
    }

    private OAuth2RefreshTokenFilterConfigurer refreshTokenSecurityConfigurerAdapter() {
        return new OAuth2RefreshTokenFilterConfigurer(authenticationService, tokenStore,
                cookieHelper, authClient);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenExtractor(tokenExtractor);
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
