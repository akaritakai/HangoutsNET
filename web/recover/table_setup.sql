CREATE DATABASE hangouts;

CREATE TABLE `hangouts`.`users` (
  `username`  VARCHAR(100) NOT NULL,
  `password`  VARCHAR(100) NOT NULL,
  `email`     VARCHAR(100) NOT NULL,
  `real_name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `hangouts`.`recover_keys` (
  `username`      VARCHAR(100) NOT NULL,
  `token`         VARCHAR(100) NOT NULL,
  `creation_date` TIMESTAMP    NOT NULL,
  PRIMARY KEY (`token`),
  FOREIGN KEY (`username`) REFERENCES users(username) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
