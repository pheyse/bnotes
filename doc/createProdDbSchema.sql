CREATE TABLE `chapter` (
  `chapter_id` bigint(20) NOT NULL,
  `active` int(11) NOT NULL,
  `body` varchar(10000) DEFAULT NULL,
  `document_id` bigint(20) NOT NULL,
  `level` int(11) NOT NULL,
  `order_sequence` bigint(20) NOT NULL,
  `title` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `document` (
  `document_id` bigint(20) NOT NULL,
  `active` int(11) NOT NULL,
  `creation_time` datetime DEFAULT NULL,
  `last_change_time` datetime DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `document_access` (
  `document_access_id` bigint(20) NOT NULL,
  `document_id` bigint(20) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `history_log` (
  `history_log_id` bigint(20) NOT NULL,
  `action` varchar(255) DEFAULT NULL,
  `object_type` varchar(255) DEFAULT NULL,
  `object_value` varchar(20000) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `object_binary_data` blob DEFAULT NULL,
  `user_name` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `picture` (
  `pic_id` bigint(20) NOT NULL,
  `chapter_id` bigint(20) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL,
  `data` MEDIUMBLOB DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `security_role` (
  `id` bigint(20) NOT NULL,
  `role_name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `chapter`
  ADD PRIMARY KEY (`chapter_id`);

ALTER TABLE `document`
  ADD PRIMARY KEY (`document_id`);

ALTER TABLE `document_access`
  ADD PRIMARY KEY (`document_access_id`);

ALTER TABLE `history_log`
  ADD PRIMARY KEY (`history_log_id`);

ALTER TABLE `picture`
  ADD PRIMARY KEY (`pic_id`);

ALTER TABLE `security_role`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `user_role`
  ADD PRIMARY KEY (`user_id`,`role_id`),
  ADD KEY `role_id` (`role_id`);

ALTER TABLE `chapter`
  MODIFY `chapter_id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `document`
  MODIFY `document_id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `document_access`
  MODIFY `document_access_id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `history_log`
  MODIFY `history_log_id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `picture`
  MODIFY `pic_id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `security_role`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `user`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `user_role`
  ADD CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `security_role` (`id`);





/*
CREATE TABLE `chapter` (
  `chapter_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` int(11) NOT NULL,
  `body` varchar(10000) DEFAULT NULL,
  `document_id` bigint(20) NOT NULL,
  `level` int(11) NOT NULL,
  `order_sequence` bigint(20) NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`chapter_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8



CREATE TABLE `document` (
  `document_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` int(11) NOT NULL,
  `creation_time` datetime DEFAULT NULL,
  `last_change_time` datetime DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`document_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8



CREATE TABLE `document_access` (
  `document_access_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `document_id` bigint(20) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`document_access_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8





CREATE TABLE `history_log` (
  `history_log_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action` varchar(255) DEFAULT NULL,
  `object_type` varchar(255) DEFAULT NULL,
  `object_value` varchar(20000) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `user_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`history_log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8




CREATE TABLE security_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  description varchar(100) DEFAULT NULL,
  role_name varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8


CREATE TABLE `user` (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8


CREATE TABLE user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  CONSTRAINT FK_SECURITY_USER_ID FOREIGN KEY (user_id) REFERENCES security_user (id),
  CONSTRAINT FK_SECURITY_ROLE_ID FOREIGN KEY (role_id) REFERENCES security_role (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

*/