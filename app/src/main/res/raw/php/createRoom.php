<?php
include_once 'connectSql.php';

$id = isset($_REQUEST['id']) ? $_REQUEST['id'] : -1; // allow to edit a room
$name = $_REQUEST['name'];
$desc = $_REQUEST['desc'];
$buildingId = $_REQUEST['buildingId'];

if($id <= 0) { // create new
	if(!($stmt = $conn->prepare("INSERT INTO room (name, description, buildingId) VALUES (?, ?, ?);"))) {
		die("Could not prepare statement for insertion into room! " . $stmt->error);
	}
	if(!($stmt->bind_param("ssd", $name, $desc, $buildingId))) {
		die("Could not bind params for statement for insertion into room! " . $stmt->error);
	}
	if(!($stmt->execute())) {
		die("Could not execute statement for insertion into room! " . $stmt->error);
	}
	echo $conn->insert_id; // print inserted id for use by application
} else {
	if(!($stmt = $conn->prepare("UPDATE room SET name=?, description=?, buildingId=? WHERE id=?;"))) {
                die("Could not prepare statement for update room!");
        }
        if(!($stmt->bind_param("ssdi", $name, $desc, $buildingId, $id))) {
		die("Could not bind params for statement for update room! " . $stmt->error);
	}
        if(!($stmt->execute())) {
                die("Could not execute statement for update room!");
        }
}


?>
