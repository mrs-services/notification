package mrs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
		"spring.datasource.url=jdbc:h2:mem:test;MODE=MySQL", "spring.jpa.database=H2",
		"spring.datasource.driver-class-name=org.h2.Driver" })
public class NotificationApplicationTests {
	@Autowired
	TestRestTemplate restTemplate;
	@Autowired
	NotificationRepository notificationRepository;

	@Before
	public void setUp() {
		notificationRepository.deleteAll(); // delete all data every time
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindByUserId() {
		JsonNode notification1 = restTemplate.postForObject("/v1/notifications",
				new Notification(NotificationType.ERROR, "error1!", "maki@example.com"),
				JsonNode.class);
		JsonNode notification2 = restTemplate.postForObject("/v1/notifications",
				new Notification(NotificationType.INFO, "error2!", "foo@example.com"),
				JsonNode.class);
		JsonNode notification3 = restTemplate.postForObject("/v1/notifications",
				new Notification(NotificationType.WARN, "error3!", "maki@example.com"),
				JsonNode.class);

		JsonNode ret1 = restTemplate.getForObject(
				"/v1/notifications/search/findByUserId?userId=maki@example.com",
				JsonNode.class).get("_embedded").get("notifications");
		JsonNode ret2 = restTemplate.getForObject(
				"/v1/notifications/search/findByUserId?userId=foo@example.com",
				JsonNode.class).get("_embedded").get("notifications");

		assertThat(ret1.elements()).hasSize(2);
		assertThat(ret1.get(0).get("_links").get("self").get("href").asText())
				.isEqualTo(notification3.get("_links").get("self").get("href").asText());
		assertThat(ret1.get(0).get("notificationType").asText()).isEqualTo("WARN");
		assertThat(ret1.get(0).get("notificationMessage").asText()).isEqualTo("error3!");
		assertThat(ret1.get(0).get("userId").asText()).isEqualTo("maki@example.com");
		assertThat(ret1.get(0).get("createdAt").asText()).isNotNull();
		assertThat(ret1.get(1).get("_links").get("self").get("href").asText())
				.isEqualTo(notification1.get("_links").get("self").get("href").asText());
		assertThat(ret1.get(1).get("notificationType").asText()).isEqualTo("ERROR");
		assertThat(ret1.get(1).get("notificationMessage").asText()).isEqualTo("error1!");
		assertThat(ret1.get(1).get("userId").asText()).isEqualTo("maki@example.com");
		assertThat(ret1.get(1).get("createdAt").asText()).isNotNull();

		assertThat(ret2.elements()).hasSize(1);
		assertThat(ret2.get(0).get("_links").get("self").get("href").asText())
				.isEqualTo(notification2.get("_links").get("self").get("href").asText());
		assertThat(ret2.get(0).get("notificationType").asText()).isEqualTo("INFO");
		assertThat(ret2.get(0).get("notificationMessage").asText()).isEqualTo("error2!");
		assertThat(ret2.get(0).get("userId").asText()).isEqualTo("foo@example.com");
		assertThat(ret2.get(0).get("createdAt").asText()).isNotNull();
	}

}
