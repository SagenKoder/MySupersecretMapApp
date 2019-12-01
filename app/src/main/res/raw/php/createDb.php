<?php

include_once 'connectSql.php';

if($conn->query("DROP TABLE reservation;") != TRUE) {
        echo("Could not drop table reservation! " . $conn->error);
}

if($conn->query("DROP TABLE room;") != TRUE) {
        echo("Could not drop table room! " . $conn->error);
}

if($conn->query("DROP TABLE building;") != TRUE) {
	echo("Could not drop table building! " . $conn->error);
}

$sql = "CREATE TABLE building (
  `id` INT(6) UNSIGNED AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `geolat` FLOAT(10, 6) NOT NULL,
  `geolng` FLOAT(10, 6) NOT NULL,
  PRIMARY KEY(id)
) ENGINE = InnoDB;";

if ($conn->query($sql) != TRUE) {
        echo("Could not create table building! " . $conn->error);
}

$sql = "CREATE TABLE  room (
  `id` INT(6) UNSIGNED AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(500) NOT NULL,
  `buildingId` INT(6) UNSIGNED NOT NULL,
  FOREIGN KEY (buildingId) REFERENCES building(id),
  PRIMARY KEY(id)
) ENGINE = InnoDB;";

if ($conn->query($sql) != TRUE) {
        echo("Could not create table room! " . $conn->error);
}

$sql = "CREATE TABLE reservation (
  `id` INT(6) UNSIGNED AUTO_INCREMENT,
  `roomId` INT(6) UNSIGNED NOT NULL,
  `datetime_from` DATETIME NOT NULL,
  `durationInSeconds` INT NOT NULL,
  `datetime_to` varchar(101) GENERATED ALWAYS AS (DATE_ADD(`datetime_from`, INTERVAL `durationInSeconds` SECOND)),
  FOREIGN KEY (roomId) REFERENCES room(id),
  PRIMARY KEY(id)
) ENGINE = InnoDB;";

if ($conn->query($sql) != TRUE) {
        echo("Could not create table reservation!" . $conn->error);
}
