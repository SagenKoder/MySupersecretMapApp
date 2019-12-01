<?php
include_once 'connectSql.php';

$id = isset($_REQUEST['id']) ? $_REQUEST['id'] : -1; // allow to edit a room
$name = $_REQUEST['name'];
$geolat = $_REQUEST['geolat'];
$geolng = $_REQUEST['geolng'];

if($id <= 0) { // create new
	if(!($stmt = $conn->prepare("INSERT INTO building (name, geolat, geolng) VALUES (?, ?, ?);"))) {
		die("Could not prepare statement for insertion into building! " . $stmt->error);
	}
	if(!($stmt->bind_param("sdd", $name, $geolat, $geolng))) {
		die("Could not bind params for statement for insertion into building! " . $stmt->error);
	}
	if(!($stmt->execute())) {
		die("Could not execute statement for insertion into building! " . $stmt->error);
	}
} else {
	if(!($stmt = $conn->prepare("UPDATE building SET name=?, geolat=?, geolng=? WHERE id=?;"))) {
                die("Could not prepare statement for update building!");
        }
        if(!($stmt->bind_param("sddi", $name, $geolat, $geolng, $id))) {
		die("Could not bind params for statement for update building! " . $stmt->error);
	}
        if(!($stmt->execute())) {
                die("Could not execute statement for update building!");
        }
}


?>
