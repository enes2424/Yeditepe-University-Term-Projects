<!DOCTYPE html>
<html>
<body>
<?php
if (isset($_POST["username"])) {
	if ($_POST["username"] == "")
		echo 'Username field cannot be empty';
	else if ($_POST["realname"] == "")
		echo 'Realname field cannot be empty';
	else {
		session_start();
		$isThereUser = false;
		if (isset($_SESSION[$_POST["role"]]))
			foreach	($_SESSION[$_POST["role"]] as $info)
				if ($info["username"] == $_POST["username"] and $info["name"] == $_POST["realname"]) {
					echo $info["password"];
					$isThereUser = true;
					break;
				}
		if (!$isThereUser)
			echo "No such user";
	}
} else
	echo '<form action="Forgot Password.php" method="post">
		Username<br>
		<input type="text" name="username" value=""><br><br>
		Realname<br>
		<input type="text" name="realname" value=""><br><br>
		<select name="role">
		<option value="Assistant">Assistant</option>
		<option value="Secretary">Secretary</option>
		<option value="Head of Department">Head of Department</option>
		<option value="Head of Secretary">Head of Secretary</option>
		<option value="Dean">Dean</option></select>
		<input type="submit" value="Show Password"></form>';
echo "<form action='Start Screen.php' method='post'>";
echo '<input type="submit" value="Return Start Screen"></form>';
?>
</body>
</html>