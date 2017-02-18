<?php

	$con = mysqli_connect("mysql7.000webhost.com","a5670195_AAS","r1207001","a5670195_MyData");

	$username = $_POST["username"];
	$password = $_POST["password"];

	$statement = mysqli_prepare($con,"SELECT * FROM User WHERE username = ? AND password = ?");
	mysqli_bind_param($statement, "ss", $username, $password);
	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement,$userID,$name,$age,$username,$password);

	$user = array();

	while (mysqli_stmt_fetch($statement)) {
		$user["name"] = $name;
		$user["age"] = $age;
		$user["username"] = $username;
		$user["password"] = $password;
	}

	echo json_encode($user);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>