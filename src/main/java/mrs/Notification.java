package mrs;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Notification implements Serializable {
	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(columnDefinition = "varchar(36)")
	private String notificationId;
	@Enumerated(EnumType.STRING)
	@NotNull
	private NotificationType notificationType;
	@NotEmpty
	@Size(max = 255)
	private String notificationMessage;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@NotEmpty
	@Size(max = 255)
	private String userId;

	public Notification(NotificationType notificationType, String notificationMessage,
			String userId) {
		this.notificationType = notificationType;
		this.notificationMessage = notificationMessage;
		this.userId = userId;
	}

	@PrePersist
	private void beforeCreate() {
		this.createdAt = new Date();
	}

	private Notification() {
	}

	public String getNotificationId() {
		return notificationId;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}

	public String getUserId() {
		return userId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}
}
