CREATE TABLE IF NOT EXISTS notification (
  notification_id      VARCHAR(36)  NOT NULL,
  notification_type    VARCHAR(8)   NOT NULL,
  notification_message VARCHAR(255) NOT NULL,
  user_id              VARCHAR(255) NOT NULL,
  created_at           TIMESTAMP    NOT NULL,
  PRIMARY KEY (notification_id)
) /*! ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_general_ci */;

ALTER TABLE notification
  ADD INDEX idx_for_sort(user_id, created_at);
