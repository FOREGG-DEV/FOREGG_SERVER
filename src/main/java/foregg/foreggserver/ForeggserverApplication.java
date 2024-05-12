package foregg.foreggserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@EnableJpaAuditing
@SpringBootApplication//(exclude = {SecurityAutoConfiguration.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ForeggserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForeggserverApplication.class, args);
	}

}
