package mrs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface NotificationRepository extends CrudRepository<Notification, String> {

	@RestResource(rel = "findByUserId", path = "findByUserId")
	Page<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId,
			Pageable pageable);

}
