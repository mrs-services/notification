package mrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.validation.Validator;

@SpringBootApplication
public class NotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}

	@Configuration
	public static class RestConfig extends RepositoryRestConfigurerAdapter {
		private final Validator validator;

		public RestConfig(@Lazy Validator validator) {
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
