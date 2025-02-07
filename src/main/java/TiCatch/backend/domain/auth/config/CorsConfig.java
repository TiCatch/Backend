package TiCatch.backend.domain.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
        config.addExposedHeader("Authorization");
        config.addExposedHeader("accessToken");
        config.addExposedHeader("refreshToken");

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

}