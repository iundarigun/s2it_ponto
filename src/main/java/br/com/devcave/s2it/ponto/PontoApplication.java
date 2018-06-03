package br.com.devcave.s2it.ponto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Controller
@EnableSwagger2
@EnableFeignClients
@SpringBootApplication
public class PontoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PontoApplication.class, args);
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors
						.basePackage("br.com.devcave.s2it.ponto.restcontroller"))
				.build();
	}

	@GetMapping("/")
	public String getSwagger() {
		return "redirect:/login";
	}
}
