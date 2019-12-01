<?php
include_once 'connectSql.php';

function getReservations($conn, $id) {
	$result = $conn->query("SELECT * FROM reservation WHERE roomId=" . $id);

	$outputReservation = array();
	while($row = $result->fetch_assoc()) {
		$outputReservation[] = $row;
	}

	return $outputReservation;
}

function getRooms($conn, $id) {
	$result = $conn->query("SELECT * FROM room WHERE buildingId=" . $id);

	$outputRoom = array();
	while($row = $result->fetch_assoc()) {
		$row['reservations'] = getReservations($conn, $row['id']);
		$outputRoom[] = $row;
	}

	return $outputRoom;
}

function getData($conn) {
	$result = $conn->query("SELECT * FROM building;");

	$output = array();
	while($row = $result->fetch_assoc()) {
        	$row['rooms'] = getRooms($conn, $row['id']);
        	$output[] = $row;
	}

	// return json data
	header('Content-Type: application/json');
	print(json_encode($output));
}

getData($conn);

$conn->close();

?>
