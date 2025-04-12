package TiCatch.backend.global.config;

import TiCatch.backend.global.filter.JwtAuthenticationFilter;
import TiCatch.backend.global.handler.CustomAccessDeniedHandler;
import TiCatch.backend.global.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.filter.CorsFilter;

import static TiCatch.backend.global.constant.PathConstants.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsFilter corsFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers(STATIC_PATH+ "/**").permitAll()
                        .requestMatchers(SWAGGER_UI_PATH+"/**").permitAll()
                        .requestMatchers(API_DOCS_PATH+"/**").permitAll()
                        .requestMatchers(WEBJARS_PATH+"/**").permitAll()
                        .requestMatchers(LOGIN_PATH).permitAll()
                        .requestMatchers(OAUTH2_PATH).permitAll()
                        .requestMatchers(REISSUE_REQUEST_PATH).permitAll()
                        .requestMatchers(LOGOUT_PATH).permitAll()
                        .requestMatchers(AUTH_PATH).permitAll()
                        .requestMatchers(ROOT_PATH).permitAll()
                        .requestMatchers(WAITING_PATH).permitAll()
                        .requestMatchers(ERROR_PATH).permitAll()
                        .anyRequest().authenticated()
                ).securityContext(securityContext -> securityContext
                .securityContextRepository(new HttpSessionSecurityContextRepository())
        )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri(OAUTH2_AUTHORIZATION)
                        )
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri(OAUTH2_REDIRECTION)
                        )
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}