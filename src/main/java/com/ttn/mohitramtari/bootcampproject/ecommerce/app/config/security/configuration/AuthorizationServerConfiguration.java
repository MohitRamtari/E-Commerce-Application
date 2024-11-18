package com.ttn.mohitramtari.bootcampproject.ecommerce.app.config.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;


@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    @Bean
    @Primary
    DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }


    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore()).userDetailsService(userDetailsService)
                .authenticationManager(authenticationManager).accessTokenConverter(accessTokenConverter());
    }

    @Bean
    JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("1234");
        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient("mohit-client").secret(passwordEncoder.encode("Mohit123"))
                .authorizedGrantTypes("password", "refresh_token")
                .refreshTokenValiditySeconds(30 * 24 * 3600).scopes("app")
                .accessTokenValiditySeconds(24 * 2600);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer authorizationServerSecurityConfigurer)
            throws Exception {
        authorizationServerSecurityConfigurer.allowFormAuthenticationForClients();
        authorizationServerSecurityConfigurer.checkTokenAccess("permitAll()");
    }
}