package mrs;

import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.validation.Validator;

@SpringBootApplication
@EnableDiscoveryClient
@EnableResourceServer
public class NotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}

	@Profile("!cloud")
	@Bean
	RequestDumperFilter requestDumperFilter() {
		return new RequestDumperFilter();
	}

	@Configuration
	public static class RestConfig extends RepositoryRestConfigurerAdapter {
		private final Validator validator;

		public RestConfig(@Lazy @Qualifier("mvcValidator") Validator validator) {
			this.validator = validator;
		}

		@Override
		public void configureValidatingRepositoryEventListener(
				ValidatingRepositoryEventListener validatingListener) {
			validatingListener.addValidator("beforeCreate", validator);
			validatingListener.addValidator("beforeSave", validator);
		}
	}
}
