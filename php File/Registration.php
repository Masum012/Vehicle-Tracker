<?php

	$con = mysqli_connect("mysql7.000webhost.com","a5670195_AAS","r1207001","a5670195_MyData");

	$name = $_POST["name"];
	$age = $_POST["age"];
	$username = $_POST["username"];
	$password = $_POST["password"];

	$statement = mysqli_prepare($con,"INSERT INTO User(name,age,username,password) VALUES(?,?,?,?)");
	mysqli_bind_param($statement, "siss", $name, $age, $username, $password);
	mysqli_stmt_execute($statement);
	mysqli_stmt_close($statement);

	mysqli_close($con);

?>