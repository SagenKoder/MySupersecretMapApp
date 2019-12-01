<?php
include_once 'connectSql.php';

$id = isset($_REQUEST['id']) ? $_REQUEST['id'] : -1; // allow to edit a reservation
$roomId = $_REQUEST['roomId'];
$startTime = $_REQUEST['startTime'];
$duration = $_REQUEST['duration'];

if($id <= 0) { // create new
	if(!($stmt = $conn->prepare("INSERT INTO reservation (roomId, datetime_from, durationInSeconds) VALUES (?, ?, ?);"))) {
		die("Could not prepare statement for insertion into reservation! " . $stmt->error);
	}
	if(!($stmt->bind_param("isi", $roomId, $startTime, $duration))) {
		die("Could not bind params for statement for insertion into reservation! " . $stmt->error);
	}
	if(!($stmt->execute())) {
		die("Could not execute statement for insertion into reservation! " . $stmt->error);
	}
} else {
	if(!($stmt = $conn->prepare("UPDATE room SET roomId=?, durationInSeconds=?, datetime_from=? WHERE id=?;"))) {
                die("Could not prepare statement for update room!");
        }
        if(!($stmt->bind_param("iisi", $roomId, $duration, $startTime, $id))) {
		die("Could not bind params for statement for update reservation! " . $stmt->error);
	}
        if(!($stmt->execute())) {
                die("Could not execute statement for update reservation!");
        }
}


?>
