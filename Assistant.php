<!DOCTYPE html>
<html>
<body>
<?php
session_start();

if (isset($_SESSION["Assistant"]))
	foreach	($_SESSION["Assistant"] as $index => $info)
		if ($info["username"] == $_SESSION["currentUsername"]) {
			$id = $index;
			break;
		}
if (!isset($id))
	exit();

$hasCourses = false;
$newCourseAdded = false;
$username = $_SESSION["Assistant"][$id]["username"];
echo "<title>".$_SESSION["Assistant"][$id]["name"]."</title>";
$servername = "localhost";
$server_username = "root";
$password = "mysql";
$dbname = "exam_planning";

$conn = mysqli_connect($servername, $server_username, $password, $dbname);

if (!$conn)
	die("Connection failed: " . mysqli_connect_error());

if (isset($_POST["course"])) {
	if (isset($_SESSION["Assistant"][$id]["courses"])) {
		foreach	($_SESSION["Assistant"][$id]["courses"] as $index => $info) {
			if ($info == $_POST["course"]) {
				$hasCourses = true;
				unset($_SESSION["Assistant"][$id]["courses"][$index]);
				$sql = "SELECT courses_name, courses_day, courses_time FROM employee, department, courses";
				$sql .= " WHERE employee.department_id = department.department_id";
				$sql .= " and courses.department_id = department.department_id";
				$sql .= " and employee.username = '".$username."' and courses_name = '".$_POST["course"]."'";
				$result = mysqli_query($conn, $sql) or die("Error");

				if (mysqli_num_rows($result) > 0)
					while($row = mysqli_fetch_array($result))
						unset($_SESSION["Assistant"][$id]["plan"][$row["courses_day"]][$row["courses_time"]]);
				break;
			}
		}
	}
	if (!$hasCourses) {
		$index = 0;
		while (isset($_SESSION["Assistant"][$id]["courses"][$index]))
			$index++;
		$_SESSION["Assistant"][$id]["courses"][$index] = $_POST["course"];
		$newCourseAdded = true;
	}
}

$sql = "SELECT courses_name FROM employee, department, courses";
$sql .= " WHERE employee.department_id = department.department_id";
$sql .= " and courses.department_id = department.department_id and employee.username = '".$username."'";
$sql .= " GROUP BY courses_name";

$result = mysqli_query($conn, $sql) or die("Error");

if (mysqli_num_rows($result) > 0) {
	echo "<form action='Assistant.php' method='post'><select name='course'>";
    while($row = mysqli_fetch_array($result))
		echo "<option value='" . $row["courses_name"] . "'>".$row["courses_name"]."</option>";
	echo '</select><input type="submit" value="Select Course"></form>';
}

if (isset($_SESSION["Assistant"][$id]["courses"]) && $newCourseAdded) {
	$sql = "SELECT courses_name, courses_day, courses_time FROM employee, department, courses";
	$sql .= " WHERE employee.department_id = department.department_id";
	$sql .= " and courses.department_id = department.department_id";
	$sql .= " and employee.username = '".$username."' and courses_name = '".$_POST["course"]."'";
	$result = mysqli_query($conn, $sql) or die("Error");

	if (mysqli_num_rows($result) > 0)
		while($row = mysqli_fetch_array($result))
			$tmp[$row["courses_day"]][$row["courses_time"]] = $row["courses_name"];
	$ctrl = true;
	foreach	($tmp as $day => $info)
		foreach ($info as $time => $info2) {
			if (isset($_SESSION["Assistant"][$id]["plan"][$day][$time]))
				$ctrl = false;
			$days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
			$daynum = 0;
			foreach ($days as $day2) {
				if ($day == $day2)
					break;
				$daynum++;
			}
			$num =  $daynum + $_SESSION["Assistant"][$id]["time"];
			if (isset($_SESSION["Assistant"][$id]["plan"][date('Y-m-d', strtotime("this week +$num days"))][$time]))
				$ctrl = false;
		}
	if ($ctrl) {
		foreach	($tmp as $day => $info)
			foreach ($info as $time => $info2)
				if ($info2 != "wrong")
					$_SESSION["Assistant"][$id]["plan"][$day][$time] = $info2;
	} else {
		unset($_SESSION["Assistant"][$id]["courses"][$index]);
		echo "This course cannot be taken due to schedule conflicts<br>";
	}
	unset($tmp);
}
	
$days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
$times = ["09:00:00", "11:00:00", "13:00:00", "15:00:00"];

echo "<table border='1'>";
echo "<tr>";
echo "<th>Date</th>";

if (isset($_POST["prev"]))
	$_SESSION["Assistant"][$id]["time"] -= 7;
else if (isset($_POST["next"]))
	$_SESSION["Assistant"][$id]["time"] += 7;

for ($i = 0; $i < 7; $i++) {
	$num = $i + $_SESSION["Assistant"][$id]["time"];
	echo "<th>".date('Y-m-d', strtotime("this week +$num days"))."</th>";
}

echo "</tr>";

echo "<tr>";
echo "<th>Time\Day</th>";

foreach ($days as $day)
	echo "<th>$day</th>";
echo "</tr>";

foreach ($times as $time) {
	echo "<tr>";
	echo "<th>$time</th>";
	$daynum = 0;
	foreach ($days as $day) {
		if (isset($_SESSION["Assistant"][$id]["plan"][$day][$time]))
			echo "<td>".$_SESSION["Assistant"][$id]["plan"][$day][$time]."</td>";
		else {
			$num =  $daynum + $_SESSION["Assistant"][$id]["time"];
			$daydate = date('Y-m-d', strtotime("this week +$num days"));
			if (isset($_SESSION["Assistant"][$id]["plan"][$daydate][$time]))
				echo "<td>".$_SESSION["Assistant"][$id]["plan"][$daydate][$time]."</td>";
			else	
				echo "<td></td>";
		}
		$daynum++;
	}
	echo "</tr>";
}

echo "</table><table>";
echo "<form action='Assistant.php' method='post'>";
echo '<input type="submit" name="prev" value="      prev      "></form>';
echo "<form action='Assistant.php' method='post'>";
echo '<input type="submit" name="next"  value="      next      "></form></table>';
echo "<form action='Assistant.php' method='post'>";
echo '<input type="submit" value="Refresh the table"></form>';
echo "<form action='Start Screen.php' method='post'>";
echo '<input type="submit" value="Return Start Screen"></form>';

mysqli_close($conn);

?>
</body>
</html>