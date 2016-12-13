package mrs;

import java.util.Arrays;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotificationApplication.class)
public abstract class RestBase {
	protected MockMvc mockMvc;
	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	WebApplicationContext webApplicationContext;

	@Before
	public void setUp() throws Exception {
		RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

		// clean up test data
		notificationRepository.deleteAll();
		notificationRepository.save(Arrays.asList(
				new Notification(NotificationType.ERROR, "error1!", "maki@example.com"),
				new Notification(NotificationType.ERROR, "error2!", "maki@example.com"),
				new Notification(NotificationType.ERROR, "error3!", "maki@example.com"),
				new Notification(NotificationType.ERROR, "error4!", "maki@example.com"),
				new Notification(NotificationType.INFO, "info1!", "foo@example.com"),
				new Notification(NotificationType.INFO, "info2!", "foo@example.com"),
				new Notification(NotificationType.ERROR, "error5!", "maki@example.com"),
				new Notification(NotificationType.WARN, "warn1!", "maki@example.com")));
	}

}
