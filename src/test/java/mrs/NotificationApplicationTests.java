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
	static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsib2F1dGgyLXJlc291cmNlIl0sInVzZXJfaWQiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAiLCJ1c2VyX25hbWUiOiJtYWtpQGV4YW1wbGUuY29tIiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl0sImV4cCI6MTQ4MTczMTcyNSwiZ2l2ZW5fbmFtZSI6IlRvc2hpYWtpIiwiZGlzcGxheV9uYW1lIjoiTWFraSBUb3NoaWFraSIsImZhbWlseV9uYW1lIjoiTWFraSIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiXSwianRpIjoiNDRmNTBlZTItN2UwMi00N2Q1LTlhMzMtOGNjYmM3YmI3ODEwIiwiY2xpZW50X2lkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAxIn0.NuyICgG14-DkFyesLGxqJH6er6LmmaaSC6s6RAqV8ibzZ5qq1HC3KIGjoTFshBsOKoPqX9eOROmotQdW_AJXc0LisSMWzuHd5E5fOHOrj--5I8FS_KGq2ZazRRLCPcyKddU9hUDPRUM0P5VrX0IYq3AM1hJ_KIXH0zJcMTFm4qyCAICUX41owcew__ZiaoCsrgETwshp1ixwV875dOSkt3PeI77XnCw-7nZY36K8sw2_UM0VYttk_Kd9PGVTs9JPUEnLU5E_mwWDtSahJk-DIuvPUWm-ZpoXXZ_AHR0W0YNQZm28KsY1KuAhqvAhT3Oi5W49VxRHGj1yywpxhbPJJg";

	@BeforeClass
	public static void init() throws Exception {
		factoryBean = new SimpleHttpServerFactoryBean();
		factoryBean.setPort(UAA_PORT);
		factoryBean
				.setContexts(Collections
						.singletonMap(
								"/uaa/token_key",
								(exec) -> {
									String response = "{\"alg\":\"SHA256withRSA\",\"value\":\"-----BEGIN PUBLIC KEY-----\\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApSmVdhRnVGlYsT3kYkxU\\nR780Amka0qafzl2+qNRWVWcknhVxCZAJ4Y4tQd7D8RpBTbHTQw5jO/gYMKyfLtrM\\nfTv05XXT4I+f2TmspleWOSEQSNvQYfjPjto+/1GkDC0OJTn7xe0oSFuGySu9XJxW\\nemhNZH9beP1N7shw2uX2a6fO6jo/E0S/X0SxIsaZJxyzYoeEc6iovjv1+orC+HZ/\\ngqiT4q0SiRwO72VSu3OmwY95z8J0P4LtpzJfvTok0JreJFv4hauhuxU/qLeDDgga\\nCvYxNRyattFQX+ivXnUcLCt7cwaNvvJSjY6dGLmKhbw7nKwmdpRgKV/s4yhWHS+s\\noQIDAQAB\\n-----END PUBLIC KEY-----\\n\"}";
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
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
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
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
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
