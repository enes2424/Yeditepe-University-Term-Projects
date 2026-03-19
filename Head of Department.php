<!DOCTYPE html>
<html>
<body>
<?php
session_start();

if (isset($_SESSION["Head of Department"]))
	foreach	($_SESSION["Head of Department"] as $index => $info)
		if ($info["username"] == $_SESSION["currentUsername"]) {
			$id = $index;
			break;
		}
if (!isset($id))
	exit();

$username = $_SESSION["Head of Department"][$id]["username"];
echo "<title>".$_SESSION["Head of Department"][$id]["name"]."</title>";
$servername = "localhost";
$server_username = "root";
$password = "mysql";
$dbname = "exam_planning";

$conn = mysqli_connect($servername, $server_username, $password, $dbname);

if (!$conn)
	die("Connection failed: " . mysqli_connect_error());

echo "<form action='Head of Department.php' method='post'>";
if (!isset($_POST["displayExamList"]))
	echo '<input type="submit" name="displayExamList" value="Display Exam List"></form>';
else {
	echo '<input type="submit" value="Hide Exam List"></form>';
	$sql = "SELECT exam_date, exam_time, courses_name FROM exam, employee ";
	$sql .= "WHERE employee.employee_id = exam.employee_id and ";
	$sql .= "department_id = $id ";
	$sql .= "GROUP BY exam_date, exam_time, courses_name";
	$result = mysqli_query($conn, $sql) or die("Error");


	if (mysqli_num_rows($result) > 0) {
		$index = 0;
		while($row = mysqli_fetch_array($result)) {
			$exams[$index]["exam_date"] = $row["exam_date"];
			$exams[$index]["exam_time"] = $row["exam_time"];
			$exams[$index++]["courses_name"] = $row["courses_name"];
		}
		if (isset($exams)) {
			for ($i = 0; $i < $index - 1; $i++) {
				$date1 = strtotime($exams[$i]["exam_date"]);
				$time1 = strtotime($exams[$i]["exam_time"]); 
				for ($j = $i + 1; $j < $index; $j++) {
					$date2 = strtotime($exams[$j]["exam_date"]);
					$time2 = strtotime($exams[$j]["exam_time"]);
					if 	($date1 > $date2 || ($date1 == $date2 && $time1 > $time2)) {
						$date1 = $date2;
						$time1 = $time2;
						$tmp = $exams[$i];
						$exams[$i] = $exams[$j];
						$exams[$j] = $tmp;
					}
				}
			}
			echo "<table border='1'>";
			echo "<tr><th>Date</th><th>Time</th><th>Course</th></tr>";
			for ($i = 0; $i < $index; $i++)
				echo "<tr><td>".$exams[$i]["exam_date"]."</td><td>".$exams[$i]["exam_time"]."</td><td>".$exams[$i]["courses_name"]."</td></tr>";
			echo "</table>";
		}
	}
}

echo "<form action='Head of Department.php' method='post'>";
if (!isset($_POST["displayWorkloadList"]))
	echo '<input type="submit" name="displayWorkloadList" value="Display Workload List"></form>';
else {
	echo '<input type="submit" value="Hide Workload List"></form>';
	$result = mysqli_query($conn, "SELECT * FROM employee WHERE department_id = $id and role = 'Assistant'") or die("Error");
	if (mysqli_num_rows($result) > 0) {
		$index = 0;
		while($row = mysqli_fetch_array($result))
			$assistants[$index++] = $row["username"];
		$total_point = 0;
		for ($i = 0; $i < $index; $i++)
			foreach	($_SESSION["Assistant"] as $info)
				if ($info["username"] == $assistants[$i]) {
					$total_point += $info["point"];
					break;
				}
		echo "<table border='1'>";
		echo "<tr><th>Assistant Name</th><th>Percentage</th></tr>";
		for ($i = 0; $i < $index; $i++)
			foreach	($_SESSION["Assistant"] as $info)
				if ($info["username"] == $assistants[$i]) {
					if ($total_point != 0)
						echo "<tr><td>".$info["name"]."</td><td>".(100 * $info["point"] / $total_point)."%</td></tr>";
					else
						echo "<tr><td>".$info["name"]."</td><td>0%</td></tr>";
					break;
				}
		echo "</table>";
	}
}

echo "<form action='Start Screen.php' method='post'>";
echo '<input type="submit" value="Return Start Screen"></form>';

mysqli_close($conn);
?>
</body>
</html>