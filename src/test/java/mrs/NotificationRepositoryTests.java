package mrs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class NotificationRepositoryTests {
	@Autowired
	TestEntityManager entityManager;
	@Autowired
	NotificationRepository notificationRepository;

	private void flush() {
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	public void testSave() {
		Notification notification = new Notification(NotificationType.ERROR,
				"The given id is duplicated.", "maki@example.com");
		Notification created = notificationRepository.save(notification);
		flush();
		Notification found = entityManager.find(Notification.class,
				created.getNotificationId());
		assertThat(found.getNotificationMessage())
				.isEqualTo("The given id is duplicated.");
		assertThat(found.getNotificationType()).isEqualTo(NotificationType.ERROR);
		assertThat(found.getUserId()).isEqualTo("maki@example.com");
		assertThat(found.getCreatedAt()).isNotNull();
	}
}