package TiCatch.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Ticatch API")
						.version("1.0")
						.description("<h3>Ticatch 문서를 제공합니다.</h3>")
				)
				.addSecurityItem(new SecurityRequirement().addList("JWT"))
				.components(new Components()
						.addSecuritySchemes("JWT",
								new SecurityScheme()
										.name("JWT")
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")));
	}
}

