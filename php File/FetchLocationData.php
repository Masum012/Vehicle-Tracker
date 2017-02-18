<?php

	$con = mysqli_connect("mysql7.000webhost.com","a5670195_AAS","r1207001","a5670195_MyData");

	$counter = intval($_POST["counter"]);

	$statement = mysqli_prepare($con,"SELECT * FROM VehicleLocation WHERE location_id IN(SELECT max(location_id)-$counter FROM VehicleLocation)");

	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement,$location_id,$latitude,$longitude,$dateTime);

	$location = array();

	while (mysqli_stmt_fetch($statement)) { 
		$location["latitude"] = $latitude;
		$location["longitude"] = $longitude;
		$location["location_id"] = $location_id;
		$location["dateTime"] = $dateTime;
	}

	echo json_encode($location);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>
