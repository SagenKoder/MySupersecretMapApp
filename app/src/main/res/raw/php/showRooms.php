<?php
include_once 'connectSql.php';

$result = $conn->query("SELECT * FROM room;");

while($row = $result->fetch_assoc()) {
	$output[] = $row;
}

// return json data
header('Content-Type: application/json');
print(json_encode($output));

$conn->close();

?>
