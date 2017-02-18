<?php

	$con = mysqli_connect("mysql7.000webhost.com","a5670195_AAS","r1207001","a5670195_MyData");

	$latitude = $_POST["latitude"];
	$longitude = $_POST["longitude"];
	$dateTime = $_POST["dateTime"];
	
	$statement = mysqli_prepare($con,"INSERT INTO VehicleLocation(latitude,longitude,dateTime) VALUES(?,?,?)");
	mysqli_bind_param($statement, "dds", $latitude, $longitude,$dateTime);
	mysqli_stmt_execute($statement);
	mysqli_stmt_close($statement);

	mysqli_close($con);

?>