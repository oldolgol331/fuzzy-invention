package com.example.demo.common.security.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.example.demo.common.security.entrypoint.CustomAuthenticationEntryPoint;
import com.example.demo.common.security.handler.CustomAccessDeniedHandler;
import com.example.demo.common.security.jwt.filter.JwtAuthenticationFilter;
import com.example.demo.infra.security.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.example.demo.infra.security.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.example.demo.infra.security.oauth.service.CustomOAuth2UserService;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * PackageName : com.example.demo.common.security.config
 * FileName    : SecurityConfig
 * Author      : oldolgol331
 * Date        : 25. 8. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 23.    oldolgol331          Initial creation
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter            jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint     authenticationEntryPoint;
    private final CustomAccessDeniedHandler          accessDeniedHandler;
    private final CustomOAuth2UserService            oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration)
    throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)

            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))

            .httpBasic(AbstractHttpConfigurer::disable)

            .formLogin(AbstractHttpConfigurer::disable)

            .authorizeRequests(authorize -> authorize
                    //Error & Swagger & Console
                    .mvcMatchers("/error", "/favicon.ico", "/swagger-ui/**", "/swagger-ui.html",
                                 "/swagger-resources/**", "/v3/api-docs/**", "/h2-console/**").permitAll()

                    //Auth
                    .mvcMatchers(POST, "/api/v1/auth/signin", "/api/v1/auth/refresh").permitAll()
                    .mvcMatchers(GET, "/api/v1/auth/signout").authenticated()

                    //Member
                    .mvcMatchers(GET, "/api/v1/members/verify-email").permitAll()
                    .mvcMatchers(POST, "/api/v1/members", "/api/v1/members/verify-email-resend").permitAll()
                    .mvcMatchers(GET, "/api/v1/members").authenticated().mvcMatchers(PUT, "/api/v1/members")
                    .authenticated().mvcMatchers(PATCH, "/api/v1/members").authenticated()
                    .mvcMatchers(DELETE, "/api/v1/members").authenticated()

                    //Post
                    .mvcMatchers(POST, "/api/v1/posts").authenticated()
                    .mvcMatchers(GET, "/api/v1/posts", "/api/v1/posts/{id}").permitAll()
                    .mvcMatchers(PUT, "/api/v1/posts/{id}").authenticated()
                    .mvcMatchers(DELETE, "/api/v1/posts/{id}").authenticated()
                    .mvcMatchers(PATCH, "/api/v1/posts/{id}").authenticated()
                    .mvcMatchers(POST, "/api/v1/posts/batch").hasAuthority("ROLE_ADMIN")

                    //Comment
                    .mvcMatchers(POST, "/api/v1/comments/{postId}").authenticated()
                    .mvcMatchers(GET, "/api/v1/comments/{postId}").permitAll()
                    .mvcMatchers(PUT, "/api/v1/comments/{postId}").authenticated()
                    .mvcMatchers(DELETE, "/api/v1/comments/{postId}").authenticated()
                    .mvcMatchers(PATCH, "/api/v1/comments/{postId}").authenticated()

                    //ETC
                    .anyRequest().authenticated())

            .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                                         .successHandler(oAuth2AuthenticationSuccessHandler)
                                         .failureHandler(oAuth2AuthenticationFailureHandler))

            .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint)
                                                     .accessDeniedHandler(accessDeniedHandler))

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
