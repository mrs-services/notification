package mrs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.OutputStream;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.remoting.support.SimpleHttpServerFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
		"spring.cloud.config.enabled=false",
		"security.oauth2.resource.jwt.key-uri=http://localhost:"
				+ NotificationApplicationTests.UAA_PORT + "/uaa/token_key" })
public class NotificationApplicationTests {
	@Autowired
	TestRestTemplate restTemplate;
	@Autowired
	NotificationRepository notificationRepository;

	public final static int UAA_PORT = 39876;

	static SimpleHttpServerFactoryBean factoryBean;
	static String accessToken;

	@BeforeClass
	public static void init() throws Exception {
		TestTokenGenerator tokenGenerator = new TestTokenGenerator();
		accessToken = tokenGenerator.getAccessToken();
		factoryBean = new SimpleHttpServerFactoryBean();
		factoryBean.setPort(UAA_PORT);
		factoryBean.setContexts(Collections.singletonMap(
				"/uaa/token_key",
				(exec) -> {
					String response = tokenGenerator.getTokenKey();
					exec.getResponseHeaders().add("Content-Type",
							"application/json;charset=UTF-8");
					exec.sendResponseHeaders(200, response.length());
					try (OutputStream stream = exec.getResponseBody()) {
						stream.write(response.getBytes());
					}
				}));
		factoryBean.afterPropertiesSet();
	}

	@Before
	public void setUp() {
		notificationRepository.deleteAll(); // delete all data every time
	}

	@AfterClass
	public static void tearDown() throws Exception {
		factoryBean.destroy();
	}

	@Test
	public void testFindByUserId() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		JsonNode notification1 = restTemplate.exchange(
				"/v1/notifications",
				HttpMethod.POST,
				new HttpEntity<>(new Notification(NotificationType.ERROR, "error1!",
						"maki@example.com"), headers), JsonNode.class).getBody();
		JsonNode notification2 = restTemplate.exchange(
				"/v1/notifications",
				HttpMethod.POST,
				new HttpEntity<>(new Notification(NotificationType.INFO, "error2!",
						"foo@example.com"), headers), JsonNode.class).getBody();
		JsonNode notification3 = restTemplate.exchange(
				"/v1/notifications",
				HttpMethod.POST,
				new HttpEntity<>(new Notification(NotificationType.WARN, "error3!",
						"maki@example.com"), headers), JsonNode.class).getBody();

		JsonNode ret1 = restTemplate
				.exchange(
						"/v1/notifications/search/findByUserId?userId=maki@example.com",
						HttpMethod.GET, new HttpEntity<Void>(headers), JsonNode.class)
				.getBody().get("_embedded").get("notifications");
		JsonNode ret2 = restTemplate
				.exchange("/v1/notifications/search/findByUserId?userId=foo@example.com",
						HttpMethod.GET, new HttpEntity<Void>(headers), JsonNode.class)
				.getBody().get("_embedded").get("notifications");

		assertThat(ret1.elements()).hasSize(2);
		assertThat(ret1.get(0).get("_links").get("self").get("href").asText()).isEqualTo(
				notification3.get("_links").get("self").get("href").asText());
		assertThat(ret1.get(0).get("notificationType").asText()).isEqualTo("WARN");
		assertThat(ret1.get(0).get("notificationMessage").asText()).isEqualTo("error3!");
		assertThat(ret1.get(0).get("userId").asText()).isEqualTo("maki@example.com");
		assertThat(ret1.get(0).get("createdAt").asText()).isNotNull();
		assertThat(ret1.get(1).get("_links").get("self").get("href").asText()).isEqualTo(
				notification1.get("_links").get("self").get("href").asText());
		assertThat(ret1.get(1).get("notificationType").asText()).isEqualTo("ERROR");
		assertThat(ret1.get(1).get("notificationMessage").asText()).isEqualTo("error1!");
		assertThat(ret1.get(1).get("userId").asText()).isEqualTo("maki@example.com");
		assertThat(ret1.get(1).get("createdAt").asText()).isNotNull();

		assertThat(ret2.elements()).hasSize(1);
		assertThat(ret2.get(0).get("_links").get("self").get("href").asText()).isEqualTo(
				notification2.get("_links").get("self").get("href").asText());
		assertThat(ret2.get(0).get("notificationType").asText()).isEqualTo("INFO");
		assertThat(ret2.get(0).get("notificationMessage").asText()).isEqualTo("error2!");
		assertThat(ret2.get(0).get("userId").asText()).isEqualTo("foo@example.com");
		assertThat(ret2.get(0).get("createdAt").asText()).isNotNull();
	}

	@Test
	public void testInvalidRequest() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		ResponseEntity<JsonNode> userIdIsEmpty = restTemplate.exchange(
				"/v1/notifications", HttpMethod.POST, new HttpEntity<>(new Notification(
						NotificationType.ERROR, "error1!", ""), headers), JsonNode.class);
		System.out.println(userIdIsEmpty);
		assertThat(userIdIsEmpty.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(userIdIsEmpty.getBody().get("errors").elements()).hasSize(1);
		assertThat(userIdIsEmpty.getBody().get("errors").get(0).get("entity").asText())
				.isEqualTo("Notification");
		assertThat(userIdIsEmpty.getBody().get("errors").get(0).get("property").asText())
				.isEqualTo("userId");
		assertThat(
				userIdIsEmpty.getBody().get("errors").get(0).get("invalidValue").asText())
				.isEmpty();
		assertThat(userIdIsEmpty.getBody().get("errors").get(0).get("message").asText())
				.isEqualTo("may not be empty");

		ResponseEntity<JsonNode> notificationMessageIsEmpty = restTemplate.exchange(
				"/v1/notifications", HttpMethod.POST, new HttpEntity<>(new Notification(
						NotificationType.ERROR, "", "maki@example.com"), headers),
				JsonNode.class);
		assertThat(notificationMessageIsEmpty.getStatusCode()).isEqualTo(
				HttpStatus.BAD_REQUEST);
		assertThat(notificationMessageIsEmpty.getBody().get("errors").elements())
				.hasSize(1);
		assertThat(
				notificationMessageIsEmpty.getBody().get("errors").get(0).get("entity")
						.asText()).isEqualTo("Notification");
		assertThat(
				notificationMessageIsEmpty.getBody().get("errors").get(0).get("property")
						.asText()).isEqualTo("notificationMessage");
		assertThat(
				notificationMessageIsEmpty.getBody().get("errors").get(0)
						.get("invalidValue").asText()).isEmpty();
		assertThat(
				notificationMessageIsEmpty.getBody().get("errors").get(0).get("message")
						.asText()).isEqualTo("may not be empty");

		ResponseEntity<JsonNode> notificationTypeIsNull = restTemplate.exchange(
				"/v1/notifications", HttpMethod.POST, new HttpEntity<>(new Notification(
						null, "error1!", "maki@example.com"), headers), JsonNode.class);
		assertThat(notificationTypeIsNull.getStatusCode()).isEqualTo(
				HttpStatus.BAD_REQUEST);
		assertThat(notificationTypeIsNull.getBody().get("errors").elements()).hasSize(1);
		assertThat(
				notificationTypeIsNull.getBody().get("errors").get(0).get("entity")
						.asText()).isEqualTo("Notification");
		assertThat(
				notificationTypeIsNull.getBody().get("errors").get(0).get("property")
						.asText()).isEqualTo("notificationType");
		assertThat(
				notificationTypeIsNull.getBody().get("errors").get(0).get("invalidValue")
						.asText()).isEqualTo("null");
		assertThat(
				notificationTypeIsNull.getBody().get("errors").get(0).get("message")
						.asText()).isEqualTo("may not be null");

		ResponseEntity<JsonNode> allFieldIsNull = restTemplate.exchange(
				"/v1/notifications", HttpMethod.POST, new HttpEntity<>(new Notification(
						null, null, null), headers), JsonNode.class);
		assertThat(allFieldIsNull.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(allFieldIsNull.getBody().get("errors").elements()).hasSize(3);
	}
}
