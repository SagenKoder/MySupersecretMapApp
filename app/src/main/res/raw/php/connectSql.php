<?php
$servername = "student.cs.hioa.no";
$username = "s326194";
$password = "";
$database = "s326194";

$conn = new mysqli($servername, $username, $password, $database);

if ($conn->connect_error) {
	die("Connection failed: " . $conn->connect_error);
}

$sql = "CREATE TABLE IF NOT EXISTS room (
  `id` INT(6) UNSIGNED AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(500) NOT NULL,
  `geolat` FLOAT(10, 6) NOT NULL,
  `geolng` FLOAT(10, 6) NOT NULL,
  PRIMARY KEY(id)
) ENGINE = InnoDB;";

if ($conn->query($sql) != TRUE) {
	die("Could not create table room! " . $conn->error);
}

$sql = "CREATE TABLE IF NOT EXISTS reservation (
  `id` INT(6) UNSIGNED AUTO_INCREMENT,
  `roomId` INT(6) UNSIGNED NOT NULL,
  `datetime_from` DATETIME NOT NULL,
  `durationInSeconds` INT NOT NULL,
  `datetime_to` varchar(101) GENERATED ALWAYS AS (DATE_ADD(`datetime_from`, INTERVAL `durationInSeconds` SECOND)),
  FOREIGN KEY (roomId) REFERENCES room(id),
  PRIMARY KEY(id)
) ENGINE = InnoDB;";

if ($conn->query($sql) != TRUE) {
	die("Could not create table reservation!" . $conn->error);
}

