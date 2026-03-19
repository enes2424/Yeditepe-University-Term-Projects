<!DOCTYPE html>
<html>
<body>
<?php
if (isset($_POST["username"])) {
	if ($_POST["username"] == "")
		echo 'Username field cannot be empty';
	else if ($_POST["password"] == "")
		echo 'Password field cannot be empty';
	else {
		session_start();
		$hasUser = false;
		$role = $_POST["role"];
		if (isset($_SESSION[$role]))
			foreach	($_SESSION[$role] as $info)
				if ($info["username"] == $_POST["username"] and $info["password"] == $_POST["password"]) {
					$_SESSION["currentUsername"] = $info["username"];
					$hasUser = true;
					break;
				}
		if ($hasUser)
			header("Location: $role.php");
		else
			echo "Username or password is incorrect";
	}
	exit();
}

session_start();
if (!isset($_SESSION["Dean"])) {
	$servername = "localhost";
	$username = "root";
	$password = "mysql";
	$dbname = "exam_planning";

	$conn = mysqli_connect($servername, $username, $password, $dbname);

	if (!$conn)
		die("Connection failed: " . mysqli_connect_error());

	$result = mysqli_query($conn, "SELECT * FROM employee") or die("Error");

	if (mysqli_num_rows($result) > 0)
		while($row = mysqli_fetch_array($result)) {
			$_SESSION[$row["role"]][$row["employee_id"]] = ["name" => $row["employee_name"], "username" => $row["username"], "password" => $row["password"], "role" => $row["role"], "department_id" => $row["department_id"]];
			if ($row["role"] == "Assistant") {
				$_SESSION["Assistant"][$row["employee_id"]]["point"] = 0;
				$_SESSION["Assistant"][$row["employee_id"]]["time"] = 0;
			}
		}
	$result = mysqli_query($conn, "SELECT * FROM faculty") or die("Error");
	
	if (mysqli_num_rows($result) > 0)
		while($row = mysqli_fetch_array($result)) {
			$_SESSION["Dean"][$row["faculty_id"]] = ["name" => $row["dean_name"], "username" => $row["dean_username"], "password" => $row["dean_password"]];
			$_SESSION["Head of Secretary"][$row["faculty_id"]] = ["name" => $row["head_of_secretary_name"], "username" => $row["head_of_secretary_username"], "password" => $row["head_of_secretary_password"]];
			$_SESSION["faculty"][$row["faculty_name"]] = $row["faculty_id"];
		}

	$result = mysqli_query($conn, "SELECT * FROM department") or die("Error");
	
	if (mysqli_num_rows($result) > 0)
		while($row = mysqli_fetch_array($result))
			$_SESSION["Head of Department"][$row["department_id"]] = ["name" => $row["head_of_department_name"], "username" => $row["username"], "password" => $row["password"]];
	
	$result = mysqli_query($conn, "SELECT * FROM exam") or die("Error");
	
	if (mysqli_num_rows($result) > 0)
		while($row = mysqli_fetch_array($result)) {
			$_SESSION["Assistant"][$row["employee_id"]]["plan"][$row["exam_date"]][$row["exam_time"]] = $row["courses_name"]." exam";
			$_SESSION["Assistant"][$row["employee_id"]]["point"]++;
		}
	mysqli_close($conn);
}

echo '<form action="Start Screen.php" method="post">
		Username<br>
		<input type="text" name="username" value=""><br><br>
		Password<br>
		<input type="password" name="password" value=""><br><br>
		<select name="role">
		<option value="Assistant">Assistant</option>
		<option value="Secretary">Secretary</option>
		<option value="Head of Department">Head of Department</option>
		<option value="Head of Secretary">Head of Secretary</option>
		<option value="Dean">Dean</option>
		</select>
		<input type="submit" name="login" value="Log in"></form>
		<form action="Forgot Password.php" method="post"><br>
		<input type="submit" value="Forgot Password"></form>';
?>
</body>
</html>