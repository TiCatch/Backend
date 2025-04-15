package TiCatch.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static TiCatch.backend.global.constant.UserConstants.*;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origin.local}")
    private String localOrigin;

    @Value("${cors.allowed-origin.ec2}")
    private String ec2Origin;

    @Value("${cors.allowed-origin.vercel}")
    private String vercelOrigin;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern(localOrigin);
        config.addAllowedOriginPattern(ec2Origin);
        config.addAllowedOriginPattern(vercelOrigin);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader(HEADER_AUTHORIZATION);
        config.addExposedHeader(HEADER_ACCESS_TOKEN);
        config.addExposedHeader(HEADER_REFRESH_TOKEN);

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

}